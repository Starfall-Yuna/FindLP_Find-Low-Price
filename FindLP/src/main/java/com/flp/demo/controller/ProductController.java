package com.flp.demo.controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import com.flp.demo.dto.ProductSearchResponseDTO;
import com.flp.demo.entity.Product;
import com.flp.demo.service.ProductService;

@Controller
public class ProductController {
	@Autowired(required=true)
	private ProductService productService;
	
	// 메인 페이지
	@GetMapping(value={"/findlp/main", "/findlp"})
	public String MainPage() {
		return "findlp/main";
	}
	
	// 검색 기능 수행
	@PostMapping("/findlp/search")
	public ResponseEntity<ProductSearchResponseDTO> searchProduct(@RequestParam("query") String query){
		// 파이썬 웹크롤링 파일 실행
	    try {
            // 파이썬 실행 파일의 전체 경로를 지정
            ProcessBuilder processBuilder = new ProcessBuilder("C:\\Users\\admin\\AppData\\Local\\Programs\\Python\\Python312\\python.exe", 
                                                               "C:\\Users\\admin\\Desktop\\FindLP\\src\\main\\resources\\static\\crawling.py", query);
            processBuilder.redirectErrorStream(true); // 표준 오류를 표준 출력과 병합
            Process process = processBuilder.start(); // 웹크롤링 실행

            // 프로세스가 완료될 때까지 대기 (비동기로 진행할 예정)
            int exitCode = process.waitFor();
            System.out.println("Python script execution completed with exit code: " + exitCode);
            if (exitCode != 0) {
                System.err.println("Python script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

	    // /searchList로 리다이렉트
	    HttpHeaders headers = new HttpHeaders();
	    headers.add("Location", "/findlp/searchList?query="+URLEncoder.encode(query, StandardCharsets.UTF_8));
	    return new ResponseEntity<>(headers, HttpStatus.FOUND);
	}
	// 검색 결과 출력
	@GetMapping("/findlp/searchList")
	public ModelAndView searchResultPage(@RequestParam("query") String query, ModelAndView mav) {
	    List<ProductSearchResponseDTO> search = productService.ShowList("file:C:/Users/admin/Desktop/FindLP/src/main/resources/static/output.json");
	    mav.setViewName("/findlp/search");
	    mav.addObject("query", query);
	    mav.addObject("Result", search);
	    return mav; 
	}
	
	// 리스트 출력 수행 (사용 안하고 있는 중)
	@PostMapping("/findlp/list/{category}")
	public ModelAndView List(@PathVariable("category") String category, ModelAndView mav) {
		List<ProductSearchResponseDTO> pro = productService.ShowList("file:///C:/Users/admin/Desktop/FindLP/src/main/resources/static/output.json");
		mav.setViewName("findlp/list");
		mav.addObject("Products", pro);
		return mav;
	}
}
