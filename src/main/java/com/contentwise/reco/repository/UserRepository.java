package com.contentwise.reco.repository;

import com.contentwise.reco.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository          extends JpaRepository<User,Long> {}
