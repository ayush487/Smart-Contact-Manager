package com.ayush.smartcontact.controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.ayush.smartcontact.dao.ContactRepository;
import com.ayush.smartcontact.dao.UserRepository;
import com.ayush.smartcontact.entities.Contact;
import com.ayush.smartcontact.entities.User;
import com.ayush.smartcontact.helper.FileUploadHelper;
import com.ayush.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private FileUploadHelper uploadHelper;
	
	@Autowired
	private ContactRepository contactRepository;
	
	@ModelAttribute
	public void addCommonData(Model m, Principal p) {
		String username = p.getName();
		User user = userRepository.getUserByUserName(username);
		m.addAttribute("user", user);
		
	}

	//Dash-board
	@RequestMapping("/index")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String dashboard(Model model, Principal principal) {
		User user = userRepository.getUserByUserName(principal.getName());
		model.addAttribute("pageTitle", user.getName() + " - Dashboard");
		return "normal/user_dashboard";
	}
	
	//Add Contact Page Request
	@GetMapping("/add-contact")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String addContactForm(Model model, Principal principal, HttpSession session) {
		if(session.getAttribute("message")!=null) {
			session.removeAttribute("message");
		}
		User user = userRepository.getUserByUserName(principal.getName());
		model.addAttribute("pageTitle", user.getName() + " - Add Contacts");
		model.addAttribute("contact", new Contact());
		return "normal/add_contact_form";
	}
	
	//View Contact Page Request
	@GetMapping("/view-contacts/{page}")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String viewContacts(@PathVariable("page") Integer page, Model model, Principal principal) {
		User user = userRepository.getUserByUserName(principal.getName());
//		List<Contact> contacts = contactRepository.findContactsByUser(user);
		
		Pageable pageable =  PageRequest.of(page, 5);
		
		Page<Contact> contacts = contactRepository.findContactsByUserId(user.getId(), pageable);
		model.addAttribute("pageTitle", user.getName() + " - Contacts");
		model.addAttribute("contacts", contacts);
		model.addAttribute("currentPage", page);
		int tPages = contacts.getTotalPages();
		model.addAttribute("totalPages",(tPages > 0) ? tPages : 1 );
		return "normal/view_contacts";
	}
	
	//Add Contact Process Request
	@PostMapping("/process-contact")
	@PreAuthorize("hasAuthority('ROLE_USER')")
	public String addContacts(@ModelAttribute("contact") Contact contact,@RequestParam("profileImage") MultipartFile img,
			Principal principal, HttpSession session) {
		try {
			User user = userRepository.getUserByUserName(principal.getName());
			if(!img.isEmpty()) {
				if(img.getContentType().equals("image/png") || img.getContentType().equals("image/jpeg")) {
//					System.out.println("File type is either png or jpeg");
					
					String filename;
					if(img.getContentType().equals("image/png")) {
						filename = "scm_"+ System.currentTimeMillis() + ".png";
					}else {
						filename ="scm_" + System.currentTimeMillis() + ".jpg";
					}
					
					boolean isUploaded = uploadHelper.upload_file(img, filename);
					if(isUploaded)	contact.setImage(filename);
				}else {
					System.out.println("Only Png and Jpg files allowed!");
				}
			}else {
				contact.setImage("contact.png");
			}
			contact.setUser(user);
			user.getContacts().add(contact);
			userRepository.save(user);
			session.setAttribute("message", new Message("Contact Added Successfully", "alert-success"));
		}catch (Exception e) {
			System.out.println("ERROR : " + e.getMessage());
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong ! Try again...", "alert-danger"));
		}
		return "normal/add_contact_form";
	}
	
	//Contact Details Page Request
	@GetMapping("/{cId}/contact")
	public String showContactDetails(@PathVariable("cId") Integer cId, Model model, Principal principal) {
		Contact contact = contactRepository.findById(cId).orElse(null);
		if(contact==null) {
			model.addAttribute("title", "Error - Can't find Contact");
			return "normal/contact_detail";
		}
		User logged_user = userRepository.getUserByUserName(principal.getName());
		if(logged_user.getId()==contact.getUser().getId()) {
			model.addAttribute("contact", contact);
			model.addAttribute("pageTitle", contact.getName());
		}
		return "normal/contact_detail";
	}
	
	//Delete Contact Process Request
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId") Integer cId, Principal principal) {
		User u = userRepository.getUserByUserName(principal.getName());
		Contact c = contactRepository.findById(cId).get();
		try {
			if(!c.getImage().equals("contact.png")) {
				uploadHelper.delete_file(c.getImage());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(u.getId()==c.getUser().getId()) {
			contactRepository.delete(c);
		}
		return "redirect:/user/view-contacts/0";
	}
	
	//Update Contact Page Request
	@PostMapping("/update-contact/{cId}")
	public String updateContact(@PathVariable("cId") Integer cId, Model model) {
		model.addAttribute("pageTitle", "Update Contact");
		Contact c = contactRepository.findById(cId).get();
		model.addAttribute("contact", c);
		return "normal/update_contact";
	}
	
	//Update Contact Process Request
	@PostMapping("/process-update")
	public String processUpdate(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile img, Principal principal) {
		try {
			if(!img.isEmpty()) {
				if(img.getContentType().equals("image/png") || img.getContentType().equals("image/jpeg")) {
					String filename = "SCM"+ contact.getcId() +"_"+ img.getOriginalFilename();
					boolean isUploaded = uploadHelper.upload_file(img, filename);
					if(isUploaded) {
						if(!contact.getImage().equals("contact.png")) {
							uploadHelper.delete_file(contact.getImage());
						}
						contact.setImage(filename);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		User u = userRepository.getUserByUserName(principal.getName());
		contact.setUser(u);
		contactRepository.save(contact);
		return "redirect:/user/" + contact.getcId()+ "/contact";
	}
	
	//My Profile Page Request
	@GetMapping("/profile")
	public String myprofile(Model model) {
		model.addAttribute("pageTitle", "My Profile");
		return "normal/myprofile";
	}
}
