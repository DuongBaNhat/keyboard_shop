package com.nhat.keyboard_shop.controller.site;

import com.nhat.keyboard_shop.domain.entity.AppRole;
import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.domain.entity.UserRole;
import com.nhat.keyboard_shop.model.dto.ChangePassword;
import com.nhat.keyboard_shop.model.dto.CustomerDto;
import com.nhat.keyboard_shop.repository.AppRoleRepository;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import com.nhat.keyboard_shop.repository.UserRoleRepository;
import com.nhat.keyboard_shop.service.SendMailService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Controller
public class CustomerSiteController {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    HttpSession session;
    @Autowired
    SendMailService sendMailService;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    AppRoleRepository appRoleRepository;
    @Autowired
    UserRoleRepository userRoleRepository;

    //******LOGIN - LOGOUT*******//
    @GetMapping("/login")
    public ModelAndView loginForm(ModelMap model, @RequestParam("error") Optional<String> error) {
        System.out.println("CustomerSiteController.loginForm");
        String errorString = error.orElse("false");
        if (errorString.equals("true")) {
            model.addAttribute("error", "Tài khoản hoặc mật khẩu không đúng!");
        }
        return new ModelAndView("/site/login", model);
    }

    @RequestMapping("/logout")
    public String login() {
        return "redirect:/home";
    }

    //******REGISTER*******//
    @GetMapping("/register")
    public ModelAndView registerForm(ModelMap model) {
        model.addAttribute("customer", new CustomerDto());
        return new ModelAndView("/site/register", model);
    }

    @PostMapping("/register")
    public String register(ModelMap model, @Valid @ModelAttribute("customer") CustomerDto dto, BindingResult result,
                           @RequestParam("password") String password) {
        if (result.hasErrors()) {
            return "/site/register";
        }
        if (!checkEmail(dto.getEmail())) {
            model.addAttribute("error", "Email này đã được sử dụng!");
            return "/site/register";
        }
        session.removeAttribute("otp");
        int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
        session.setAttribute("otp", random_otp);
        String body = "<div>\r\n" + "        <h3>Mã OTP của bạn là: <span style=\"color:red; font-weight: bold;\">"
                + random_otp + "</span></h3>\r\n" + "    </div>";
        sendMailService.queue(dto.getEmail(), "Đăng kí tài khoản", body);

        model.addAttribute("customer", dto);
        model.addAttribute("message", "Mã OTP đã được gửi tới Email, hãy kiểm tra Email của bạn!");

        return "/site/confirmOtpRegister";
    }

    @PostMapping("/confirmOtpRegister")
    public ModelAndView confirmRegister(ModelMap model, @ModelAttribute("customer") CustomerDto dto, @RequestParam("password") String password,
                                        @RequestParam("otp") String otp) {
        if (otp.equals(String.valueOf(session.getAttribute("otp")))) {
            dto.setPassword(bCryptPasswordEncoder.encode(password));
            Customer c = new Customer();
            BeanUtils.copyProperties(dto, c);
            c.setRegisterDate(new Date());
            c.setStatus(true);
            c.setImage("user.png");
            customerRepository.save(c);
            Optional<AppRole> a = appRoleRepository.findById(2L);
            UserRole urole = new UserRole(0L, c, a.get());
            userRoleRepository.save(urole);

            session.removeAttribute("otp");
            model.addAttribute("message", "Đăng kí thành công");
            return new ModelAndView("/site/login");
        }

        model.addAttribute("customer", dto);
        model.addAttribute("error", "Mã OTP không đúng, hãy thử lại!");
        return new ModelAndView("/site/confirmOtpRegister", model);
    }


    //******FORGOT PASSWORD*******//
    @GetMapping("/forgotPassword")
    public ModelAndView forgotFrom() {
        return new ModelAndView("/site/forgotPassword");
    }
    @PostMapping("/forgotPassword")
    public ModelAndView forgotPassword(ModelMap model, @RequestParam("email") String email) {
        List<Customer> listC = customerRepository.findAll();
        for (Customer c : listC) {
            if (email.trim().equals(c.getEmail())) {
                session.removeAttribute("otp");
                int random_otp = (int) Math.floor(Math.random() * (999999 - 100000 + 1) + 100000);
                session.setAttribute("otp", random_otp);
                String body = "<div>\r\n"
                        + "        <h3>Mã OTP của bạn là: <span style=\"color:red; font-weight: bold;\">" + random_otp
                        + "</span></h3>\r\n" + "    </div>";
                sendMailService.queue(email, "Quên mật khẩu?", body);

                model.addAttribute("email", email);
                model.addAttribute("message", "Mã OTP đã được gửi tới Email, hãy kiểm tra Email của bạn!");
                return new ModelAndView("/site/confirmOtp", model);
            }
        }
        model.addAttribute("error", "Email này không tồn tại trong hệ thống!");
        return new ModelAndView("/site/forgotPassword", model);
    }

    @PostMapping("/confirmOtp")
    public ModelAndView confirm(ModelMap model, @RequestParam("otp") String otp, @RequestParam("email") String email) {
        if (otp.equals(String.valueOf(session.getAttribute("otp")))) {
            model.addAttribute("email", email);
            model.addAttribute("newPassword", "");
            model.addAttribute("confirmPassword", "");
            model.addAttribute("changePassword", new ChangePassword());
            return new ModelAndView("/site/changePassword", model);
        }
        model.addAttribute("error", "Mã OTP không trùng, vui lòng kiểm tra lại!");
        return new ModelAndView("/site/confirmOtp", model);
    }

    //******CHANGE PASSWORD*******//
    @PostMapping("/changePassword")
    public ModelAndView changeForm(ModelMap model,
                                   @Valid @ModelAttribute("changePassword") ChangePassword changePassword, BindingResult result,
                                   @RequestParam("email") String email, @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword) {
        if (result.hasErrors()) {

            model.addAttribute("newPassword", newPassword);
            model.addAttribute("newPassword", confirmPassword);
//			model.addAttribute("changePassword", changePassword);
            model.addAttribute("email", email);
            return new ModelAndView("/site/changePassword", model);
        }

        if (!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {

            model.addAttribute("newPassword", newPassword);
            model.addAttribute("newPassword", confirmPassword);
//			model.addAttribute("changePassword", changePassword);
            model.addAttribute("error", "error");
            model.addAttribute("email", email);
            return new ModelAndView("/site/changePassword", model);
        }
        Customer c = customerRepository.FindByEmail(email).get();
        c.setStatus(true);
        c.setPassword(bCryptPasswordEncoder.encode(newPassword));
        customerRepository.save(c);
        model.addAttribute("message", "Đổi mật khẩu thành công!");
        model.addAttribute("email", "");
        session.removeAttribute("otp");
        return new ModelAndView("/site/changePassword", model);
    }

    //********************PRIVATE METHOD***********************//
    // check email
    private boolean checkEmail(String email) {
        List<Customer> list = customerRepository.findAll();
        for (Customer c : list) {
            if (c.getEmail().equalsIgnoreCase(email)) {
                return false;
            }
        }
        return true;
    }


}
