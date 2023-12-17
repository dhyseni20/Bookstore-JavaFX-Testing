import application.bookstore.models.Author;
import application.bookstore.models.BaseModel;
import application.bookstore.models.Book;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBook {

    private static final Author validAuthor = new Author("author", "author");

    @BeforeAll
    static void setUp() {
        BaseModel.FOLDER_PATH = "testing_data_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        if (!new File(BaseModel.FOLDER_PATH).mkdirs())
            throw new RuntimeException("Could not create test data folder! Test SetUp failed!");
    }

    @AfterAll
    static void cleanUp() {
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
    }

    @BeforeEach
    void beforeEach() {
        if (!Book.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
    }

    @Test
    @Order(0)
    void testIsValid() {
        Assertions.assertEquals("1", new Book("1234567890111", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book(" ", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("      ", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("      asd", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("      1", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("asd", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("123456789011", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345678901a", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345678901aa", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("123456789011a", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345678901 11", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("1234568901 11", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("123456-890111", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("123456-8901111", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("123456-89011", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345689011?", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345689011*", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("12345689011/", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", new Book("a12345689011", "test", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", new Book("1234567890111", "", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", new Book("1234567890111", " ", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", new Book("1234567890111", "   ", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "12", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "asd_", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "asd asd", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "asd asd_", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "asdËë", 5, 5, 5, validAuthor).isValid());

        Assertions.assertEquals("1", new Book("1234567890111", "asd'", 5, 5, 5, validAuthor).isValid());
        // this one was failing, change [a-zA-Z0-9_'] to [\p{L}\p{N}_] to consider unicode chars

        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", new Book("1234567890111", "aaaaaaaaaaabbbbbbbbbbcccccccccca", 5, 5, 5, validAuthor).isValid());
        // this one was failing, changed ([\p{L}\p{N}_']{1,30}\s*)+ to ([\p{L}\p{N}_']\s*){1,30}

        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", new Book("1234567890111", " 123456789012345678901234567890", 5, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("Quantity cannot be negative.", new Book("1234567890111", "test", -1, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "test", 0, 5, 5, validAuthor).isValid());
        Assertions.assertEquals("Purchased Price cannot be negative.", new Book("1234567890111", "test", 5, -1, 5, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "test", 5, 0, 5, validAuthor).isValid());
        Assertions.assertEquals("Selling Price cannot be negative.", new Book("1234567890111", "test", 5, 5, -1, validAuthor).isValid());
        Assertions.assertEquals("1", new Book("1234567890111", "test", 5, 5, 0, validAuthor).isValid());
    }

    @Test
    @Order(1)
    void testSave() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        String res = book.saveInFile();
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(book, Book.getBooks().get(0));
        Assertions.assertEquals(1, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals(book, Utilities.getData(Book.getDataFile()).get(0));
    }

    @Test
    @Order(2)
    void testSaveInvalid() {
        Book book = new Book("", "test", 5, 5, 5, validAuthor);
        String res = book.saveInFile();
        Assertions.assertEquals("ISBN must contain exactly 13 digits with no spaces/dashes.", res);
        Assertions.assertEquals(0, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals(0, Book.getBooks().size());
    }


    @Test
    @Order(3)
    void testSaveDuplicate() {
        new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile();
        Assertions.assertEquals("Book with this ISBN exists.", new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile());
        Assertions.assertEquals("1", new Book("1234567890112", "test", 5, 5, 5, validAuthor).saveInFile());
        Assertions.assertEquals("Book with this ISBN exists.", new Book("1234567890111", "test123", 5, 5, 5, validAuthor).saveInFile());
        Assertions.assertEquals(2, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals(2, Book.getBooks().size());
    }


    @Test
    @Order(4)
    void testGet() {
        List<Book> books = Book.getBooks();
        Assertions.assertEquals(0, books.size());
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();
        books = Book.getBooks();
        Assertions.assertTrue(books.contains(book));
    }

    @Test
    @Order(5)
    void testDelete() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();
        Assertions.assertEquals("1", book.deleteFromFile());
        Assertions.assertEquals(0, Book.getBooks().size());
        Assertions.assertEquals(0, Utilities.getData(Book.getDataFile()).size());
    }

    @Test
    @Order(6)
    void testDeleteNonexistent() {
        Book book1 = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        Book book2 = new Book("1234567890112", "test", 5, 5, 5, validAuthor);
        book2.saveInFile();
        Assertions.assertEquals("1", book1.deleteFromFile());
        Assertions.assertEquals(1, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertTrue(Book.getBooks().contains(book2));
        Assertions.assertFalse(Book.getBooks().contains(book1));
    }

    @Test
    @Order(7)
    void testUpdate() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();
        Book updated = book.clone();
        updated.setTitle("test" + "_");
        updated.updateInFile(book);
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(1, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals("test_", Book.getBooks().get(0).getTitle());
        Assertions.assertEquals("test_", ((Book) Utilities.getData(Book.getDataFile()).get(0)).getTitle());
    }

    @Test
    @Order(8)
    void testUpdateInvalid() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();
        Book updated = book.clone();
        updated.setTitle("test?");
        String res = updated.updateInFile(book);
        Assertions.assertEquals("Title must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", res);
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(1, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals(book, Book.getBooks().get(0));
        Assertions.assertEquals(book, Utilities.getData(Book.getDataFile()).get(0));
    }

    @Test
    @Order(9)
    void testUpdateSameId() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();
        Book updated = book.clone();
        updated.setIsbn("1234567890111");
        updated.setTitle("123");
        String res = updated.updateInFile(book);
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(1, Utilities.getData(Book.getDataFile()).size());
        Assertions.assertEquals(updated, Book.getBooks().get(0));
        Assertions.assertEquals(updated, Utilities.getData(Book.getDataFile()).get(0));
    }

    @Test
    @Order(10)
    void testSearch() {
        new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890112", "Test", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890113", "asd", 5, 5, 5, new Author("epoka", "epoka")).saveInFile();
        new Book("1234567890115", "epoka", 5, 5, 5, new Author("asd", "asd")).saveInFile();
        new Book("1234567890114", "temp", 5, 5, 5, validAuthor).saveInFile();
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112").sorted().collect(Collectors.toList()), Book.getSearchResults("test").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112").sorted().collect(Collectors.toList()), Book.getSearchResults("TEst").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112").sorted().collect(Collectors.toList()), Book.getSearchResults("TEst").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890113").collect(Collectors.toList()), Book.getSearchResults("epoka epoka").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890112").collect(Collectors.toList()), Book.getSearchResults("1234567890112").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112", "1234567890114").sorted().collect(Collectors.toList()), Book.getSearchResults("TE").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112", "1234567890113", "1234567890114", "1234567890115").sorted().collect(Collectors.toList()), Book.getSearchResults("").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112", "1234567890113", "1234567890114", "1234567890115").sorted().collect(Collectors.toList()), Book.getSearchResults("123456789011").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("1234567890111", "1234567890112", "1234567890113", "1234567890114", "1234567890115").sorted().collect(Collectors.toList()), Book.getSearchResults("23456789011").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(List.of(), Book.getSearchResults("TEST1").stream().map(Book::getIsbn).collect(Collectors.toList()));
    }

}
