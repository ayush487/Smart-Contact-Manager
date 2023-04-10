package com.ayush.smartcontact.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.ayush.smartcontact.dao.UserRepository;
import com.ayush.smartcontact.entities.User;
import com.ayush.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/")
	public String home(Model model) {
		model.addAttribute("pageTitle", "Home - Smart Contact Manager");
		return "home";
	}

	@GetMapping("/about")
	public String about(Model model) {
		model.addAttribute("pageTitle", "About - Smart Contact Manager");
		return "about";
	}

	@GetMapping("/signup")
	public String signup(Model model, HttpSession session) {
		if (session.getAttribute("message") != null) {
			session.removeAttribute("message");
		}
		model.addAttribute("pageTitle", "Register - Smart Contact Manager");
		model.addAttribute("user", new User());
		return "signup";
	}

	@GetMapping("/signin")
	public String login(Model model) {
		model.addAttribute("pageTitle", "Login - Smart Contact Manager");
		return "login";
	}

	@PostMapping(value = "/do_register")
	public String registerUser(@Valid @ModelAttribute("user") User user, BindingResult bindingResult,
			@RequestParam(value = "agreement", defaultValue = "false") boolean agreement, Model model,
			HttpSession session) {
		try {
			if (!agreement) {
				System.out.println("Please accept Terms & Conditions");
				throw new Exception("you have not accepted Terms & Conditions");
			}
			if (bindingResult.hasErrors()) {
				System.out.println("ERROR : " + bindingResult.toString());
				model.addAttribute("user", user);
				return "signup";
			}
			if (userRepository.existsByEmail(user.getEmail())) {
				throw new Exception("email address already exists");
			}
			user.setPassword(passwordEncoder.encode(user.getPassword()));
			user.setEnabled(true);
			user.setRole("ROLE_USER");
			userRepository.save(user);
//			System.out.println("Agreement : " + agreement);
//			System.out.println("User : " + result);
			model.addAttribute("user", new User());
			session.setAttribute("message", new Message("Successfully Registered", "alert-success"));
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something went wrong : " + e.getMessage(), "alert-danger"));
		}

		return "signup";
	}


}
