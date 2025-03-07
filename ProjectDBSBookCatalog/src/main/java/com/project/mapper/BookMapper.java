package com.project.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.project.dto.BookDTO;
import com.project.models.Book;

@Mapper
public interface BookMapper {
	 BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

	    BookDTO bookToBookDTO(Book book);
	    Book bookDTOToBook(BookDTO bookDTO);
}

