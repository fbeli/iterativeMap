package com.becb.processnewpoint.repository;

import com.becb.processnewpoint.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,String> {

    User findByUserId(String userId);
}
