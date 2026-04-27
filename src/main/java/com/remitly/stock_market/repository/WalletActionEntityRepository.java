package com.remitly.stock_market.repository;

import com.remitly.stock_market.entity.WalletActionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface WalletActionEntityRepository extends JpaRepository<WalletActionEntity, Long> {
    @Query("SELECT w FROM WalletActionEntity w ORDER BY w.actionDateTime ASC")
    List<WalletActionEntity> findAllOrderedByActionDateTime();
}
