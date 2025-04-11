package no.ntnu.gr10.bachelor_gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
  @GetMapping("/hello")
  public String hello() {
    return "Hello! You are authenticated via JWT.";
  }
}
