package com.example.sqa.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Data
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;
    //Ma bai hiem xa hoi = username
    @Column
    private String username;
    @Column
    private String password;
    @Column
    private Integer status;
    @Column
    private String role;
    @Column
    private String cccd;
    @Column
    private String fullname;
    @Column
    private Date dob;
    @Column
    private String phone;
    @Column
    private Integer salary;
    @Column
    private String job;
    @Column
    private String address;
    @Column
    private Boolean isApprenticeship;
    @Column
    private Boolean isToxicJob;
    @Column
    private Integer salaryAllowance;
}
