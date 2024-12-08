package com.flp.demo.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductSearchResponseDTO {
	private Integer ProId;
	private String name;
	private String st_price;
	private Integer int_price;
	private String link;
	private String img_link;
	
	@JsonCreator
    public ProductSearchResponseDTO(@JsonProperty("ProId") Integer proId, 
            @JsonProperty("name") String name, 
            @JsonProperty("int_price") Integer int_price, 
            @JsonProperty("price") String st_price,
            @JsonProperty("link") String link,
            @JsonProperty("image_src") String img_link) {
		this.ProId=proId;
		this.name=name;
		this.int_price=int_price;
		this.st_price=st_price;
		this.link=link;
		this.img_link=img_link;
	}
}
