package com.idle.rushcutter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "stations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubwayStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "number", unique = true, nullable = false)
    private String number;

    @Column(nullable = false)
    private String name;

    @Column(name = "operator_code")
    private String operatorCode;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    private Boolean transferAvailable = false;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToMany(mappedBy = "station")
    private List<SubwayStationLine> stationLines;
}
