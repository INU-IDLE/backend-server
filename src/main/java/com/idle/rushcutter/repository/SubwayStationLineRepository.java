package com.idle.rushcutter.repository;

import com.idle.rushcutter.dto.StationLineDto;
import com.idle.rushcutter.entity.SubwayStationLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubwayStationLineRepository extends JpaRepository<SubwayStationLine, Long> {

    @Query("SELECT new com.idle.rushcutter.dto.StationLineDto(" +
            "sl.station.number, sl.station.name, sl.line.name, sl.sequence, sl.isExpress) " +
            "FROM SubwayStationLine sl " +
            "WHERE (:lineName IS NULL OR sl.line.name = :lineName) " +
            "ORDER BY sl.sequence")
    List<StationLineDto> findByLineName(@Param("lineName") String lineName);
}
