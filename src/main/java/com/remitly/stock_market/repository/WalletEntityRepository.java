package com.remitly.stock_market.repository;

import com.remitly.stock_market.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletEntityRepository extends JpaRepository<WalletEntity, String> {
}
