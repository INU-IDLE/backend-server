package com.idle.rushcutter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station_lines", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"station_id", "line_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayStationLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "station_id", nullable = false)
    private SubwayStation station;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private SubwayLine line;

    @Column(nullable = false)
    private Integer sequence; // ex) 1, 2, 3... 해당 노선 내 정렬 순서

    private Boolean isExpress; // 급행 정차 여부 (null or false면 무정차)
}