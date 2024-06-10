package com.becb.processnewpoint.repository;

import com.becb.processnewpoint.domain.Point;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;


public interface PointRepository extends PagingAndSortingRepository<Point,String> {

    @Query(value = "SELECT p FROM Point p WHERE p.user.userId = ?1")
    List<Point> findAllByUser(String userId);

    @Query(value = "SELECT p FROM Point p WHERE p.user.userId = ?1 order by p.pointId desc ")
    Page<Point> findAllByUser(Pageable pageable, String userId);

    @Query(value = "SELECT p FROM Point p WHERE p.user.userId = ?1 and (p.aproved = 'true' or p.aproved = 'false') order by p.pointId desc ")
    Page<Point> findAllByUserNotBlocked(Pageable pageable, String userId);

    @Query(value = "SELECT p FROM Point p WHERE p.user.userId = ?1 and (p.aproved = 'true' or p.aproved = 'false') and p.pointParent =null order by p.pointId desc ")
    Page<Point> findFathersByUserNotBlocked(Pageable pageable, String userId);

    @Query(value = "SELECT p  FROM Point p  WHERE p.pointId = ?1")
    Optional<Point> findPointByPointId(String pointId);

    @Query(value = "SELECT p FROM Point p WHERE p.aproved = ?1")
    List<Point> findAllByAproved(String aproved);



}
