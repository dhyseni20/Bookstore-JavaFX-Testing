package application.bookstore.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;

public class Author extends BaseModel<Author> implements Serializable, Cloneable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    public static final String FILE_PATH = BaseModel.FOLDER_PATH + "authors.ser";
    public static final File DATA_FILE = new File(FILE_PATH);

    private static final ObservableList<Author> authors = FXCollections.observableArrayList();

    private String firstName;
    private String lastName;

    public Author(String firstName, String lastName) {
        setFirstName(firstName);
        setLastName(lastName);
    }

    public static ObservableList<Author> getAuthors() {
        return getData(DATA_FILE, authors);
    }

    public boolean exists() {
        for (Author a : authors) {
            if (a.getFullName().equalsIgnoreCase(this.getFullName()))
                return true;
        }
        return false;
    }

    public static String clearData() {
        return clearData(DATA_FILE, authors);
    }

    public static ArrayList<Author> getSearchResults(String searchText) {
        ArrayList<Author> searchResults = new ArrayList<>();
        if (searchText == null)
            return searchResults;
        searchText = searchText.trim();
        if (searchText.length() == 0)
            return new ArrayList<>(getAuthors());
        String[] patterns = searchText.split(" ", 2);
        patterns[0] = ".*" + patterns[0].toLowerCase() + ".*";
        if (patterns.length > 1)
            patterns[1] = ".*" + patterns[1].toLowerCase() + ".*";

        for (Author author : getAuthors())
            if ((patterns.length > 1 && author.getFirstName().toLowerCase().matches(patterns[0]) && author.getLastName().toLowerCase().matches(patterns[1])) || (patterns.length == 1 && (author.getFirstName().toLowerCase().matches(patterns[0]) || author.getLastName().toLowerCase().matches(patterns[0]))))
                searchResults.add(author);
        return searchResults;
    }

    @Override
    public Author clone() {
        return new Author(firstName, lastName);
    }


    @Override
    public String toString() {
        return getFullName();
    }

    // toString for logging
    // since Author is widely used as an object in combo-boxes/table-views the original toString is used by them
    public String toString_() {
        return "\nAuthor{" +
                "\n\t\"firstName\": " + getFirstName() +
                ",\n\t\"lastName\": " + getLastName() +
                "\n}";
    }

    @Override
    public String isValid() {
        // firstname and last name must contain only letters
        if (!getFirstName().matches("[\\p{L}]{1,30}"))
            return "First Name must contain only letters.";
        if (!getLastName().matches("[\\p{L}]{1,30}")) {
            return "Last Name must contain only letters.";
        }
        return "1";
    }

    @Override
    public String saveInFile() {
        if (exists())
            return "Author with this Full Name exists.";
        return save(DATA_FILE, authors);
    }

    @Override
    public String deleteFromFile() {
        return delete(DATA_FILE, authors);
    }

    @Override
    public String updateInFile(Author old) {
        return update(DATA_FILE, authors, old);
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getFullName() {
        return getFirstName() + " " + getLastName();
    }

    public static String getFilePath() {
        return FILE_PATH;
    }

    public static File getDataFile() {
        return DATA_FILE.getAbsoluteFile();
    }


    @Override
    public boolean equals(Object o) {
        if (o instanceof Author a)
            return getFullName().equals(a.getFullName());
        return false;
    }
}
