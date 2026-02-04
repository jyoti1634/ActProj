package com.example.accounting.repository;

import com.example.accounting.entity.FinancialYear;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FinancialYearRepository extends JpaRepository<FinancialYear, Integer> {
    List<FinancialYear> findByAccountId(Integer accountId);

    // Find by id only when the parent account belongs to the specified user
    java.util.Optional<FinancialYear> findByIdAndAccount_User_Id(Integer id, Integer userId);

    // Find all years for account only when account belongs to the specified user
    java.util.List<FinancialYear> findByAccountIdAndAccount_User_Id(Integer accountId, Integer userId);
}