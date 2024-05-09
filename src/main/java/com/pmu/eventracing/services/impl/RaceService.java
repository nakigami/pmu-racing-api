package com.pmu.eventracing.services.impl;

import com.pmu.eventracing.entities.Race;
import com.pmu.eventracing.entities.Runner;
import com.pmu.eventracing.repositories.RaceRepository;
import com.pmu.eventracing.repositories.RunnerRepository;
import com.pmu.eventracing.services.IRaceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Race service handle all business operations related to races
 */
@Slf4j
@Service
public class RaceService implements IRaceService {

    private final RaceRepository raceRepository;
    private final RunnerRepository runnerRepository;

    private KafkaTemplate<String, String> kafkaTemplate;


    /**
     * The service constructor
     *
     * @param raceRepository - Race repository
     * @param runnerRepository - Runner repository
     * @param kafkaTemplate - Kafka template
     */
    public RaceService(RaceRepository raceRepository, RunnerRepository runnerRepository, KafkaTemplate<String, String> kafkaTemplate) {
        this.raceRepository = raceRepository;
        this.runnerRepository = runnerRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * This function is used to create the races
     *
     * @param race - The given race
     * @return Race
     */
    @CacheEvict(value = "races", allEntries = true)
    @Override
    public Race createRace(Race race) {
        log.info("Start of RaceService -> createRace()");

        // Check if there is already a race with the same number in the same day
        if (!this.isRaceNumberUniqueForDate(race.getNumber(), race.getDate())) {
            throw new IllegalArgumentException("A race with the same number already exists for the given date.");
        }

        // Check if there is a gap in the numbers of runners
        if (!this.validateRunnersNumbers(race)) {
            throw new IllegalArgumentException("Runner numbers must start from 1 without any gaps or duplicates.");
        }

        try {
            Race createdRace =  raceRepository.save(race);

            // Associate each runner to the created race
            for (Runner runner : createdRace.getRunners()) {
                runner.setRace(createdRace);
            }

            // Save the modification done on runners objects
            List<Runner> runnersWithRace = runnerRepository.saveAll(createdRace.getRunners());

            // Check if all runners are well saved
            if (runnersWithRace.size() != createdRace.getRunners().size()) {
                throw new RuntimeException("Failed to save all runners with associated races.");
            }

            // Publish race creation event to Kafka in the topic race-events
            String raceTopic = "race-events";
            StringBuilder raceMessage = new StringBuilder("*********************************\n" +
                    " " +
                    "A race has been created with the following infos :\n" +
                    "  - Number : " + createdRace.getNumber() + "\n" +
                    "  - Date : " + createdRace.getDate() + "\n" +
                    "  - Name : " + createdRace.getName() + "\n" +
                    "  - Runners : \n");

            // Add the runners infos to the message
            for (Runner runner : createdRace.getRunners()) {
                raceMessage.append("        - Number : ").append(runner.getNumber()).append("\n").append("        - Name : ").append(runner.getName()).append("\n");
            }

            // Send the message to the kafka topic
            kafkaTemplate.send(raceTopic, raceMessage.toString());

            log.info("A message has been sent to " + raceTopic + " topic : " + raceMessage);
            log.info("End of RaceService -> createRace()");

            return createdRace;
        } catch (Exception e) {
            log.warn("Exception caused by : RaceService -> createRace()");
            throw new RuntimeException("Failed to create race: " + e.getMessage(), e);
        }
    }

    /**
     * This function is used to extract all the stored races
     *
     * @return a list of races
     */
    @Cacheable("races")
    @Override
    public Page<Race> getAllRaces(Pageable pageable) {
        return raceRepository.findAll(pageable);
    }

    /**
     * This function is used to extract a race with a given id
     *
     * @param id - The race id
     * @return - the found race
     */
    @Cacheable(value = "races", key = "#id")
    @Override
    public Race getRaceById(Long id) {
        return raceRepository.findById(id).orElse(null);
    }

    /**
     * This is a private helper function to check if the number of the race is unique or nada
     *
     * @param number - The number of the race
     * @param date - The date of the race
     * @return - True if the number is unique and false if it's not
     */
    private boolean isRaceNumberUniqueForDate(int number, LocalDate date) {
        return raceRepository.findByNumberAndDate(number, date) == null;
    }

    /**
     * This is a private helper function to validate the runners numbers
     *
     * @param race - The given race
     * @return - True if the given runners are valid or no
     */
    private boolean validateRunnersNumbers(Race race) {
        Set<Integer> runnerNumbers = new HashSet<>();
        Set<String> runnerNames = new HashSet<>();

        for (Runner runner : race.getRunners()) {
            if (runner.getNumber() < 1) {
                return false; // Start number must be >= 1
            }
            if (!runnerNumbers.add(runner.getNumber())) {
                return false; // Duplicate number detected
            }
            if (!runnerNames.add(runner.getName())) {
                return false; // Duplicate name detected
            }
        }

        // Check for any gaps
        for (int i = 1; i <= race.getRunners().size(); i++) {
            if (!runnerNumbers.contains(i)) {
                return false; // Gap detected
            }
        }
        return true;

    }
}