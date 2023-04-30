package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Category;
import com.nhat.keyboard_shop.model.dto.CategoryDto;
import com.nhat.keyboard_shop.repository.CategoryRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
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
    @RequestMapping("")
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

    /**
     * PAGEABLE
     * @param model
     * @param page
     * @param size
     * @param name
     * @return
     */
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

    /**
     * SEARCH BY NAME CATEGORY
     * @param model
     * @param name
     * @param size
     * @return
     */
    @GetMapping("/search")
    public String search(ModelMap model, @RequestParam(name = "name", required = false) String name,
                         @RequestParam("size") Optional<Integer> size) {
        int pageSize = size.orElse(5);
        Pageable pageable = PageRequest.of(0, pageSize);
        Page<Category> list = categoryRepository.findByNameContaining(name, pageable);

        model.addAttribute("categories", list);
        model.addAttribute("name", name);

        //set activity front-end
        model.addAttribute("menuCa", "menu");

        return "/admin/category";
    }


    /**
     * FORM CREATE CATEGORY
     * @param model
     * @return
     */
    @GetMapping("/add")
    public String addForm(ModelMap model) {
        model.addAttribute("category", new CategoryDto());

        //set activity font-end
        model.addAttribute("menuCa", "menu");
        return "/admin/addCategory";
    }

    /**
     * SAVE CATEGORY INTO DATABASE
     * @param model
     * @param dto
     * @param result
     * @return
     */
    @PostMapping("/add")
    public ModelAndView add(ModelMap model, @Valid @ModelAttribute("category") CategoryDto dto,
                            BindingResult result) {
        //set activity front-end
        model.addAttribute("menuCa", "menu");

        if(result.hasErrors()) {
            return new ModelAndView("/admin/addCategory", model);
        }

        if(!checkCategory(dto.getName()) && !dto.isEdit()) {
            model.addAttribute("error", "Nhãn hiệu này đã tồn tại");
            return new ModelAndView("/admin/addCategory", model);
        }

        Category category = new Category();
        BeanUtils.copyProperties(dto, category);
        categoryRepository.save(category);

        if(dto.isEdit()) {
            model.addAttribute("message", "Sửa thành công !");
        } else  {
            model.addAttribute("message", "Thêm thành công");
        }

        return new ModelAndView("forward:/admin/categories", model);
    }

    /**
     * RESET FORM INPUT CATEGORY
     * @param model
     * @return
     */
    @PostMapping("/reset")
    public String reset(ModelMap model) {
        model.addAttribute("category", new CategoryDto());
        //set activity front-end
        model.addAttribute("menuCa", "menu");

        return "/admin/addCategory";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(ModelMap model, @PathVariable("id") Long id) {
        //set activity front-end
        model.addAttribute("menuCa", "menu");

        Optional<Category> categoryOpt = categoryRepository.findById(id);
        CategoryDto dto = new CategoryDto();
        if(categoryOpt.isPresent()) {
            Category category = categoryOpt.get();
            BeanUtils.copyProperties(category, dto);
            dto.setEdit(true);

            model.addAttribute("category", dto);
            return new ModelAndView("admin/addCategory", model);
        }

        model.addAttribute("error", "Không tồn tại thương hệu này !");
        return  new ModelAndView("forward:/admin/categories/add", model);
    }


    //************PRIVATE METHOD*******************//

    /**
     * Kiem tra ten thuong hieu
     * @param name
     * @return
     */
    private boolean checkCategory(String name) {
        List<Category> list = categoryRepository.findAll();
        for (Category item : list) {
            if (item.getName().equalsIgnoreCase(name)) {
                return false;
            }
        }
        return true;
    }




}
