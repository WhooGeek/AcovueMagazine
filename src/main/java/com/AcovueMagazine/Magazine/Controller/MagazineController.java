package com.AcovueMagazine.Magazine.Controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MagazineController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello Spring!";
    }


}
