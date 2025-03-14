package com.project.repository;

import com.project.models.Book;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.dto.BookDTO;

public interface BookRepository extends JpaRepository<Book, String> {
	
}
