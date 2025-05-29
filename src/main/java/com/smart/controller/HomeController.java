package com.smart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helpers.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/test")
	@ResponseBody
	public String test() {
		
		User user = new User();
		user.setName("Durgesh Tiwari");
		user.setEmail("durgesh@dev1.io");
		
		Contact contact = new Contact();
		user.getContacts().add(contact);
		userRepository.save(user);
		
		return "working";
	}
	
	@RequestMapping("/home")
	public String home(Model model) {
		//model.setAtrribute("title", "Home - Smart Home Controller");
		return "home";
	}
	
	@RequestMapping("/about")
	public String about() {
		return "about";
	}
	
	@RequestMapping("/signup")
	public String signup(Model model) {
		model.addAttribute("title", "Sign up - Smart Home Controller");
		model.addAttribute("user", new User());
		return "signup";
	}
	
	// handler for registering user
	@RequestMapping(value = "/do_register", method = RequestMethod.POST)
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult res, @RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model, HttpSession session) {
		try {
			// Whatever form fields will be matched with the fields present in the User
			// We have done request param because we have not mentioned it in user
			
			if (!agreement) {
				System.out.println("You have not agreed the terms and conditions");
				throw new Exception("You have not agreed the terms and conditions");
			}
			
			if (res.hasErrors()) {
				System.out.println("Errors " + res);
				model.addAttribute("user", user);
				return "signup";
			}
			
			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("default.png");
			user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			
			System.out.println("Agreement " + agreement);
			System.out.println("USER " + user);
			
			User result = userRepository.save(user);
			
			model.addAttribute("user", new User());
			
			model.addAttribute("message", new Message("Successfully registered !!", "alert-success"));
			return "signup";
			
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			model.addAttribute("message", new Message("Something went wrong !! " + e.getMessage(), "alert-danger"));
			return "signup";
		}
	}
	
	// handler for custom login
	@GetMapping("/signin")
	public String customLogin(Model model) {
		model.addAttribute("title", "Login Page");
		return "signin";
	}
}
