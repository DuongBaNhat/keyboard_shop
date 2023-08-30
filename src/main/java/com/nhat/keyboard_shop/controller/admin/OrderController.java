package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Order;
import com.nhat.keyboard_shop.domain.entity.OrderDetail;
import com.nhat.keyboard_shop.repository.OrderDetailRepository;
import com.nhat.keyboard_shop.repository.OrderRepository;
import com.nhat.keyboard_shop.service.SendMailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/orders")
public class OrderController {
    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderDetailRepository orderDetailRepository;

    @Autowired
    SendMailService sendMailService;

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

    /**
     * confirm order of admin
     * @param model
     * @param id
     * @return
     */
    @RequestMapping("/confirm/{order-id}")
    public ModelAndView confirm(ModelMap model, @PathVariable("order-id") int id) {
        Optional<Order> orderOptional =  orderRepository.findById(id);
        String view = "forward:/admin/orders";
        if(orderOptional.isEmpty()) {
            return new ModelAndView(view, model);
        }

        Order order = orderOptional.get();
        order.setStatus((short) 1);
        orderRepository.save(order);

        sendMailAction(order, "Bạn có 1 đơn hàng ở KeyBoard Shop đã được xác nhận!",
                "Chúng tôi sẽ sớm giao hàng cho bạn!",
                "Thông báo đơn hàng đã được xác nhận!");

        return new ModelAndView(view, model);
    }

    @RequestMapping("/cancel/{order-id}")
    public ModelAndView cancel(ModelMap model, @PathVariable("order-id") int id) {
        Optional<Order> orderOptional = orderRepository.findById(id);
        if(orderOptional.isEmpty()) {
            return new ModelAndView("forward:/admin/orders", model);
        }

        Order order = orderOptional.get();
        order.setStatus((short) 3);
        orderRepository.save(order);

        sendMailAction(order, "Bạn đã bị huỷ 1 đơn hàng từ KeyBoard Shop!",
                "Chúng tôi rất tiếc!", "Thông báo huỷ đơn hàng!");
        return new ModelAndView("forward:/admin/orders", model);
    }

    @RequestMapping("/search")
    public ModelAndView search(ModelMap model, @RequestParam("id") String id) {
        Page<Order> listO = null;
        if(id == null || id.isEmpty() || id.equalsIgnoreCase("null")) {
            listO = orderRepository.findAll(PageRequest.of(
                    0, 5, Sort.by(Sort.Direction.DESC, "orderId"))
            );
        } else {
            listO = orderRepository.findByOrderId(
                    Integer.parseInt(id), PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "orderId"))
            );
        }
        model.addAttribute("id", id);
        model.addAttribute("orders", listO);

        //set active front-end
        model.addAttribute("menuO", "menu");
        return new ModelAndView("/admin/order");
    }

    /**
     * filter, page size, current page
     * @param model
     * @param page
     * @param size
     * @param filter
     * @return
     */
    @RequestMapping("/page")
    public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
                             @RequestParam("size") Optional<Integer> size,
                             @RequestParam("filter") Optional<Integer> filter) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        int filterPage = filter.orElse(0);

        Pageable pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "order_id"));
        Page<Order> listO = null;
        if(filterPage == 0) {
            pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "orderId"));
            listO = orderRepository.findAll(pageable);
        } else if(filterPage == 1) {
            listO = orderRepository.findByStatus(0, pageable);
        } else if(filterPage == 2) {
            listO = orderRepository.findByStatus(1, pageable);
        } else if (filterPage == 3) {
            listO = orderRepository.findByStatus(2, pageable);
        } else if (filterPage == 4) {
            listO = orderRepository.findByStatus(3, pageable);
        } else if (filterPage == 5) {
            pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "amount"));
            listO = orderRepository.findAll(pageable);
        }

        model.addAttribute("filter", filterPage);
        model.addAttribute("page", currentPage);
        model.addAttribute("orders", listO);

        //set active front-end
        model.addAttribute("menuO", "menu");

        return new ModelAndView("/admin/order");
    }

    //*** private method ***//

    /**
     * Format number
     * @param number
     * @return
     */
    private String format(String number) {
        DecimalFormat formater = new DecimalFormat("###,###,###.##");
        return formater.format(Double.valueOf(number)) + " VNĐ";
    }

    /**
     * Send mail
     * @param order
     * @param status
     * @param cmt
     * @param notification
     */
    private void sendMailAction(Order order, String status, String cmt, String notification) {
        List<OrderDetail> orderDetails = orderDetailRepository.findByOrderId(order.getOrderId());

        //table
        String title = "<h3>Xin chào " + order.getCustomer().getName() + "!</h3>\r\n" +
                "<h4>" + status + "</h4>\r\n";
        String styleBorder = "style=\"border: 1px solid gray;\"";
        String styleWidth = "style=\"width: 100%; border: 1px solid gray;\"";
        StringBuilder tableBuilder = new StringBuilder();
        int index = 0;
        tableBuilder.append(
                title
                +  "<table " + styleBorder + ">\r\n"
                + "     <tr " + styleWidth + ">"
                + "         <th " + styleBorder + ">" + "STT</th>"
                + "         <th " + styleBorder + ">" + "Tên sản phẩm</th>"
                + "         <th " + styleBorder + ">" + "Số lượng</th>"
                + "         <th " + styleBorder + ">" + "Đơn giá</th>"
                + "     </tr>"
        );

        for(OrderDetail detail : orderDetails) {
            index++;
            tableBuilder.append(
                    "<tr>"
                            + "<td " + styleBorder + ">" + index + "</td>"
                            + "<td " + styleBorder + ">" + detail.getProduct().getName() + "</td>"
                            + "<td " + styleBorder + ">" + detail.getQuantity() + "</td>"
                            + "<td " + styleBorder + ">" + format(String.valueOf(detail.getUnitPrice())) + "</td>"

                    + "</tr>"
            );
        }

        tableBuilder.append("\r\n" + "</table>\r\n"
                + "<h3>Tổng tiền: " + format(String.valueOf(order.getAmount())) + "</h3>\r\n"
                + "<hr>\r\n"
                + "<h5>" + cmt + "</h5>\r\n"
                + "<h5>Chúc bạn 1 ngày tốt lành!</h5>"
        );

        sendMailService.queue(order.getCustomer().getEmail().trim(), notification, tableBuilder.toString());
    }
}
