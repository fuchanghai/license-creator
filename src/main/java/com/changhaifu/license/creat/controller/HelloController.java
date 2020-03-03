package com.changhaifu.license.creat.controller;

import com.changhaifu.license.interceptor.ChLicense;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>证书验证测试</p>
 *
 * @author appleyk
 * @version V.1.0.1
 * @blob https://blog.csdn.net/appleyk
 * @date created on 下午 4:57 2019-9-26
 */
@CrossOrigin
@RestController
@ChLicense
public class HelloController {
    @GetMapping("/hello")
    public String sayHello(){
        return "hello license !";
    }

}
