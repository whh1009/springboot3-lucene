package com.whh.springboot3;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SpringBootApplication
@RestController
public class Springboot3Application {

    public static void main(String[] args) {
        SpringApplication.run(Springboot3Application.class, args);
    }
    @RequestMapping("/")
    public ResponseEntity<String> home() {
        return ResponseEntity.ok("Hello Docker World");
    }

    @RequestMapping("/api/test")
    public ResponseEntity test() {
        return ResponseEntity.ok("test");
    }

    @RequestMapping("/api/time")
    public ResponseEntity time() {
        return ResponseEntity.ok(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @RequestMapping("/api/id")
    public ResponseEntity id(HttpServletRequest request) {
        return ResponseEntity.ok(request.getRequestId());
    }
}
