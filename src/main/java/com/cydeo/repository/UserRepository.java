package com.cydeo.repository;

import com.cydeo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

// get user based on username:
    User findByUserName(String username);
    @Transactional  // can also go at Class level
    void deleteByUserName(String username);

}
