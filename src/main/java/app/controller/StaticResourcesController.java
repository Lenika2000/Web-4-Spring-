package app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class StaticResourcesController {
    @GetMapping("/auth/*")
    public String auth(Map<String, Object> model) {
        return "/index.html";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        return "/index.html";
    }
}
