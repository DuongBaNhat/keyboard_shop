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

    @RequestMapping("/reports/statistical/year")
    public ModelAndView statisticalByYear(ModelMap model) {
        List<Object[]> statistical = productRepository.getStatisticalByYear();
        model.addAttribute("statistical", statistical);

        //set active front - end
        model.addAttribute("menuR", "menu");
        return new ModelAndView("/admin/statistical-year");
    }

    /**
     * report
     * @param model
     * @param reports
     * @return
     */
    @RequestMapping("/reports")
    public ModelAndView report(ModelMap model, @RequestParam("reports") Optional<Integer> reports) {
        String forward = "forward:/admin/reports";
        int report = reports.orElse(0);
        if(report == 0) {
            forward += "/statistical";
        } else if (report == 1) {
            forward += "/best-selling-category";
        } else if (report == 2) {
            forward += "/best-selling-product";
        } else if (report == 3) {
            forward += "/best-buyer";
        } else {
            forward += "/best-selling-category";
        }

        model.addAttribute("report", report);
        return new ModelAndView(forward, model);
    }

    @RequestMapping("/reports/best-selling-product")
    public ModelAndView bestSellCategory(ModelMap model) {
        List<Object[]> bestSellingProducts = productRepository.getBestSellingProduct();
        model.addAttribute("bestSellingProducts", bestSellingProducts);

        //set active front - end
        model.addAttribute("menuR", "menu");
        return new ModelAndView("/admin/best-selling-product");
    }
}
