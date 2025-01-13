package com.example.movie15.domain.cinema.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
public class Cinema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CinemaHall> cinemaHalls = new ArrayList<>();

    public Cinema(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public Cinema() {

    }

    public void addCinemaHall(CinemaHall cinemaHall) {
        this.cinemaHalls.add(cinemaHall);
    }

}
