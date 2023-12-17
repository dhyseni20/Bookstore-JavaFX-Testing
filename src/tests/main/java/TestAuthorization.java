import application.bookstore.Main;
import application.bookstore.models.BaseModel;
import application.bookstore.models.Role;
import application.bookstore.models.User;
import application.bookstore.views.AuthorView;
import application.bookstore.views.BookView;
import application.bookstore.views.MainView;
import application.bookstore.views.View;
import javafx.scene.layout.BorderPane;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;


@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAuthorization {

    static User adminUser;
    static User managerUser;
    static User librarianUser;

    @BeforeAll
    static void setUp() {
        BaseModel.FOLDER_PATH = "testing_data_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        if (!new File(BaseModel.FOLDER_PATH).mkdirs())
            throw new RuntimeException("Could not create test data folder! Test SetUp failed!");

        Main.launchTest();

        adminUser = new User("admin", "admin", Role.ADMIN);
        managerUser = new User("manager", "manager", Role.MANAGER);
        librarianUser = new User("librarian", "librarian", Role.LIBRARIAN);
    }

    @AfterAll
    static void cleanUp() {
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
    }

    @BeforeEach
    void beforeEach() {
        if (!User.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
        View.setCurrentUser(null);
    }

    @Test
    void testUserPermissionsAccessibleMenusLibrarian() {
        View.setCurrentUser(librarianUser);
        MainView mainView = new MainView(Main.getStage());
        mainView.getView();
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getBooksMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getSalesMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getControlMenu()));

        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewBooks()));
        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewAuthors()));

        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemNewOrder()));
        Assertions.assertFalse(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemViewSales()));
        Assertions.assertFalse(mainView.getSalesMenu().getItems().contains(mainView.getStatsMenu()));

        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemProfile()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemChangePassword()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemLogout()));
        Assertions.assertFalse(mainView.getControlMenu().getItems().contains(mainView.getManageUsers()));
        Assertions.assertFalse(mainView.getControlMenu().getItems().contains(mainView.getMenuItemSettings()));
    }

    @Test
    void testUserPermissionsAccessibleMenusManager() {
        View.setCurrentUser(managerUser);
        MainView mainView = new MainView(Main.getStage());
        mainView.getView();
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getBooksMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getSalesMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getControlMenu()));

        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewBooks()));
        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewAuthors()));

        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemNewOrder()));
        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemViewSales()));
        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getStatsMenu()));

        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemProfile()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemChangePassword()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemLogout()));
        Assertions.assertFalse(mainView.getControlMenu().getItems().contains(mainView.getManageUsers()));
        Assertions.assertFalse(mainView.getControlMenu().getItems().contains(mainView.getMenuItemSettings()));
    }

    @Test
    void testUserPermissionsAccessibleMenusAdmin() {
        View.setCurrentUser(adminUser);
        MainView mainView = new MainView(Main.getStage());
        mainView.getView();
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getBooksMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getSalesMenu()));
        Assertions.assertTrue(mainView.getMenuBar().getMenus().contains(mainView.getControlMenu()));

        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewBooks()));
        Assertions.assertTrue(mainView.getBooksMenu().getItems().contains(mainView.getMenuItemViewAuthors()));

        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemNewOrder()));
        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getMenuItemViewSales()));
        Assertions.assertTrue(mainView.getSalesMenu().getItems().contains(mainView.getStatsMenu()));

        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemProfile()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemChangePassword()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemLogout()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getManageUsers()));
        Assertions.assertTrue(mainView.getControlMenu().getItems().contains(mainView.getMenuItemSettings()));
    }

    @Test
    void testUserPermissionsAccessibleAuthor() {
        View.setCurrentUser(adminUser);
        AuthorView authorView = new AuthorView();
        Assertions.assertTrue(authorView.getTableView().isEditable());
        Assertions.assertNotNull(((BorderPane) authorView.getView()).getBottom());
        Assertions.assertNotNull(authorView.getDeleteBtn().getOnAction());
        Assertions.assertNotNull(authorView.getSaveBtn().getOnAction());

        View.setCurrentUser(managerUser);
        authorView = new AuthorView();
        Assertions.assertTrue(authorView.getTableView().isEditable());
        Assertions.assertNotNull(((BorderPane) authorView.getView()).getBottom());
        Assertions.assertNotNull(authorView.getDeleteBtn().getOnAction());
        Assertions.assertNotNull(authorView.getSaveBtn().getOnAction());

        View.setCurrentUser(librarianUser);
        authorView = new AuthorView();
        Assertions.assertFalse(authorView.getTableView().isEditable());
        Assertions.assertNull(((BorderPane) authorView.getView()).getBottom());
        Assertions.assertNull(authorView.getDeleteBtn().getOnAction());
        Assertions.assertNull(authorView.getSaveBtn().getOnAction());
    }

    @Test
    void testUserPermissionsAccessibleBook() {
        View.setCurrentUser(adminUser);
        BookView bookView = new BookView();
        Assertions.assertTrue(bookView.getTableView().isEditable());
        Assertions.assertNotNull(((BorderPane) bookView.getView()).getBottom());
        Assertions.assertNotNull(bookView.getDeleteBtn().getOnAction());
        Assertions.assertNotNull(bookView.getSaveBtn().getOnAction());

        View.setCurrentUser(managerUser);
        bookView = new BookView();
        Assertions.assertTrue(bookView.getTableView().isEditable());
        Assertions.assertNotNull(((BorderPane) bookView.getView()).getBottom());
        Assertions.assertNotNull(bookView.getDeleteBtn().getOnAction());
        Assertions.assertNotNull(bookView.getSaveBtn().getOnAction());

        View.setCurrentUser(librarianUser);
        bookView = new BookView();
        Assertions.assertFalse(bookView.getTableView().isEditable());
        Assertions.assertNull(((BorderPane) bookView.getView()).getBottom());
        Assertions.assertNull(bookView.getDeleteBtn().getOnAction());
        Assertions.assertNull(bookView.getSaveBtn().getOnAction());
    }

}
