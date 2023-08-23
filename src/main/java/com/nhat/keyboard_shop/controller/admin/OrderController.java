package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Order;
import com.nhat.keyboard_shop.domain.entity.OrderDetail;
import com.nhat.keyboard_shop.repository.OrderDetailRepository;
import com.nhat.keyboard_shop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    /**
     * View list order
     * @param model
     * @return
     */
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

    @RequestMapping("/detail/{order-id}")
    public ModelAndView detail(ModelMap model, @PathVariable("order-id") int id) {
        List<OrderDetail> listO = orderDetailRepository.findByOrderId(id);
        double amount = orderRepository.findById(id).get().getAmount();

        model.addAttribute("orderDetail", listO);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", id);

        //set active fe
        model.addAttribute("menuO", "menu");

        return new ModelAndView("/admin/orderDetail", model);
    }
}
