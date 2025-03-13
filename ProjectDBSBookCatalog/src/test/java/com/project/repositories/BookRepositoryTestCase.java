package com.project.repositories;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import com.project.models.Author;
import com.project.models.Book;
import com.project.models.Category;

@DataJpaTest
class BookRepositoryTestCase {
	
//	@Mock
//	private Book bookMock;
	
	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private TestEntityManager testEntityManager;
	
	
	@BeforeEach
	void setUp() throws Exception {
		MockitoAnnotations.openMocks(this);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * @Author Preethi
	 */
	@Test
	void testFindAll_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		
		testEntityManager.persist(book);
		
		Iterable<Book> bookList=bookRepository.findAll();
		assertTrue(bookList.iterator().hasNext());
	}
	
	@Test
	void testFindAll_negative() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		
		Iterable<Book> bookList=bookRepository.findAll();
		assertFalse(bookList.iterator().hasNext());

	}
	
	@Test
	void testFindById_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		testEntityManager.persist(book);
		
		Optional<Book> optionalOfBook=bookRepository.findById("B001");
		assertEquals(optionalOfBook.get(), book);
	}
	
	@Test
	void testFindById_negative() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		testEntityManager.persist(book);
		
		Optional<Book> optionalOfBook=bookRepository.findById("B002");
		assertFalse(optionalOfBook.isPresent());
	}
	
	@Test
	void testGetBookByCategory_positive() {
		//mock for Book
		
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		
		testEntityManager.persist(book);
		
		Category category=new Category();
		category.setCategoryID(1);
		category.setCategoryName("test");
		
		testEntityManager.persist(category);
		
		List<Book> bookList=bookRepository.getByCategory("test");
		assertTrue(!bookList.isEmpty());
	}
	@Test
	void testGetBookByCategory_negative() {
		//mock for Book
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		
		//testEntityManager.persist(book);
		
		Category category=new Category();
		category.setCategoryID(1);
		category.setCategoryName("test");
		
		//testEntityManager.persist(category);
		
		List<Book> bookList=bookRepository.getByCategory("test");
		assertTrue(bookList.isEmpty());
	}
	
	@Test
	void testGetBookByAuthor_positive() {
		//mock for Book
		
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setInventoryID(1);
		book.setTitle("test");
		
		testEntityManager.persist(book);
		
		Author author=new Author();
		author.setAuthorID(1);
		author.setAuthorName("test");
		
		testEntityManager.persist(author);
		
		List<Book> bookList=bookRepository.getByAuthor("test");
		assertTrue(!bookList.isEmpty());
	}
	
	@Test
	void testGetBookByAuthor_negative() {
		//mock for Book
		
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setInventoryID(1);
		book.setTitle("test");
		
		//testEntityManager.persist(book);
		
		Author author=new Author();
		author.setAuthorID(1);
		author.setAuthorName("test");
		
		//testEntityManager.persist(author);
		
		List<Book> bookList=bookRepository.getByAuthor("test");
		assertTrue(bookList.isEmpty());
	}
	
	
	
	
	/**
	 * @Author Suryanarayanan 
	 */
	@Test
	void testAddBook_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setInventoryID(1);
		book.setTitle("Wimpy Kid");
		
		testEntityManager.persist(book);
		Book savedBook= bookRepository.save(book);
		assertEquals(book.getBookID(), savedBook.getBookID());	
	}
	
	@Test
	void testAddBook_negative() {
		try {
			Book savedBook= bookRepository.save(null);
			assertTrue(false);	
		}catch(Exception e) {
			assertTrue(true);
		}
		
	}
	
	@Test
	void testDeleteBookById_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setInventoryID(1);
		book.setTitle("Wimpy Kid");
		testEntityManager.persist(book);
		
		bookRepository.deleteById("B001");
		Optional<Book> optionalOfBook=bookRepository.findById("B001");
		assertFalse(optionalOfBook.isPresent());
	}
	
	@Test
	void testDeleteBookById_negative() {
		try {
			bookRepository.deleteById(null);
			assertTrue(false);
		}catch(Exception e) {
			assertTrue(true);
		}
	}
	
	@Test
	void testDeleteBookByTitle_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setInventoryID(1);
		book.setTitle("Wimpy Kid");
		testEntityManager.persistAndFlush(book);
		bookRepository.deleteByTitle("Wimpy Kid");
		testEntityManager.clear();
		Optional<Book> optionalOfBook= bookRepository.findById("B001");
		assertFalse(optionalOfBook.isPresent());
	}
	
	@Test
	void testDeleteBookByTitle_negative() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setInventoryID(1);
		book.setTitle("Wimpy Kid");
		testEntityManager.persistAndFlush(book);
		bookRepository.deleteByTitle("Percy Jackson");
		testEntityManager.clear();
		Optional<Book> optionalOfBook= bookRepository.findById("B001");
		assertTrue(optionalOfBook.isPresent());
	}


//	@Test
//    void testUpdateBook_positive() {
//        Book book = new Book();
//        book.setBookID("B001");
//        book.setTitle("Original Title");
//        book.setAuthorID(123);
//        book.setCategoryID(456);
//        book.setInventoryID(1);
//        book.setPrice(300);
//        bookRepository.save(book);
//
//        Optional<Book> optionalBook = bookRepository.findById("B001");
//        assertTrue(optionalBook.isPresent());
//        Book bookToUpdate = optionalBook.get();
//        bookToUpdate.setTitle("Updated Title");
//        book.setAuthorID(123);
//        book.setCategoryID(456);
//        book.setInventoryID(1);
//        bookToUpdate.setPrice(450);
//        bookRepository.save(bookToUpdate);
//
//        Optional<Book> updatedBook = bookRepository.findById("B001");
//        assertTrue(updatedBook.isPresent());
//        assertEquals("Updated Title", updatedBook.get().getTitle());
//        assertEquals(123, updatedBook.get().getAuthorID());
//        assertEquals(456, updatedBook.get().getCategoryID());
//        assertEquals(10, updatedBook.get().getInventoryID());
//        assertEquals(450, updatedBook.get().getPrice());
//    }
		
}
