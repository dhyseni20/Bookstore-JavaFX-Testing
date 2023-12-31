package application.bookstore.models;

import application.bookstore.auxiliaries.TableGenerator;
import application.bookstore.controllers.ControllerCommon;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.PrintWriter;
import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Order extends BaseModel<Order> implements Serializable {
    @Serial
    private static final long serialVersionUID = 1234567L;
    public static final String FILE_PATH = BaseModel.FOLDER_PATH + "orders.ser";
    public static final File DATA_FILE = new File(FILE_PATH);
    public static String PRINT_PATH = "bills/";

    private static final ObservableList<Order> orders = FXCollections.observableArrayList();

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");


    private static final DateTimeFormatter idFormatter = DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss");

    private String orderID;
    private String clientName;
    private String username;
    private String date;
    private ArrayList<BookOrder> booksOrdered;


    public Order() {
        booksOrdered = new ArrayList<>();
        UUID uuid = UUID.randomUUID();
        orderID = "Order_" + uuid.toString().replaceAll("-", "_");
    }

    public static ObservableList<Order> getOrders() {
        return getData(DATA_FILE, orders);
    }

    public static ArrayList<Order> getSearchResults(String searchText) {
        searchText = ".*" + searchText.toLowerCase() + ".*";
        ArrayList<Order> searchResults = new ArrayList<>();
        for (Order order : getOrders()) {
            if (order.getClientName().toLowerCase().matches(searchText) || order.getUsername().toLowerCase().matches(searchText))
                searchResults.add(order);
        }
        return searchResults;
    }


    public float getTotal() {
        float sum = 0;
        for (BookOrder b : booksOrdered)
            sum += b.getTotalPrice();
        return sum;
    }

    public String print() {
        try {
            PrintWriter p = new PrintWriter(PRINT_PATH + orderID + ".txt");
            p.println(this);
            p.close();
            return PRINT_PATH + orderID + ".txt";
        } catch (Exception ex) {
            ControllerCommon.LOGGER.info(Arrays.toString(ex.getStackTrace()));
        }
        return null;
    }

    public void completeOrder(String username, String clientName) {
        setUsername(username);
        setClientName(clientName);
        LocalDateTime now = LocalDateTime.now();
        setDate(dateFormatter.format(now));
        for (int i = 0; i < booksOrdered.size(); i++)
            booksOrdered.set(i, booksOrdered.get(i).clone());// we need to clone them because the originals will be removed from the view and thus unreferenced
    }

    @Override
    public String toString() {
        String s = "Thank you from buying from our store!\n\nOrder: " + orderID + "\nDate: " + date + "\nClient: " + clientName + "\nBooks Ordered:";

        TableGenerator tableGenerator = new TableGenerator();

        List<String> headersList = new ArrayList<>();
        headersList.add("Quantity");
        headersList.add("Title");
        headersList.add("Author");
        headersList.add("Unit Price");
        headersList.add("Total");

        List<List<String>> rowsList = new ArrayList<>();

        for (BookOrder b : booksOrdered) {
            List<String> row = new ArrayList<>();
            row.add(Integer.toString(b.getQuantity()));
            row.add(b.getTitle());
            row.add(b.getAuthor().getFullName());
            row.add(Float.toString(b.getUnitPrice()));
            row.add(Float.toString(b.getTotalPrice()));
            rowsList.add(row);
        }

        s += tableGenerator.generateTable(headersList, rowsList);
        s += String.format("\n=======================\nTotal: %.2f", getTotal());

        return s;
    }

    @Override
    public String isValid() {
        if (getBooksOrdered().size() == 0)
            return "Please choose at least 1 book.";
        if (!clientName.matches("([\\p{L}]{1,30}\\s*)+"))
            return "Client Name must contain 1 to 30 lower/upper case letters numbers spaces or underscore.";
        if (!username.matches("([\\p{L}]{1,30}\\s*)+"))
            return "Username must contain 1 to 30 lower/upper case letters numbers spaces or underscore.";
        return "1";
    }

    public static String clearData() {
        return clearData(DATA_FILE, orders);
    }

    @Override
    public String saveInFile() {
        return save(DATA_FILE, orders);
    }

    // Deleting or modifying previous orders is not allowed."
    @Override
    public String deleteFromFile() {
        return "Deleting or modifying previous orders is not allowed.";
    }

    @Override
    public String updateInFile(Order old) {
        return "Deleting or modifying previous orders is not allowed.";
    }


    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<BookOrder> getBooksOrdered() {
        return booksOrdered;
    }

    public void setBooksOrdered(ArrayList<BookOrder> booksOrdered) {
        this.booksOrdered = booksOrdered;
    }

    public static String getFilePath() {
        return FILE_PATH;
    }

    public static File getDataFile() {
        return DATA_FILE.getAbsoluteFile();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Order order)
            return order.getOrderID().equals(getOrderID()) && order.getTotal() == getTotal() && order.getUsername().equals(getUsername()) && order.getClientName().equals(getClientName()) && order.getDate().equals(getDate());
        return false;
    }
}
