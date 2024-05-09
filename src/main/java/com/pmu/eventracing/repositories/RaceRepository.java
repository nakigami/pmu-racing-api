package com.pmu.eventracing.repositories;

import com.pmu.eventracing.entities.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
public interface RaceRepository extends JpaRepository<Race, Long>{
    Race findByNumberAndDate(int number, LocalDate date);
    Page<Race> findAll(Pageable pageable);
}
