package com.example.oops.api.user.repository;

import com.example.oops.api.user.domain.User;
import com.example.oops.api.user.domain.enums.AuthType;
import com.example.oops.api.user.domain.enums.Line;
import com.example.oops.api.user.dto.UserListResponseDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {


    Optional<User> findByLoginInfo_Username(String userName);
    boolean existsByLoginInfo_Username(String username);

    Optional<User> findBySocialIdAndAuthType(String socialId, AuthType authType);

    Optional<User> findBySocialId(String socialId);


    @Query("SELECT new com.example.oops.api.user.dto.UserListResponseDto(u.userInfo.nickname,u.userInfo.email,u.id)  FROM User u")
    List<UserListResponseDto> findAllUserAsDto();


}
