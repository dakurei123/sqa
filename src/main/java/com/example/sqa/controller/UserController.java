package com.example.sqa.controller;

import com.example.sqa.dto.AddInfoDto;
import com.example.sqa.dto.LoginResponseDto;
import com.example.sqa.dto.SignupDto;
import com.example.sqa.entity.Account;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.service.AccountService;
import com.example.sqa.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RequestMapping("")
@Slf4j
public class UserController extends BaseWebService {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private AccountService userService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody Account loginRequest) {
        log.info("[login] - START - username: {}", loginRequest.getUsername());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername().toLowerCase(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            throw e;
        }
        LoginResponseDto loginResponseDto = userService.login(loginRequest);
        log.info("[login] - DONE - username: {}", JsonUtils.writeToStringWithoutException(loginRequest));
        return loginResponseDto;
    }

    @PostMapping("/signup")
    public Integer signup(@RequestBody SignupDto signupDto)
            throws ApiInputException {
        log.info("[createAccount] - START - username: {}", signupDto.getUsername());
        Integer accountId = userService.createNewAccount(signupDto);
        log.info("[createAccount] - DONE - username: {}", JsonUtils.writeToStringWithoutException(signupDto));
        return accountId;
    }

    @GetMapping("/test-role")
    public String test(@RequestHeader(HEADER_AUTHORIZATION) String authorization) {
        return getRole();
    }

    @PostMapping("/active")
    public Integer active(@RequestParam Integer id, @RequestHeader(HEADER_AUTHORIZATION) String authorization)
            throws ApiInputException {
        log.info("[active] - START - username: {}", getUsername());
        Integer accountId = userService.activeUser(getUsername(), getRole(), id);
        log.info("[active] - DONE - username: {}", getUsername());
        return accountId;
    }

    @PostMapping("/add-info")
    public Integer addInfo(@RequestBody AddInfoDto signupDto)
            throws ApiInputException {
        log.info("[addInfo] - START - username: {}", getUsername());
        Integer accountId = userService.addInfo(signupDto, getUsername());
        log.info("[addInfo] - DONE - username: {}", JsonUtils.writeToStringWithoutException(getUsername()));
        return accountId;
    }
}