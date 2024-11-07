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
	private ZoyAdminService zoyAdminService;

	@GetMapping("/verify-email")
	public String verifyEmail(@RequestParam("token") String token, Model model) {
		String result = zoyAdminService.validateVerificationToken(token);
		if (result.equals("valid")) {
			model.addAttribute("message", "Your account has been verified successfully.");
			return "verified";
		} else {
			model.addAttribute("message", "Invalid verification token.");
			return "verify-email";
		}
	}

	@GetMapping("/forgotPassword")
	public String forgotPassword(@RequestParam("token") String token, Model model) {
		UserForgotPasswordDto forgotPasswordDto=new UserForgotPasswordDto();
		forgotPasswordDto.setToken(token);
		model.addAttribute("user", forgotPasswordDto);
		return "forgotPassword";
	}


	@PostMapping("/forgot-password")
	public String registerUserAccount(@ModelAttribute("user") UserForgotPasswordDto userDto, Model model) {
		String result = zoyAdminService.validateOtpPassword(userDto);
		if(result.equals("success")) {
			model.addAttribute("message", "Password Changed Successfully.");
			return "forgot-password";
		} else {
			model.addAttribute("message", result);
			return "forgotPassword";
		}
		
	}
}