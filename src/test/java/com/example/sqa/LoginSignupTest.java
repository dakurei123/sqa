package com.example.sqa;

import com.example.sqa.controller.UserController;
import com.example.sqa.dto.AddInfoDto;
import com.example.sqa.dto.FullInfoDto;
import com.example.sqa.dto.LoginResponseDto;
import com.example.sqa.dto.SignupDto;
import com.example.sqa.entity.Account;
import com.example.sqa.entity.BhxhWithCccd;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.repository.AccountRepository;
import com.example.sqa.repository.BhxhWithCccdRepository;
import com.example.sqa.utils.ConvertUtils;
import com.example.sqa.utils.JwtUtils;
import com.google.gson.Gson;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
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
//27
@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:test.properties")
public class LoginSignupTest {
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    BhxhWithCccdRepository bhxhWithCccdRepository;
    @Autowired
    UserController userController;
    @Autowired
    private DataSource dataSource;
    Account user = new Account(1, "1234567891", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
            "123456781", "User thử nghiệm", new Date(), "0234567891",
            21800000, "Thợ may", "Thanh Xuân, Hà Nội",
            true, false, 1000000, 1, "female");
    String token = JwtUtils.generateAccessToken(user, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);

    BhxhWithCccd bhxhWithCccdTest = new BhxhWithCccd(1, "0123456789", "123456782");
    Gson gson = new Gson();

    @BeforeEach
    public void setUp() throws SQLException {
        // Fake data
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute("update hibernate_sequence set next_val = 1");
        accountRepository.save(user);
        bhxhWithCccdRepository.save(bhxhWithCccdTest);
        statement.execute("update hibernate_sequence set next_val = 100");
        connection.close();
    }

    @Test
    @Transactional
    void testLoginOK() throws SQLException {
        // Test login:
        Account login = new Account();
        login.setUsername("1234567891");
        login.setPassword("123123123");
        LoginResponseDto res = userController.login(login);
        FullInfoDto checkUser = ConvertUtils.convert(user, FullInfoDto.class);

        // Test response data:
        checkUser.setId(1);
        String checkAdminObject = gson.toJson(checkUser);
        String checkAdminRes = gson.toJson(res.getAccount());
        assertThat(res.getRole()).isEqualTo("USER");
        assertThat(res.getAccessToken()).isNotBlank();
        assertThat(res.getIsActive()).isTrue();
        assertThat(checkAdminRes).isEqualTo(checkAdminObject);
    }

    @Test
    @Transactional
    void testLoginFail() {
        Account login = new Account();
        login.setUsername("1234567899");
        login.setPassword("1234567899");
        assertThatThrownBy(() -> userController.login(login)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testLoginFailEmptyAccount() {
        Account login = new Account();
        login.setUsername("");
        login.setPassword("123123123");
        assertThatThrownBy(() -> userController.login(login)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testLoginFailEmptyPassword() {
        Account login = new Account();
        login.setUsername("123123123");
        login.setPassword("");
        assertThatThrownBy(() -> userController.login(login)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testLoginFailNullAccount() {
        Account login = new Account();
        login.setUsername(null);
        login.setPassword("123123123");
        assertThatThrownBy(() -> userController.login(login)).isInstanceOf(ApiInputException.class);
    }

    @Test
    @Transactional
    void testLoginFailNullPassword() {
        Account login = new Account();
        login.setUsername("123123123");
        login.setPassword(null);
        assertThatThrownBy(() -> userController.login(login)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupOK_1_PasswordIsEqual8Char() {
        SignupDto signup = new SignupDto("0123456789", "012345678", "123456782", "012345678");
        Boolean tc1 = userController.signup(signup);

        //Kiểm tra đăng ký thành công
        assertThat(tc1).isTrue();

        //Kiểm tra dữ liệu database
        Account account = accountRepository.findOneByUsername(signup.getUsername());
        assertThat(account).isNotNull();
        assertThat(account.getCccd()).isEqualTo(signup.getCccd());

        //Kiểm tra có thể login
        Account login = new Account();
        login.setUsername(signup.getUsername());
        login.setPassword(signup.getPassword2());
        LoginResponseDto res = userController.login(login);
        assertThat(res).isNotNull();
    }

    @Transactional
    @Test
    void testSignupOK_2_PasswordIsEqual255Char() {
        SignupDto signup = new SignupDto("0123456789", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqr"
                , "123456782", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789abcdefghijklmnopqr");
        Boolean tc1 = userController.signup(signup);
        assertThat(tc1).isTrue();

        //Kiểm tra dữ liệu database
        Account account = accountRepository.findOneByUsername(signup.getUsername());
        assertThat(account).isNotNull();
        assertThat(account.getCccd()).isEqualTo(signup.getCccd());

        //Kiểm tra có thể login
        Account login = new Account();
        login.setUsername(signup.getUsername());
        login.setPassword(signup.getPassword2());
        LoginResponseDto res = userController.login(login);
        assertThat(res).isNotNull();
    }

    @Transactional
    @Test
    void testSignupFail_1_CCCDAndBHXHIsNotCorrect() {
        SignupDto signup = new SignupDto("0123456789", "0123456789", "123456783", "0123456789");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_1_1_TestInputIsNull() {
        SignupDto signup = new SignupDto(null, "0123456789", "123456783", "0123456789");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_1_2_TestInputIsNull() {
        SignupDto signup = new SignupDto("123", null, "123456783", "0123456789");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_1_3_TestInputIsNull() {
        SignupDto signup = new SignupDto("123", "0123456789", null, "0123456789");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_1_4_TestInputIsNull() {
        SignupDto signup = new SignupDto("123", "0123456789", "123456783", null);
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }
    @Transactional
    @Test
    void testSignupFail_2_BHXHIsExisted() {
        SignupDto signup = new SignupDto(user.getUsername(), "0123456789", "123456783", "0123456789");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_3_PasswordIsNotCorrect() {
        SignupDto signup = new SignupDto("0123456781", "1234567", "123456782", "1234567");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_4_PasswordIsNotCorrect() {
        SignupDto signup = new SignupDto("0123456781", "0123456789", "123456782", "01234567891");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_4_PasswordIsLessThan8Char() {
        SignupDto signup = new SignupDto("0123456781", "1234567", "123456782", "1234567");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_4_PasswordIsMoreThan255Char() {
        SignupDto signup = new SignupDto("0123456781", "1QwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnm",
                "123456782", "1QwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnmQwertyuiopasdfghjklzxcvbnm");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_5_BHXH_NotNumber() {
        SignupDto signup = new SignupDto("0@2345z781", "1234567", "123456782", "1234567");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_6_CCCD_NotNumber() {
        SignupDto signup = new SignupDto("0223452781", "1234567", "!@3aS6782", "1234567");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testSignupFail_7_CCCD_LengthNotEqual9() {
        SignupDto signup = new SignupDto("0223452781", "1234567", "123123123", "1234567");
        assertThatThrownBy(() -> userController.signup(signup)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testToken_Fail_1() {
        assertThatThrownBy(() -> userController.getInfo(null)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testToken_Fail_2() {
        assertThatThrownBy(() -> userController.getInfo(token + "123")).isInstanceOf(SignatureException.class);
    }

    @Transactional
    @Test
    void testToken_Fail_3() {
        assertThatThrownBy(() -> userController.getInfo("    ")).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testToken_GetInfo_Fail_3() {
        Account userFake = new Account(1, "1234567899", "$2a$10$K8x.0GbrTLgFBtTdwUkmtO0Fr6dIgjMLzgPCk6w01akNNA.vuXPoy", 1, "USER",
                "123456781", "User thử nghiệm", new Date(), "0234567891",
                21800000, "Thợ may", "Thanh Xuân, Hà Nội",
                true, false, 1000000, 1, "female");
        String tokenFake = JwtUtils.generateAccessToken(userFake, "secret", JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);
        assertThatThrownBy(() -> userController.getInfo(tokenFake)).isInstanceOf(ApiInputException.class);
    }

    @Transactional
    @Test
    void testGetInfo_OK() {
        AddInfoDto addInfoTest = ConvertUtils.convert(user, AddInfoDto.class);
        AddInfoDto addInfoResp = userController.getInfo(token);
        assertThat(gson.toJson(addInfoResp)).isEqualTo(gson.toJson(addInfoTest));
    }

    @Transactional
    @Test
    void testToken_GetInfo_OK() {
        AddInfoDto addInfoTest = ConvertUtils.convert(user, AddInfoDto.class);
        AddInfoDto addInfoResp = userController.getInfo("Bearer " + token);
        assertThat(gson.toJson(addInfoResp)).isEqualTo(gson.toJson(addInfoTest));
    }

}
