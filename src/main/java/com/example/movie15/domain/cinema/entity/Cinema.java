package com.example.movie15.domain.cinema.entity;

import com.example.movie15.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Cinema extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Hall> hallList = new ArrayList<>();

    // 연관관계 편의 메서드
    public void addHall(Hall hall){
        hallList.add(hall);
        hall.setCinema(this);
    }
}
