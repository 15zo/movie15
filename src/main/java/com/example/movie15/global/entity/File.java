package com.example.movie15.global.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class File extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false)
    private Long size;

    @Column(nullable = false, length = 10)
    private String extension;

    @Column(nullable = false, length = 255)
    private String name;

    @Column(nullable = false, length = 30)
    private String type;
}
