package com.mistminds;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CCavenueController {
	
	@RequestMapping(value = "/ccAvenueResponseHandler", method = RequestMethod.POST)
    public ModelAndView ccavResponseHandler() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("ccavResponseHandler"); 
        return mav;
    }
	
}

