package com.smart.controller;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helpers.Message;
import com.smart.serv.EmailService;

import jakarta.servlet.http.HttpSession;

@Controller
public class ForgotController {
	
	Random random = new Random(1000);
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	EmailService emailService;
	//email id form open handler
	@RequestMapping("/forgot")
	public String openEmailForm() {
		return "forgot_email_form";
	}
	
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email, Model m, HttpSession session) {
		// generating otp of 4 digit
		
		int otp = random.nextInt(9999);
		
		// write code for email
		
		String subject = "OTP From SCM";
		String message=""
				+ "<div style='border: 1px solid #e2e2e2; padding: 20px'>"
				+ "<h1>"
				+ "OTP is "
				+ "<b>"+otp
				+ "</n>" 
				+ "</h1>"
				+ "</div>";
		
		
		boolean f = this.emailService.sendEmail(subject, message, email);
		
		System.out.println("OTP"+otp);
		if (f) {
//			m.addAttribute("myotp", otp);
//			m.addAttribute("email", email);
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			m.addAttribute("msg", new Message("OTP successfully sent to your email", "success"));
			return "verify_otp";
			
		} else {
			m.addAttribute("msg", new Message("Email not sent, try again !!", "danger"));
			return "forgot_email_form";
		}
	}
	
	// verify otp
	@RequestMapping(value =  "/verify-otp", method = RequestMethod.POST)
	public String verifyOtp(@RequestParam("otp") Integer otp, HttpSession session, Model m) {
		Integer myOtp = (Integer) session.getAttribute("myotp");
		String email = (String)session.getAttribute("email");
		System.out.println(otp + " " + myOtp);
		if (myOtp.equals(otp)) {
			// password change form
			
			User user = this.userRepository.getUserByUserName(email);
			if (user == null) {
				m.addAttribute("msg", new Message("No user exists with this email !!", "danger"));
				return "forgot_email_form";
			} else {
				return "password_change_form";
			}
		} else {
			m.addAttribute("msg", new Message("You have entered wrong otp !!", "danger"));
			return "verify_otp";
		}
		
		//change password'
		
		
	}
	
	@PostMapping("/change-password") 
	public String changePassword(@RequestParam("password") String password, @RequestParam("confirm_password") String confirm_password, Model m, HttpSession session) {
		if (!password.equals(confirm_password)) {
			m.addAttribute("msg", new Message("Passwords didn't matched !!", "danger"));
			return "password_change_form";
		}
		
		String email = (String)session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(confirm_password));
		this.userRepository.save(user);
		session.removeAttribute("email");
		return "redirect:/signin?change=Password changed successfully";
	}
}
