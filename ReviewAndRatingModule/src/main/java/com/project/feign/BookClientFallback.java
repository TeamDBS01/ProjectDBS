package com.project.feign;

import com.project.dto.BookDTO;
import com.project.exception.ServiceUnavailableException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class BookClientFallback implements BookClient {

    @Override
    public ResponseEntity<BookDTO> getBookById(String bookId) throws ServiceUnavailableException {
        throw new ServiceUnavailableException("Book Service is Not Available");
    }
}