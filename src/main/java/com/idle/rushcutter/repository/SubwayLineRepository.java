package com.idle.rushcutter.repository;

import com.idle.rushcutter.entity.SubwayLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubwayLineRepository extends JpaRepository<SubwayLine, Long> {
    Optional<SubwayLine> findByName(String name);
}