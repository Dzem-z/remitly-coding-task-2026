package com.remitly.stock_market.repository;

import com.remitly.stock_market.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StockEntityRepository extends JpaRepository<StockEntity, Long> {

    Optional<StockEntity> findByWalletIdAndName(String walletId, String name);

    Optional<StockEntity> findByWalletIsNullAndName(String name);

    List<StockEntity> findAllByWalletIsNull();
}
