package com.flp.demo.entity;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Integer>{
	public List<Product> findByNameContains(String name, Pageable pageable);
}
