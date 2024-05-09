package com.pmu.eventracing.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.Hibernate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Entity
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private LocalDate date;

    @Min(1)
    private int number;

    @NotBlank
    private String name;

    @OneToMany(mappedBy = "race", cascade = CascadeType.ALL)
    @ToString.Exclude
    @Size(min = 3)
    @NotNull
    private List<Runner> runners = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Race race = (Race) o;
        return id != null && Objects.equals(id, race.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
