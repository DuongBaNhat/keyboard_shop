package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Category;
import com.nhat.keyboard_shop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    /**
     * LIST CATEGORY
     * @param model
     * @param size
     * @return
     */
    @GetMapping("")
    public String list(ModelMap model, @RequestParam("size") Optional<Integer> size) {
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Category> categories = categoryRepository.findAll(pageable);

        model.addAttribute("size", pageSize);
        model.addAttribute("categories", categories);

        //set active front-end
        model.addAttribute("menuCa", "menu");
        return "/admin/category";
    }

    @GetMapping("/page")
    public String page(ModelMap model, @RequestParam("page") Optional<Integer> page,
                       @RequestParam("size") Optional<Integer> size,
                       @RequestParam(name = "name", required = false) String name) {
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        if(name.equalsIgnoreCase("null")) {
            name = "";
        }
        Pageable pageable = PageRequest.of(currentPage, pageSize);
        Page<Category> list = categoryRepository.findAll(pageable);

        model.addAttribute("categories", list);
        model.addAttribute("name", name);

        //set active front-end
        model.addAttribute("menuCa", "menu");

        return "/admin/category";
    }


}
