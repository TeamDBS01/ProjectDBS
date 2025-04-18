package com.project.service;

import com.project.dto.AuthorDTO;
import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.models.Author;
import com.project.models.Book;
import com.project.repositories.BookRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
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
     * @param page
     * @param size
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found
     */

    @Override
    public List<BookDTO> getAllBooks(int page, int size) throws BookResourceNotFoundException {
        Pageable pageable = PageRequest.of(page, size);
        Page<Book> bookPage = bookRepository.findAll(pageable);

        if (page >= bookPage.getTotalPages() && bookPage.getTotalPages() > 0) {
            throw new PageOutOfBoundsException("Page number exceeds total pages available");
        }
        if (bookPage.isEmpty()) {
            throw new BookResourceNotFoundException("No books found");
        }

        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : bookPage) {
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            String authorName = bookRepository.findAuthorNameByBookTitle(book.getTitle())
                    .orElse("Unknown Author");
            String categoryName = bookRepository.findCategoryNameByBookTitle(book.getTitle())
                    .orElse("Unknown Category");
            bookDTO.setAuthorName(authorName);
            bookDTO.setCategoryName(categoryName);
            if (book.getCoverImage() != null) {
                bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
            }
            bookDTOs.add(bookDTO);
        }

        return bookDTOs;
    }

    @Override
    public int getNoOfPages(){
        Pageable pageable=PageRequest.of(0,3);
        Page<Book> bookPage=bookRepository.findAll(pageable);
        return bookPage.getTotalPages();
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
        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

        String authorName = bookRepository.findAuthorNameByBookTitle(book.getTitle())
                .orElse("Unknown Author");
        String categoryName = bookRepository.findCategoryNameByBookTitle(book.getTitle())
                .orElse("Unknown Category");
        bookDTO.setAuthorName(authorName);
        bookDTO.setCategoryName(categoryName);

        if (book.getCoverImage() != null) {
            bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
        }
        return bookDTO;
    }

    @Override
    public BookDTO getBookByTitle(String title) throws BookResourceNotFoundException {
        Book book = bookRepository.findByTitle(title)
                .orElseThrow(() -> new BookResourceNotFoundException("No book found with title: " + title));

        String authorName = bookRepository.findAuthorNameByBookTitle(title)
                .orElseThrow(() -> new BookResourceNotFoundException("No author found for book title: " + title));

        BookDTO bookDTO = modelMapper.map(book, BookDTO.class);

        if (book.getCoverImage() != null) {
            bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
        }

        // Set author name
        bookDTO.setAuthorName(authorName);

        return bookDTO;
    }

    @Override
    public List<BookDTO> getBooksByTitle(String title) throws BookResourceNotFoundException {
        List<Book> books = bookRepository.findBooksByTitleContaining(title);

        if (books.isEmpty()) {
            throw new BookResourceNotFoundException("No books found with title containing: " + title);
        }

        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : books) {
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            String authorName = bookRepository.findAuthorNameByBookTitle(book.getTitle())
                    .orElse("Unknown Author");
            String categoryName = bookRepository.findCategoryNameByBookTitle(book.getTitle())
                    .orElse("Unknown Category");
            bookDTO.setAuthorName(authorName);
            bookDTO.setCategoryName(categoryName);
            if (book.getCoverImage() != null) {
                bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
            }
            bookDTOs.add(bookDTO);
        }

        return bookDTOs;
    }


    /**
     * Retrieves books by category.
     *
     * @param categoryName the name of the category
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found in the given category
     */
    @Override
    public List<BookDTO> getBooksByCategory(String categoryName) throws BookResourceNotFoundException {
        List<Book> bookList = bookRepository.getByCategory(categoryName);

        if (bookList.isEmpty()) {
            throw new BookResourceNotFoundException("No books found in category: " + categoryName);
        }

        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : bookList) {
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            String authorName = bookRepository.findAuthorNameByBookTitle(book.getTitle())
                    .orElse("Unknown Author");
            bookDTO.setAuthorName(authorName);
            bookDTO.setCategoryName(categoryName);
            if (book.getCoverImage() != null) {
                bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
            }
            bookDTOs.add(bookDTO);
        }

        return bookDTOs;
    }

    /**
     * Retrieves books by author.
     *
     * @param authorName the name of the author
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found by the given author
     */
    @Override
    public List<BookDTO> getBooksByAuthor(String authorName) throws BookResourceNotFoundException {
        List<Book> bookList = bookRepository.getByAuthor(authorName);

        if (bookList.isEmpty()) {
            throw new BookResourceNotFoundException("No books found by author: " + authorName);
        }

        List<BookDTO> bookDTOs = new ArrayList<>();
        for (Book book : bookList) {
            BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
            String categoryName = bookRepository.findCategoryNameByBookTitle(book.getTitle())
                    .orElse("Unknown Category");
            bookDTO.setAuthorName(authorName);
            bookDTO.setCategoryName(categoryName);
            if (book.getCoverImage() != null) {
                bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
            }
            bookDTOs.add(bookDTO);
        }

        return bookDTOs;
    }


    public List<String> getAllAuthors() {
        return bookRepository.findDistinctAuthors();
    }

    public List<String> getAllCategories() {
        return bookRepository.findDistinctCategories();
    }


    /**
     * Filters books based on given criteria.
     *
     * @return a list of BookDTO objects
     * @throws BookResourceNotFoundException if no books are found for the given criteria
     */
//    public List<BookDTO> filter(String... criteria) throws BookResourceNotFoundException {
//        if (criteria.length == 0) {
//            throw new IllegalArgumentException("At least one criterion must be provided");
//        }
//
//        List<Book> filteredBooks = new ArrayList<>();
//
//        if (criteria.length == 1) {
//            String criterion = criteria[0];
//            filteredBooks.addAll(bookRepository.getByAuthor(criterion));
//            filteredBooks.addAll(bookRepository.getByCategory(criterion));
//        } else if (criteria.length == 2) {
//            String author = criteria[0];
//            String category = criteria[1];
//            List<Book> booksByAuthor = bookRepository.getByAuthor(author);
//            filteredBooks = booksByAuthor.stream()
//                    .filter(book -> bookRepository.getByCategory(category).contains(book))
//                    .collect(Collectors.toList());
//        }
//
//        if (filteredBooks.isEmpty()) {
//            throw new BookResourceNotFoundException("No books found for the given criteria");
//        }
//
//        return filteredBooks.stream()
//                .map(book -> modelMapper.map(book, BookDTO.class))
//                .collect(Collectors.toList());
//    }

//    public List<BookDTO> filter(String author, String category) throws BookResourceNotFoundException {
//        List<Book> filteredBooks = new ArrayList<>();
//
//        if (author != null && !author.isEmpty()) {
//            filteredBooks.addAll(bookRepository.getByAuthor(author));
//        }
//
//        if (category != null && !category.isEmpty()) {
//            if (!filteredBooks.isEmpty()) {
//                filteredBooks = filteredBooks.stream()
//                        .filter(book -> bookRepository.getByCategory(category).contains(book))
//                        .collect(Collectors.toList());
//            } else {
//                filteredBooks.addAll(bookRepository.getByCategory(category));
//            }
//        }
//
//        if (filteredBooks.isEmpty()) {
//            throw new BookResourceNotFoundException("No books found for the given criteria");
//        }
//
//        return filteredBooks.stream()
//                .map(book -> modelMapper.map(book, BookDTO.class))
//                .collect(Collectors.toList());
//    }
    public List<BookDTO> filter(String author, String category) throws BookResourceNotFoundException {
        List<Book> filteredBooks = new ArrayList<>();

        if (author != null && !author.isEmpty()) {
            filteredBooks.addAll(bookRepository.findByAuthorContaining(author));
        }

        if (category != null && !category.isEmpty()) {
            if (!filteredBooks.isEmpty()) {
                filteredBooks = filteredBooks.stream()
                        .filter(book -> bookRepository.findByCategoryContaining(category).contains(book))
                        .collect(Collectors.toList());
            } else {
                filteredBooks.addAll(bookRepository.findByCategoryContaining(category));
            }
        }

        if (filteredBooks.isEmpty()) {
            throw new BookResourceNotFoundException("No books found for the given criteria");
        }

        return filteredBooks.stream()
                .map(book -> {
                    BookDTO bookDTO = modelMapper.map(book, BookDTO.class);
                    bookDTO.setAuthorName(bookRepository.findAuthorNameById(book.getAuthorID()).orElse("Unknown"));
                    bookDTO.setCategoryName(bookRepository.findCategoryNameById(book.getCategoryID()).orElse("Unknown"));
                    bookDTO.setBase64img(Base64.getEncoder().encodeToString(book.getCoverImage()));
                    return bookDTO;
                })
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

        Integer authorId = bookRepository.findAuthorIDByName(bookDTO.getAuthorName());
        if (authorId == null) {
            authorId = bookRepository.insertNewAuthor(bookDTO.getAuthorName());
            if (authorId == null) {
                throw new BookResourceNotFoundException("Failed to create and retrieve Author ID for: " + bookDTO.getAuthorName());
            }
        }
        authorId = bookRepository.findAuthorIDByName(bookDTO.getAuthorName());
        bookDTO.setAuthorID(authorId);

        Integer categoryId = bookRepository.findCategoryIDByName(bookDTO.getCategoryName());
        if (categoryId == null) { // Using null for clarity
            categoryId = bookRepository.insertNewCategory(bookDTO.getCategoryName());
            if (categoryId == null) {
                throw new BookResourceNotFoundException("Failed to create and retrieve Category ID for: " + bookDTO.getCategoryName());
            }
        }
        categoryId = bookRepository.findCategoryIDByName(bookDTO.getCategoryName());
        bookDTO.setCategoryID(categoryId);
        Book book = modelMapper.map(bookDTO, Book.class);

        if (bookDTO.getBase64img() != null && !bookDTO.getBase64img().isEmpty() && !bookDTO.getBase64img().equals("null")) {
            book.setCoverImage(base64ToByteArray(bookDTO.getBase64img()));
        }
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
            //inventoryInterface.deleteBookFromInventory(bookID);
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
     * @param bookID  the ID of the book
     * @param bookDTO the book data transfer object
     * @return true if the book is updated successfully
     * @throws BookResourceNotFoundException if no book with the given ID is found
     */
    public boolean updateBookById(String bookID, BookDTO bookDTO) throws BookResourceNotFoundException {
        Optional<Book> optionalOfBook = bookRepository.findById(bookID);
        if (optionalOfBook.isPresent()) {
            Book book = optionalOfBook.get();
            modelMapper.map(bookDTO, book);
            if (bookDTO.getBase64img() != null && !bookDTO.getBase64img().isEmpty() && !bookDTO.getBase64img().equals("null")) {
                book.setCoverImage(base64ToByteArray(bookDTO.getBase64img()));
            }
            System.out.println(book + " " + bookDTO );
            bookRepository.save(book);
            return true;
        } else {
            throw new BookResourceNotFoundException(BOOK_NOT_FOUND_MESSAGE);
        }
    }

    public void saveBookImage(String bookID, MultipartFile imageFile) throws IOException {
        Optional<Book> optionalBook = bookRepository.findById(bookID);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setCoverImage(imageFile.getBytes());
            bookRepository.save(book);
        } else {
            throw new RuntimeException("Book not found");
        }
    }

    private byte[] base64ToByteArray(String base64Image) throws IllegalArgumentException {
        if (base64Image == null || base64Image.isEmpty() || base64Image.equals("null")) {
            return null;
        }

        String imageData;
        if (base64Image.startsWith("data:")) {
            imageData = base64Image.substring(base64Image.indexOf(',') + 1);
        } else {
            imageData = base64Image; // Assume it's already just the base64 data
        }

        try {
            return Base64.getDecoder().decode(imageData);
        } catch (IllegalArgumentException e) {
            System.err.println("Error decoding base64 image (invalid format): " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the caller
        }
    }

    @Override
    public void saveBookSampleChapter(String bookID, MultipartFile sampleChapterFile) throws IOException {
        Optional<Book> optionalBook = bookRepository.findById(bookID);
        if (optionalBook.isPresent()) {
            Book book = optionalBook.get();
            book.setSampleChapter(sampleChapterFile.getBytes());
            bookRepository.save(book);
        } else {
            throw new IOException("Book not found");
        }
    }


    @Override
    public byte[] getSampleChapter(String bookID) throws BookResourceNotFoundException {
        Optional<Book> optionalBook = bookRepository.findById(bookID);
        if (optionalBook.isPresent()) {
            return optionalBook.get().getSampleChapter();
        } else {
            throw new BookResourceNotFoundException("Book not found");
        }
    }

}
