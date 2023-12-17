import application.bookstore.Main;
import application.bookstore.controllers.LoginController;
import application.bookstore.models.BaseModel;
import application.bookstore.models.Role;
import application.bookstore.models.User;
import application.bookstore.ui.ChangePasswordDialog;
import application.bookstore.views.LoginView;
import application.bookstore.views.MainView;
import application.bookstore.views.View;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.util.UUID;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestAuthentication {

    static User adminUser;
    static User managerUser;
    static User librarianUser;

    @BeforeAll
    static void setUp() throws InterruptedException {
        BaseModel.FOLDER_PATH = "testing_data_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        if (!new File(BaseModel.FOLDER_PATH).mkdirs())
            throw new RuntimeException("Could not create test data folder! Test SetUp failed!");

        Main.launchTest();

        Thread.sleep(500);

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
    @Order(0)
    void testLoginValidCredentials() {
        adminUser.saveInFile();
        LoginView loginView = new LoginView();
        new LoginController(loginView, Main.getStage());
        loginView.getUsernameField().setText("admin");
        loginView.getPasswordField().setText("admin");
        try {
            loginView.getLoginBtn().fire();
        } catch (IllegalStateException ignored) {
        }
        Assertions.assertEquals(adminUser, View.getCurrentUser());
    }


    @Test
    @Order(1)
    void testLoginInvalidCredentials() {
        adminUser.saveInFile();
        LoginView loginView = new LoginView();
        new LoginController(loginView, Main.getStage());
        loginView.getUsernameField().setText("admin");
        loginView.getPasswordField().setText("admin123");
        loginView.getLoginBtn().fire();
        Assertions.assertNull(View.getCurrentUser());
        Assertions.assertEquals("Wrong username or password", loginView.getErrorLabel().getText());

        loginView = new LoginView();
        new LoginController(loginView, Main.getStage());
        loginView.getUsernameField().setText("admin123");
        loginView.getPasswordField().setText("admin");
        loginView.getLoginBtn().fire();
        Assertions.assertNull(View.getCurrentUser());
        Assertions.assertEquals("Wrong username or password", loginView.getErrorLabel().getText());
    }

    @Test
    @Order(2)
    void testLogout() throws InterruptedException {
        View.setCurrentUser(adminUser);
        MainView mainView = new MainView(Main.getStage());
        try {
            mainView.getLogoutButton().fire();
        } catch (IllegalStateException ignored) {
        }
        Assertions.assertNull(View.getCurrentUser());

        View.setCurrentUser(adminUser);
        mainView = new MainView(Main.getStage());
        try {
            mainView.getMenuItemLogout().fire();
        } catch (IllegalStateException ignored) {
        }
        Assertions.assertNull(View.getCurrentUser());
    }


    @Test
    @Order(3)
    void testChangePasswordValid() throws InterruptedException {
        View.setCurrentUser(adminUser);
        adminUser.saveInFile();
        MainView mainView = new MainView(Main.getStage());
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(Main.getStage(), mainView);

        Assertions.assertEquals("User: " + adminUser.getUsername(), changePasswordDialog.getUsernameLabel().getText());

        changePasswordDialog.getOldPassField().setText("admin");
        changePasswordDialog.getNewPassField().setText("admin12");
        changePasswordDialog.getConfirmPassword().setText("admin12");
        try {
            changePasswordDialog.getOkButton().fire();
        } catch (IllegalStateException ignored) {
        }
        Assertions.assertEquals("", changePasswordDialog.getMessageLabel().getText());
        Assertions.assertNotNull(User.getIfExists(new User("admin", "admin12")));
    }

    @Test
    @Order(4)
    void testChangePasswordInvalidOldPassword() {
        View.setCurrentUser(adminUser);
        adminUser.saveInFile();
        MainView mainView = new MainView(Main.getStage());
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(Main.getStage(), mainView);

        Assertions.assertEquals("User: " + adminUser.getUsername(), changePasswordDialog.getUsernameLabel().getText());

        changePasswordDialog.getOldPassField().setText("admin12");
        changePasswordDialog.getNewPassField().setText("admin");
        changePasswordDialog.getConfirmPassword().setText("admin");
        changePasswordDialog.getOkButton().fire();
        Assertions.assertEquals("Old Password Incorrect!", changePasswordDialog.getMessageLabel().getText());
        Assertions.assertEquals(adminUser, User.getIfExists(adminUser));
    }

    @Test
    @Order(5)
    void testChangePasswordNewPasswordsDoNotMatch() {
        View.setCurrentUser(adminUser);
        adminUser.saveInFile();
        MainView mainView = new MainView(Main.getStage());
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(Main.getStage(), mainView);

        Assertions.assertEquals("User: " + adminUser.getUsername(), changePasswordDialog.getUsernameLabel().getText());

        changePasswordDialog.getOldPassField().setText("admin");
        changePasswordDialog.getNewPassField().setText("admin1234");
        changePasswordDialog.getConfirmPassword().setText("admin123");
        changePasswordDialog.getOkButton().fire();
        Assertions.assertEquals("New Passwords do not match!", changePasswordDialog.getMessageLabel().getText());
        Assertions.assertEquals(adminUser, User.getIfExists(adminUser));
    }

    @Test
    @Order(6)
    void testChangePasswordInvalidNewPassword() {
        View.setCurrentUser(adminUser);
        adminUser.saveInFile();
        MainView mainView = new MainView(Main.getStage());
        ChangePasswordDialog changePasswordDialog = new ChangePasswordDialog(Main.getStage(), mainView);

        Assertions.assertEquals("User: " + adminUser.getUsername(), changePasswordDialog.getUsernameLabel().getText());

        changePasswordDialog.getOldPassField().setText("admin");
        changePasswordDialog.getNewPassField().setText("admin~");
        changePasswordDialog.getConfirmPassword().setText("admin~");
        changePasswordDialog.getOkButton().fire();
        Assertions.assertEquals("New Password Invalid!\n" +
                "Password must contain at least 1 lower/upper case letters, numbers or underscore.", changePasswordDialog.getMessageLabel().getText());
        Assertions.assertEquals(adminUser, User.getIfExists(adminUser));
    }
}
