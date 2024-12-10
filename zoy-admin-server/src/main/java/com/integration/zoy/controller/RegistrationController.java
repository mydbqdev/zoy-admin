package com.integration.zoy.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.service.ZoyAdminService;

@Controller
public class RegistrationController {

	@Autowired
	private ZoyAdminService zoyService;

	@GetMapping("/verify-email")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		String result = zoyService.validateVerificationToken(token);
		if (result.equals("valid")) {
			model.addAttribute("message", "Your account has been verified successfully.");
			return "verified";
		} else {
			if(result.equals("invalid")) {
				model.addAttribute("message", "Invalid verification token.");
				return "verify-email";
			} else if(result.equals("Email already verified")) {
				model.addAttribute("message", result);
				return "verify-email";
			} else {
				model.addAttribute("message", "Token is invalid");
				return "verify-email";
			}
		}
	}

	
}