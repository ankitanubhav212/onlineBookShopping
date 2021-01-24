package com.ecommerce.repository;

import com.ecommerce.model.ShopOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShopOrderRepository extends JpaRepository<ShopOrder,Long> {
}
