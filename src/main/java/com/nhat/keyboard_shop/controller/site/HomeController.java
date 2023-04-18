package com.nhat.keyboard_shop.controller.site;

import com.nhat.keyboard_shop.domain.entity.Category;
import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.domain.entity.Product;
import com.nhat.keyboard_shop.domain.entity.UserRole;
import com.nhat.keyboard_shop.repository.CategoryRepository;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import com.nhat.keyboard_shop.repository.ProductRepository;
import com.nhat.keyboard_shop.repository.UserRoleRepository;
import com.nhat.keyboard_shop.service.ShoppingCartService;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
public class HomeController {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ShoppingCartService shoppingCartService;
    @Autowired
    CategoryRepository categoryRepository;

    /**
     * All page
     * @param model
     * @param principal
     * @return
     */
    @RequestMapping(value = {"/home", "/"})
    public ModelAndView home(ModelMap model, Principal principal) {
        System.out.println("HomeController.home");
        boolean isLogin = false;
        if (principal != null) {
            isLogin = true;
        }
        model.addAttribute("isLogin", isLogin);

        ModelAndView modelAdmin = getModelAndView(model, principal);
        if (modelAdmin != null) return modelAdmin;

        System.out.println("HomeController.home");
        Page<Product> listP = productRepository.findAll(PageRequest.of(0, 6));
        int totalPage = listP.getTotalPages();
        if (totalPage > 0) {
            int start = 1;
            int end = Math.min(2, totalPage);
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("totalCartItems", shoppingCartService.getCount());
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        model.addAttribute("products", listP);
        model.addAttribute("slide", true);

        System.out.println("listP = " + listP.getSize());

        return new ModelAndView("site/index", model);
    }

    /**
     * Page filter
     * @param model
     * @param page
     * @param name
     * @param filter
     * @param brand
     * @param principal
     * @return
     */
    @RequestMapping("/home/page")
    public ModelAndView page(ModelMap model, @RequestParam("page") Optional<Integer> page,
                             @RequestParam("name") String name, @RequestParam("filter") Optional<Integer> filter,
                             @RequestParam("brand") Long brand, Principal principal) {

        boolean isLogin = false;
        if (principal != null) {
            System.out.println(principal.getName());
            isLogin = true;
        }
        model.addAttribute("isLogin", isLogin);

        ModelAndView modelAdmin = getModelAndView(model, principal);
        if (modelAdmin != null) return modelAdmin;

        int currentPage = page.orElse(0);
        int filterPage = filter.orElse(0);

        Pageable pageable = PageRequest.of(currentPage, 6);

        if (brand != -1) {
            if (filterPage == 0) {
                pageable = PageRequest.of(currentPage, 6);
            } else if (filterPage == 1) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "entered_date"));
            } else if (filterPage == 2) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "entered_date"));
            } else if (filterPage == 3) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "unit_price"));
            } else if (filterPage == 4) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "unit_price"));
            }
        } else {
            if (filterPage == 0) {
                pageable = PageRequest.of(currentPage, 6);
            } else if (filterPage == 1) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "enteredDate"));
            } else if (filterPage == 2) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "enteredDate"));
            } else if (filterPage == 3) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.DESC, "unitPrice"));
            } else if (filterPage == 4) {
                pageable = PageRequest.of(currentPage, 6, Sort.by(Sort.Direction.ASC, "unitPrice"));
            }
        }

        Page<Product> listP = null;
        if (brand == -1) {
            listP = productRepository.findByNameContaining(name, pageable);
        } else {
            listP = productRepository.findAllProductByCategoryId(brand, pageable);
        }

        int totalPage = listP.getTotalPages();
        if (totalPage > 0) {
            int start = Math.max(1, currentPage - 2);
            int end = Math.min(currentPage + 2, totalPage);
            if (totalPage > 5) {
                if (end == totalPage) {
                    start = end - 5;
                } else if (start == 1) {
                    end = start + 5;
                }
            }
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        model.addAttribute("totalCartItems", shoppingCartService.getCount());
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        model.addAttribute("brand", brand);
        model.addAttribute("filter", filterPage);
        model.addAttribute("name", name);
        model.addAttribute("products", listP);
        model.addAttribute("slide", true);
        return new ModelAndView("/site/index", model);
    }

    /**
     * Brand filter
     * @param model
     * @param categoryId
     * @param principal
     * @return
     */
    @RequestMapping("/home/brand/{id}")
    public ModelAndView brand(ModelMap model, @PathVariable("id") Long categoryId, Principal principal) {
        boolean isLogin = false;
        if (principal != null) {
            System.out.println(principal.getName());
            isLogin = true;
        }
        model.addAttribute("isLogin", isLogin);

        ModelAndView modelAdmin = getModelAndView(model, principal);
        if (modelAdmin != null) return modelAdmin;

        Page<Product> listP = productRepository.findAllProductByCategoryId(categoryId, PageRequest.of(0, 6));

        System.out.println(listP.getTotalElements());
        int totalPage = listP.getTotalPages();
        if (totalPage > 0) {
            int start = 1;
            int end = Math.min(2, totalPage);
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("totalCartItems", shoppingCartService.getCount());
        List<Category> listC = categoryRepository.findAll();
        model.addAttribute("categories", listC);
        model.addAttribute("brand", categoryId);
        model.addAttribute("products", listP);
        model.addAttribute("slide", true);
        return new ModelAndView("/site/index", model);
    }

    /**
     * Item filter
     * @param model
     * @param id
     * @param principal
     * @return
     */
    @RequestMapping("/home/item/{id}")
    public ModelAndView item(ModelMap model, @PathVariable("id") Long id, Principal principal) {

        boolean isLogin = false;
        if (principal != null) {
            System.out.println(principal.getName());
            isLogin = true;
        }
        model.addAttribute("isLogin", isLogin);

        ModelAndView modelAdmin = getModelAndView(model, principal);
        if (modelAdmin != null) return modelAdmin;

        Optional<Product> p = productRepository.findById(id);

        // random item product
        Long quantity = productRepository.count();
        int index = (int) (Math.random() * (quantity / 6));
        if (index > quantity - 6) {
            index -= 6;
        }
        Page<Product> productSuggest = productRepository.findAll(PageRequest.of(index, 6));
        if (p.isPresent()) {
            model.addAttribute("product", p.get());
            model.addAttribute("productSuggest", productSuggest);
            List<Category> listC = categoryRepository.findAll();
            model.addAttribute("categories", listC);
            model.addAttribute("totalCartItems", shoppingCartService.getCount());
            return new ModelAndView("/site/item", model);
        } else {
            model.addAttribute("message", "Sản phẩm đã bị xoá !");
        }
        model.addAttribute("totalCartItems", shoppingCartService.getCount());
        return new ModelAndView("forward:/home", model);
    }

    /**
     * Search filter
     * @param model
     * @param name
     * @param filter
     * @param principal
     * @return
     */
    @RequestMapping("/home/search")
    public ModelAndView search(ModelMap model, @RequestParam("name") String name,
                               @RequestParam("filter") Optional<Integer> filter, Principal principal) {

        boolean isLogin = false;
        if (principal != null) {
            System.out.println(principal.getName());
            isLogin = true;
        }
        model.addAttribute("isLogin", isLogin);

        ModelAndView modelAdmin = getModelAndView(model, principal);
        if (modelAdmin != null) return modelAdmin;

        int filterPage = filter.orElse(0);
        Pageable pageable = PageRequest.of(0, 6);

        if (filterPage == 0) {
            pageable = PageRequest.of(0, 6);
        } else if (filterPage == 1) {
            pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "enteredDate"));
        } else if (filterPage == 2) {
            pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "enteredDate"));
        } else if (filterPage == 3) {
            pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.DESC, "unitPrice"));
        } else if (filterPage == 4) {
            pageable = PageRequest.of(0, 6, Sort.by(Sort.Direction.ASC, "unitPrice"));
        }
        Page<Product> listP = productRepository.findByNameContaining(name, pageable);

        int totalPage = listP.getTotalPages();
        if (totalPage > 0) {
            int start = 1;
            int end = Math.min(2, totalPage);
            List<Integer> pageNumbers = IntStream.rangeClosed(start, end).boxed().collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }
        model.addAttribute("totalCartItems", shoppingCartService.getCount());

        List<Category> listC = categoryRepository.findAll();

        setModel(model, name, filterPage, listP, listC);
        return new ModelAndView("/site/index", model);
    }


    //*****************PRIVATE METHOD*********************//

    /**
     * Set Model Attribute
     * @param model
     * @param name
     * @param filterPage
     * @param listP
     * @param listC
     */
    private void setModel(ModelMap model, String name, int filterPage, Page<Product> listP, List<Category> listC) {
        model.addAttribute("name", name);
        model.addAttribute("categories", listC);
        model.addAttribute("filter", filterPage);
        model.addAttribute("products", listP);
        model.addAttribute("slide", true);
    }
    @Nullable
    private ModelAndView getModelAndView(ModelMap model, Principal principal) {
        if (principal != null) {
            Optional<Customer> c = customerRepository.FindByEmail(principal.getName());
            Optional<UserRole> uRole = userRoleRepository.findByCustomerId(Long.valueOf(c.get().getCustomerId()));
            if (uRole.get().getAppRole().getName().equals("ROLE_ADMIN")) {
                return new ModelAndView("forward:/admin/customers", model);
            }
        }
        return null;
    }



}
