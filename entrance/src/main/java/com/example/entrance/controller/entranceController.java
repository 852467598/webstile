package com.example.entrance.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

/**
 * @ProjectName: webstile
 * @Package: com.example.entrance.controller
 * @ClassName: entranceController
 * @Author: 陈凯
 * @Description: ${description}
 * @Date: 2019/6/20 10:32
 * @Version: 1.0
 */
@RestController
@RequestMapping("/")
public class entranceController {

    @RequestMapping("/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }

}
