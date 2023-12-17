import application.bookstore.models.Author;
import application.bookstore.models.BaseModel;
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
public class TestAuthor {

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
        if (!Author.clearData().equals("1"))
            throw new RuntimeException("Test setup failed, could clear data.");
    }

    @Test
    @Order(0)
    void testIsValid() {
        Assertions.assertEquals("1", new Author("test", "test").isValid());
        Assertions.assertEquals("1", new Author("a", "test").isValid());
        Assertions.assertEquals("1", new Author("asdËë", "test").isValid());
        // this one was failing, change [a-zA-Z] to [\p{L}] to consider unicode chars
        Assertions.assertEquals("1", new Author("aaaaaaaaaabbbbbbbbbbcccccccccc", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("aaaaaaaaaabbbbbbbbbbcccccccccca", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author(" ", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("      ", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("      asd", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("      1", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("1", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("1123", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("1123 ", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("1123 as", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as?", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as*", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as-", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as=", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as_", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as/", "test").isValid());
        Assertions.assertEquals("First Name must contain only letters.", new Author("as.", "test").isValid());
        Assertions.assertEquals("1", new Author("test", "test").isValid());
        Assertions.assertEquals("1", new Author("test", "a").isValid());
        Assertions.assertEquals("1", new Author("test", "asdËë").isValid());
        // this one was failing, change [a-zA-Z] to [\p{L}] to consider unicode chars
        Assertions.assertEquals("1", new Author("test", "aaaaaaaaaabbbbbbbbbbcccccccccc").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "aaaaaaaaaabbbbbbbbbbcccccccccca").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", " ").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "      ").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "      asd").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "      1").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "1").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "1123").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "1123 ").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "1123 as").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as?").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as*").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as-").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as=").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as_").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as/").isValid());
        Assertions.assertEquals("Last Name must contain only letters.", new Author("test", "as.").isValid());
    }

    @Test
    @Order(1)
    void testSave() {
        Author author = new Author("test", "test");
        String res = author.saveInFile();
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(author, Author.getAuthors().get(0));
        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals(author, Utilities.getData(Author.getDataFile()).get(0));
    }

    @Test
    @Order(2)
    void testSaveInvalid() {
        Author author = new Author("", "test");
        String res = author.saveInFile();
        Assertions.assertEquals("First Name must contain only letters.", res);
        Assertions.assertEquals(0, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals(0, Author.getAuthors().size());
    }


    @Test
    @Order(3)
    void testSaveDuplicate() {
        new Author("test", "test").saveInFile();
        Assertions.assertEquals("Author with this Full Name exists.", new Author("test", "test").saveInFile());
        Assertions.assertEquals("1", new Author("testasd", "test").saveInFile());
        Assertions.assertEquals("Author with this Full Name exists.", new Author("test", "Test").saveInFile());
        // was failing, changed a.getFullName().equals(this.getFullName()) to a.getFullName().equalsIgnoreCase(this.getFullName())

        Assertions.assertEquals("Author with this Full Name exists.", new Author("Test", "Test").saveInFile());
        Assertions.assertEquals("Author with this Full Name exists.", new Author("Test", "test").saveInFile());
        Assertions.assertEquals(2, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals(2, Author.getAuthors().size());
    }


    @Test
    @Order(4)
    void testGet() {
        List<Author> authors = Author.getAuthors();
        Assertions.assertEquals(0, authors.size());
        Author author = new Author("test", "test");
        author.saveInFile();
        authors = Author.getAuthors();
        Assertions.assertTrue(authors.contains(author));
    }

    @Test
    @Order(5)
    void testDelete() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Assertions.assertEquals("1", author.deleteFromFile());
        Assertions.assertEquals(0, Author.getAuthors().size());
        Assertions.assertEquals(0, Utilities.getData(Author.getDataFile()).size());
    }

    @Test
    @Order(6)
    void testDeleteNonexistent() {
        Author author1 = new Author("test", "test");
        Author author2 = new Author("ttest", "test");
        author2.saveInFile();
        Assertions.assertEquals("1", author1.deleteFromFile());
        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertTrue(Author.getAuthors().contains(author2));
        Assertions.assertFalse(Author.getAuthors().contains(author1));
    }

    @Test
    @Order(7)
    void testUpdate() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author updated = author.clone();
        updated.setFirstName("test" + "a");
        updated.updateInFile(author);
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals("testa", Author.getAuthors().get(0).getFirstName());
        Assertions.assertEquals("testa", ((Author) Utilities.getData(Author.getDataFile()).get(0)).getFirstName());
    }

    @Test
    @Order(8)
    void testUpdateInvalid() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author updated = author.clone();
        updated.setFirstName("test?");
        String res = updated.updateInFile(author);
        Assertions.assertEquals("First Name must contain only letters.", res);
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals(author, Author.getAuthors().get(0));
        Assertions.assertEquals(author, Utilities.getData(Author.getDataFile()).get(0));
    }

    @Test
    @Order(9)
    void testUpdateSameId() {
        Author author = new Author("test", "test");
        author.saveInFile();
        Author updated = author.clone();
        updated.setFirstName("test");
        updated.setLastName("test");
        String res = updated.updateInFile(author);
        Assertions.assertEquals("1", res);
        Assertions.assertEquals(1, Author.getAuthors().size());
        Assertions.assertEquals(1, Utilities.getData(Author.getDataFile()).size());
        Assertions.assertEquals(updated, Author.getAuthors().get(0));
        Assertions.assertEquals(updated, Utilities.getData(Author.getDataFile()).get(0));
    }

    @Test
    @Order(10)
    void testFullName() {
        Assertions.assertEquals("test test", new Author("test", "test").getFullName());
    }

    @Test
    @Order(11)
    void testSearch() {
        new Author("test", "asd").saveInFile();
        new Author("Test", "asda").saveInFile();
        new Author("authora", "authora").saveInFile();
        new Author("authorb", "authorb").saveInFile();
        new Author("temp", "asd").saveInFile();
        // searching authors had to e recoded as it only allowed search by either name or surname not both
        Assertions.assertEquals(Stream.of("test asd", "Test asda").sorted().collect(Collectors.toList()), Author.getSearchResults("test").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test asd", "Test asda").sorted().collect(Collectors.toList()), Author.getSearchResults("TEST").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test asd", "Test asda", "temp asd").sorted().collect(Collectors.toList()), Author.getSearchResults("TE").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test asd", "Test asda", "temp asd").sorted().collect(Collectors.toList()), Author.getSearchResults("asd").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test asd", "Test asda").sorted().collect(Collectors.toList()), Author.getSearchResults("test asd").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("Test asda").collect(Collectors.toList()), Author.getSearchResults("test asda").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(Stream.of("test asd", "Test asda", "temp asd", "authora authora", "authorb authorb").sorted().collect(Collectors.toList()), Author.getSearchResults("").stream().map(Author::getFullName).sorted().collect(Collectors.toList()));
        Assertions.assertEquals(List.of(), Author.getSearchResults("TEST1").stream().map(Author::getFullName).collect(Collectors.toList()));
    }

}
