package com.example.accounting.repository;

import com.example.accounting.entity.Month;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MonthRepository extends JpaRepository<Month, Integer> {
    List<Month> findByYearId(Integer yearId);

    // Find by id only when that year's account belongs to the specified user
    java.util.Optional<Month> findByIdAndYear_Account_User_Id(Integer id, Integer userId);

    java.util.List<Month> findByYearIdAndYear_Account_User_Id(Integer yearId, Integer userId);
}