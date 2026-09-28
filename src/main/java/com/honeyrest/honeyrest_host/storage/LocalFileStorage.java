package com.honeyrest.honeyrest_host.storage;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 로컬 디스크 저장소 (기본값).
 * <p>
 * {@code app.storage.local.dir} (기본 ./uploads) 아래에 파일을 저장하고,
 * {@code /uploads/{dir}/{파일명}} 형태의 URL을 반환한다. 정적 서빙은 WebMvcConfig 의 리소스 핸들러가 담당한다.
 * Firebase 키 파일 없이도 애플리케이션을 띄울 수 있도록 하기 위한 구현이다.
 */
@Log4j2
@Component
@ConditionalOnProperty(name = "app.storage.type", havingValue = "local", matchIfMissing = true)
public class LocalFileStorage implements FileStorage {

    /** 업로드 파일이 서빙되는 URL 접두사 */
    public static final String URL_PREFIX = "/uploads/";

    private final Path baseDir;

    public LocalFileStorage(@Value("${app.storage.local.dir:./uploads}") String dir) {
        this.baseDir = Path.of(dir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(baseDir);
        } catch (IOException e) {
            throw new UncheckedIOException("업로드 디렉터리를 생성할 수 없습니다: " + baseDir, e);
        }
        log.info("로컬 파일 저장소 사용: {}", baseDir);
    }

    @Override
    public String upload(MultipartFile file, String dir) throws IOException {
        String safeDir = normalizeDir(dir);
        String filename = UUID.randomUUID() + extensionOf(file.getOriginalFilename());
        String key = safeDir.isEmpty() ? filename : safeDir + "/" + filename;

        Path target = resolveInsideBase(key);
        Files.createDirectories(target.getParent());
        try (InputStream in = file.getInputStream()) {
            Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
        }
        return URL_PREFIX + key;
    }

    @Override
    public void delete(String url) {
        String key = extractKey(url);
        if (key == null) {
            return;
        }
        try {
            Files.deleteIfExists(resolveInsideBase(key));
        } catch (IOException | IllegalArgumentException e) {
            log.warn("로컬 파일 삭제 실패: {} ({})", url, e.getMessage());
        }
    }

    @Override
    public String extractKey(String url) {
        if (url == null) {
            return null;
        }
        String path = url;
        int q = path.indexOf('?');
        if (q >= 0) {
            path = path.substring(0, q);
        }
        int idx = path.indexOf(URL_PREFIX);
        if (idx < 0) {
            return null;
        }
        return URLDecoder.decode(path.substring(idx + URL_PREFIX.length()), StandardCharsets.UTF_8);
    }

    /** 저장소 루트 디렉터리 (리소스 핸들러 등록용) */
    public Path getBaseDir() {
        return baseDir;
    }

    // 상위 경로 탈출(../) 방지를 위해 기준 디렉터리 밖으로 나가는 경로는 거부한다.
    private Path resolveInsideBase(String key) {
        Path resolved = baseDir.resolve(key).normalize();
        if (!resolved.startsWith(baseDir)) {
            throw new IllegalArgumentException("허용되지 않은 경로입니다: " + key);
        }
        return resolved;
    }

    private static String normalizeDir(String dir) {
        if (dir == null) {
            return "";
        }
        String d = dir.replace('\\', '/').trim();
        while (d.startsWith("/")) d = d.substring(1);
        while (d.endsWith("/")) d = d.substring(0, d.length() - 1);
        if (d.contains("..")) {
            throw new IllegalArgumentException("허용되지 않은 디렉터리입니다: " + dir);
        }
        return d;
    }

    // 원본 파일명은 한글/공백 등으로 URL 문제가 생길 수 있어 확장자만 보존한다.
    private static String extensionOf(String originalFilename) {
        if (originalFilename == null) {
            return "";
        }
        int dot = originalFilename.lastIndexOf('.');
        if (dot < 0 || dot == originalFilename.length() - 1) {
            return "";
        }
        String ext = originalFilename.substring(dot + 1).toLowerCase();
        return ext.matches("[a-z0-9]{1,10}") ? "." + ext : "";
    }
}
