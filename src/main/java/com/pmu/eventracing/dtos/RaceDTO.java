package com.pmu.eventracing.dtos;

import com.pmu.eventracing.entities.Race;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RaceDTO {
    private Long id;

    @NotBlank
    private String date;

    @Min(1)
    private int number;

    @NotBlank
    private String name;

    @NotNull
    @NotEmpty
    @Size(min = 3)
    private List<RunnerDTO> runners;

    public static RaceDTO fromRaceEntity(Race race) {
        RaceDTO dto = new RaceDTO();
        dto.setId(race.getId());
        dto.setDate(race.getDate().toString());
        dto.setNumber(race.getNumber());
        dto.setName(race.getName());
        dto.setRunners(race.getRunners().stream()
                .map(RunnerDTO::fromRunnerEntity)
                .collect(Collectors.toList()));
        return dto;
    }

    public Race toRaceEntity() {
        Race race = new Race();
        race.setId(this.getId());
        race.setDate(LocalDate.parse(this.getDate()));
        race.setNumber(this.getNumber());
        race.setName(this.getName());
        race.setRunners(this.getRunners().stream()
                .map(RunnerDTO::toRunnerEntity)
                .collect(Collectors.toList()));
        return race;
    }
}
