package com.example.sqa;

import com.example.sqa.controller.UserController;
import com.example.sqa.dto.AddInfoDto;
import com.example.sqa.entity.Account;
import com.example.sqa.entity.MinSalary;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.repository.AccountRepository;
import com.example.sqa.repository.MinSalaryRepository;
import com.example.sqa.utils.ConvertUtils;
import com.example.sqa.utils.JwtUtils;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

//40 Testcase
//Total : 40 + 3 + 27 + 4 = 74
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:test.properties")
class ChangeInfoTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MinSalaryRepository minSalaryRepository;
    @Autowired
    UserController userController;

    //123123123
    Account user = new Account(1, "1234567891", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
            "123456781", "User thử nghiệm", new Date(), "0234567891",
            21800000, "Thợ may", "Thanh Xuân, Hà Nội",
            true, false, 0, 1, "female");
    Gson gson = new Gson();
    String token = JwtUtils.generateAccessToken(user, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    public void setUp() throws SQLException {
        // Fake data
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("update hibernate_sequence set next_val = 1");

        MinSalary m1 = new MinSalary(1, 4680000, 1);
        minSalaryRepository.save(m1);
        MinSalary m2 = new MinSalary(2, 4160000, 2);
        minSalaryRepository.save(m2);
        MinSalary m3 = new MinSalary(3, 3640000, 3);
        minSalaryRepository.save(m3);
        MinSalary m4 = new MinSalary(4, 3250000, 4);
        minSalaryRepository.save(m4);
        MinSalary m5 = new MinSalary(5, 1490000, -1);
        minSalaryRepository.save(m5);
        accountRepository.save(user);
        connection.close();
    }

    @Test
    @Transactional
    void testChangeInfoOk_1_DontChange() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        Boolean res = userController.changeInfo(infoTest, token);

        assertThat(res).isTrue();

        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));

    }

    @Test
    @Transactional
    void testChangeInfoOk_2_ChangeSomeField() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setAddress("Hồ Chí Minh");
        infoTest.setJob("Kỹ sư phần mềm");
        userController.changeInfo(infoTest, token);
        Boolean res = userController.changeInfo(infoTest, token);

        assertThat(res).isTrue();

        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));
    }

    @Test
    @Transactional
    void testChangeInfoOk_3_NullSalaryAllowance() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setSalaryAllowance(null);
        userController.changeInfo(infoTest, token);

        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));
    }

    @Test
    @Transactional
    void testChangeInfoFail_1_Invalid_Job_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setJob("    ");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_1_Invalid_Job_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setJob("!!");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_1_Invalid_Job_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setJob(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_2_Invalid_Dob() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setDob(null);

        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_3_Invalid_Gender_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setGender(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }


    @Test
    @Transactional
    void testChangeInfoFail_3_Invalid_Gender_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setGender("   ");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_3_Invalid_Gender_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setGender("!!");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_4_Invalid_Fullname_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setFullname(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_4_Invalid_Fullname_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setFullname("       ");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_4_Invalid_Fullname_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setFullname("!!");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_5_Invalid_Phone_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setPhone(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_5_Invalid_Phone_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setPhone("           ");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_5_Invalid_Phone_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setPhone("!! !! !!!!");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_5_Invalid_Phone_4() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setPhone("XXXXXXXXDD");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_5_Invalid_Phone_5() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setPhone("1234567890");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_6_Invalid_Salary() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setSalary(null);

        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_7_Invalid_ToxicJob() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(null);

        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_8_Invalid_Address_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setAddress(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_8_Invalid_Address_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setAddress("!!");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_8_Invalid_Address_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setAddress(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
        infoTest.setAddress("           ");
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_9_Invalid_IsApprenticeship() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsApprenticeship(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_10_Invalid_RegionCode() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setRegionCode(null);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_10_Invalid_RegionCode_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setRegionCode(0);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testChangeInfoFail_10_Invalid_RegionCode_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setRegionCode(5);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void test_11_1_Salary_Max() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setSalary(29800001);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void test_11_2_Salary_Max() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setSalary(29800000);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));
    }

    @Test
    @Transactional
    void test_12_Salary_Min_ToxicJob_Fail_Apprenticeship_False_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(false);
        infoTest.setIsApprenticeship(false);

        //Code = 1
        infoTest.setRegionCode(1);
        int salary = 4680000;
        infoTest.setSalary(salary - 1);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);

    }


    @Test
    @Transactional
    void test_12_Salary_Min_ToxicJob_Fail_Apprenticeship_False_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(false);
        infoTest.setIsApprenticeship(false);

        //Code = 1
        infoTest.setRegionCode(1);
        int salary = 4680000;

        infoTest.setSalary(salary);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));

    }

    @Test
    @Transactional
    void test_13_Salary_Min_ToxicJob_True_Apprenticeship_False_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(false);

        //Code = 2
        int salary = 4368000;
        infoTest.setRegionCode(2);


        infoTest.setSalary(salary - 1);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);

    }

    @Test
    @Transactional
    void test_13_Salary_Min_ToxicJob_True_Apprenticeship_False_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(false);

        //Code = 2
        int salary = 4368000;
        infoTest.setRegionCode(2);


        infoTest.setSalary(salary);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));

    }

    @Test
    @Transactional
    void test_14_Salary_Min_ToxicJob_False_Apprenticeship_True_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(false);
        infoTest.setIsApprenticeship(true);

        //Code = 3
        infoTest.setRegionCode(3);
        int salary = 3894800;

        infoTest.setSalary(salary - 1);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);

    }

    @Test
    @Transactional
    void test_14_Salary_Min_ToxicJob_False_Apprenticeship_True_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(false);
        infoTest.setIsApprenticeship(true);

        //Code = 3
        infoTest.setRegionCode(3);
        int salary = 3894800;

        infoTest.setSalary(salary);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));

    }

    @Test
    @Transactional
    void test_15_Salary_Min_ToxicJob_True_Apprenticeship_True_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(true);

        //Code = 4
        infoTest.setRegionCode(4);
        int salary = 3651375;

        infoTest.setSalary(salary - 1);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void test_15_Salary_Min_ToxicJob_True_Apprenticeship_True_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(true);

        //Code = 4
        infoTest.setRegionCode(4);
        int salary = 3651375;

        infoTest.setSalary(salary);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));
    }

    @Test
    @Transactional
    void test_16_Salary_Min_ToxicJob_True_Apprenticeship_True_WithAllowanceSalary_1() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(true);

        //Code = 1
        infoTest.setRegionCode(1);
        int salary = 5257980;

        infoTest.setSalary(salary - 100001);
        infoTest.setSalaryAllowance(100000);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void test_16_Salary_Min_ToxicJob_True_Apprenticeship_True_WithAllowanceSalary_2() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(true);

        //Code = 1
        infoTest.setRegionCode(1);
        int salary = 5257980;

        infoTest.setSalary(salary - 100000);
        infoTest.setSalaryAllowance(100000);
        Boolean res = userController.changeInfo(infoTest, token);
        assertThat(res).isTrue();
        Account a = accountRepository.findOneByUsername(user.getUsername());
        AddInfoDto resDb = ConvertUtils.convert(a, AddInfoDto.class);
        assertThat(gson.toJson(resDb)).isEqualTo(gson.toJson(infoTest));

    }

    @Test
    @Transactional
    void test_16_Salary_Min_ToxicJob_True_Apprenticeship_True_WithAllowanceSalary_3() {
        AddInfoDto infoTest = ConvertUtils.convert(user, AddInfoDto.class);
        infoTest.setIsToxicJob(true);
        infoTest.setIsApprenticeship(true);

        //Code = 1
        infoTest.setRegionCode(1);
        int salary = 5257980;

        infoTest.setSalary(salary - 100000);
        infoTest.setSalaryAllowance(100000 - 1);
        assertThatThrownBy(() -> userController.changeInfo(infoTest, token)).isInstanceOf(ApiInputException.class);
    }
}





















