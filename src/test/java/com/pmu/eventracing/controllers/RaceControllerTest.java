package com.pmu.eventracing.controllers;

import com.pmu.eventracing.dtos.RaceDTO;
import com.pmu.eventracing.dtos.RunnerDTO;
import com.pmu.eventracing.entities.Race;
import com.pmu.eventracing.entities.Runner;
import com.pmu.eventracing.services.IRaceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class RaceControllerTest {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private IRaceService raceService;

    @Test
    public void testCreateRace() throws Exception {
        RaceDTO raceDTO = new RaceDTO();
        String dateString = "2022-10-20";
        raceDTO.setDate(dateString);
        raceDTO.setNumber(1);
        raceDTO.setName("Test Race");

        List<RunnerDTO> runnerDTOs = new ArrayList<>();
        RunnerDTO runnerDTO1 = new RunnerDTO();
        runnerDTO1.setNumber(1);
        runnerDTO1.setName("Runner 1");
        runnerDTOs.add(runnerDTO1);
        RunnerDTO runnerDTO2 = new RunnerDTO();
        runnerDTO2.setNumber(2);
        runnerDTO2.setName("Runner 2");
        runnerDTOs.add(runnerDTO2);
        raceDTO.setRunners(runnerDTOs);
        RunnerDTO runnerDTO3 = new RunnerDTO();
        runnerDTO3.setNumber(3);
        runnerDTO3.setName("Runner 3");
        runnerDTOs.add(runnerDTO3);
        raceDTO.setRunners(runnerDTOs);

        List<Runner> runners = new ArrayList<>();
        for (RunnerDTO runnerDTO : runnerDTOs) {
            Runner runner = new Runner();
            runner.setNumber(runnerDTO.getNumber());
            runner.setName(runnerDTO.getName());
            runners.add(runner);
        }

        Race race = new Race();
        race.setId(1L);
        race.setDate(convertStringToLocalDate(dateString));
        race.setNumber(raceDTO.getNumber());
        race.setName(raceDTO.getName());
        race.setRunners(runners);

        when(raceService.createRace(any())).thenReturn(race);

        mockMvc.perform(post("/api/v1/races")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"date\":\"2022-10-20\",\"number\":1,\"name\":\"Test Race\",\"runners\":[{\"number\":1,\"name\":\"Runner 1\"},{\"number\":2,\"name\":\"Runner 2\"}, {\"number\":3,\"name\":\"Runner 3\"}]}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.date").value("2022-10-20")) // Date in String format
                .andExpect(jsonPath("$.number").value(1))
                .andExpect(jsonPath("$.name").value("Test Race"))
                .andExpect(jsonPath("$.runners", hasSize(3)))
                .andExpect(jsonPath("$.runners[0].number").value(1))
                .andExpect(jsonPath("$.runners[0].name").value("Runner 1"))
                .andExpect(jsonPath("$.runners[1].number").value(2))
                .andExpect(jsonPath("$.runners[1].name").value("Runner 2"))
                .andExpect(jsonPath("$.runners[2].number").value(3))
                .andExpect(jsonPath("$.runners[2].name").value("Runner 3"));
    }

    @Test
    public void testGetAllRaces() throws Exception {
        // Mocking races for the first page
        Race race1 = new Race();
        race1.setId(1L);
        race1.setDate(LocalDate.of(2022, 10, 20));
        race1.setNumber(1);
        race1.setName("Marseille Race");
        race1.setRunners(Arrays.asList(new Runner(1L, race1, 1, "Joaquin Correa"),
                new Runner(2L, race1, 2, "Valentin Rongier"),
                new Runner(3L, race1, 3, "Luis Henrique")));

        Race race2 = new Race();
        race2.setId(2L);
        race2.setDate(LocalDate.of(2022, 10, 21));
        race2.setNumber(2);
        race2.setName("PSG Race");
        race2.setRunners(Arrays.asList(new Runner(4L, race2, 4, "Kylian Noisette"),
                new Runner(5L, race2, 5, "Lucas Herna-Null"),
                new Runner(6L, race2, 6, "Keylor Nada")));

        List<Race> racesFirstPage = Arrays.asList(race1, race2);
        Page<Race> racesPage = new PageImpl<>(racesFirstPage);

        when(raceService.getAllRaces(any(Pageable.class))).thenReturn(racesPage);

        // Perform the GET request for the first page
        mockMvc.perform(get("/api/v1/races")
                        .param("page", "0") // Simulating the first page
                        .param("size", "10") // Setting the page size
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/hal+json"))
                .andExpect(jsonPath("$._embedded.raceDTOList", hasSize(2)))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].id").value(1))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].date").value("2022-10-20"))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].number").value(1))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].name").value("Marseille Race"))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners", hasSize(3)))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[0].number").value(1))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[0].name").value("Joaquin Correa"))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[1].number").value(2))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[1].name").value("Valentin Rongier"))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[2].number").value(3))
                .andExpect(jsonPath("$._embedded.raceDTOList[0].runners[2].name").value("Luis Henrique"))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].id").value(2))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].date").value("2022-10-21"))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].number").value(2))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].name").value("PSG Race"))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners", hasSize(3)))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[0].number").value(4))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[0].name").value("Kylian Noisette"))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[1].number").value(5))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[1].name").value("Lucas Herna-Null"))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[2].number").value(6))
                .andExpect(jsonPath("$._embedded.raceDTOList[1].runners[2].name").value("Keylor Nada"));
    }

    private LocalDate convertStringToLocalDate(String dateString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        return LocalDate.parse(dateString, formatter);
    }
}
