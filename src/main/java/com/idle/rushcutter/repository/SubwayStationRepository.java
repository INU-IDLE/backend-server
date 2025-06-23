package com.idle.rushcutter.repository;

import com.idle.rushcutter.entity.SubwayStation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubwayStationRepository extends JpaRepository<SubwayStation, Long> {
    Optional<SubwayStation> findByNumber(String number);
    Optional<SubwayStation> findByNumberAndLineCode(String number, String lineCode);
    List<SubwayStation> findAllByNumberAndLineCode(String number, String lineCode);
}
