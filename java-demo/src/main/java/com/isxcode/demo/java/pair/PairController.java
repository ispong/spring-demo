package com.isxcode.demo.java.pair;

import javafx.util.Pair;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/java/pair")
public class PairController {

    @GetMapping("/test")
    public Object test() {

        return generatePair();
    }

    public Pair<String, String> generatePair() {

        return new Pair<>("key", "value");
    }

}
