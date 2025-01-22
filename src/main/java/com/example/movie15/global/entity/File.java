package com.example.movie15.global.entity;

import com.example.movie15.global.model.FileExtension;
import com.example.movie15.global.model.FileType;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class File extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 파일 고유 식별자

    @Column(nullable = false)
    private String url; // 파일 URL

    @Column(nullable = false)
    private Integer size; // 파일 크기

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileExtension extension; // 파일 확장자

    @Column(nullable = false, length = 255)
    private String name; // 파일 이름

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileType type; // 파일 유형

    public File(String url, String name, Integer size, FileExtension extension, FileType type) {
        this.url = url;
        this.name = name;
        this.size = size;
        this.extension = extension;
        this.type = type;
    }

    public File(){}
}
