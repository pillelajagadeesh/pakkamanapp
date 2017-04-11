package com.mistminds;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class GetRSAController {
	
	
	@RequestMapping(value = "/getResponseHandler", method = RequestMethod.POST)
    public ModelAndView getResponseHandler() {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("GetRSA"); 
        return mav;
    }

}
