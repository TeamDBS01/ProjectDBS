package com.project.repositories;


import com.project.models.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, Long> {
    UserCredit findByUserId(Long userId);
}