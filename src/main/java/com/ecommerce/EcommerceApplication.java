package com.ecommerce;

import com.ecommerce.model.Product;
import com.ecommerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.*;

@SpringBootApplication
public class EcommerceApplication implements ApplicationRunner {

	@Autowired
	ProductRepository productRepository;

	public static void main(String[] args) {
		SpringApplication.run(EcommerceApplication.class, args);
	}

	@Override
	public void run(ApplicationArguments args) throws Exception {
			RestTemplate restTemplate = new RestTemplate();
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			if(productRepository.findAll().size() == 0) {
				ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange("https://s3-ap-southeast-1.amazonaws.com/he-public-data/books8f8fe52.json", HttpMethod.GET, null, new ParameterizedTypeReference<List<Map<String, Object>>>() {
				});
				List<Map<String, Object>> responseBody1 = response.getBody();
				List<Map<String, Object>> responseBody = responseBody1.subList(0,800);
				List<Product> products = new ArrayList<>();
				System.out.println("Fetching list of items");
				responseBody.forEach(value -> {
					Product product = new Product();
					product.setBookID((Integer) value.get("bookID"));
					product.setTitle(String.valueOf(value.get("title")));
					product.setAuthors((String) value.get("authors"));
					product.setPrice(Double.valueOf(Integer.valueOf(String.valueOf(value.get("price")))));
					try {
						product.setAverageRating((Double) value.get("average_rating"));
					} catch (Exception e) {
						product.setAverageRating(Double.valueOf(0));
					}

					try {
						product.setRatingsCount(Integer.valueOf(value.get("ratings_count").toString()));
					} catch (Exception e) {
						product.setRatingsCount(0);
					}
					product.setIsbn(String.valueOf(value.get("isbn")));
					product.setLanguage(String.valueOf(value.get("language_code")));
					products.add(product);
				});
				productRepository.saveAll(products);
				System.out.println("All Item fetched succesfully");
			}
	}

}
