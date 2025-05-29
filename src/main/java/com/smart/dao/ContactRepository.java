package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends CrudRepository<Contact, Integer>{
	
	//pagination.....
	@Query("select c from Contact c where c.user.id =:userId")
	//currentPage - page
	//contact per page - 5 
	//Pageable object contains this two information
	public Page<Contact> findContactsByUser(@Param("userId") int userId, Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name, User user);
}
