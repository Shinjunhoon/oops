package com.example.oops.api.user.repository;

import com.example.oops.api.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByLoginInfo_Username(String userName);
    boolean existsByLoginInfo_Username(String username);

}
