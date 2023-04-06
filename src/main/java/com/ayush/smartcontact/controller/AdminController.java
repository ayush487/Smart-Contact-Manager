package com.ayush.smartcontact.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {
	@RequestMapping("/index")
	@PreAuthorize("hasAuthority('ROLE_ADMIN')")
	public String dashboard() {
		return "admin/admin_dashboard";
	}
	
}
