import application.bookstore.models.BaseModel;
import application.bookstore.models.Role;
import application.bookstore.models.User;
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
public class TestUser {

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

    String randomValidUsername;

    @BeforeEach
    void beforeEach() {
        if (!User.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
        randomValidUsername = UUID.randomUUID().toString().replaceAll("-", "_");
    }


    @Test
    @Order(0)
    void testIsValid() {
        Assertions.assertEquals("1", new User("username", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("asd", "asd", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("a", "a", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("_", "_", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("ëËÇ", "ëËÇ", Role.ADMIN).isValid()); // this one was failing, change \w to [\p{L}\p{N}_] to consider unicode chars
        Assertions.assertEquals("1", new User("_asd", "_asd", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("1234567890", "1234567890", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("1234567890asd_123asd", "1234567890asd_123asd", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("username", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("1", new User("username", "password", Role.MANAGER).isValid());
        Assertions.assertEquals("1", new User("username", "password", Role.LIBRARIAN).isValid());
        Assertions.assertEquals("1", new User("username", "password", Role.LIBRARIAN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User(null, "password", Role.ADMIN).isValid(), "Username cannot be null!");
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", null, Role.ADMIN).isValid(), "Password cannot be null!");
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User(" ", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("     ", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("?asd", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("-", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("*", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("=", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User(")", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("(", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("!", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", new User("'", "password", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", " ", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "      ", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "?asd", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "-", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "*", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "=", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", ")", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "(", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "!", Role.ADMIN).isValid());
        Assertions.assertEquals("Password must contain at least 1 lower/upper case letters, numbers or underscore.", new User("username", "'", Role.ADMIN).isValid());
    }

    @Test
    @Order(1)
    void testSave() {
        User user = new User("username", "password", Role.ADMIN);
        String res = user.saveInFile();
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(user, User.getUsers().get(0));
        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
        Assertions.assertEquals(user, Utilities.getData(User.getDataFile()).get(0));
    }

    @Test
    @Order(2)
    void testSaveInvalid() {
        User user = new User("", "", Role.ADMIN);
        String res = user.saveInFile();
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", res);
        Assertions.assertEquals(0, User.getUsers().size());
        Assertions.assertEquals(0, Utilities.getData(User.getDataFile()).size());
    }


    @Test
    @Order(3)
    void testSaveDuplicate() {
        new User(randomValidUsername, randomValidUsername, Role.ADMIN).saveInFile();
        Assertions.assertEquals("Username Exists", new User(randomValidUsername, randomValidUsername, Role.ADMIN).saveInFile());
        Assertions.assertEquals("Username Exists", new User(randomValidUsername, randomValidUsername, Role.LIBRARIAN).saveInFile());
        Assertions.assertEquals("Username Exists", new User(randomValidUsername, "password", Role.ADMIN).saveInFile());
        Assertions.assertEquals("1", new User(randomValidUsername + "_", "password", Role.ADMIN).saveInFile());
        Assertions.assertEquals(2, User.getUsers().size());
        Assertions.assertEquals(2, Utilities.getData(User.getDataFile()).size());
    }

    @Test
    @Order(4)
    void testSaveSameUsernameCaseSensitive() {
        Assertions.assertEquals("1", new User("asd", "password", Role.ADMIN).saveInFile());
        Assertions.assertEquals("Username Exists", new User("asd", "password", Role.LIBRARIAN).saveInFile());
        Assertions.assertEquals("1", new User("Asd", "password", Role.ADMIN).saveInFile());
        Assertions.assertEquals(2, User.getUsers().size());
        Assertions.assertEquals(2, Utilities.getData(User.getDataFile()).size());
    }

    @Test
    @Order(5)
    void testGet() {
        List<User> users = User.getUsers();
        Assertions.assertEquals(0, users.size());
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        users = User.getUsers();
        Assertions.assertTrue(users.contains(user));
    }

    @Test
    @Order(6)
    void testGetIfExists() {
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        Assertions.assertNotNull(User.getIfExists(user));
    }

    @Test
    @Order(7)
    void testDelete() {
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        Assertions.assertEquals("1", user.deleteFromFile());
        Assertions.assertEquals(0, User.getUsers().size());
        Assertions.assertEquals(0, Utilities.getData(User.getDataFile()).size());
    }

    @Test
    @Order(8)
    void testDeleteNonexistent() {
        User user1 = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        User user2 = new User(randomValidUsername + "_", randomValidUsername, Role.ADMIN);
        user2.saveInFile();
        Assertions.assertEquals("1", user1.deleteFromFile());
        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
        Assertions.assertTrue(User.getUsers().contains(user2));
        Assertions.assertFalse(User.getUsers().contains(user1));
    }


    @Test
    @Order(9)
    void testUpdate() {
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        User updated = user.clone();
        updated.setUsername(randomValidUsername + "_");
        updated.updateInFile(user);
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
        Assertions.assertEquals(randomValidUsername + "_", User.getUsers().get(0).getUsername());
        Assertions.assertEquals(randomValidUsername + "_", ((User) Utilities.getData(User.getDataFile()).get(0)).getUsername());
    }

    @Test
    @Order(10)
    void testUpdateInvalid() {
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        User updated = user.clone();
        updated.setUsername(randomValidUsername + "?");
        String res = updated.updateInFile(user);
        Assertions.assertEquals("Username must contain at least 1 lower/upper case letters, numbers or underscore.", res);
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
        Assertions.assertEquals(user, User.getUsers().get(0));
        Assertions.assertEquals(user, Utilities.getData(User.getDataFile()).get(0));
    }

    @Test
    @Order(11)
    void testUpdateSameId() {
        User user = new User(randomValidUsername, randomValidUsername, Role.ADMIN);
        user.saveInFile();
        User updated = user.clone();
        updated.setUsername(randomValidUsername);
        updated.setPassword("asd");
        updated.setRole(Role.MANAGER);
        String res = updated.updateInFile(user);
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, User.getUsers().size());
        Assertions.assertEquals(1, Utilities.getData(User.getDataFile()).size());
        Assertions.assertEquals(updated, User.getUsers().get(0));
        Assertions.assertEquals(updated, Utilities.getData(User.getDataFile()).get(0));
    }

    @Test
    @Order(12)
    void testSearch() {
        new User("test", "asd", Role.ADMIN).saveInFile();
        new User("user1", "asd", Role.ADMIN).saveInFile();
        new User("admin", "asd", Role.ADMIN).saveInFile();
        new User("Test", "asd", Role.ADMIN).saveInFile();
        new User("temp", "asd", Role.ADMIN).saveInFile();
        Assertions.assertEquals(Stream.of("test", "Test").sorted().collect(Collectors.toList()), User.getSearchResults("test").stream().map(User::getUsername).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test", "Test").sorted().collect(Collectors.toList()), User.getSearchResults("TEST").stream().map(User::getUsername).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test", "Test", "temp").sorted().collect(Collectors.toList()), User.getSearchResults("TE").stream().map(User::getUsername).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test", "Test", "temp", "admin", "user1").sorted().collect(Collectors.toList()), User.getSearchResults("").stream().map(User::getUsername).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(List.of(), User.getSearchResults("TEST1").stream().map(User::getUsername).collect(Collectors.toList()));
    }
}
