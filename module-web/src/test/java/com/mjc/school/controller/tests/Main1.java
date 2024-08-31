//package com.mjc.school.controller.tests;
//
//import com.mjc.school.controller.Main;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.boot.SpringApplication;
//
//import java.security.Key;
//import java.util.Base64;
//
//public class Main1 {
//    public static void main(String[] args) {
//        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256); // Генерация ключа для HS256
//        String base64Key = Base64.getEncoder().encodeToString(key.getEncoded());
//        System.out.println("Base64 Secret Key: " + base64Key);
//    }
//}
