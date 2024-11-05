package com.flp.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flp.demo.dto.ProductSearchResponseDTO;
import com.flp.demo.entity.ProductRepository;

@Service
public class ProductService {
	private ProductRepository productRepository;
	private final ResourceLoader resourceLoader;
	
	@Autowired
	public ProductService(ProductRepository pr, ResourceLoader rl) {
		this.productRepository = pr;
		this.resourceLoader = rl;
	}
	
	@Scheduled(fixedRate = 5000)
	public List<ProductSearchResponseDTO> ShowList(String filePath) {
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<ProductSearchResponseDTO> productList = new ArrayList<>();
	    try {
	    	Resource resource = resourceLoader.getResource(filePath);
	    	productList = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<ProductSearchResponseDTO>>() {});
	    	
	    	// 가격순 정렬
	    	Collections.sort(productList, Comparator.comparingInt(ProductSearchResponseDTO::getInt_price));
	    	
            int proId = 1;
            for (ProductSearchResponseDTO product : productList) {
                product.setProId(proId++);
            }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    return productList;
	}
}
