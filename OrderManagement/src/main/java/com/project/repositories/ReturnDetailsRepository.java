package com.project.repositories;

import com.project.models.ReturnDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReturnDetailsRepository extends JpaRepository<ReturnDetails, Long> {
    ReturnDetails findByOrderId(Long orderId);
}