package com.example.sqa.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
public class MinSalary {
    @Column
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String region;
    @Column
    private Integer minSalary;
}
