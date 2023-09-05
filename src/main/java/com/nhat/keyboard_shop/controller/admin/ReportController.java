package com.nhat.keyboard_shop.controller.admin;

import com.nhat.keyboard_shop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Optional;

@RequestMapping("/admin")
@Controller
public class ReportController {

    @Autowired
    private ProductRepository productRepository;

    /**
     * statistical
     * @param model
     * @param statisticalId
     * @return
     */
    @RequestMapping("/reports/statistical")
    public ModelAndView statistical(ModelMap model, @RequestParam("statisticalId") Optional<Integer> statisticalId) {
        int idStatistical = statisticalId.orElse(0);
        model.addAttribute("statisticalId", idStatistical);

        if(idStatistical == 0) {
            return new ModelAndView("forward:/admin/reports/statistical/day", model);
        } else if (idStatistical == 1) {
            return new ModelAndView("forward:/admin/reports/statistical/month", model);
        } else if (idStatistical == 2) {
            return new ModelAndView("forward:/admin/reports/statistical/year", model);
        }

        //set active front-end
        model.addAttribute("menuR", "menu");
        return new ModelAndView("/admin/statistical-day");
    }

    @RequestMapping("/reports/statistical/day")
    public ModelAndView statisticalByDay(ModelMap model) {
        List<Object[]> statistical = productRepository.getStatisticalByDay();
        model.addAttribute("statistical", statistical);

        //set active front - end
        model.addAttribute("menuR", "menu");
        return new ModelAndView("/admin/statistical-day");
    }

    /**
     * statistical by month
     * @param model
     * @return
     */
    @RequestMapping("/reports/statistical/month")
    public ModelAndView statisticalByMonth(ModelMap model) {
        List<Object[]> statistical = productRepository.getStatisticalByMonth();
        model.addAttribute("statistical", statistical);

        //set active front - end
        model.addAttribute("menu", "menu");
        return new ModelAndView("/admin/statistical-month");
    }
}
