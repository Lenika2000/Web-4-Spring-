package app.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
public class StaticResourcesController implements ErrorController {
    @GetMapping("/auth/*")
    public String auth(Map<String, Object> model) {
        return "/index.html";
    }

    @GetMapping("/main")
    public String main(Map<String, Object> model) {
        return "/index.html";
    }

    @GetMapping("/error")
    public String errors(Map<String, Object> model) {
        return "/index.html";
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}
