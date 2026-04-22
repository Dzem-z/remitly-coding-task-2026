package com.remitly.stock_market.repository;

import com.remitly.stock_market.entity.WalletActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletActionEntityRepository extends JpaRepository<WalletActionEntity, Long> {
}
