import application.bookstore.Main;
import application.bookstore.models.*;
import application.bookstore.ui.DeleteAuthorDialog;
import application.bookstore.views.AuthorView;
import application.bookstore.views.View;
import javafx.application.Platform;
import javafx.scene.control.ButtonType;
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
public class TestAuthorUI {

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
        if (!Author.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
        if (!Book.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
    }

    @Test
    @Order(0)
    void testCreateValid() {
        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        Author author = new Author("test", "test");
        authorView.getFirstNameField().setText(author.getFirstName());
        authorView.getLastNameField().setText(author.getLastName());
        authorView.getSaveBtn().fire();
        Assertions.assertEquals("Author created successfully!" +
                "", authorView.getMessageLabel().getText());
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(author, Author.getAuthors().get(0));
        Assertions.assertEquals(1, authorView.getTableView().getItems().size());
        Assertions.assertEquals(author, authorView.getTableView().getItems().get(0));
    }

    @Test
    @Order(1)
    void testCreateInvalid() {
        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        Author author = new Author("", "test");
        authorView.getFirstNameField().setText(author.getFirstName());
        authorView.getLastNameField().setText(author.getLastName());
        authorView.getSaveBtn().fire();
        Assertions.assertEquals("Author creation failed!\n" +
                "First Name must contain only letters.", authorView.getMessageLabel().getText());
        Assertions.assertEquals(0, Author.getAuthors().size());
        Assertions.assertEquals(0, authorView.getTableView().getItems().size());
    }


    @Test
    @Order(2)
    void testCreateDuplicate() {
        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        Author author = new Author("test", "test");
        authorView.getFirstNameField().setText(author.getFirstName());
        authorView.getLastNameField().setText(author.getLastName());
        authorView.getSaveBtn().fire();

        authorView.getFirstNameField().setText(author.getFirstName());
        authorView.getLastNameField().setText(author.getLastName());
        authorView.getSaveBtn().fire();

        Assertions.assertEquals("Author creation failed!\n" +
                "Author with this Full Name exists.", authorView.getMessageLabel().getText());
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(1, authorView.getTableView().getItems().size());
        Assertions.assertEquals(author, authorView.getTableView().getItems().get(0));
    }


    @Test
    @Order(3)
    void testLoadData() {
        new Author("test", "test").saveInFile();
        new Author("testa", "testa").saveInFile();
        new Author("testb", "testb").saveInFile();
        List<Author> authors = Author.getAuthors();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        Assertions.assertEquals(authors, authorView.getTableView().getItems());
    }

    @Test
    @Order(4)
    void testSearch() {
        new Author("test", "test").saveInFile();
        new Author("asd", "ad").saveInFile();
        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getSearchView().getSearchField().setText("test");
        authorView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(Author.getSearchResults("test").stream().map(Author::getFullName).sorted().collect(Collectors.toList()), authorView.getTableView().getItems().stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(5)
    void testSearchClear() {
        new Author("test", "test").saveInFile();
        new Author("asd", "ad").saveInFile();
        List<Author> authors = Author.getAuthors();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getSearchView().getSearchField().setText("test");
        authorView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(Author.getSearchResults("test").stream().map(Author::getFullName).sorted().collect(Collectors.toList()), authorView.getTableView().getItems().stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        authorView.getSearchView().getClearBtn().fire();
        Assertions.assertEquals(authors.stream().map(Author::getFullName).sorted().collect(Collectors.toList()), authorView.getTableView().getItems().stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(6)
    void testDeleteAuthorsDialog() {
        Author author = new Author("test", "test");
        author.saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        authorView.getTableView().getSelectionModel().select(0);
        authorView.getDeleteBtn().fire();
        Assertions.assertTrue(authorView.getAuthorController().isDeleteAuthorDialogIsOpen());
    }

    @Test
    @Order(7)
    void testDeleteAuthorsOnly() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author author2 = new Author("testa", "testa");
        author2.saveInFile();
        new Book("1234567890111", "test", 5, 5, 5, author).saveInFile();
        new Book("1234567890112", "testa", 5, 5, 5, author).saveInFile();
        new Book("1234567890113", "testb", 5, 5, 5, author2).saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getTableView().getSelectionModel().select(0);
        authorView.deleteAuthors(false);

        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(author2, Author.getAuthors().get(0));
        Assertions.assertEquals(3, Book.getBooks().size());
    }

    @Test
    @Order(7)
    void testDeleteAuthorsAndBooks() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author author2 = new Author("testa", "testa");
        author2.saveInFile();
        new Book("1234567890111", "test", 5, 5, 5, author).saveInFile();
        new Book("1234567890112", "testa", 5, 5, 5, author).saveInFile();
        Book persistBook = new Book("1234567890113", "testb", 5, 5, 5, author2);
        persistBook.saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getTableView().getSelectionModel().select(0);
        authorView.deleteAuthors(true);

        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(author2, Author.getAuthors().get(0));
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(persistBook, Book.getBooks().get(0));
    }

    @Test
    @Order(8)
    void testDeleteAllAuthorsOnly() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author author2 = new Author("testa", "testa");
        author2.saveInFile();
        new Book("1234567890111", "test", 5, 5, 5, author).saveInFile();
        new Book("1234567890112", "testa", 5, 5, 5, author).saveInFile();
        new Book("1234567890113", "testb", 5, 5, 5, author2).saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getTableView().getSelectionModel().selectAll();
        authorView.deleteAuthors(false);

        Assertions.assertEquals(0, Author.getAuthors().size());
        Assertions.assertEquals(3, Book.getBooks().size());
    }

    @Test
    @Order(9)
    void testDeleteMultipleAuthorsOnly() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author author2 = new Author("testa", "testa");
        author2.saveInFile();
        Author author3 = new Author("testb", "testb");
        author3.saveInFile();
        Author author4 = new Author("testc", "testc");
        author4.saveInFile();
        new Book("1234567890111", "test", 5, 5, 5, author).saveInFile();
        new Book("1234567890112", "testa", 5, 5, 5, author).saveInFile();
        new Book("1234567890113", "testb", 5, 5, 5, author2).saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getTableView().getSelectionModel().selectRange(1, 3);
        authorView.deleteAuthors(false);

        Assertions.assertEquals(2, Author.getAuthors().size());
        Assertions.assertEquals(3, Book.getBooks().size());
    }

    @Test
    @Order(9)
    void testDeleteMultipleAuthorsAndBooks() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author author2 = new Author("testa", "testa");
        author2.saveInFile();
        Author author3 = new Author("testb", "testb");
        author3.saveInFile();
        Author author4 = new Author("testc", "testc");
        author4.saveInFile();
        Book persistBook = new Book("1234567890111", "test", 5, 5, 5, author);
        persistBook.saveInFile();
        new Book("1234567890112", "test1", 5, 5, 5, author2).saveInFile();
        new Book("1234567890113", "test2", 5, 5, 5, author3).saveInFile();

        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();

        authorView.getTableView().getSelectionModel().selectRange(1, 3);
        authorView.deleteAuthors(true);

        Assertions.assertEquals(2, Author.getAuthors().size());
        Assertions.assertEquals(1, Book.getBooks().size());
        Assertions.assertEquals(persistBook, Book.getBooks().get(0));
    }

//    @Test
//    @Order(7)
//    void testUpdate() {
//        Author author = new Author("test", "test");
//        author.saveInFile();
//
//        View.setCurrentUser(adminUser);
//        AuthorView authorView = new AuthorView();
//
//
//        authorView.getFirstNameCol().se
//        Author updated = author.clone();
//        updated.setFirstName("test?");
//        String res = updated.updateInFile(author);
//
//        Assertions.assertEquals("Author creation failed!\n" +
//                "Author with this Full Name exists.", authorView.getMessageLabel().getText());
//        Assertions.assertEquals(1, authorView.getTableView().getItems().size());
//        Assertions.assertEquals(author, authorView.getTableView().getItems().get(0));
//    }

//    @Test
//    @Order(8)
//    void testUpdateInvalid() {
//        Author author = new Author("test", "test");
//        author.saveInFile();
//        Author updated = author.clone();
//        updated.setFirstName("test?");
//        String res = updated.updateInFile(author);
//        Assertions.assertEquals("First Name must contain only letters.", res);
//        Assertions.assertEquals(1, Author.getAuthors().size());
//        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
//        Assertions.assertEquals(author, Author.getAuthors().get(0));
//        Assertions.assertEquals(author, Utilities.getData(Author.getDataFile()).get(0));
//    }
//
//    @Test
//    @Order(9)
//    void testUpdateSameId() {
//        Author author = new Author("test", "test");
//        author.saveInFile();
//        Author updated = author.clone();
//        updated.setFirstName("test");
//        updated.setLastName("test");
//        String res = updated.updateInFile(author);
//        Assertions.assertEquals("1", res);
//        Assertions.assertEquals(1, Author.getAuthors().size());
//        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
//        Assertions.assertEquals(updated, Author.getAuthors().get(0));
//        Assertions.assertEquals(updated, Utilities.getData(Author.getDataFile()).get(0));
//    }


}
