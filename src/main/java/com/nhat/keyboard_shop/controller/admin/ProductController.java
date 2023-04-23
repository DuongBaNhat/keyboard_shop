package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Category;
import com.nhat.keyboard_shop.domain.entity.Product;
import com.nhat.keyboard_shop.repository.CategoryRepository;
import com.nhat.keyboard_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;


    @RequestMapping("")
    public ModelAndView list(ModelMap model) {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Product> list = productRepository.findAll(pageable);

        model.addAttribute("products", list);
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        // set active front-end
        model.addAttribute("menuP", "menu");
        return new ModelAndView("/admin/product", model);
    }
}
