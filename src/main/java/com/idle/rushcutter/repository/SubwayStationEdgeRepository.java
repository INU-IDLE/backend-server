package com.idle.rushcutter.repository;

import com.idle.rushcutter.entity.SubwayStation;
import com.idle.rushcutter.entity.SubwayStationEdge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface SubwayStationEdgeRepository extends JpaRepository<SubwayStationEdge, Long> {
    List<SubwayStationEdge> findByFromStation(SubwayStation station);
    List<SubwayStationEdge> findByLineId(Long lineId);

    @Modifying
    @Transactional
    @Query("""
                DELETE FROM SubwayStationEdge e
                WHERE e.fromStation.number = :fromNumber
                AND e.toStation.number = :toNumber
            """)
    void deleteByFromStationNumberAndToStationNumber(String fromNumber, String toNumber);
}
