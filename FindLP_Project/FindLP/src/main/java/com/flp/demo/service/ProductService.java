package com.flp.demo.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.scheduling.annotation.Scheduled;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.flp.demo.dto.ProductSearchResponseDTO;

@Service
public class ProductService {
	private final ResourceLoader resourceLoader;
	
	@Value("${python.path}")
	private String pythonPath;
	@Value("${script.path}")
	private String scriptPath;
	@Value("${json.path}")
	private String jsonPath;
	
	@Autowired
	public ProductService(ResourceLoader rl) {
		this.resourceLoader = rl;
	}
	
	public boolean executeCrawling(String query) {
        try {
            // 파이썬 실행 파일의 전체 경로를 지정
            ProcessBuilder processBuilder = new ProcessBuilder(pythonPath, scriptPath,query);
            Process process = processBuilder.start(); // 웹크롤링 실행

            // 프로세스가 완료될 때까지 대기
            int exitCode = process.waitFor();
            System.out.println("Python script execution completed with exit code: " + exitCode);
            
            return true;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }
	
	@Scheduled(fixedRate = 5000)
	public List<ProductSearchResponseDTO> ShowList() {
	    ObjectMapper objectMapper = new ObjectMapper();
	    List<ProductSearchResponseDTO> productList = new ArrayList<>();
	    try {
	    	Resource resource = resourceLoader.getResource(jsonPath);
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
