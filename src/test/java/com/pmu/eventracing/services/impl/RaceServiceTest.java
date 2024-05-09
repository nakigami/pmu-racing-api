package com.pmu.eventracing.services.impl;

import com.pmu.eventracing.entities.Race;
import com.pmu.eventracing.entities.Runner;
import com.pmu.eventracing.repositories.RaceRepository;
import com.pmu.eventracing.repositories.RunnerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class RaceServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @InjectMocks
    private RaceService raceService;

    @Mock
    private RunnerRepository runnerRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testCreateRace() {
        // Mocking data
        Race race = new Race();
        race.setNumber(1);
        race.setDate(LocalDate.of(2024, 5, 10));
        race.setName("Marathon de Marseille");
        Runner runner1 = new Runner();
        runner1.setNumber(1);
        runner1.setName("Joaquin Correa");
        Runner runner2 = new Runner();
        runner2.setNumber(2);
        runner2.setName("Valentin Rongier");
        List<Runner> runners = new ArrayList<>();
        runners.add(runner1);
        runners.add(runner2);
        race.setRunners(runners);

        // Mocking repository behavior
        when(raceRepository.save(race)).thenReturn(race);
        when(runnerRepository.saveAll(race.getRunners())).thenReturn(race.getRunners());

        // Calling the method under test
        Race createdRace = raceService.createRace(race);

        // Verifying behavior
        verify(raceRepository, times(1)).save(race);
        verify(runnerRepository, times(1)).saveAll(race.getRunners());
        verify(kafkaTemplate, times(1)).send(anyString(), anyString());

        // Assertions
        assertEquals(race, createdRace);
        assertEquals(race.getRunners().size(), createdRace.getRunners().size());
    }

    @Test
    public void testGetAllRaces() {
        // Mocking data
        Race race1 = new Race();
        race1.setId(1L);
        race1.setNumber(1);
        race1.setDate(LocalDate.of(2024, 5, 10));
        race1.setName("Marathon of PSG - The khéné version");
        Runner runner1 = new Runner();
        runner1.setId(1L);
        runner1.setNumber(1);
        runner1.setName("Kylian Noisetteppé");
        List<Runner> runners1 = new ArrayList<>();
        runners1.add(runner1);
        race1.setRunners(runners1);

        Race race2 = new Race();
        race2.setId(2L);
        race2.setNumber(2);
        race2.setDate(LocalDate.of(2024, 6, 15));
        race2.setName("The Fada Battle of Paris");
        Runner runner2 = new Runner();
        runner2.setId(2L);
        runner2.setNumber(12);
        runner2.setName("Lucas Herna-Null");
        List<Runner> runners2 = new ArrayList<>();
        runners2.add(runner2);
        race2.setRunners(runners2);

        List<Race> races = Arrays.asList(race1, race2);

        // Mocking repository behavior
        when(raceRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(races));

        // Calling the method under test
        List<Race> allRaces = raceService.getAllRaces(PageRequest.of(0, 10)).getContent();

        // Verifying behavior
        verify(raceRepository, times(1)).findAll(PageRequest.of(0, 10));

        // Assertions
        assertEquals(races.size(), allRaces.size());
        assertEquals(race1.getRunners().size(), allRaces.get(0).getRunners().size());
        assertEquals(race2.getRunners().size(), allRaces.get(1).getRunners().size());
    }

    @Test
    public void testGetRaceById() {
        // Mocking data
        Race race = new Race();
        race.setId(1L);
        race.setNumber(1);
        race.setDate(LocalDate.of(2024, 5, 10));
        race.setName("The regional battle of Marseille");
        Runner runner = new Runner();
        runner.setId(1L);
        runner.setNumber(11);
        runner.setName("Azzedine Ounahi");
        List<Runner> runners = new ArrayList<>();
        runners.add(runner);
        race.setRunners(runners);

        // Mocking repository behavior
        when(raceRepository.findById(1L)).thenReturn(Optional.of(race));

        // Calling the method under test
        Race retrievedRace = raceService.getRaceById(1L);

        // Verifying behavior
        verify(raceRepository, times(1)).findById(1L);

        // Assertions
        assertEquals(race.getId(), retrievedRace.getId());
        assertEquals(race.getNumber(), retrievedRace.getNumber());
        assertEquals(race.getDate(), retrievedRace.getDate());
        assertEquals(race.getName(), retrievedRace.getName());
        assertEquals(race.getRunners().size(), retrievedRace.getRunners().size());
        assertEquals(race.getRunners().get(0).getId(), retrievedRace.getRunners().get(0).getId());
        assertEquals(race.getRunners().get(0).getNumber(), retrievedRace.getRunners().get(0).getNumber());
        assertEquals(race.getRunners().get(0).getName(), retrievedRace.getRunners().get(0).getName());
    }
}
