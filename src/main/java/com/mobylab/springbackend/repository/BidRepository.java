package com.mobylab.springbackend.repository;

import com.mobylab.springbackend.entity.Bid;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BidRepository extends JpaRepository<Bid, Integer> {
}
