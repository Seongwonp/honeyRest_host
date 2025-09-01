package com.honeyrest.honeyrest_host.dto;

import lombok.*;

import java.util.List;

@ToString
@Getter
public class PageResponseDTO<E> {
    // 현재 페이지 수
    private int page;
    // 한페이지에 몇개 보여껀지
    private int size;

    // 전체 페이지 숫자
    private int total;

    // 시작 페이지 번호
    private int start;
    //끝 페이지 번호
    private int end;
    // 이전 페이지 존재 여부
    private boolean prev;
    // 다음 페이지의 존재 여부
    private boolean next;

    private List<E> dtoList;

    @Builder(builderMethodName = "withAll")
    public PageResponseDTO(PageRequestDTO pageRequestDTO, List<E> dtoList, int total) {// 매개변수 3개
        // PageRequestDTO 여기서 이거 두개만 받아오면 되니까.
        this.page = pageRequestDTO.getPage();
        this.size = pageRequestDTO.getSize();

        this.dtoList = dtoList;
        this.total = total;
        // => 이 세가지만 들고오면 된다.(page,size,total) 나머지는 계산을 통해서 들고온다.



        // 페이징에서 나올 개수
//        this.end = (int)(Math.ceil(this.page / 10.0)) * 10; // 끝 페이지 번호 -> 10으로 나눈 값을 올림처리를 한후 *10을 해준다.
//        this.start = this.end - 9; // 왜냐하면 한페이지 10개가 나올꺼기 때문에.

        int displayPageNum = 5;
        int last = (int)(Math.ceil((total/(double)size))); // 3.0 -> 3/ 3/1 -> 5 올림처리 됨.
//        if(last < this.end) {
//            this.end = last; // 엔드 값을 고정하기 위한것.
//        }
        int tempStart = page - 2;
        int tempEnd = page + 2;

        if(tempStart < 1) {
            tempStart = 1;
            tempEnd = displayPageNum;
        }
        if(tempEnd > last) {
            tempEnd = last;
            tempStart = last - displayPageNum + 1;
            if(tempStart < 1) tempStart = 1;
        }
        this.start = tempStart;
        this.end = tempEnd;
//        this.end = Math.min(end, last); // 삼항연산자 -> Math 를 써서 end(끝값)과 작은값을 들고와도 된다.

        // 이전페이지 존재 여부
        this.prev = start > 1; // start 가 1 보다 크다면 무조건 true -> 1, 11, 21 이 오기 때문에

        // 다음 페이지 존재 여부
        this.next = total > this.end * this.page; // 마지막까지 나왔던 게시물 숫자. ex) 20 * 10

    }
    public int getTotalPages() {
        if (size <= 0) return 0;
        return (int)Math.ceil((double) total / size);
    }
    public boolean isFirst() { return page <= 0; }
    public boolean isLast()  { return page >= Math.max(0, getTotalPages() - 1); }

}
