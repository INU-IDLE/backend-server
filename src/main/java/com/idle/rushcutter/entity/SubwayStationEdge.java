package com.idle.rushcutter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station_edges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubwayStationEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "from_station_id", nullable = false)
    private SubwayStation fromStation;

    @ManyToOne
    @JoinColumn(name = "to_station_id", nullable = false)
    private SubwayStation toStation;

    @ManyToOne
    @JoinColumn(name = "line_id", nullable = false)
    private SubwayLine line;

    @Builder.Default
    private Boolean isExpress = false;
}
