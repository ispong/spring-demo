package com.isxcode.demo.groovy.controller;


import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import groovy.lang.Script;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.isxcode.demo.groovy.function.Add;

@Slf4j
@RestController
@RequestMapping("/groovy")
public class GroovyController {

    @GetMapping("/executeFun")
    public int executeFun() {

        Binding binding = new Binding();
        binding.setVariable("a", 1);
        binding.setVariable("b", 2);
        binding.setVariable("add", new Add());

        Script script;
        GroovyShell shell = new GroovyShell(binding);
        script = shell.parse("");
        try {
            Object value = script.evaluate("add.invoke(a,b)");
            return Integer.parseInt(String.valueOf(value));
        } catch (Exception e) {
            log.info(e.getMessage());
            return -1;
        }
    }
}
