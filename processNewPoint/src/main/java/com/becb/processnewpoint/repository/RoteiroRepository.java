package com.becb.processnewpoint.repository;

import com.becb.processnewpoint.domain.Roteiro;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface RoteiroRepository extends PagingAndSortingRepository<Roteiro,String> {

    @Query(value = "SELECT p FROM Roteiro p WHERE p.userOwner.userId = ?1")
    List<Roteiro> findAllByUserOwner(String userId);

    @Query(value = "SELECT p FROM Roteiro p WHERE LOWER(p.city) LIKE LOWER(?1)")
    List<Roteiro> findAllByCity(String city);
}
