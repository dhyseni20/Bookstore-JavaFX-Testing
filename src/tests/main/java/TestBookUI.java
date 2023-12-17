import application.bookstore.Main;
import application.bookstore.models.*;
import application.bookstore.views.BookView;
import application.bookstore.views.UsersView;
import application.bookstore.views.View;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestBookUI {
    private static final Author validAuthor = new Author("author", "author");
    static User adminUser;

    @BeforeAll
    static void setUp() {
        BaseModel.FOLDER_PATH = "testing_data_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        if (!new File(BaseModel.FOLDER_PATH).mkdirs())
            throw new RuntimeException("Could not create test data folder! Test SetUp failed!");

        Main.launchTest();

        adminUser = new User("admin", "admin", Role.ADMIN);
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
    void testCreateValid() {
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Book book =  new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        bookView.getIsbnField().setText(book.getIsbn());
        bookView.getQuantityField().setText(String.valueOf(book.getQuantity()));
        bookView.getTitleField().setText(book.getTitle());
        bookView.getPurchasedPriceField().setText(String.valueOf(book.getPurchasedPrice()));
        bookView.getSellingPriceField().setText(String.valueOf(book.getSellingPrice()));
        bookView.getSaveBtn().fire();
        Assertions.assertEquals("Book created successfully!", bookView.getMessageLabel().getText());
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(book, Book.getBooks().get(0));
        Assertions.assertEquals(1, bookView.getTableView().getItems().size());
        Assertions.assertEquals(book, bookView.getTableView().getItems().get(0));
    }

    @Test
    @Order(1)
    void testCreateInvalid() {
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Book book =  new Book("asd", "test", 5, 5, 5, validAuthor);
        bookView.getIsbnField().setText(book.getIsbn());
        bookView.getQuantityField().setText(String.valueOf(book.getQuantity()));
        bookView.getTitleField().setText(book.getTitle());
        bookView.getPurchasedPriceField().setText(String.valueOf(book.getPurchasedPrice()));
        bookView.getSellingPriceField().setText(String.valueOf(book.getSellingPrice()));
        bookView.getSaveBtn().fire();
        Assertions.assertEquals("Book creation failed!\n" +
                "ISBN must contain exactly 13 digits with no spaces/dashes.", bookView.getMessageLabel().getText());
        Assertions.assertEquals(0, Book.getBooks().size());
        Assertions.assertEquals(0, bookView.getTableView().getItems().size());
    }

    @Test
    @Order(2)
    void testCreateInvalidEmptyField() {
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Book book =  new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        bookView.getQuantityField().setText(String.valueOf(book.getQuantity()));
        bookView.getTitleField().setText(book.getTitle());
        bookView.getPurchasedPriceField().setText(String.valueOf(book.getPurchasedPrice()));
        bookView.getSellingPriceField().setText(String.valueOf(book.getSellingPrice()));
        bookView.getSaveBtn().fire();
        Assertions.assertEquals("Book creation failed!\n" +
                "ISBN must contain exactly 13 digits with no spaces/dashes.", bookView.getMessageLabel().getText());
        Assertions.assertEquals(0, Book.getBooks().size());
        Assertions.assertEquals(0, bookView.getTableView().getItems().size());
    }


    @Test
    @Order(3)
    void testCreateDuplicate() {
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Book book =  new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        bookView.getIsbnField().setText(book.getIsbn());
        bookView.getQuantityField().setText(String.valueOf(book.getQuantity()));
        bookView.getTitleField().setText(book.getTitle());
        bookView.getPurchasedPriceField().setText(String.valueOf(book.getPurchasedPrice()));
        bookView.getSellingPriceField().setText(String.valueOf(book.getSellingPrice()));
        bookView.getSaveBtn().fire();

        bookView.getIsbnField().setText(book.getIsbn());
        bookView.getQuantityField().setText(String.valueOf(book.getQuantity()));
        bookView.getTitleField().setText(book.getTitle());
        bookView.getPurchasedPriceField().setText(String.valueOf(book.getPurchasedPrice()));
        bookView.getSellingPriceField().setText(String.valueOf(book.getSellingPrice()));
        bookView.getSaveBtn().fire();

        Assertions.assertEquals("Book creation failed!\n" +
                "Book with this ISBN exists.", bookView.getMessageLabel().getText());
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(1, bookView.getTableView().getItems().size());
        Assertions.assertEquals(book, bookView.getTableView().getItems().get(0));
    }


    @Test
    @Order(4)
    void testLoadData() {
        new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890112", "test1", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890113", "test2", 5, 5, 5, validAuthor).saveInFile();
        List<Book> books = Book.getBooks();

        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Assertions.assertEquals(books, bookView.getTableView().getItems());
    }

    @Test
    @Order(5)
    void testSearch() {
        new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890112", "asd", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890113", "ast", 5, 5, 5, validAuthor).saveInFile();
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();

        bookView.getSearchView().getSearchField().setText("test");
        bookView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(Book.getSearchResults("test").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()), bookView.getTableView().getItems().stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(6)
    void testSearchClear() {
        new Book("1234567890111", "test", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890112", "asd", 5, 5, 5, validAuthor).saveInFile();
        new Book("1234567890113", "ast", 5, 5, 5, validAuthor).saveInFile();
        List<Book> books = Book.getBooks();

        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();

        bookView.getSearchView().getSearchField().setText("test");
        bookView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(Book.getSearchResults("test").stream().map(Book::getIsbn).sorted().collect(Collectors.toList()), bookView.getTableView().getItems().stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
        bookView.getSearchView().getClearBtn().fire();
        Assertions.assertEquals(books.stream().map(Book::getIsbn).sorted().collect(Collectors.toList()), bookView.getTableView().getItems().stream().map(Book::getIsbn).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(6)
    void testDelete() {
        Book book = new Book("1234567890111", "test", 5, 5, 5, validAuthor);
        book.saveInFile();

        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        bookView.getTableView().getSelectionModel().select(0);
        bookView.getDeleteBtn().fire();
        Assertions.assertEquals("Book removed successfully", bookView.getMessageLabel().getText());
        Assertions.assertEquals(0, Book.getBooks().size());
        Assertions.assertEquals(0, bookView.getTableView().getItems().size());
    }

}
