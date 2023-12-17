import application.bookstore.Main;
import application.bookstore.models.User;
import application.bookstore.models.BaseModel;
import application.bookstore.models.Role;
import application.bookstore.models.User;
import application.bookstore.views.UsersView;
import application.bookstore.views.View;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestUserUI {

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
        if (!User.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
    }

    @Test
    @Order(0)
    void testCreateValid() {
        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();
        User user = new User("test", "test");
        usersView.getUserNameField().setText(user.getUsername());
        usersView.getPasswordField().setText(user.getPassword());
        usersView.getSaveBtn().fire();
        Assertions.assertEquals("User created successfully!", usersView.getMessageLabel().getText());
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(user, User.getUsers().get(0));
        Assertions.assertEquals(1, usersView.getTableView().getItems().size());
        Assertions.assertEquals(user, usersView.getTableView().getItems().get(0));
    }

    @Test
    @Order(1)
    void testCreateInvalid() {
        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();
        User user = new User("", "test");
        usersView.getUserNameField().setText(user.getUsername());
        usersView.getPasswordField().setText(user.getPassword());
        usersView.getSaveBtn().fire();
        Assertions.assertEquals("User creation failed!\n" +
                "Username must contain at least 1 lower/upper case letters, numbers or underscore.", usersView.getMessageLabel().getText());
        Assertions.assertEquals(0, User.getUsers().size());
        Assertions.assertEquals(0, usersView.getTableView().getItems().size());
    }


    @Test
    @Order(2)
    void testCreateDuplicate() {
        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();
        User user = new User("test", "test");
        usersView.getUserNameField().setText(user.getUsername());
        usersView.getPasswordField().setText(user.getPassword());
        usersView.getSaveBtn().fire();

        usersView.getUserNameField().setText(user.getUsername());
        usersView.getPasswordField().setText(user.getPassword());
        usersView.getSaveBtn().fire();

        Assertions.assertEquals("User creation failed!\n" +
                "Username Exists", usersView.getMessageLabel().getText());
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(1, usersView.getTableView().getItems().size());
        Assertions.assertEquals(user, usersView.getTableView().getItems().get(0));
    }


    @Test
    @Order(3)
    void testLoadData() {
        new User("test", "test").saveInFile();
        new User("test1", "test1").saveInFile();
        new User("test2", "test2").saveInFile();
        List<User> authors = User.getUsers();

        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();
        Assertions.assertEquals(authors, usersView.getTableView().getItems());
    }

    @Test
    @Order(4)
    void testSearch() {
        new User("test", "test").saveInFile();
        new User("asd", "ad").saveInFile();
        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();

        usersView.getSearchView().getSearchField().setText("test");
        usersView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(User.getSearchResults("test").stream().map(User::getUsername).sorted().collect(Collectors.toList()), usersView.getTableView().getItems().stream().map(User::getUsername).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(5)
    void testSearchClear() {
        new User("test", "test").saveInFile();
        new User("asd", "ad").saveInFile();
        List<User> authors = User.getUsers();

        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();

        usersView.getSearchView().getSearchField().setText("test");
        usersView.getSearchView().getSearchBtn().fire();
        Assertions.assertEquals(User.getSearchResults("test").stream().map(User::getUsername).sorted().collect(Collectors.toList()), usersView.getTableView().getItems().stream().map(User::getUsername).sorted().collect(Collectors.toList()));
        usersView.getSearchView().getClearBtn().fire();
        Assertions.assertEquals(authors.stream().map(User::getUsername).sorted().collect(Collectors.toList()), usersView.getTableView().getItems().stream().map(User::getUsername).sorted().collect(Collectors.toList()));
    }

    @Test
    @Order(6)
    void testDelete() {
        User user = new User("test", "test");
        user.saveInFile();

        View.setCurrentUser(adminUser);
        UsersView usersView = new UsersView();
        usersView.getTableView().getSelectionModel().select(0);
        usersView.getDeleteBtn().fire();
        Assertions.assertEquals("User removed successfully", usersView.getMessageLabel().getText());
        Assertions.assertEquals(0, User.getUsers().size());
        Assertions.assertEquals(0, usersView.getTableView().getItems().size());
    }

//    @Test
//    @Order(7)
//    void testUpdate() {
//        User user = new User("test", "test");
//        user.saveInFile();
//
//        View.setCurrentUser(adminUser);
//        UsersView usersView = new UsersView();
//
//
//        usersView.getFirstNameCol().se
//        User updated = user.clone();
//        updated.setFirstName("test?");
//        String res = updated.updateInFile(user);
//
//        Assertions.assertEquals("User creation failed!\n" +
//                "User with this Full Name exists.", usersView.getMessageLabel().getText());
//        Assertions.assertEquals(1, usersView.getTableView().getItems().size());
//        Assertions.assertEquals(user, usersView.getTableView().getItems().get(0));
//    }

//    @Test
//    @Order(8)
//    void testUpdateInvalid() {
//        User user = new User("test", "test");
//        user.saveInFile();
//        User updated = user.clone();
//        updated.setFirstName("test?");
//        String res = updated.updateInFile(user);
//        Assertions.assertEquals("First Name must contain only letters.", res);
//        Assertions.assertEquals(1, User.getUsers().size());
//        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
//        Assertions.assertEquals(user, User.getUsers().get(0));
//        Assertions.assertEquals(user, Utilities.getData(User.getDataFile()).get(0));
//    }
//
//    @Test
//    @Order(9)
//    void testUpdateSameId() {
//        User user = new User("test", "test");
//        user.saveInFile();
//        User updated = user.clone();
//        updated.setFirstName("test");
//        updated.setLastName("test");
//        String res = updated.updateInFile(user);
//        Assertions.assertEquals("1", res);
//        Assertions.assertEquals(1, User.getUsers().size());
//        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
//        Assertions.assertEquals(updated, User.getUsers().get(0));
//        Assertions.assertEquals(updated, Utilities.getData(User.getDataFile()).get(0));
//    }


}
