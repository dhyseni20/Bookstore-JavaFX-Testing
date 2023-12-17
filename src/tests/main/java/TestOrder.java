import application.bookstore.models.Author;
import application.bookstore.models.BaseModel;
import application.bookstore.models.Book;
import application.bookstore.models.BookOrder;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.parallel.Execution;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.parallel.ExecutionMode.SAME_THREAD;

@Execution(SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TestOrder {

    private static final List<Author> validAuthors = new ArrayList<>();
    private static final List<Book> validBooks = new ArrayList<>();

    @BeforeAll
    static void setUp() {
        BaseModel.FOLDER_PATH = "testing_data_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        if (!new File(BaseModel.FOLDER_PATH).mkdirs())
            throw new RuntimeException("Could not create test data folder! Test SetUp failed!");
        application.bookstore.models.Order.PRINT_PATH = "testing_print_" + UUID.randomUUID().toString().replaceAll("-", "_") + "/";
        Utilities.deleteDir(new File(application.bookstore.models.Order.PRINT_PATH).getAbsoluteFile());
        if (!new File(application.bookstore.models.Order.PRINT_PATH).mkdirs())
            throw new RuntimeException("Could not create test print folder! Test SetUp failed!");
        Author a;
        a = new Author("authora", "authora");
        validAuthors.add(a);
        validBooks.add(new Book("0047719162559", "book1", 15, 5, 5.2f, a));
        validBooks.add(new Book("5053175174463", "book2", 10, 6, 6.3f, a));
        a = new Author("authorb", "authorb");
        validAuthors.add(a);
        validBooks.add(new Book("0575057483715", "book3", 15, 5, 5.2f, a));
    }

    @AfterAll
    static void cleanUp() {
        Utilities.deleteDir(new File(BaseModel.FOLDER_PATH).getAbsoluteFile());
        Utilities.deleteDir(new File(application.bookstore.models.Order.PRINT_PATH).getAbsoluteFile());
    }

    @BeforeEach
    void beforeEach() {
        if (!application.bookstore.models.Order.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
    }

    @Test
    void testBookOrder() {
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> new BookOrder(0, validBooks.get(0)));
        Assertions.assertEquals(exception.getMessage(), "Quantity cannot be less than 1");
    }

    @Test
    @Order(0)
    void testIsValid() {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        Assertions.assertEquals("Please choose at least 1 book.", order.isValid());
        BookOrder bookOrder1 = new BookOrder(2, validBooks.get(0));
        BookOrder bookOrder2 = new BookOrder(1, validBooks.get(2));
        order.setBooksOrdered(new ArrayList<>(List.of(bookOrder1, bookOrder2)));

        order.completeOrder("test", "test");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("asd", "test");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("asd asd", "test");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("a", "test");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("ëËÇ", "test");
        Assertions.assertEquals("1", order.isValid());

        order.completeOrder("", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder(" ", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("     ", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("?asd", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("?1234567890", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("-", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("*", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("=", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder(")", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("(", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("!", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("'", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("_", "test");
        Assertions.assertEquals("Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());

        order.completeOrder("test", "test");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("test", "asd");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("test", "asd asd");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("test", "a");
        Assertions.assertEquals("1", order.isValid());
        order.completeOrder("test", "ëËÇ");
        Assertions.assertEquals("1", order.isValid());

        order.completeOrder("test", "");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", " ");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "     ");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "?asd");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "?1234567890");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "-");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "*");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "=");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", ")");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "(");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "!");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "'");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
        order.completeOrder("test", "_");
        Assertions.assertEquals("Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.", order.isValid());
    }

    @Test
    @Order(1)
    void testSave() {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        BookOrder bookOrder1 = new BookOrder(2, validBooks.get(0));
        BookOrder bookOrder2 = new BookOrder(1, validBooks.get(2));
        order.setBooksOrdered(new ArrayList<>(List.of(bookOrder1, bookOrder2)));
        order.completeOrder("test", "test");
        String res = order.saveInFile();
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, application.bookstore.models.Order.getOrders().size());
        Assertions.assertEquals(order, application.bookstore.models.Order.getOrders().get(0));
        Assertions.assertEquals(1, Utilities.getData(application.bookstore.models.Order.getDataFile()).size());
        Assertions.assertEquals(order, Utilities.getData(application.bookstore.models.Order.getDataFile()).get(0));
    }

    @Test
    @Order(2)
    void testSaveInvalid() {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        order.completeOrder("test", "test");
        String res = order.saveInFile();
        Assertions.assertEquals("Please choose at least 1 book.", res);
        Assertions.assertEquals(0, Utilities.getData(application.bookstore.models.Order.getDataFile()).size());
        Assertions.assertEquals(0, application.bookstore.models.Order.getOrders().size());
    }

    @Test
    @Order(3)
    void testGet() {
        List<application.bookstore.models.Order> books = application.bookstore.models.Order.getOrders();
        Assertions.assertEquals(0, books.size());
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        BookOrder bookOrder1 = new BookOrder(2, validBooks.get(0));
        BookOrder bookOrder2 = new BookOrder(1, validBooks.get(2));
        order.setBooksOrdered(new ArrayList<>(List.of(bookOrder1, bookOrder2)));
        order.completeOrder("test", "test");
        order.saveInFile();
        books = application.bookstore.models.Order.getOrders();
        Assertions.assertTrue(books.stream().anyMatch(s -> s.getOrderID().equals(order.getOrderID())));
    }

    @Test
    @Order(4)
    void testDelete() {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        BookOrder bookOrder1 = new BookOrder(2, validBooks.get(0));
        BookOrder bookOrder2 = new BookOrder(1, validBooks.get(2));
        order.setBooksOrdered(new ArrayList<>(List.of(bookOrder1, bookOrder2)));
        order.completeOrder("test", "test");
        order.saveInFile();
        Assertions.assertEquals("Deleting or modifying previous orders is not allowed.", order.deleteFromFile());
        Assertions.assertEquals(1, application.bookstore.models.Order.getOrders().size());
        Assertions.assertEquals(1, Utilities.getData(application.bookstore.models.Order.getDataFile()).size());
    }

    @Test
    @Order(5)
    void testPrint() throws IOException {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        BookOrder bookOrder1 = new BookOrder(2, validBooks.get(0));
        BookOrder bookOrder2 = new BookOrder(1, validBooks.get(2));
        order.setBooksOrdered(new ArrayList<>(List.of(bookOrder1, bookOrder2)));
        order.completeOrder("test", "test");
        String printFilePath = order.print();
        Assertions.assertNotNull(printFilePath);
        Assertions.assertTrue(new File(printFilePath).exists());
        Assertions.assertEquals(String.join("\n", Files.readAllLines(Path.of(printFilePath))), order.toString());
    }

    @Test
    @Order(6)
    void testSearch() {
        application.bookstore.models.Order order = new application.bookstore.models.Order();
        order.setBooksOrdered(new ArrayList<>(List.of(new BookOrder(2, validBooks.get(0)))));
        order.completeOrder("test", "test");
        order.saveInFile();
        String id0 = order.getOrderID();
        order = new application.bookstore.models.Order();
        order.setBooksOrdered(new ArrayList<>(List.of(new BookOrder(2, validBooks.get(0)))));
        order.completeOrder("admin", "test asd");
        order.saveInFile();
        String id1 = order.getOrderID();
        order = new application.bookstore.models.Order();
        order.setBooksOrdered(new ArrayList<>(List.of(new BookOrder(2, validBooks.get(0)))));
        order.completeOrder("admin", "hello world");
        order.saveInFile();
        String id2 = order.getOrderID();
        Assertions.assertEquals(Stream.of(id0, id1).sorted().collect(Collectors.toList()), application.bookstore.models.Order.getSearchResults("test").stream().map(application.bookstore.models.Order::getOrderID).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of(id0, id1).sorted().collect(Collectors.toList()), application.bookstore.models.Order.getSearchResults("TEst").stream().map(application.bookstore.models.Order::getOrderID).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of(id2).collect(Collectors.toList()), application.bookstore.models.Order.getSearchResults("hello world").stream().map(application.bookstore.models.Order::getOrderID).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of(id2).collect(Collectors.toList()), application.bookstore.models.Order.getSearchResults("Hello ").stream().map(application.bookstore.models.Order::getOrderID).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of(id0, id1, id2).sorted().collect(Collectors.toList()), application.bookstore.models.Order.getSearchResults("").stream().map(application.bookstore.models.Order::getOrderID).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(List.of(), application.bookstore.models.Order.getSearchResults("TEST1").stream().map(application.bookstore.models.Order::getOrderID).collect(Collectors.toList()));
    }

}
