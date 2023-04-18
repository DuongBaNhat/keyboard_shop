package com.nhat.keyboard_shop.config.oauth;

import com.nhat.keyboard_shop.domain.entity.AppRole;
import com.nhat.keyboard_shop.domain.entity.Customer;
import com.nhat.keyboard_shop.domain.entity.UserRole;
import com.nhat.keyboard_shop.domain.util.AuthenticationProvider;
import com.nhat.keyboard_shop.repository.AppRoleRepository;
import com.nhat.keyboard_shop.repository.CustomerRepository;
import com.nhat.keyboard_shop.repository.UserRoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    UserRoleRepository userRoleRepository;
    @Autowired
    AppRoleRepository appRoleRepository;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
        String email = oauth2User.getName();
        System.out.println(email);
        Optional<Customer> cus = customerRepository.FindByEmail(email);
        if(cus.isEmpty()) {
            Customer c = new Customer();
            c.setRegisterDate(new Date());
            c.setStatus(true);
            c.setImage("user.png");
            c.setName(oauth2User.getNameReal());
            c.setEmail(email);
            c.setGender(true);
            c.setPassword(bCryptPasswordEncoder.encode("123@ABCxyzalualua"));
            c.setAddress("Chưa có");
            c.setPhone("");
            c.setAuthProvider(AuthenticationProvider.FACEBOOK);
            customerRepository.save(c);
            Optional<AppRole> a = appRoleRepository.findById(2L);
            UserRole urole = new UserRole(0L, c, a.get());
            userRoleRepository.save(urole);
        }
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
