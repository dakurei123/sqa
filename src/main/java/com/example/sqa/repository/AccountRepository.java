package com.example.sqa.repository;

import com.example.sqa.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
    Account findByUsername(String username);

    Account findOneById(Integer id);

    Account findOneByUsername(String username);

    boolean existsByUsername(String username);
}
