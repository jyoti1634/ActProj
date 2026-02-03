package com.example.accounting.repository;

import com.example.accounting.entity.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonthRepository extends JpaRepository<Month, Integer> {
    List<Month> findByYearId(Integer yearId);
}