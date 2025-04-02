package com.idle.rushcutter.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "lines")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubwayLine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    private String color;

    @OneToMany(mappedBy = "line")
    private List<SubwayStationLine> stationLines;
}
