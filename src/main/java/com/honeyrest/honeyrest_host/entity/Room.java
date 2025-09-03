package com.honeyrest.honeyrest_host.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class Room extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "room_id", nullable = false)
    private Long roomId;

    @ManyToOne(fetch = FetchType.LAZY ,cascade = CascadeType.REMOVE)
    @JoinColumn(name = "accommodation_id", nullable = false)
    private Accommodation accommodation; // 숙소 ID

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "type", length = 60)
    private String type;

    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "max_occupancy", nullable = false)
    private Integer maxOccupancy;

    @Column(name = "standard_occupancy", nullable = false)
    private Integer standardOccupancy;

    @Column(name = "extra_person_fee", precision = 10, scale = 2)
    private BigDecimal extraPersonFee;

    @Column(name = "bed_info", columnDefinition = "JSON")
    private String bedInfo;

    @Column(name = "amenities", columnDefinition = "JSON")
    private String amenities;

    @Column(name = "description",columnDefinition = "TEXT")
    private String description;

    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;

    @Column(name = "status" , length = 20)
    private String status; // 운영 상태


    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RoomImage> images = new ArrayList<>();


}
