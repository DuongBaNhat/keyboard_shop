package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Category;
import com.nhat.keyboard_shop.domain.entity.Product;
import com.nhat.keyboard_shop.model.dto.ProductDto;
import com.nhat.keyboard_shop.repository.CategoryRepository;
import com.nhat.keyboard_shop.repository.ProductRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin/products")
public class ProductController {
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;


    /**
     * /admin/product: GET
     * @param model
     * @return
     */
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

    /**
     * /admin/product: SEARCH
     * @param model
     * @param name
     * @param size
     * @param filter
     * @return
     */
    @RequestMapping("/search")
    public ModelAndView search(ModelMap model, @RequestParam("name") String name,
                               @RequestParam("size") Optional<Integer> size, @RequestParam("filter") Optional<Integer> filter) {
        int pageSize = size.orElse(5);
        int filterPage = filter.orElse(0);
        Pageable pageable = PageRequest.of(0, pageSize);

        if (filterPage == 0) {
            pageable = PageRequest.of(0, pageSize);
        } else if (filterPage == 1) {
            pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "enteredDate"));
        } else if (filterPage == 2) {
            pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "enteredDate"));
        } else if (filterPage == 3) {
            pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.ASC, "unitPrice"));
        } else if (filterPage == 4) {
            pageable = PageRequest.of(0, pageSize, Sort.by(Sort.Direction.DESC, "unitPrice"));
        }

        Page<Product> list = productRepository.findByNameContaining(name, pageable);

        model.addAttribute("name", name);
        model.addAttribute("filter", filterPage);
        model.addAttribute("products", list);
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        // set active front-end
        model.addAttribute("menuP", "menu");
        return new ModelAndView("/admin/product", model);
    }

    /**
     * /admin/product: FILTER, PAGE
     * @param model
     * @param page
     * @param name
     * @param size
     * @param filter
     * @param brandPage
     * @return
     */
    @RequestMapping("/page")
    public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
                             @RequestParam(value = "name", required = false) String name, @RequestParam("size") Optional<Integer> size,
                             @RequestParam("filter") Optional<Integer> filter, @RequestParam("brand") Optional<Long> brandPage) {

        int filterPage = filter.orElse(0);
        int currentPage = page.orElse(0);
        int pageSize = size.orElse(5);
        if (name.equalsIgnoreCase("null")) {
            name = "";
        }
        Long brand = brandPage.orElse(0L);

        Pageable pageable = PageRequest.of(currentPage, pageSize);

        if (brand == 0) {
            if (filterPage == 0) {
                pageable = PageRequest.of(currentPage, pageSize);
            } else if (filterPage == 1) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "enteredDate"));
            } else if (filterPage == 2) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "enteredDate"));
            } else if (filterPage == 3) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "unitPrice"));
            } else if (filterPage == 4) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "unitPrice"));
            }
        } else {
            if (filterPage == 0) {
                pageable = PageRequest.of(currentPage, pageSize);
            } else if (filterPage == 1) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "entered_date"));
            } else if (filterPage == 2) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "entered_date"));
            } else if (filterPage == 3) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.ASC, "unit_price"));
            } else if (filterPage == 4) {
                pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "unit_price"));
            }
        }

        Page<Product> list = null;

        if (brand == 0) {
            list = productRepository.findByNameContaining(name, pageable);
        } else {
            list = productRepository.findAllProductByCategoryId(brand, pageable);
        }

        model.addAttribute("brand", brand);
        model.addAttribute("products", list);
        model.addAttribute("name", name);
        model.addAttribute("filter", filterPage);
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        // set active front-end
        model.addAttribute("menuP", "menu");
        return new ModelAndView("/admin/product", model);
    }

    /**
     * /admin/addProduct: FORM
     * @param model
     * @return
     */
    @GetMapping("/add")
    public ModelAndView add(ModelMap model) {
        model.addAttribute("product", new ProductDto());
        model.addAttribute("photo", "keyboard.png");
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);

        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        // set active front-end
        model.addAttribute("menuP", "menu");
        return new ModelAndView("/admin/addProduct", model);
    }

    /**
     * /admin/addProduct: SAVE/UPDATE
     * @param model
     * @param dto
     * @param result
     * @param photo
     * @param img
     * @return
     * @throws IOException
     */
    @PostMapping("/add")
    public ModelAndView save(ModelMap model, @Valid @ModelAttribute("product") ProductDto dto,
                             BindingResult result, @RequestParam("photo") MultipartFile photo,
                             @RequestParam("imgP") String img) throws IOException {
        List<Category> categories = categoryRepository.findAll();
        model.addAttribute("categories", categories);
        if (result.hasErrors()) {
            if (dto.isEdit()) {
                model.addAttribute("photo", img);
                dto.setImage(img);
            } else {
                model.addAttribute("photo", "keyboard.png");
            }
            List<Category> listC = categoryRepository.findAll();
            model.addAttribute("categories", listC);
            // set active front-end
            model.addAttribute("menuP", "menu");
            return new ModelAndView("/admin/addProduct", model);
        }
        Product p = new Product();
        BeanUtils.copyProperties(dto, p);
        p.setCategory(new Category(dto.getCategoryId(),
                categoryRepository.findById(dto.getCategoryId()).get().getName(), true, null));
        p.setEnteredDate(new Date());

        if (photo.getOriginalFilename().equals("")) {
            if (!img.equals("")) {
                p.setImage(img);
            } else {
                p.setImage("keyboard.png");
            }
        } else {
            p.setImage(photo.getOriginalFilename());
            upload(photo, "uploads/products/", p.getImage());
        }

        productRepository.save(p);
        if (dto.isEdit()) {
            model.addAttribute("message", "Sửa thành công!");
        } else {
            model.addAttribute("message", "Thêm thành công!");
        }

        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        // set active front-end
        model.addAttribute("menuP", "menu");
        return new ModelAndView("forward:/admin/products", model);
    }

    //**********PRIVATE METHOD**********//
    // save file
    private void upload(MultipartFile file, String dir, String name) throws IOException {
        Path path = Paths.get(dir);
        InputStream inputStream = file.getInputStream();
        Files.copy(inputStream, path.resolve(file.getOriginalFilename()), StandardCopyOption.REPLACE_EXISTING);
    }

}
