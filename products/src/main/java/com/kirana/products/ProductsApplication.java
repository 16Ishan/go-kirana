package com.kirana.products;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
@OpenAPIDefinition(
    info =
        @Info(
            title = "Go Kirana Products API",
            version = "1.0.0",
            description = "API documentation for the products service."))
public class ProductsApplication {
  public static void main(String[] args) {
    SpringApplication.run(ProductsApplication.class, args);
  }
}
