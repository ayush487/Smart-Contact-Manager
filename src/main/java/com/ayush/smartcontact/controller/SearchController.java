package com.ayush.smartcontact.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.ayush.smartcontact.dao.ContactRepository;
import com.ayush.smartcontact.dao.UserRepository;
import com.ayush.smartcontact.entities.Contact;
import com.ayush.smartcontact.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;

	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal){
		//System.out.println(query);
		User user = userRepository.getUserByUserName(principal.getName());
		List<Contact> contacts = contactRepository.findByNameContainingAndUser(query, user);
		return ResponseEntity.ok(contacts);
	}
	
}
