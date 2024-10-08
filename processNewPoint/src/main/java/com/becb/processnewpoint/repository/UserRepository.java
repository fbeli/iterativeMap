package com.becb.processnewpoint.repository;

import com.becb.processnewpoint.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,String> {

    User findByUserId(String userId);

    //find User having pointId??
    @Query("SELECT u FROM User u JOIN u.points p WHERE p.pointId = :pointId")
    User findUserByPoints(String pointId);

    @Query("Select u from User u where u.instagram = :instagram")
    List<User> findByUserByInstgram(String instagram);

}
