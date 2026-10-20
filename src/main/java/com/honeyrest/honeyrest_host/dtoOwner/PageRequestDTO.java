package com.honeyrest.honeyrest_host.dtoOwner;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageRequestDTO {
    /** 한 페이지 최대 건수 (size 파라미터 조작으로 전체 테이블을 한 번에 읽지 않도록) */
    public static final int MAX_SIZE = 100;

    @Builder.Default
    private int page = 1;
    @Builder.Default
    private int size = 10;

    /** 페이지는 1부터 시작한다 (1 미만은 1로 보정) */
    public void setPage(int page) {
        this.page = Math.max(page, 1);
    }

    public void setSize(int size) {
        this.size = size <= 0 ? 10 : Math.min(size, MAX_SIZE);
    }

    public int getPage() {
        return Math.max(page, 1);
    }

    public int getSize() {
        return size <= 0 ? 10 : Math.min(size, MAX_SIZE);
    }
}
