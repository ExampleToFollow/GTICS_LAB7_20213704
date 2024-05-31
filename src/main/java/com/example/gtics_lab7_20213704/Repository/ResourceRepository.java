package com.example.gtics_lab7_20213704.Repository;

import com.example.gtics_lab7_20213704.Entity.Resource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ResourceRepository extends JpaRepository<Resource,Integer> {
    @Query(nativeQuery = true, value = "select *  from  resources r  where name = ?1 limit 1")
    Resource findResourceByName(String name);
}
