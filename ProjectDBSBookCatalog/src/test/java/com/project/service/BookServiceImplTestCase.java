package com.project.service;

import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
import com.project.exception.PageOutOfBoundsException;
import com.project.models.Book;
import com.project.repositories.BookRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTestCase {

	@Mock
    private BookRepository bookRepository;

	@Mock
	private ModelMapper modelMapper;

	@Mock
	private InventoryInterface inventoryInterface;

    @InjectMocks
    private BookServiceImpl bookServiceImpl;

    
    private Book book;
    private BookDTO bookDTO;
    
    @BeforeEach
    void setUp() {
        book = new Book();
        bookDTO = new BookDTO();
    }
    
    @AfterEach
	void tearDown() {
		bookRepository=null;
		bookServiceImpl=null;
	}

	@Test
	void getAllBooks_positive() throws BookResourceNotFoundException {
		// Arrange
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		Book book = new Book();
		book.setBookID("B001");
		book.setTitle("Test Book");
		book.setCategoryID(1);
		book.setAuthorID(101);
		book.setCoverImage(new byte[]{1, 2, 3});

		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID("B001");
		bookDTO.setTitle("Test Book");
		bookDTO.setCategoryID(1);
		bookDTO.setAuthorID(101);
		bookDTO.setBase64img("AQID"); // Base64 representation of {1, 2, 3}

		Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book), pageable, 1);

		when(bookRepository.findAll(pageable)).thenReturn(bookPage);
		when(bookRepository.findAuthorNameByBookTitle("Test Book")).thenReturn(Optional.of("Test Author"));
		when(bookRepository.findCategoryNameByBookTitle("Test Book")).thenReturn(Optional.of("Test Category"));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		// Act
		List<BookDTO> result = bookServiceImpl.getAllBooks(page, size);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(bookDTO, result.get(0));
	}

	@Test
	void getAllBooks_negative_noBooksFound() {
		// Arrange
		int page = 0;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);
		Page<Book> emptyPage = Page.empty();

		when(bookRepository.findAll(pageable)).thenReturn(emptyPage);

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getAllBooks(page, size));
	}

	@Test
	void getAllBooks_negative_pageOutOfBounds() {
		// Arrange
		int page = 1;
		int size = 10;
		Pageable pageable = PageRequest.of(page, size);

		Book book = new Book();
		book.setBookID("B001");
		book.setTitle("Test Book");
		book.setCategoryID(1);
		book.setAuthorID(101);
		book.setCoverImage(new byte[]{1, 2, 3});

		Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book), PageRequest.of(0, size), 1); // totalPages = 1

		when(bookRepository.findAll(pageable)).thenReturn(bookPage);

		// Act & Assert
		assertThrows(PageOutOfBoundsException.class, () -> bookServiceImpl.getAllBooks(page, size));
	}

	@Test
	void getBookById_positive() throws BookResourceNotFoundException {
		// Arrange
		String bookId = "B001";
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle("Test Book");
		book.setCategoryID(1);
		book.setAuthorID(101);

		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID(bookId);
		bookDTO.setTitle("Test Book");
		bookDTO.setCategoryID(1);
		bookDTO.setAuthorID(101);

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		// Act
		BookDTO result = bookServiceImpl.getBookById(bookId);

		// Assert
		assertNotNull(result);
		assertEquals(bookId, result.getBookID());
	}

	@Test
	void getBookById_negative() {
		// Arrange
		String bookId = "B001";
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getBookById(bookId));
	}


	@Test
	void getNoOfPages_positive() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 6);
		List<Book> books = Arrays.asList(new Book(), new Book(), new Book(), new Book(), new Book());
		Page<Book> bookPage = new PageImpl<>(books, pageable, 6);
		when(bookRepository.findAll(pageable)).thenReturn(bookPage);

		// Act
		int result = bookServiceImpl.getNoOfPages();

		// Assert
		assertEquals(1, result);
	}

	@Test
	void getNoOfPages_negative() {
		// Arrange
		Pageable pageable = PageRequest.of(0, 6);
		Page<Book> bookPage = new PageImpl<>(Collections.emptyList(), pageable, 0); // Total pages = 0
		when(bookRepository.findAll(pageable)).thenReturn(bookPage);

		// Act
		int result = bookServiceImpl.getNoOfPages();

		// Assert
		assertEquals(0, result);
	}

	@Test
	void getBookByTitle_positive() throws BookResourceNotFoundException {
		// Arrange
		String title = "Test Book";
		Book book = new Book();
		book.setBookID("B001");
		book.setTitle(title);
		book.setCategoryID(1);
		book.setAuthorID(101);
		book.setCoverImage(new byte[]{1, 2, 3});

		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID("B001");
		bookDTO.setTitle(title);
		bookDTO.setCategoryID(1);
		bookDTO.setAuthorID(101);
		bookDTO.setBase64img("AQID");

		when(bookRepository.findByTitle(title)).thenReturn(Optional.of(book));
		when(bookRepository.findAuthorNameByBookTitle(title)).thenReturn(Optional.of("Test Author"));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		// Act
		BookDTO result = bookServiceImpl.getBookByTitle(title);

		// Assert
		assertNotNull(result);
		assertEquals(title, result.getTitle());
		assertEquals("Test Author", result.getAuthorName());
		assertEquals("AQID", result.getBase64img());
	}

	@Test
	void getBookByTitle_negative() {
		// Arrange
		String title = "Nonexistent Book";
		when(bookRepository.findByTitle(title)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getBookByTitle(title));
	}

	@Test
	void getBooksByTitle_positive() throws BookResourceNotFoundException {
		// Arrange
		String title = "Test Book";
		Book book = new Book();
		book.setBookID("B001");
		book.setTitle(title);
		book.setCategoryID(1);
		book.setAuthorID(101);
		book.setCoverImage(new byte[]{1, 2, 3});

		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID("B001");
		bookDTO.setTitle(title);
		bookDTO.setCategoryID(1);
		bookDTO.setAuthorID(101);
		bookDTO.setBase64img("AQID");

		List<Book> bookList = Collections.singletonList(book);
		when(bookRepository.findBooksByTitleContaining(title)).thenReturn(bookList);
		when(bookRepository.findAuthorNameByBookTitle(title)).thenReturn(Optional.of("Test Author"));
		when(bookRepository.findCategoryNameByBookTitle(title)).thenReturn(Optional.of("Test Category"));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		// Act
		List<BookDTO> result = bookServiceImpl.getBooksByTitle(title);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(bookDTO, result.get(0));
		assertEquals("Test Author", result.get(0).getAuthorName());
		assertEquals("Test Category", result.get(0).getCategoryName());
		assertEquals("AQID", result.get(0).getBase64img());
	}

	@Test
	void getBooksByTitle_negative() {
		// Arrange
		String title = "Nonexistent Book";
		when(bookRepository.findBooksByTitleContaining(title)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getBooksByTitle(title));
	}

//	@Test
//	void getBooksByCategory_positive() throws BookResourceNotFoundException {
//		String categoryName = "Fantasy";
//		Book book = new Book();
//		book.setBookID("B001");
//		book.setTitle("Test Book");
//		book.setCategoryID(1);
//		book.setAuthorID(101);
//		book.setCoverImage(new byte[]{1, 2, 3});
//
//		BookDTO bookDTO = new BookDTO();
//		bookDTO.setBookID("B001");
//		bookDTO.setTitle("Test Book");
//		bookDTO.setCategoryID(1);
//		bookDTO.setAuthorID(101);
//		bookDTO.setBase64img("AQID");
//
//		List<Book> bookList = Collections.singletonList(book);
//		when(bookRepository.getByCategory(categoryName)).thenReturn(bookList);
//		when(bookRepository.findAuthorNameByBookTitle("Test Book")).thenReturn(Optional.of("Test Author"));
//		when(bookRepository.findCategoryNameByBookTitle("Test Book")).thenReturn(Optional.of("Fantasy"));
//		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
//
//		List<BookDTO> result = bookServiceImpl.getBooksByCategory(categoryName);
//
//		assertNotNull(result);
//		assertEquals(1, result.size());
//		assertEquals(bookDTO, result.get(0));
//		assertEquals("Test Author", result.get(0).getAuthorName());
//		assertEquals("Fantasy", result.get(0).getCategoryName());
//		assertEquals("AQID", result.get(0).getBase64img());
//	}

	@Test
	void getBooksByCategory_negative() {
		// Arrange
		String categoryName = "Nonexistent Category";
		when(bookRepository.getByCategory(categoryName)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getBooksByCategory(categoryName));
	}

	@Test
	void getBooksByAuthor_positive() throws BookResourceNotFoundException {
		// Arrange
		String authorName = "JK Rowling";
		Book book = new Book();
		book.setBookID("B001");
		book.setTitle("Test Book");
		book.setCategoryID(1);
		book.setAuthorID(101);
		book.setCoverImage(new byte[]{1, 2, 3});

		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID("B001");
		bookDTO.setTitle("Test Book");
		bookDTO.setCategoryID(1);
		bookDTO.setAuthorID(101);
		bookDTO.setBase64img("AQID");

		List<Book> bookList = Collections.singletonList(book);
		when(bookRepository.getByAuthor(authorName)).thenReturn(bookList);
		when(bookRepository.findCategoryNameByBookTitle("Test Book")).thenReturn(Optional.of("Test Category"));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		// Act
		List<BookDTO> result = bookServiceImpl.getBooksByAuthor(authorName);

		// Assert
		assertNotNull(result);
		assertEquals(1, result.size());
		assertEquals(bookDTO, result.get(0));
		assertEquals("Test Category", result.get(0).getCategoryName());
		assertEquals("AQID", result.get(0).getBase64img());
	}

	@Test
	void getBooksByAuthor_negative() {
		// Arrange
		String authorName = "Nonexistent Author";
		when(bookRepository.getByAuthor(authorName)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getBooksByAuthor(authorName));
	}

	@Test
	void getAllAuthors_positive() {
		// Arrange
		List<String> authors = Arrays.asList("Author 1", "Author 2", "Author 3");
		when(bookRepository.findDistinctAuthors()).thenReturn(authors);

		// Act
		List<String> result = bookServiceImpl.getAllAuthors();

		// Assert
		assertNotNull(result);
		assertEquals(3, result.size());
		assertEquals(authors, result);
	}

	@Test
	void getAllAuthors_negative() {
		// Arrange
		when(bookRepository.findDistinctAuthors()).thenReturn(Collections.emptyList());

		// Act
		List<String> result = bookServiceImpl.getAllAuthors();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	void getAllCategories_positive() {
		// Arrange
		List<String> categories = Arrays.asList("Category 1", "Category 2");
		when(bookRepository.findDistinctCategories()).thenReturn(categories);

		// Act
		List<String> result = bookServiceImpl.getAllCategories();

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(categories, result);
	}

	@Test
	void getAllCategories_negative() {
		// Arrange
		when(bookRepository.findDistinctCategories()).thenReturn(Collections.emptyList());

		// Act
		List<String> result = bookServiceImpl.getAllCategories();

		// Assert
		assertNotNull(result);
		assertEquals(0, result.size());
	}

	@Test
	void filter_positive() throws BookResourceNotFoundException {
		// Arrange
		String author = "Test Author";
		String category = "Test Category";
		Book book1 = new Book();
		book1.setBookID("B001");
		book1.setTitle("Book 1");
		book1.setAuthorID(101);
		book1.setCategoryID(1);
		book1.setCoverImage(new byte[]{1, 2, 3});
		Book book2 = new Book();
		book2.setBookID("B002");
		book2.setTitle("Book 2");
		book2.setAuthorID(101);
		book2.setCategoryID(1);
		book2.setCoverImage(new byte[]{4, 5, 6});
		BookDTO bookDTO1 = new BookDTO();
		bookDTO1.setBookID("B001");
		bookDTO1.setTitle("Book 1");
		bookDTO1.setAuthorName("Test Author");
		bookDTO1.setCategoryName("Test Category");
		bookDTO1.setBase64img("AQID");
		BookDTO bookDTO2 = new BookDTO();
		bookDTO2.setBookID("B002");
		bookDTO2.setTitle("Book 2");
		bookDTO2.setAuthorName("Test Author");
		bookDTO2.setCategoryName("Test Category");
		bookDTO2.setBase64img("BAUG");

		List<Book> filteredBooks = Arrays.asList(book1, book2);

		when(bookRepository.findByAuthorContaining(author)).thenReturn(Arrays.asList(book1, book2));
		when(bookRepository.findByCategoryContaining(category)).thenReturn(Arrays.asList(book1, book2));
		when(bookRepository.findAuthorNameById(101)).thenReturn(Optional.of("Test Author"));
		when(bookRepository.findCategoryNameById(1)).thenReturn(Optional.of("Test Category"));
		when(modelMapper.map(book1, BookDTO.class)).thenReturn(bookDTO1);
		when(modelMapper.map(book2, BookDTO.class)).thenReturn(bookDTO2);

		// Act
		List<BookDTO> result = bookServiceImpl.filter(author, category);

		// Assert
		assertNotNull(result);
		assertEquals(2, result.size());
		assertEquals(bookDTO1, result.get(0));
		assertEquals(bookDTO2, result.get(1));
	}

	@Test
	void filter_negative() {
		// Arrange
		String author = "Nonexistent Author";
		String category = "Nonexistent Category";
		when(bookRepository.findByAuthorContaining(author)).thenReturn(Collections.emptyList());
		when(bookRepository.findByCategoryContaining(category)).thenReturn(Collections.emptyList());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.filter(author, category));
	}

	@Test
	void addBook_positive() throws BookResourceNotFoundException {
		// Arrange
		BookDTO bookDTO = new BookDTO();
		bookDTO.setBookID("B001");
		bookDTO.setTitle("Test Book");
		bookDTO.setAuthorName("Test Author");
		bookDTO.setCategoryName("Test Category");
		bookDTO.setBase64img("AQID");
		Book book = new Book();
		book.setBookID("B001");
		book.setTitle("Test Book");
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(new byte[]{1, 2, 3});

		when(bookRepository.findAuthorIDByName("Test Author")).thenReturn(101);
		when(bookRepository.findCategoryIDByName("Test Category")).thenReturn(1);
		when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
		when(bookRepository.save(book)).thenReturn(book);

		// Act
		boolean result = bookServiceImpl.addBook(bookDTO);

		// Assert
		assertTrue(result);
		verify(inventoryInterface, times(1)).addBookToInventory("B001", 1);
	}

	@Test
	void addBook_negative() {
		// Arrange
		BookDTO bookDTO = null;

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.addBook(bookDTO));
	}

	@Test
	void deleteBookById_positive() throws BookResourceNotFoundException {
		// Arrange
		String bookId = "B001";
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle("Test Book");
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(new byte[]{1,2,3});
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		// Act
		boolean result = bookServiceImpl.deleteBookById(bookId);

		// Assert
		assertTrue(result);
		verify(bookRepository, times(1)).deleteById(bookId);
	}

	@Test
	void deleteBookById_negative() {
		// Arrange
		String bookId = "B001";
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.deleteBookById(bookId));
	}

	@Test
	void deleteBookByTitle_positive() throws BookResourceNotFoundException {
		// Arrange
		String title = "Test Book";
		String bookId = "B001";
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle(title);
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(new byte[]{1,2,3});
		when(bookRepository.findByTitle(title)).thenReturn(Optional.of(book));

		// Act
		boolean result = bookServiceImpl.deleteBookByTitle(title);

		// Assert
		assertTrue(result);
		verify(bookRepository, times(1)).deleteByTitle(title);
		verify(inventoryInterface, times(1)).deleteBookFromInventory(bookId);
	}

	@Test
	void deleteBookByTitle_negative() {
		// Arrange
		String title = "Nonexistent Book";
		when(bookRepository.findByTitle(title)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.deleteBookByTitle(title));
	}
	@Test
	void updateBookById_positive() throws BookResourceNotFoundException {
		// Arrange
		String bookId = "B001";
		Book existingBook = new Book();
		existingBook.setBookID(bookId);
		existingBook.setTitle("Old Title");
		existingBook.setAuthorID(101);
		existingBook.setCategoryID(1);
		existingBook.setCoverImage(new byte[]{1, 2, 3});

		BookDTO updatedBookDTO = new BookDTO();
		updatedBookDTO.setBookID("B001");
		updatedBookDTO.setTitle("New Title");
		updatedBookDTO.setAuthorName("New Author");
		updatedBookDTO.setCategoryName("New Category");
		updatedBookDTO.setBase64img("ABCD");

		// Define updatedBook *before* the when()
		Book updatedBook = new Book();
		updatedBook.setBookID(bookId);
		updatedBook.setTitle("New Title");
		updatedBook.setAuthorID(102);
		updatedBook.setCategoryID(2);
		updatedBook.setCoverImage(new byte[]{4, 5, 6});

		when(bookRepository.findById(bookId)).thenReturn(Optional.of(existingBook));

		when(bookRepository.save(existingBook)).thenReturn(updatedBook);
		doNothing().when(modelMapper).map(updatedBookDTO, existingBook);

		// Act
		boolean result = bookServiceImpl.updateBookById(bookId, updatedBookDTO);

		// Assert
		assertTrue(result);
		verify(bookRepository, times(1)).save(existingBook);
	}

	@Test
	void updateBookById_negative() {
		// Arrange
		String bookId = "B001";
		BookDTO updatedBookDTO = new BookDTO();
		updatedBookDTO.setBookID("B001");
		updatedBookDTO.setTitle("New Title");
		updatedBookDTO.setAuthorName("New Author");
		updatedBookDTO.setCategoryName("New Category");
		updatedBookDTO.setBase64img("ABCD");
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.updateBookById(bookId, updatedBookDTO));
	}

	@Test
	void saveBookImage_positive() throws IOException {
		// Arrange
		String bookId = "B001";
		byte[] imageData = {1, 2, 3};
		MultipartFile imageFile = new MockMultipartFile("image", imageData);
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle("Test");
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(null);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookRepository.save(book)).thenReturn(book);

		// Act
		bookServiceImpl.saveBookImage(bookId, imageFile);

		// Assert
		verify(bookRepository, times(1)).save(book);
		assertArrayEquals(imageData, book.getCoverImage());
	}

	@Test
	void saveBookImage_negative() throws IOException{
		// Arrange
		String bookId = "B001";
		MultipartFile imageFile = new MockMultipartFile("image", new byte[]{1,2,3});
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(RuntimeException.class, () -> bookServiceImpl.saveBookImage(bookId, imageFile));

	}

	@Test
	void saveBookSampleChapter_positive() throws IOException {
		// Arrange
		String bookId = "B001";
		byte[] chapterData = {4, 5, 6};
		MultipartFile chapterFile = new MockMultipartFile("chapter", chapterData);
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle("Test");
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(new byte[]{1,2,3});
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
		when(bookRepository.save(book)).thenReturn(book);

		// Act
		bookServiceImpl.saveBookSampleChapter(bookId, chapterFile);

		// Assert
		verify(bookRepository, times(1)).save(book);
		assertArrayEquals(chapterData, book.getSampleChapter());
	}

	@Test
	void saveBookSampleChapter_negative() throws IOException {
		// Arrange
		String bookId = "B001";
		MultipartFile chapterFile = new MockMultipartFile("chapter", new byte[]{4,5,6});
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		//Act & Assert
		assertThrows(IOException.class, () -> bookServiceImpl.saveBookSampleChapter(bookId, chapterFile));
	}

	@Test
	void getSampleChapter_positive() throws BookResourceNotFoundException {
		// Arrange
		String bookId = "B001";
		byte[] chapterData = {7, 8, 9};
		Book book = new Book();
		book.setBookID(bookId);
		book.setTitle("Test");
		book.setAuthorID(101);
		book.setCategoryID(1);
		book.setCoverImage(new byte[]{1,2,3});
		book.setSampleChapter(chapterData);
		when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));

		// Act
		byte[] result = bookServiceImpl.getSampleChapter(bookId);

		// Assert
		assertArrayEquals(chapterData, result);
	}

	@Test
	void getSampleChapter_negative() {
		// Arrange
		String bookId = "B001";
		when(bookRepository.findById(bookId)).thenReturn(Optional.empty());

		// Act & Assert
		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.getSampleChapter(bookId));
	}




//	@Test
//	void testGetAllBooks_positive() throws BookResourceNotFoundException {
//		Pageable pageable = PageRequest.of(0, 10);
//		Page<Book> bookPage = new PageImpl<>(Collections.singletonList(book), pageable, 1);
//		when(bookRepository.findAll(pageable)).thenReturn(bookPage);
//		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
//
//		List<BookDTO> result = bookServiceImpl.getAllBooks(0, 10);
//		assertNotNull(result);
//		assertEquals(1, result.size());
//		assertEquals(bookDTO, result.get(0));
//	}
//
//	@Test
//	void testGetAllBooks_negative() throws PageOutOfBoundsException {
//		Pageable pageable = PageRequest.of(1, 10);
//		when(bookRepository.findAll(pageable)).thenReturn(Page.empty());
//
//		assertThrows(PageOutOfBoundsException.class, () -> bookServiceImpl.getAllBooks(1, 10));
//	}
//	@Test
//	void testGetBookById_positive() throws BookResourceNotFoundException{
//	    book.setBookID("B001");
//	    bookDTO.setBookID("B001");
//		when(bookRepository.findById(book.getBookID())).thenReturn(Optional.of(book));
//		//when(bookMapper.bookToBookDTO(any())).thenReturn(bookDTO);
//		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
//
//		BookDTO bookDTO=bookServiceImpl.getBookById("B001");
//
//		assertNotNull(bookDTO);
//		assertEquals("B001", bookDTO.getBookID());
//
//	}
//
//	@Test
//	void testGetBooksById_negative() throws BookResourceNotFoundException {
//		when(bookRepository.findById("B001")).thenReturn(Optional.empty());
//		assertThrows(BookResourceNotFoundException.class, ()-> bookServiceImpl.getBookById("B001"));
//	}
//
//	@Test
//	void testGetBooksByCategory_positive() throws BookResourceNotFoundException {
//		when(bookRepository.getByCategory(any())).thenReturn(Collections.singletonList(book));
//		//when(bookMapper.bookListToBookDTOList(any())).thenReturn(Arrays.asList(bookDTO));
//		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
//
//        List<BookDTO> result = bookServiceImpl.getBooksByCategory("Fantasy");
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(bookDTO, result.getFirst());
//
//	}
//
//	@Test
//	void testGetBooksByCategory_negative() throws BookResourceNotFoundException {
//		when(bookRepository.getByCategory("Fantasy")).thenReturn(Collections.emptyList());
//		assertThrows(BookResourceNotFoundException.class, ()-> bookServiceImpl.getBooksByCategory("Fantasy"));
//	}
//
//	@Test
//	void testGetBooksByAuthor_positive() throws BookResourceNotFoundException {
//		when(bookRepository.getByAuthor("JK Rowling")).thenReturn(Collections.singletonList(book));
//		//when(bookMapper.bookListToBookDTOList(any())).thenReturn(Arrays.asList(bookDTO));
//		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);
//
//        List<BookDTO> result = bookServiceImpl.getBooksByAuthor("JK Rowling");
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(bookDTO, result.getFirst());
//
//	}
//
//	@Test
//	void testGetBooksByAuthor_negative() throws BookResourceNotFoundException {
//		when(bookRepository.getByAuthor("JK Rowling")).thenReturn(Collections.emptyList());
//		assertThrows(BookResourceNotFoundException.class, ()-> bookServiceImpl.getBooksByAuthor("JK Rowling"));
//	}
//@Test
//void testAddBook_Positive() throws BookResourceNotFoundException{
//	when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
//	when(bookRepository.save(book)).thenReturn(book);
//	when(inventoryInterface.addBookToInventory(book.getBookID(), 1)).thenReturn(ResponseEntity.ok("Book added"));
//
//	boolean result = bookServiceImpl.addBook(bookDTO);
//
//	assertTrue(result);
//	verify(bookRepository, times(1)).save(book);
//}
//
//	@Test
//	void testAddBook_Negative() {
//		assertThrows(BookResourceNotFoundException.class, ()-> bookServiceImpl.addBook(null));
//		verify(bookRepository, never()).save(any(Book.class));
//	}
//
//	@Test
//	void testDeleteBookById_positive() throws BookResourceNotFoundException {
//		book.setBookID("B001");
//		bookDTO.setBookID("B001");
//
//		when(bookRepository.findById(any())).thenReturn(Optional.of(book));
//		boolean result = bookServiceImpl.deleteBookById(book.getBookID());
//
//		assertTrue(result);
//		verify(bookRepository).findById(any());
//		verify(bookRepository).deleteById(any());
//	}
//
//	@Test
//	void testDeleteBookById_negative() {
//		when(bookRepository.findById(any())).thenReturn(Optional.empty());
//
//		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.deleteBookById(any()));
//
//		verify(bookRepository, times(1)).findById(any());
//		verify(bookRepository, times(0)).deleteById(any());
//	}
//
//	@Test
//	void testDeleteBookByTitle_positive() throws BookResourceNotFoundException {
//		book.setTitle("Wimpy Kid");
//		book.setBookID("B001");
//		bookDTO.setTitle("Wimpy Kid");
//
//		when(bookRepository.findByTitle(any())).thenReturn(Optional.of(book));
//
//		boolean result = bookServiceImpl.deleteBookByTitle(book.getTitle());
//
//		assertTrue(result);
//		verify(bookRepository).findByTitle(any());
//		verify(bookRepository).deleteByTitle(any());
//		verify(inventoryInterface).deleteBookFromInventory(book.getBookID());
//	}
//
//	@Test
//	void testDeleteBookByTitle_negative(){
//		when(bookRepository.findByTitle(any())).thenReturn(Optional.empty());
//
//		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.deleteBookByTitle(any()));
//
//		verify(bookRepository, times(1)).findByTitle(any());
//		verify(bookRepository, times(0)).deleteByTitle(any());
//		verify(inventoryInterface, times(0)).deleteBookFromInventory(any());
//	}
//
//	@Test
//	void testUpdateBookById_positive() throws BookResourceNotFoundException {
//		book.setBookID("B001");
//		book.setPrice(450);
//		book.setAuthorID(123);
//		book.setCategoryID(456);
//		book.setTitle("Wimpy Kid");
//
//		bookDTO.setBookID("B001");
//		bookDTO.setTitle("Book New");
//		bookDTO.setAuthorID(123);
//		bookDTO.setCategoryID(456);
//		bookDTO.setPrice(236.0);
//
//		when(bookRepository.findById(any())).thenReturn(Optional.of(book));
//		boolean updatedBook = bookServiceImpl.updateBookById(book.getBookID(), bookDTO);
//
//		assertTrue(updatedBook);
//		verify(bookRepository, times(1)).findById(any());
//		verify(bookRepository, times(1)).save(any(Book.class));
//	}
//
//	@Test
//	void testUpdateBookById_negative(){
//		when(bookRepository.findById(any())).thenReturn(Optional.empty());
//
//		assertThrows(BookResourceNotFoundException.class, () -> bookServiceImpl.updateBookById(null, bookDTO));
//
//		verify(bookRepository, times(1)).findById(any());
//		verify(bookRepository, times(0)).updateBookById("B001", "Book New", 123, 789, 236);
//	}
}
