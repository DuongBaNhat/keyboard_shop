package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;

@Controller
@RequestMapping("/admin/account")
public class AccountController {
    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping("")
    public ModelAndView info(ModelMap model, Principal principal) {
        Customer customer = customerRepository.FindByEmail(principal.getName()).orElse(null);
        model.addAttribute("user", customer);

        return new ModelAndView("/admin/information", model);
    }
}
