package com.pmu.eventracing.dtos;

import com.pmu.eventracing.entities.Runner;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RunnerDTO {

    @Min(1)
    private int number;

    @NotBlank
    @NotNull
    private String name;

    public static RunnerDTO fromRunnerEntity(Runner runner) {
        RunnerDTO dto = new RunnerDTO();
        dto.setNumber(runner.getNumber());
        dto.setName(runner.getName());
        return dto;
    }

    public Runner toRunnerEntity() {
        Runner runner = new Runner();
        runner.setNumber(this.getNumber());
        runner.setName(this.getName());
        return runner;
    }
}

