package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Order;
import com.nhat.keyboard_shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;
    @RequestMapping("")
    public ModelAndView order(ModelMap model) {
        Page<Order> listO = orderRepository.findAll(
                PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderId"))
        );
        model.addAttribute("orders", listO);
        //set active front-end
        model.addAttribute("memuO", "menu");
        return new ModelAndView("/admin/order");
    }
}
