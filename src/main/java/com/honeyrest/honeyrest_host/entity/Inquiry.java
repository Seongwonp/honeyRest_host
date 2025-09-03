package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "inquiry")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inquiry extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long inquiryId;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    // 관련 숙소
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "accommodation_id")
    private Accommodation accommodation;

    // 제목
    private String title;

    // 문의 내용
    @Column(columnDefinition = "TEXT")
    private String content;

    // 답변 내용
    private String reply;

    // 답변 여부
    private Boolean isReplied;

    // 문의 카테고리
    @Column(length = 50)
    private String category;

    /* ===== 도메인 변경 메서드들 ===== */
    public void changeTitle(String title)      { this.title = title; }
    public void changeContent(String content)  { this.content = content; }
    public void changeCategory(String category){ this.category = category; }

    /** 답변 등록/수정 시 isReplied 동기화 */
    public void changeReply(String reply) {
        this.reply = reply;
        this.isReplied = (reply != null && !reply.isBlank());
    }
    public void changeUser(User user) { this.user = user; }
    public void changeAccommodation(Accommodation acc) { this.accommodation = acc; }
}