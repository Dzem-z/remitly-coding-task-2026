package com.remitly.stock_market.repository;

import com.remitly.stock_market.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankEntityRepository extends JpaRepository<BankEntity, String> {
}
