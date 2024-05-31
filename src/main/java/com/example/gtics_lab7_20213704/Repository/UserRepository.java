package com.example.gtics_lab7_20213704.Repository;

import com.example.gtics_lab7_20213704.Entity.Resource;
import com.example.gtics_lab7_20213704.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
}
