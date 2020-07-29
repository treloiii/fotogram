package com.trelloiii.fotogram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootApplication
public class FotogramApplication {

    public static void main(String[] args) {
        SpringApplication.run(FotogramApplication.class, args);
    }

}
