package com.mapleinfo.register.repository;

import com.mapleinfo.register.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    boolean existsById(String id);
}
