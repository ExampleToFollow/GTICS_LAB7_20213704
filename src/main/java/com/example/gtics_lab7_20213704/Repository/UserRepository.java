package com.example.gtics_lab7_20213704.Repository;

import com.example.gtics_lab7_20213704.Entity.Resource;
import com.example.gtics_lab7_20213704.Entity.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User,Integer> {
    @Query(nativeQuery = true, value = "select *  from  users u  where authorizedResource = ?1 ")
    List<User> findByIdResourceName(int idResource);
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "update users set active = false where userId>0 ")
    void apagarTodo();
    @Modifying
    @Transactional
    @Query(nativeQuery = true,value = "update users set authorizedResource = ?2 where userId= ?1")
    void updateAuth(int idUser , int idAuth );
}
