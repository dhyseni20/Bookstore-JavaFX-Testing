package application.bookstore;

import application.bookstore.controllers.ControllerCommon;
import application.bookstore.controllers.LoginController;
import application.bookstore.models.*;
import application.bookstore.views.LoginView;
import application.bookstore.views.MainView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {

    private static Stage stage;


    public static void main(String[] args) {
        loadData();
        createAdminAndData(); // create user:admin password:admin and sample data if it is the first time running (data/users.ser does not exist)
        launch(args);
    }

    public static void launchTest() {
        new Thread(() -> {
            try {
                Application.launch();
            } catch (IllegalStateException ex) {
                if (!ex.getMessage().startsWith("Application launch must not be called more than once"))
                    throw ex;
            }
        }).start();

    }

    public static void createAdminAndData() {
        File f = new File(User.FILE_PATH);
        if (!f.exists()) {
            ControllerCommon.LOGGER.info("Creating startup Data...");
            new File(BaseModel.FOLDER_PATH).mkdirs();
            User u = new User("admin", "admin", Role.ADMIN);
            ControllerCommon.LOGGER.info(u.saveInFile());
            u = new User("manager", "manager", Role.MANAGER);
            ControllerCommon.LOGGER.info(u.saveInFile());
            u = new User("librarian", "librarian", Role.LIBRARIAN);
            ControllerCommon.LOGGER.info(u.saveInFile());
            Author a;
            a = new Author("Fyodor", "Dostoevsky");
            a.saveInFile();
            new Book("0047719162559", "Crime and Punishment", 15, 5, 5.2f, a).saveInFile();
            new Book("5053175174463", "The Brothers Karamazov", 10, 6, 6.3f, a).saveInFile();
            a = new Author("Nick", "Hornby");
            a.saveInFile();
            new Book("0575057483715", "High Fidelity", 15, 5, 5.2f, a).saveInFile();
            a = new Author("Robert", "Heinlein");
            a.saveInFile();
            new Book("9508986466324", "The Moon Is a Harsh Mistress", 15, 5, 5.2f, a).saveInFile();
            a = new Author("David", "Nicholls");
            a.saveInFile();
            new Book("7995957884928", "One Day", 5, 3, 3.4f, a).saveInFile();
            a = new Author("William", "Simon");
            a.saveInFile();
            new Book("1327772926893", "The Art of Deception", 13, 3.5f, 4f, a).saveInFile();
            a = new Author("Toshikazu", "Kawaguchi");
            a.saveInFile();
            new Book("1327772926893", "Before The Coffee Gets Cold", 15, 5, 5.2f, a).saveInFile();
            a = new Author("David", "Nicholls");
            a.saveInFile();
            new Book("2099167240058", "One Hundred Years of Solitude", 7, 7, 7.3f, a).saveInFile();
            a = new Author("Wally", "Lamb");
            a.saveInFile();
            new Book("8094875113544", "She's Come Undone", 2, 5, 5.5f, a).saveInFile();
            a = new Author("Albert", "Camus");
            a.saveInFile();
            new Book("1234567890120", "The Stranger", 20, 5, 5.2f, a).saveInFile();
            new Book("1234567890121", "The Plague", 8, 7, 7.5f, a).saveInFile();
            new Book("1234567890122", "The Myth of Sisyphus", 10, 6, 6.4f, a).saveInFile();
            new Book("1234567890123", "The Fall", 14, 5, 5.2f, a).saveInFile();
        }
    }

    private static void loadData() {
        ControllerCommon.LOGGER.info("Loading data Files...");
        User.getUsers();
        Author.getAuthors();
        Book.getBooks();
        Order.getOrders();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Main.stage = stage;
        LoginView loginView = new LoginView();
        new LoginController(loginView, stage);
        Scene scene = new Scene(loginView.getView(), MainView.width, MainView.height);
        stage.setTitle("Bookstore");
        stage.setScene(scene);
        stage.show();
    }

    public static Stage getStage() {
        return stage;
    }

}
