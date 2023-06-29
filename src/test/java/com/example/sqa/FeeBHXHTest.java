package com.example.sqa;

import com.example.sqa.controller.UserController;
import com.example.sqa.entity.Account;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.repository.AccountRepository;
import com.example.sqa.repository.MinSalaryRepository;
import com.example.sqa.utils.JwtUtils;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;
//3
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:test.properties")
class FeeBHXHTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MinSalaryRepository minSalaryRepository;
    @Autowired
    UserController userController;
    @Autowired
    private DataSource dataSource;

    //123123123
    Account userTest1 = new Account(1, "1234567891", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
            "123456781", "User thử nghiệm", new Date(), "0234567891",
            20000000, "Thợ may", "Thanh Xuân, Hà Nội",
            true, false, 0, 1, "female");

    Account userTest2 = new Account(2, "1234567892", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
            "123456782", null, null, null,
            null, null, null,
            null, null, null, null, null);

    Account userTest3 = new Account(3, "1234567893", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
            "123456783", "User thử nghiệm", new Date(), "0234567891",
            18000000, "Thợ may", "Thanh Xuân, Hà Nội",
            true, false, 2000000, 1, "female");
    Gson gson = new Gson();
    String token2 = JwtUtils.generateAccessToken(userTest2, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);
    String token1 = JwtUtils.generateAccessToken(userTest1, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);
    String token3 = JwtUtils.generateAccessToken(userTest3, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);

    @BeforeEach
    public void setUp() throws SQLException {
        // Fake data
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("update hibernate_sequence set next_val = 1");
        accountRepository.save(userTest1);
        accountRepository.save(userTest2);
        accountRepository.save(userTest3);
        statement.execute("update hibernate_sequence set next_val = 100");
        connection.close();
    }

    @Test
    @Transactional
    void testGetFeeOK() {
        Integer fee = userController.getFee(token1);
        assertThat(fee).isEqualTo(1600000);
    }

    @Transactional
    @Test
    void testGetFeeWithAllowanceSalaryOK() {
        Integer fee = userController.getFee(token3);
        assertThat(fee).isEqualTo(1600000);
    }

    @Transactional
    @Test
    void testGetFeeFail_1() {
        assertThatThrownBy(() -> userController.getFee(token2)).isInstanceOf(ApiInputException.class);
    }
}





















