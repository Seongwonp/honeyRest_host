package com.honeyrest.honeyrest_host.dtoAdmin;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    private String type;

    private String keyword;

    /** 1 미만 페이지(이전 버튼의 page=0 등)를 1로 보정한다. PageRequest.of(-1) 이 400 을 내던 문제 */
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    public void setSize(int size) {
        this.size = size <= 0 ? 10 : Math.min(size, 100);
    }

    public String[] getTypes() {
        /* type 문자열을 배열로 변환 */
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split(""); // 한글자씩 나눠서 반환 됨.
    }

    /* Spring Data JPA Pageable 객체 생성 */
    public Pageable getPageable(Sort sort) {
        return PageRequest.of(Math.max(page, 1) - 1, size <= 0 ? 10 : Math.min(size, 100), sort);
    }

    public String getLink() {
        StringBuilder builder = new StringBuilder();
        builder.append("page=").append(this.page);
        builder.append("&size=").append(this.size);

        if (type != null && type.length() > 0) {
            builder.append("&type=").append(type);
        }
        if (keyword != null) {
            builder.append("&keyword=")
                    .append(URLEncoder.encode(keyword, StandardCharsets.UTF_8));
        }
        return builder.toString();
    }
}
