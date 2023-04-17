package com.nhat.keyboard_shop.controller.site;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.Optional;

@Controller
public class CustomerSiteController {
    @GetMapping("/login")
    public ModelAndView loginForm(ModelMap model, @RequestParam("error") Optional<String> error) {
        System.out.println("CustomerSiteController.loginForm");
        String errorString = error.orElse("false");
        if (errorString.equals("true")) {
            model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
        }
        return new ModelAndView("/site/login", model);
    }

}
