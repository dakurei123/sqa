package com.example.sqa.dto;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import java.util.Date;

@Getter
@Setter
public class AddInfoDto {
    private String cccd;
    private String fullname;
    private Date dob;
    private String phone;
    private Integer salary;
    private String job;
    private String address;
    private Boolean isApprenticeship;
    private Boolean isToxicJob;
    private Integer salaryAllowance;
}
