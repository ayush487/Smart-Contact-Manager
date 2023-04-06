package com.ayush.smartcontact.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.ayush.smartcontact.entities.Contact;
import com.ayush.smartcontact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{
	
	public List<Contact> findContactsByUser(User user);
	
	@Query(value = "Select * from Contact where user_id = ?1", nativeQuery = true)
	public Page<Contact> findContactsByUserId(@Param("userId") int userId, Pageable pageable);
}
