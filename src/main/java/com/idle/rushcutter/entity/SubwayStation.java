package com.idle.rushcutter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(
        name = "stations",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"number", "lnCd"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubwayStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", nullable = false)
    private String number;

    @Column(name = "code", nullable = false)
    private String lineCode;

    @Column(nullable = false)
    private String name;

    @Column(name = "odsay_station_id", unique = true)
    private String odsayStationId;

    @Builder.Default
    private Boolean transferAvailable = false;

    @OneToMany(mappedBy = "station")
    private List<SubwayStationLine> stationLines;
}
