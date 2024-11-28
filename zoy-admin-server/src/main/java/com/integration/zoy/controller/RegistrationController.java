package com.integration.zoy.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.integration.zoy.service.ZoyAdminService;
import com.integration.zoy.utils.UserForgotPasswordDto;

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
			model.addAttribute("message", "Invalid verification token.");
			return "verify-email";
		}
	}

	
}