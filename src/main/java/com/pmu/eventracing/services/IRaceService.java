package com.pmu.eventracing.services;

import com.pmu.eventracing.entities.Race;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IRaceService {
    Race createRace(Race race) throws Exception;
    Page<Race> getAllRaces(Pageable pageable);
    Race getRaceById(Long id);
}
