package com.project.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;


import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


import com.project.dto.BookDTO;
import com.project.exception.BookResourceNotFoundException;
//import com.project.mapper.BookMapper;
import com.project.models.Book;
import com.project.repositories.BookRepository;
import org.mockito.stubbing.OngoingStubbing;
import org.modelmapper.ModelMapper;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTestCase {
	@Mock
    private BookRepository bookRepository;

	@Mock
	private ModelMapper modelMapper;

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
	void tearDown() throws Exception {
		bookRepository=null;
		bookServiceImpl=null;
		book=null;
	}
    
	@Test
	void testGetAllBooks_positive() throws BookResourceNotFoundException {
		when(bookRepository.findAll()).thenReturn(Arrays.asList(book));
		//when(bookMapper.bookListToBookDTOList(any())).thenReturn(Arrays.asList(bookDTO));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookServiceImpl.getAllBooks();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));
        
	}
	
	@Test
	void testGetAllBooks_negative() throws BookResourceNotFoundException{
		assertThrows(BookResourceNotFoundException.class, ()->{
			bookServiceImpl.getAllBooks();
		});
	}

	@Test
	void testGetBookById_positive() throws BookResourceNotFoundException{
	    book.setBookID("B001");
	    bookDTO.setBookID("B001");
		when(bookRepository.findById(book.getBookID())).thenReturn(Optional.of(book));
		//when(bookMapper.bookToBookDTO(any())).thenReturn(bookDTO);
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

		BookDTO bookDTO=bookServiceImpl.getBookById("B001");

		assertNotNull(bookDTO);
		assertEquals("B001", bookDTO.getBookID());

	}

	@Test
	void testGetBooksById_negative() throws BookResourceNotFoundException {
		when(bookRepository.findById("B001")).thenReturn(Optional.empty());
		assertThrows(BookResourceNotFoundException.class, ()->{
			bookServiceImpl.getBookById("B001");
		});
	}

	@Test
	void testGetBooksByCategory_positive() throws BookResourceNotFoundException {
		when(bookRepository.getByCategory(any())).thenReturn(Arrays.asList(book));
		//when(bookMapper.bookListToBookDTOList(any())).thenReturn(Arrays.asList(bookDTO));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookServiceImpl.getBooksByCategory("Fantasy");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));

	}

	@Test
	void testGetBooksByCategory_negative() throws BookResourceNotFoundException {
		when(bookRepository.getByCategory("Fantasy")).thenReturn(Collections.emptyList());
		assertThrows(BookResourceNotFoundException.class, ()->{
			bookServiceImpl.getBooksByCategory("Fantasy");
		});
	}

	@Test
	void testGetBooksByAuthor_positive() throws BookResourceNotFoundException {
		when(bookRepository.getByAuthor("JK Rowling")).thenReturn(Arrays.asList(book));
		//when(bookMapper.bookListToBookDTOList(any())).thenReturn(Arrays.asList(bookDTO));
		when(modelMapper.map(book, BookDTO.class)).thenReturn(bookDTO);

        List<BookDTO> result = bookServiceImpl.getBooksByAuthor("JK Rowling");
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(bookDTO, result.get(0));

	}

	@Test
	void testGetBooksByAuthor_negative() throws BookResourceNotFoundException {
		when(bookRepository.getByAuthor("JK Rowling")).thenReturn(Collections.emptyList());
		assertThrows(BookResourceNotFoundException.class, ()->{
			bookServiceImpl.getBooksByAuthor("JK Rowling");
		});
	}

	@Test
	public void testFilter_NoCriteriaProvided_ShouldThrowException() {
		assertThrows(IllegalArgumentException.class, () -> {
			bookServiceImpl.filter();
		});
	}

	@Test
	public void testFilter_NoBooksFound_ShouldThrowException() {
		String criterion = "NonExistent";
		when(bookRepository.getByAuthor(criterion)).thenReturn(Collections.emptyList());
		when(bookRepository.getByCategory(criterion)).thenReturn(Collections.emptyList());

		assertThrows(BookResourceNotFoundException.class, () -> {
			bookServiceImpl.filter(criterion);
		});
	}
@Test
public void testFilter_ByAuthor_ShouldReturnBooks() throws BookResourceNotFoundException {
	String author = "Author1";
	List<Book> booksByAuthor = Arrays.asList(
			new Book("1", "Book1", 10.0, 1L, 1, 1),
			new Book("2", "Book2", 15.0, 2L, 1, 2)
	);
	when(bookRepository.getByAuthor(author)).thenReturn(booksByAuthor);
	when(bookRepository.getByCategory(anyString())).thenReturn(Collections.emptyList());
	when(modelMapper.map(any(Book.class), eq(BookDTO.class))).thenAnswer(invocation -> {
		Book book = invocation.getArgument(0);
		return new BookDTO(book.getBookID(), book.getTitle(), book.getPrice(), book.getInventoryID(), book.getAuthorID(), book.getCategoryID());
	});

	List<BookDTO> result = bookServiceImpl.filter(author);

	assertEquals(2, result.size());
	verify(bookRepository, times(1)).getByAuthor(author);
	verify(bookRepository, times(1)).getByCategory(author);
}

	@Test
	public void testFilter_ByCategory_ShouldReturnBooks() throws BookResourceNotFoundException {
		String category = "Category1";
		List<Book> booksByCategory = Arrays.asList(
				new Book("1", "Book1", 10.0, 1L, 1, 1),
				new Book("2", "Book2", 15.0, 2L, 2, 1)
		);
		when(bookRepository.getByCategory(category)).thenReturn(booksByCategory);
		when(bookRepository.getByAuthor(anyString())).thenReturn(Collections.emptyList());
		when(modelMapper.map(any(Book.class), eq(BookDTO.class))).thenAnswer(invocation -> {
			Book book = invocation.getArgument(0);
			return new BookDTO(book.getBookID(), book.getTitle(), book.getPrice(), book.getInventoryID(), book.getAuthorID(), book.getCategoryID());
		});

		List<BookDTO> result = bookServiceImpl.filter(category);

		assertEquals(2, result.size());
		verify(bookRepository, times(1)).getByAuthor(category);
		verify(bookRepository, times(1)).getByCategory(category);
	}

	@Test
	public void testFilter_ByAuthorAndCategory_ShouldReturnBooks() throws BookResourceNotFoundException {
		String author = "Author1";
		String category = "Category1";
		List<Book> booksByAuthor = Arrays.asList(
				new Book("1", "Book1", 10.0, 1L, 1, 1),
				new Book("2", "Book2", 15.0, 2L, 1, 2)
		);
		List<Book> booksByCategory = Arrays.asList(
				new Book("1", "Book1", 10.0, 1L, 1, 1)
		);
		when(bookRepository.getByAuthor(author)).thenReturn(booksByAuthor);
		when(bookRepository.getByCategory(category)).thenReturn(booksByCategory);
		when(modelMapper.map(any(Book.class), eq(BookDTO.class))).thenAnswer(invocation -> {
			Book book = invocation.getArgument(0);
			return new BookDTO(book.getBookID(), book.getTitle(), book.getPrice(), book.getInventoryID(), book.getAuthorID(), book.getCategoryID());
		});

		List<BookDTO> result = bookServiceImpl.filter(author, category);

		assertEquals(1, result.size());
		verify(bookRepository, times(1)).getByAuthor(author);
		verify(bookRepository, times(2)).getByCategory(category);
	}

//	@Test
//	void testAddBook_negative() throws BookResourceNotFoundException {
//		when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
//		when(bookRepository.save(book)).thenReturn(null);
//
//		boolean result = bookServiceImpl.addBook(bookDTO);
//
//		assertFalse(result);
//		verify(modelMapper, times(1)).map(bookDTO, Book.class);
//		verify(bookRepository, times(1)).save(book);
//	}
@Test
public void testAddBook_Positive() throws BookResourceNotFoundException{
	when(modelMapper.map(bookDTO, Book.class)).thenReturn(book);
	when(bookRepository.save(book)).thenReturn(book);

	boolean result = bookServiceImpl.addBook(bookDTO);

	assertTrue(result);
	verify(bookRepository, times(1)).save(book);
}

	@Test
	public void testAddBook_Negative() {
		assertThrows(BookResourceNotFoundException.class, ()->{
			bookServiceImpl.addBook(null);
		});
		//assertFalse(result);
		verify(bookRepository, never()).save(any(Book.class));
	}

	@Test
	void testDeleteBookById_positive() throws BookResourceNotFoundException {
		book.setBookID("B001");
		bookDTO.setBookID("B001");

		when(bookRepository.findById(any())).thenReturn(Optional.of(book));
		boolean result = bookServiceImpl.deleteBookById(book.getBookID());

		assertTrue(result);
		verify(bookRepository).findById(any());
		verify(bookRepository).deleteById(any());
	}

	@Test
	void testDeleteBookById_negative() {
		when(bookRepository.findById(any())).thenReturn(Optional.empty());

		assertThrows(BookResourceNotFoundException.class, () -> {
			bookServiceImpl.deleteBookById(any());
		});

		verify(bookRepository, times(1)).findById(any());
		verify(bookRepository, times(0)).deleteById(any());
	}

	@Test
	void testDeleteBookByTitle_positive() throws BookResourceNotFoundException {
		book.setTitle("Wimpy Kid");
		bookDTO.setTitle("Wimpy Kid");

		when(bookRepository.findByTitle(any())).thenReturn(Optional.of(book));

		boolean result = bookServiceImpl.deleteBookByTitle(book.getTitle());

		assertTrue(result);
		verify(bookRepository).findByTitle(any());
		verify(bookRepository).deleteByTitle(any());
	}

	@Test
	void testDeleteBookByTitle_negative() throws BookResourceNotFoundException {
		when(bookRepository.findByTitle(any())).thenReturn(Optional.empty());

		assertThrows(BookResourceNotFoundException.class, () -> {
			bookServiceImpl.deleteBookByTitle(any());
		});

		verify(bookRepository, times(1)).findByTitle(any());
		verify(bookRepository, times(0)).deleteByTitle(any());
	}

	@Test
	void testUpdateBookById_positive() throws BookResourceNotFoundException {
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setInventoryID(1);
		book.setTitle("Wimpy Kid");

		bookDTO.setBookID("B001");
		bookDTO.setTitle("Booknew");
		bookDTO.setAuthorID(123);
		bookDTO.setCategoryID(456);
		bookDTO.setInventoryID(789);
		bookDTO.setPrice(236.0);

		when(bookRepository.findById(any())).thenReturn(Optional.of(book));
		boolean updatedBook = bookServiceImpl.updateBookById(book.getBookID(), bookDTO);

		assertTrue(updatedBook);
		verify(bookRepository, times(1)).findById(any());
		verify(bookRepository, times(1)).save(any(Book.class));
	}

	@Test
	void testUpdateBookById_negative() throws BookResourceNotFoundException {
		when(bookRepository.findById(any())).thenReturn(Optional.empty());

		assertThrows(BookResourceNotFoundException.class, () -> {
			bookServiceImpl.updateBookById(null, bookDTO);
		});

		verify(bookRepository, times(1)).findById(any());
		verify(bookRepository, times(0)).updateBookById("B001", "Booknew", 123, 456, 789, 236);
	}
}
