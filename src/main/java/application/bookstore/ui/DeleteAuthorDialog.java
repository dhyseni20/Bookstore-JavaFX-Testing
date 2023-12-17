package application.bookstore.ui;

import application.bookstore.controllers.ControllerCommon;
import application.bookstore.models.Author;
import application.bookstore.models.Book;
import application.bookstore.views.AuthorView;
import javafx.collections.FXCollections;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.image.ImageView;
import javafx.stage.Window;

import java.util.List;
import java.util.Optional;

public class DeleteAuthorDialog extends Alert {


    public DeleteAuthorDialog(AuthorView view, ButtonType deleteBooks, ButtonType deleteOnlyAuthors) {
        super(AlertType.NONE, "Do you want to delete the books related to this author?", deleteBooks, deleteOnlyAuthors);
        setGraphic(getImage());
        Window window = getDialogPane().getScene().getWindow();
        window.setOnCloseRequest(e -> hide());
        Optional<ButtonType> result = showAndWait();
        if (result.isEmpty()) ;
        else if (result.get() == deleteBooks)
            view.deleteAuthors(true);
        else if (result.get() == deleteOnlyAuthors)
            view.deleteAuthors(false);
        view.getAuthorController().setDeleteAuthorDialogIsOpen(false);
    }

    private ImageView getImage() {
        ImageView imageView = new ImageView(String.valueOf(CreateButton.class.getResource("/images/edit_icon.png")));
        return imageView;
    }


}
