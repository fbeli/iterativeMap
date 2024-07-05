package com.becb.processnewpoint.repository;

import com.becb.processnewpoint.domain.Roteiro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RoteiroRepository extends PagingAndSortingRepository<Roteiro,String> {

    @Query(value = "SELECT p FROM Roteiro p WHERE p.userOwner.userId = :userId")
    Page<Roteiro> findAllByUserOwner(Pageable pageable, String userId);

    @Query(value = "SELECT p FROM Roteiro p WHERE LOWER(p.city) LIKE LOWER(:city)")
    Page<Roteiro> findAllByCity(Pageable pageable, String city);

    @Query(value = "SELECT p FROM Roteiro p WHERE LOWER(p.title) LIKE LOWER(:title)")
    Page<Roteiro> findAllByTitle(Pageable pageable, String title);

    @Query(value = "SELECT p FROM Roteiro p WHERE LOWER(p.title) LIKE LOWER(:title) " +
            "or (LOWER(p.description) LIKE LOWER(:title))")
    Page<Roteiro> findAllByTitleAndDescription(Pageable pageable, String title);

    @Query(value = "SELECT p FROM Roteiro p WHERE (LOWER(p.city) LIKE LOWER(:city)) " +
            "and (LOWER(p.title) LIKE LOWER(:title)) or (LOWER(p.description) LIKE LOWER(:title))")
    Page<Roteiro> findAllByCityAndTitle(Pageable pageable, String city, String title);

    @Query(value = "SELECT p FROM Roteiro p WHERE (LOWER(p.city) LIKE LOWER(:city)) " +
            "and (LOWER(p.title) LIKE LOWER(:title)) " +
            "and p.userOwner.userId = :userId")
    Page<Roteiro> findAllByCityAndTitleAndUserOwner(Pageable pageable, String city, String title, String userId);

    @Query(value = "SELECT p FROM Roteiro p WHERE (LOWER(p.title) LIKE LOWER(:title) or (LOWER(p.description) LIKE LOWER(:title)))" +
            "and p.userOwner.userId = :userId")
    Page<Roteiro> findAllByTitleAndUserOwner(Pageable pageable , String title, String userId);

    @Query(value = "SELECT p FROM Roteiro p WHERE (LOWER(p.city) LIKE LOWER(:city)) " +
            "and p.userOwner.userId = :userId")
    Page<Roteiro> findAllByCityAndUserOwner(Pageable pageable , String city, String userId);

}
