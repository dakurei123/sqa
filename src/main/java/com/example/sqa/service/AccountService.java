package com.example.sqa.service;

import com.example.sqa.dto.AddInfoDto;
import com.example.sqa.dto.LoginResponseDto;
import com.example.sqa.dto.SignupDto;
import com.example.sqa.entity.Account;
import com.example.sqa.exception.ApiInputException;
import com.example.sqa.repository.AccountRepository;
import com.example.sqa.utils.ConvertUtils;
import com.example.sqa.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
@RequiredArgsConstructor
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Value("${authentication.secret-key}")
    protected String authenSecretKey;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(Account loginRequestDto) {
        Account account = accountRepository.findByUsername(loginRequestDto.getUsername());
        account.setPassword(null);
        if (account == null)
            throw new ApiInputException("PERMISSION_DENIED");
        if (account.getStatus() != 1)
            throw new ApiInputException("ACCOUNT_NOT_ACTIVE", account.getStatus());

        // Generate Token
        String accessToken = JwtUtils.generateAccessToken(account, authenSecretKey, JwtUtils.TOKEN_EXPIRE_TIME_ACCESS_TOKEN);

        return new LoginResponseDto(accessToken, account);
    }

    public Integer createNewAccount(SignupDto signupDto) {
        if (!signupDto.getPassword().equals(signupDto.getPassword2()))
            throw new ApiInputException("TWO_PASSWORD_INCORRECT");
        if (accountRepository.existsByUsername(signupDto.getUsername()))
            throw new ApiInputException("EXISTED");
        // Encode password
        String password = signupDto.getPassword();
        signupDto.setPassword(passwordEncoder.encode(password));
        Account account = ConvertUtils.convert(signupDto, Account.class);
        account.setStatus(0);
        account.setRole("USER");
        accountRepository.save(account);
        return account.getId();
    }

    public Integer activeUser(String username, String role, Integer id) {
        if (!role.equals("ADMIN"))
            throw new ApiInputException("PERMISSION_DENIED");
        Account account = accountRepository.findOneById(id);
        if (account.getStatus() == 1)
            throw new ApiInputException("ACCOUNT_IS_ACTIVED");
        account.setStatus(1);
        accountRepository.save(account);
        return account.getId();
    }

    public Integer addInfo(AddInfoDto addInfo, String username) {
        Account old = accountRepository.findOneByUsername(username);
        Account account = ConvertUtils.convert(addInfo, Account.class);
        account.setStatus(old.getStatus());
        account.setRole(old.getRole());
        account.setId(old.getId());
        account.setPassword(old.getPassword());
        account.setUsername(old.getUsername());
        accountRepository.save(account);
        return account.getId();
    }
}
