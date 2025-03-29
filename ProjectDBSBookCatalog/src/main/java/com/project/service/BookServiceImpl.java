package com.project.service;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.models.Book;
import com.project.repositories.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of the BookService interface.
 * Provides methods to manage books in the repository.
 */
@Service
public class BookServiceImpl implements BookService {

    private BookRepository bookRepository;

    private ModelMapper modelMapper;

    private InventoryInterface inventoryInterface;

    private static final String BOOK_NOT_FOUND_MESSAGE = "Book Resource not found";

    /**
     * Constructor for BookServiceImpl.
     * book entities
     *
     * @param modelMapper        the model mapper for DTO conversion
     * @param inventoryInterface the inventory interface for managing inventory
     */
    @Autowired
    public BookServiceImpl(BookRepository bookRepository, ModelMapper modelMapper, InventoryInterface inventoryInterface) {
        this.bookRepository = bookRepository;
        this.modelMapper = modelMapper;
        this.inventoryInterface = inventoryInterface;
    }

    /**
     * Retrieves all books from the repository.
     *
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found
     */

    @Override
    public List<BookDTO> getAllBooks(int page, int size) throws BookResourceNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        if(page>=bookPage.getTotalPages() && bookPage.getTotalPages() > 0){
            throw new PageOutOfBoundsException("Page number exceeds total pages available");
        }
        if (bookPage.isEmpty()) {
            throw new BookResourceNotFoundException("No books found");
        }
        return bookPage.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a book by its ID.
     *
     * @param bookId the ID of the book
     * @return the BookDTO object
     * @throws BookResourceNotFoundException if no book with the given ID is found
     */
    public BookDTO getBookById(String bookId) throws BookResourceNotFoundException {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookResourceNotFoundException("No book with ID found: " + bookId));
        return modelMapper.map(book, BookDTO.class);
    }

    /**
     * Retrieves books by category.
     *
     * @param categoryName the name of the category
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found in the given category
     */
    public List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException {
        List<Book> bookList = bookRepository.getByCategory(categoryName);

        if (bookList.isEmpty()) {
            throw new BookResourceNotFoundException("No books found");
        }
        return bookList.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves books by author.
     *
     * @param authorName the name of the author
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found by the given author
     */
    public List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException {
        List<Book> bookList = bookRepository.getByAuthor(authorName);
        if (bookList.isEmpty()) {
            throw new BookResourceNotFoundException("No books found");
        }
        return bookList.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Filters books based on given criteria.
     *
     * @param criteria the criteria for filtering books
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found for the given criteria
     */
    public List<BookDTO> filter(String... criteria) throws BookResourceNotFoundException {
        if (criteria.length == 0) {
            throw new IllegalArgumentException("At least one criterion must be provided");
        }

        List<Book> filteredBooks = new ArrayList<>();

        if (criteria.length == 1) {
            String criterion = criteria[0];
            filteredBooks.addAll(bookRepository.getByAuthor(criterion));
            filteredBooks.addAll(bookRepository.getByCategory(criterion));
        } else if (criteria.length == 2) {
            String author = criteria[0];
            String category = criteria[1];
            List<Book> booksByAuthor = bookRepository.getByAuthor(author);
            filteredBooks = booksByAuthor.stream()
                    .filter(book -> bookRepository.getByCategory(category).contains(book))
                    .collect(Collectors.toList());
        }

        if (filteredBooks.isEmpty()) {
            throw new BookResourceNotFoundException("No books found for the given criteria");
        }

        return filteredBooks.stream()
                .map(book -> modelMapper.map(book, BookDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Adds a new book to the repository.
     *
     * @param bookDTO the book data transfer object
     * @return true if the book is added successfully
     * @throws BookResourceNotFoundException if the book resource is null
     */
    public boolean addBook(BookDTO bookDTO) throws BookResourceNotFoundException {
        if (bookDTO == null) {
            throw new BookResourceNotFoundException("Book resource cannot be null");
        }
        Book book = modelMapper.map(bookDTO, Book.class);
        Book save = bookRepository.save(book);
        inventoryInterface.addBookToInventory(save.getBookID(), 1); // Assuming quantity is 1 for simplicity

        return true;
    }

	/**
	 * Deletes a book by its ID.
	 *
	 * @param bookID the ID of the book
	 * @return true if the book is deleted successfully
	 * @throws BookResourceNotFoundException if no book with the given ID is found
	 */
    public boolean deleteBookById(String bookID) throws BookResourceNotFoundException {
        Optional<Book> optionalOfBook = bookRepository.findById(bookID);
        if (optionalOfBook.isPresent()) {
            bookRepository.deleteById(bookID);
            inventoryInterface.deleteBookFromInventory(bookID);
            return true;
        } else {
            throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * Deletes a book by its title.
     *
     * @param bookTitle the title of the book
     * @return true if the book is deleted successfully
     * @throws BookResourceNotFoundException if no book with the given title is found
     */
    public boolean deleteBookByTitle(String bookTitle) throws BookResourceNotFoundException {
        Optional<Book> optionalOfBook = bookRepository.findByTitle(bookTitle);
        if (optionalOfBook.isPresent()) {
            Book book = optionalOfBook.get();
            String bookID = book.getBookID();
            bookRepository.deleteByTitle(bookTitle);
            inventoryInterface.deleteBookFromInventory(bookID);
            return true;
        } else {
            throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
        }
    }

    /**
     * Updates a book by its ID.
     *
     * @param bookID the ID of the book
     * @param bookDTO the book data transfer object
     * @return true if the book is updated successfully
     * @throws BookResourceNotFoundException if no book with the given ID is found
     */
    public boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException {
        Optional<Book> optionalOfBook = bookRepository.findById(bookID);
        if (optionalOfBook.isPresent()) {
            Book book = optionalOfBook.get();
            modelMapper.map(bookDTO, book);
            bookRepository.save(book);
            return true;
        } else {
            throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
        }
    }

}
