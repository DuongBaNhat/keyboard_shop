package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.model.dto.CustomerDto;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.Objects;

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

    /**
     * edit form
     * @param model
     * @param principal
     * @return view
     */
    @GetMapping("/editProfile")
    public ModelAndView editForm(ModelMap model, Principal principal) {
        Customer customer = customerRepository.FindByEmail(principal.getName()).orElse(null);
        model.addAttribute("customer", customer);

        return new ModelAndView("/admin/editProfile", model);
    }

    /**
     * edit customer
     * @param model
     * @param principal
     * @param result
     * @param dto
     * @param photo
     * @return
     * @throws Exception
     */
    @PostMapping("/editProfile")
    public ModelAndView edit(ModelMap model, Principal principal,
                             @Valid @ModelAttribute("customer") CustomerDto dto, BindingResult result,
                             @RequestParam("photo") MultipartFile photo) throws IOException {
        if(result.hasErrors()) {
            return new ModelAndView("forward:/admin/account", model);
        }

        Customer customer = customerRepository.FindByEmail(principal.getName()).orElse(null);
        if (customer == null) {
            return new ModelAndView("forward:/admin/account");
        }
        if(photo.getOriginalFilename() != null  && !photo.getOriginalFilename().isBlank() ) {
            upload(photo, "uploads/customers");
            customer.setImage(photo.getOriginalFilename());
        }

        customer.setName(dto.getName());
        customer.setGender(dto.isGender());
        customer.setPhone(dto.getPhone());
        customer.setAddress(dto.getAddress());

        customerRepository.save(customer);

        return new ModelAndView("forward:/admin/account");
    }

    //save file
    /**
     * upload file
     * @param file
     * @param dir
     * @throws Exception
     */
    private  void upload(MultipartFile file, String dir) throws IOException {
        Path path = Paths.get(dir);
        InputStream inputStream = file.getInputStream();

        Files.copy(inputStream, path.resolve(Objects.requireNonNull(file.getOriginalFilename())),
                StandardCopyOption.REPLACE_EXISTING);
    }
}
