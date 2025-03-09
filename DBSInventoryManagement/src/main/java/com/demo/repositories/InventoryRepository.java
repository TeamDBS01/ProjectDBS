package com.demo.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.project.models.Inventory;


@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
   
}
