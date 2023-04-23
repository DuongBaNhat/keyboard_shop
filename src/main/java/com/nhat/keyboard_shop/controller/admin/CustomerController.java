package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/customers")
public class CustomerController {
    @Autowired
    CustomerRepository customerRepository;
    @RequestMapping("")
    public ModelAndView form(ModelMap model) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Customer> list = customerRepository.findAll(pageable);

        model.addAttribute("customers", list);
        // set active front-end
        model.addAttribute("menuC", "menu");
        return new ModelAndView("/admin/manageCustomer", model);
    }
}
