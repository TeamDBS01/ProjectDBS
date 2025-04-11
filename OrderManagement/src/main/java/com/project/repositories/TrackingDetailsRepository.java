package com.project.repositories;

import com.project.models.TrackingDetails;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrackingDetailsRepository extends JpaRepository<TrackingDetails, Long> {
    TrackingDetails findByOrderId(Long orderId);
}