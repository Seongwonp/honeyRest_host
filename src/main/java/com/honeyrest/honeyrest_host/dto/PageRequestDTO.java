package com.honeyrest.honeyrest_host.dto;


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

    public String[] getTypes() {
        /* type 문자열을 배열로 변환 */
        if (type == null || type.isEmpty()) {
            return null;
        }
        return type.split(""); // 한글자씩 나눠서 반환 됨.
    }

    /* Spring Data JPA Pageable 객체 생성 */
    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
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
