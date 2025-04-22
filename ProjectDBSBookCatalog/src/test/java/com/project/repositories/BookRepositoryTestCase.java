package com.project.repositories;

import com.project.models.Author;
import com.project.models.Book;
import com.project.models.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class BookRepositoryTestCase {


	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private TestEntityManager testEntityManager;

	@AfterEach
	void cleanup() {
		testEntityManager.clear();
	}


	/**
	 * @author Preethi
	 */
	@Test
	void testFindAll_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setTitle("test");

		testEntityManager.persist(book);

		Pageable pageable = PageRequest.of(0, 10);
		Page<Book> bookPage = bookRepository.findAll(pageable);
		assertTrue(bookPage.hasContent());
	}

	@Test
	void testFindAll_negative() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setTitle("test");

		Pageable pageable = PageRequest.of(0, 10);
		Page<Book> bookPage = bookRepository.findAll(pageable);
		assertFalse(bookPage.hasContent());

	}

	@Test
	void testFindById_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setTitle("test");
		testEntityManager.persist(book);

		Optional<Book> optionalOfBook=bookRepository.findById("B001");
		assertTrue(optionalOfBook.isPresent(), "Book should be present");
		assertEquals(optionalOfBook.get(), book);
	}

	@Test
	void testFindById_negative() {
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
		book.setTitle("test");
		testEntityManager.persist(book);

		Optional<Book> optionalOfBook=bookRepository.findById("B002");
		assertFalse(optionalOfBook.isPresent());
	}

	@Test
	void testGetBookByCategory_positive() {
		//mock for Book

		Category category=new Category();
//		category.setCategoryID(1);
		category.setCategoryName("test");

		Category persistedCategory = testEntityManager.persist(category);
		testEntityManager.flush();
		testEntityManager.clear();

		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(persistedCategory.getCategoryID());
		book.setPrice(500);
		book.setTitle("test");

		testEntityManager.persist(book);
		testEntityManager.flush();
		List<Book> bookList=bookRepository.getByCategory("test");
        assertFalse(bookList.isEmpty());
	}
	@Test
	void testGetBookByCategory_negative() {
		//mock for Book
		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
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
		book.setTitle("test");

		testEntityManager.persist(book);

		Author author=new Author();
		author.setAuthorID(1);
		author.setAuthorName("test");

		testEntityManager.persist(author);

		List<Book> bookList=bookRepository.getByAuthor("test");
        assertFalse(bookList.isEmpty());
	}

	@Test
	void testGetBookByAuthor_negative() {
		//mock for Book

		Book book=new Book();
		book.setBookID("B001");
		book.setAuthorID(1);
		book.setCategoryID(1);
		book.setPrice(500);
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
	 * @author Suryanarayanan
	 */
	@Test
	void testAddBook_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setTitle("Wimpy Kid");

		testEntityManager.persist(book);
		Book savedBook= bookRepository.save(book);
		assertEquals(book.getBookID(), savedBook.getBookID());
	}

	@Test
	void testAddBook_negative() {
		try {
			bookRepository.save(null);
            fail();
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
		book.setTitle("Wimpy Kid");
		testEntityManager.persist(book);

		bookRepository.deleteById("B001");
		Optional<Book> optionalOfBook=bookRepository.findById("B001");
		assertFalse(optionalOfBook.isPresent());
	}

	@Test
	void testDeleteBookById_negative() {
		assertThrows(InvalidDataAccessApiUsageException.class, () -> bookRepository.deleteById(null));
	}

	@Test
	void testDeleteBookByTitle_positive() {
		Book book=new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
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
		book.setTitle("Wimpy Kid");
		testEntityManager.persistAndFlush(book);
		bookRepository.deleteByTitle("Percy Jackson");
		testEntityManager.clear();
		Optional<Book> optionalOfBook= bookRepository.findById("B001");
		assertTrue(optionalOfBook.isPresent());
	}


	@Test
    void testUpdateBook_positive() {
        Book book = new Book();
        book.setBookID("B001");
        book.setTitle("Original Title");
        book.setAuthorID(123);
        book.setCategoryID(456);
        book.setPrice(300);
        bookRepository.save(book);

        Optional<Book> optionalBook = bookRepository.findById("B001");
        assertTrue(optionalBook.isPresent());
        Book bookToUpdate = optionalBook.get();
        bookToUpdate.setTitle("Updated Title");
        book.setAuthorID(123);
        book.setCategoryID(456);
        bookToUpdate.setPrice(450);
        bookRepository.save(bookToUpdate);

        Optional<Book> updatedBook = bookRepository.findById("B001");
        assertTrue(updatedBook.isPresent());
        assertEquals("Updated Title", updatedBook.get().getTitle());
        assertEquals(123, updatedBook.get().getAuthorID());
        assertEquals(456, updatedBook.get().getCategoryID());
        assertEquals(450, updatedBook.get().getPrice());
    }

	@Test
	void testFindByTitle_positive() {
		Book book = new Book();
		book.setBookID("B001");
		book.setPrice(450);
		book.setAuthorID(123);
		book.setCategoryID(456);
		book.setTitle("Wimpy Kid");

		testEntityManager.persistAndFlush(book);

		Optional<Book> result = bookRepository.findByTitle("Wimpy Kid");

		assertTrue(result.isPresent());
		assertEquals("Wimpy Kid", result.get().getTitle());
	}

	@Test
	void FindByTitle_negative() {
			Optional<Book> result = bookRepository.findByTitle("Nonexistent Title");
			assertFalse(result.isPresent());
		}

	@Test
	void testFindAuthorNameByBookTitle_positive() {
		Author author = new Author();
		author.setAuthorID(1);
		author.setAuthorName("John Doe");
		testEntityManager.persist(author);

		Book book = new Book();
		book.setBookID("B002");
		book.setTitle("The Great Novel");
		book.setAuthorID(1);
		testEntityManager.persist(book);

		Optional<String> authorName = bookRepository.findAuthorNameByBookTitle("The Great Novel");
		assertTrue(authorName.isPresent());
		assertEquals("John Doe", authorName.get());
	}

	@Test
	void testFindAuthorNameByBookTitle_negative() {
		Optional<String> authorName = bookRepository.findAuthorNameByBookTitle("Nonexistent Book");
		assertFalse(authorName.isPresent());
	}

	@Test
	void testFindCategoryNameByBookTitle_positive() {
		Category category = new Category();
		category.setCategoryName("Fiction");
		Category persistedCategory = testEntityManager.persist(category);
		testEntityManager.flush();
		testEntityManager.clear();
//		System.out.println("categoryID:"+category.getCategoryID());

		Book book = new Book();
		book.setBookID("B003");
		book.setTitle("Fiction Book");
		book.setCategoryID(persistedCategory.getCategoryID());
		testEntityManager.persist(book);

		Optional<String> categoryName = bookRepository.findCategoryNameByBookTitle("Fiction Book");
		assertTrue(categoryName.isPresent());
		assertEquals("Fiction", categoryName.get());
	}

	@Test
	void testFindCategoryNameByBookTitle_negative() {
		Optional<String> categoryName = bookRepository.findCategoryNameByBookTitle("Nonexistent Book");
		assertFalse(categoryName.isPresent());
	}

	@Test
	void testFindBooksByTitleContaining_positive() {
		Book book1 = new Book();
		book1.setBookID("B004");
		book1.setTitle("The Great Escape");
		testEntityManager.persist(book1);

		Book book2 = new Book();
		book2.setBookID("B005");
		book2.setTitle("Escape Plan");
		testEntityManager.persist(book2);

		List<Book> booksContaining = bookRepository.findBooksByTitleContaining("Escape");
		assertEquals(2, booksContaining.size());
		assertTrue(booksContaining.stream().anyMatch(book -> book.getTitle().equals("The Great Escape")));
		assertTrue(booksContaining.stream().anyMatch(book -> book.getTitle().equals("Escape Plan")));
	}

	@Test
	void testFindBooksByTitleContaining_negative() {
		List<Book> booksContaining = bookRepository.findBooksByTitleContaining("Nonexistent");
		assertTrue(booksContaining.isEmpty());
	}

	@Test
	void testFindByAuthorContaining_positive() {
		Author author1 = new Author();
		author1.setAuthorID(2);
		author1.setAuthorName("Jane Austen");
		testEntityManager.persist(author1);

		Book book1 = new Book();
		book1.setBookID("B006");
		book1.setAuthorID(2);
		book1.setTitle("Pride");
		testEntityManager.persist(book1);

		Author author2 = new Author();
		author2.setAuthorID(3);
		author2.setAuthorName("Agatha Christie");
		testEntityManager.persist(author2);

		Book book2 = new Book();
		book2.setBookID("B007");
		book2.setAuthorID(3);
		book2.setTitle("Murder");
		testEntityManager.persist(book2);

		List<Book> booksByAuthor = bookRepository.findByAuthorContaining("Christie");
		assertEquals(1, booksByAuthor.size());
		assertEquals("Murder", booksByAuthor.get(0).getTitle());
	}

	@Test
	void testFindByAuthorContaining_negative() {
		List<Book> booksByAuthor = bookRepository.findByAuthorContaining("Nonexistent");
		assertTrue(booksByAuthor.isEmpty());
	}

	@Test
	void testFindByCategoryContaining_positive() {
		Category category1 = new Category();
		category1.setCategoryName("Science Fiction");
		testEntityManager.persist(category1);

		Book book1 = new Book();
		book1.setBookID("B008");
		book1.setCategoryID(1);
		book1.setTitle("Space");
		testEntityManager.persist(book1);

		Category category2 = new Category();
		category2.setCategoryName("Fantasy");
		testEntityManager.persist(category2);

		Book book2 = new Book();
		book2.setBookID("B009");
		book2.setCategoryID(2);
		book2.setTitle("Magic");
		testEntityManager.persist(book2);

		List<Book> booksByCategory = bookRepository.findByCategoryContaining("Sci");
		assertEquals(1, booksByCategory.size());
		assertEquals("Space", booksByCategory.get(0).getTitle());
	}

	@Test
	void testFindByCategoryContaining_negative() {
		List<Book> booksByCategory = bookRepository.findByCategoryContaining("Nonexistent");
		assertTrue(booksByCategory.isEmpty());
	}

	@Test
	void testFindAuthorNameById_positive() {
		Author author = new Author();
		author.setAuthorID(4);
		author.setAuthorName("Stephen King");
		testEntityManager.persist(author);

		Optional<String> authorName = bookRepository.findAuthorNameById(4);
		assertTrue(authorName.isPresent());
		assertEquals("Stephen King", authorName.get());
	}

	@Test
	void testFindAuthorNameById_negative() {
		Optional<String> authorName = bookRepository.findAuthorNameById(999);
		assertFalse(authorName.isPresent());
	}

	@Test
	void testFindCategoryNameById_positive() {
		Category category = new Category();
		category.setCategoryName("Thriller");
		Category persistedCategory=testEntityManager.persist(category);
		testEntityManager.flush();
		testEntityManager.clear();

		Optional<String> categoryName = bookRepository.findCategoryNameById(persistedCategory.getCategoryID());
		assertTrue(categoryName.isPresent());
		assertEquals("Thriller", categoryName.get());
	}

	@Test
	void testFindCategoryNameById_negative() {
		Optional<String> categoryName = bookRepository.findCategoryNameById(999);
		assertFalse(categoryName.isPresent());
	}

	@Test
	void testFindAuthorIDByName_positive() {
		Author author = new Author();
		author.setAuthorID(5);
		author.setAuthorName("George Orwell");
		testEntityManager.persist(author);

		Integer authorId = bookRepository.findAuthorIDByName("George Orwell");
		assertEquals(5, authorId);
	}

	@Test
	void testFindAuthorIDByName_negative() {
		Integer authorId = bookRepository.findAuthorIDByName("Nonexistent Author");
		assertNull(authorId);
	}

	@Test
	void testFindCategoryIDByName_positive() {
		Category category = new Category();
		category.setCategoryName("Dystopian");
		Category persistedCategory = testEntityManager.persist(category);
		testEntityManager.flush();
		testEntityManager.clear();

		Integer categoryId = bookRepository.findCategoryIDByName("Dystopian");
		assertEquals(persistedCategory.getCategoryID(), categoryId);
	}

	@Test
	void testFindCategoryIDByName_negative() {
		Integer categoryId = bookRepository.findCategoryIDByName("Nonexistent Category");
		assertNull(categoryId);
	}

//	@Test
//	void testInsertNewAuthor_positive() {
//		int initialAuthorCount = testEntityManager.getEntityManager().createQuery("SELECT a FROM Author a", Author.class).getResultList().size();
//		int rowsAffected = bookRepository.insertNewAuthor("New Author");
//		assertEquals(1, rowsAffected);
//		int finalAuthorCount = testEntityManager.getEntityManager().createQuery("SELECT a FROM Author a", Author.class).getResultList().size();
//		assertEquals(initialAuthorCount + 1, finalAuthorCount);
//		Optional<Author> newAuthor = testEntityManager.getEntityManager()
//				.createQuery("SELECT a FROM Author a WHERE a.authorName = 'New Author'", Author.class)
//				.getResultList().stream().findFirst();
//		assertTrue(newAuthor.isPresent());
//		assertEquals("New Author", newAuthor.get().getAuthorName());
//	}

	@Test
	void testInsertNewAuthor_negative_duplicateName() {
		Author existingAuthor = new Author();
		existingAuthor.setAuthorName("Existing Author");
		testEntityManager.persist(existingAuthor);
		assertThrows(DataIntegrityViolationException.class, () -> bookRepository.insertNewAuthor("Existing Author"));
	}

	@Test
	void testInsertNewCategory_positive() {
		int initialCategoryCount = testEntityManager.getEntityManager().createQuery("SELECT c FROM Category c", Category.class).getResultList().size();
		int rowsAffected = bookRepository.insertNewCategory("New Category");
		assertEquals(1, rowsAffected);
		int finalCategoryCount = testEntityManager.getEntityManager().createQuery("SELECT c FROM Category c", Category.class).getResultList().size();
		assertEquals(initialCategoryCount + 1, finalCategoryCount);
		Optional<Category> newCategory = testEntityManager.getEntityManager()
				.createQuery("SELECT c FROM Category c WHERE c.categoryName = 'New Category'", Category.class)
				.getResultList().stream().findFirst();
		assertTrue(newCategory.isPresent());
		assertEquals("New Category", newCategory.get().getCategoryName());
	}

//	@Test
//	void testInsertNewCategory_negative_duplicateName() {
//		Category existingCategory = new Category();
//		existingCategory.setCategoryName("Existing Category");
//		testEntityManager.persist(existingCategory);
//		assertThrows(DataIntegrityViolationException.class, () -> bookRepository.insertNewCategory("Existing Category"));
//	}

	@Test
	void testFindDistinctAuthors_positive() {
		Author author1 = new Author();
		author1.setAuthorName("Author A");
		author1.setAuthorID(1);
		testEntityManager.persist(author1);
		testEntityManager.flush();
		testEntityManager.clear();

		Author author2 = new Author();
		author2.setAuthorName("Author B");
		author1.setAuthorID(2);
		testEntityManager.persist(author2);
		testEntityManager.flush();
		testEntityManager.clear();

		Book book1 = new Book();
		book1.setBookID("B001");
		book1.setTitle("Book 1");
		book1.setAuthorID(author1.getAuthorID()); // Use the generated ID
		testEntityManager.persist(book1);

		Book book2 = new Book();
		book2.setBookID("B002");
		book2.setTitle("Book 2");
		book2.setAuthorID(author1.getAuthorID()); // Use the generated ID
		testEntityManager.persist(book2);

		Book book3 = new Book();
		book3.setBookID("B003");
		book3.setTitle("Book 3");
		book3.setAuthorID(author2.getAuthorID()); // Use the generated ID
		testEntityManager.persist(book3);

		List<String> distinctAuthors = bookRepository.findDistinctAuthors();
		assertEquals(2, distinctAuthors.size());
		assertTrue(distinctAuthors.contains("Author A"));
		assertTrue(distinctAuthors.contains("Author B"));
	}

	@Test
	void testFindDistinctAuthors_empty() {
		List<String> distinctAuthors = bookRepository.findDistinctAuthors();
		assertTrue(distinctAuthors.isEmpty());
	}

	@Test
	void testFindDistinctCategories_positive() {
		Category category1 = new Category();
		category1.setCategoryName("Category X");
		testEntityManager.persist(category1);
		testEntityManager.flush();
		testEntityManager.clear();

		Category category2 = new Category();
		category2.setCategoryName("Category Y");
		testEntityManager.persist(category2);
		testEntityManager.flush();
		testEntityManager.clear();

		Book book1 = new Book();
		book1.setBookID("B001");
		book1.setTitle("Book 1");
		book1.setCategoryID(category1.getCategoryID());
		testEntityManager.persist(book1);

		Book book2 = new Book();
		book2.setBookID("B002");
		book2.setTitle("Book 2");
		book2.setCategoryID(category1.getCategoryID());
		testEntityManager.persist(book2);

		Book book3 = new Book();
		book3.setBookID("B003");
		book3.setTitle("Book 3");
		book3.setCategoryID(category2.getCategoryID());
		testEntityManager.persist(book3);

		List<String> distinctCategories = bookRepository.findDistinctCategories();
		assertEquals(2, distinctCategories.size());
		assertTrue(distinctCategories.contains("Category X"));
		assertTrue(distinctCategories.contains("Category Y"));
	}

	@Test
	void testFindDistinctCategories_empty() {
		List<String> distinctCategories = bookRepository.findDistinctCategories();
		assertTrue(distinctCategories.isEmpty());
	}

}
