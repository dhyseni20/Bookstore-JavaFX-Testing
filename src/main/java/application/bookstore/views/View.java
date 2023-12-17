package application.bookstore.views;

import application.bookstore.models.User;
import javafx.scene.Parent;

public abstract class View {
    static private User currentUser = null;

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser_) {
        currentUser = currentUser_;
    }

    public abstract Parent getView();
}
