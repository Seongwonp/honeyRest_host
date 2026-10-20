package com.honeyrest.honeyrest_host.serviceAdmin.accommodation;

import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationCreateRequestDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationImageDTO;
import com.honeyrest.honeyrest_host.dtoAdmin.accommodation.AccommodationUpdateRequestDTO;
import com.honeyrest.honeyrest_host.serviceAdmin.CancellationPolicyService;
import com.honeyrest.honeyrest_host.storage.FileStorage;
import com.honeyrest.honeyrest_host.storage.UploadCompensation;
import com.honeyrest.honeyrest_host.utilAdmin.AmenitiesParser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**
 * 회사 관리자 숙소 등록/수정 흐름. 전체를 한 트랜잭션으로 묶고, 파일은 {@link UploadCompensation} 으로
 * 롤백 시 이번 요청에서 올린 파일을 지우고 뗀 파일은 커밋 후 지운다.
 */
@Service
@RequiredArgsConstructor
public class AccommodationRegistrationService {

    static final String THUMBNAIL_DIR = "accommodations";

    private final AccommodationService accommodationService;
    private final AccommodationImageService accommodationImageService;
    private final AccommodationTagService accommodationTagService;
    private final CancellationPolicyService cancellationPolicyService;
    private final FileStorage fileStorage;

    /**
     * 숙소 등록 (항상 승인 대기 PENDING 으로 시작).
     *
     * @return 새 숙소 ID
     */
    @Transactional(rollbackFor = Exception.class)
    public Long register(Integer companyId, AccommodationCreateRequestDTO form, List<MultipartFile> subImages)
            throws IOException {
        UploadCompensation files = UploadCompensation.begin(fileStorage);

        form.setCompanyId(companyId);
        form.setStatus(AccommodationStatusPolicy.INITIAL_STATUS);

        // (1) 대표 썸네일 업로드. 하위 서비스가 다시 올리지 않도록 폼에서 뗀다
        MultipartFile thumbnailFile = form.getFile();
        form.setFile(null);
        if (thumbnailFile != null && !thumbnailFile.isEmpty()) {
            form.setThumbnail(files.upload(thumbnailFile, THUMBNAIL_DIR));
        }

        // (2) 편의시설 JSON 정규화
        form.setAmenities(AmenitiesParser.normalizeToJson(form.getAmenities()));
        if (form.getTagIds() == null) form.setTagIds(Collections.emptyList());

        // (3) 숙소 저장 (태그 매핑 포함)
        Long accId = accommodationService.create(form).getAccommodationId();

        // (4) 환불 정책
        cancellationPolicyService.saveOrUpdate(accId, form.getCancellationPolicyDetail());

        // (5) 메인 썸네일 → image 테이블 + 숙소 thumbnail 동기화
        if (form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
            accommodationImageService.upsertMainThumbnail(accId, mainImage(form.getThumbnail()));
            accommodationImageService.updateThumbnailUrl(accId, form.getThumbnail());
        }

        // (6) 서브 이미지 업로드 후 저장
        saveSubImages(accId, subImages, files);
        return accId;
    }

    /**
     * 숙소 수정. 소유권은 호출 전에(@PreAuthorize) 확인한다.
     * 상태 변경 검증을 파일 업로드·이미지 삭제 같은 부수효과보다 먼저 한다.
     */
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long id, AccommodationUpdateRequestDTO form, MultipartFile thumbnailFile,
                     List<MultipartFile> subImages, boolean deleteThumbnail, List<Long> deleteSubImageIds)
            throws IOException {
        // 폼의 companyId 는 신뢰하지 않는다
        form.setCompanyId(null);

        // 0) 부수효과 전에 상태 변경 검증 (ACTIVE 는 총관리자 승인으로만)
        AccommodationCreateRequestDTO current = accommodationService.getById(id);
        AccommodationStatusPolicy.resolveCompanyAdminStatus(current == null ? null : current.getStatus(), form.getStatus());

        UploadCompensation files = UploadCompensation.begin(fileStorage);

        // 1) amenities / 환불 규정 정규화
        form.setAmenities(AmenitiesParser.normalizeToJson(form.getAmenities()));
        form.setPolicyMultiline(AmenitiesParser.normalizeToJson(form.getPolicyMultiline()));

        // 2) 썸네일: 삭제 토글 또는 새 파일이면 기존 MAIN 을 떼고(파일은 커밋 후 삭제), 새 파일은 보상 대상으로 올린다
        boolean newFileUploaded = thumbnailFile != null && !thumbnailFile.isEmpty();
        if (deleteThumbnail || newFileUploaded) {
            accommodationImageService.deleteMainThumbnail(id).forEach(files::deleteAfterCommit);
            form.setThumbnail(null);
        }
        String newMainUrl = null;
        if (newFileUploaded) {
            newMainUrl = files.upload(thumbnailFile, THUMBNAIL_DIR);
            form.setThumbnail(newMainUrl);
        } else if (!deleteThumbnail && form.getThumbnail() != null && !form.getThumbnail().isBlank()) {
            newMainUrl = form.getThumbnail(); // 파일 없이 URL 만 입력/유지
        }

        // 3) 스칼라/연관 필드 (썸네일 포함)
        accommodationService.update(id, form);

        // 4) 메인 이미지(image 테이블) upsert
        if (newMainUrl != null && !newMainUrl.isBlank()) {
            accommodationImageService.upsertMainThumbnail(id, mainImage(newMainUrl));
        }

        // 5) 서브 이미지 삭제(이 숙소 소속만) / 추가
        if (deleteSubImageIds != null && !deleteSubImageIds.isEmpty()) {
            accommodationImageService.deleteSubImages(id, deleteSubImageIds);
        }
        saveSubImages(id, subImages, files);

        // 6) 태그 / 환불 규정
        accommodationTagService.replaceMapping(id, form.getTagIds() == null ? Collections.emptyList() : form.getTagIds());
        cancellationPolicyService.saveOrUpdate(id, form.getPolicyMultiline());
    }

    private void saveSubImages(Long accId, List<MultipartFile> subImages, UploadCompensation files) throws IOException {
        if (subImages == null || subImages.isEmpty()) return;
        int order = 1; // MAIN=0 다음
        for (MultipartFile file : subImages) {
            if (file == null || file.isEmpty()) continue;
            String url = files.upload(file, "accommodations/" + accId + "/images");
            accommodationImageService.saveOrUpload(accId, AccommodationImageDTO.builder()
                    .imageUrl(url) // 이미 업로드했으므로 file 은 넘기지 않는다
                    .imageType("SUB")
                    .sortOrder(order++)
                    .build());
        }
    }

    private static AccommodationImageDTO mainImage(String url) {
        return AccommodationImageDTO.builder().imageUrl(url).imageType("MAIN").sortOrder(0).build();
    }
}
