package com.example.movie15.domain.movie.entity;

import com.example.movie15.global.entity.BaseEntity;

import com.example.movie15.global.entity.File;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class MoviePoster extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "file_id", nullable = false)
    private File file;

}
