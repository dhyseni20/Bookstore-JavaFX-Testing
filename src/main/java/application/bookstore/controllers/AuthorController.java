package application.bookstore.controllers;

import application.bookstore.models.Author;
import application.bookstore.models.Role;
import application.bookstore.ui.DeleteAuthorDialog;
import application.bookstore.views.AuthorView;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.scene.control.ButtonType;

import java.util.ArrayList;

public class AuthorController {
    private final AuthorView view;
    private DeleteAuthorDialog currentDeleteAuthorDialog;
    private boolean deleteAuthorDialogIsOpen;

    public AuthorController(AuthorView view) {
        this.view = view;
        Role currentRole = view.getCurrentUser().getRole();

        setSearchListener();

        if (currentRole == Role.MANAGER || currentRole == Role.ADMIN) {
            setSaveListener();
            setDeleteListener();
            setEditListener();
            view.getTableView().setEditable(true);
        }
    }

    private void setSearchListener() {
        view.getSearchView().getClearBtn().setOnAction(e -> {
            view.getSearchView().getSearchField().setText("");
            view.getTableView().setItems(FXCollections.observableArrayList(Author.getAuthors()));
        });
        view.getSearchView().getSearchBtn().setOnAction(e -> {
            String searchText = view.getSearchView().getSearchField().getText();
            ArrayList<Author> searchResults = Author.getSearchResults(searchText);
            view.getTableView().setItems(FXCollections.observableArrayList(searchResults));
        });
    }

    private void setSaveListener() {
        view.getSaveBtn().setOnAction(e -> {
            Author author = new Author(view.getFirstNameField().getText(), view.getLastNameField().getText());
            String res = author.saveInFile();
            if (res.matches("1")) {
                ControllerCommon.showSuccessMessage(view.getMessageLabel(), "Author created successfully!");
                view.getFirstNameField().setText("");
                view.getLastNameField().setText("");
            } else
                ControllerCommon.showErrorMessage(view.getMessageLabel(), "Author creation failed!\n" + res);
        });
    }

    private void setDeleteListener() {
        view.getDeleteBtn().setOnAction(e -> {
            ButtonType deleteBooks = new ButtonType("Delete Books");
            ButtonType deleteOnlyAuthors = new ButtonType("Delete Authors Only");
            if (!deleteAuthorDialogIsOpen) {
                Platform.runLater(() -> currentDeleteAuthorDialog = new DeleteAuthorDialog(view, deleteBooks, deleteOnlyAuthors));
                deleteAuthorDialogIsOpen=true;
            }
            // deletion is handled inside the dialog
        });
    }

    private void setEditListener() {
        view.getFirstNameCol().setOnEditCommit(e -> {
            Author authorToEdit = e.getRowValue();
            Author editedAuthor = new Author(e.getNewValue(), authorToEdit.getLastName());
            String res = editedAuthor.updateInFile(authorToEdit);
            if (res.matches("1"))
                ControllerCommon.showSuccessMessage(view.getMessageLabel(), "Edit Successful!");
            else
                ControllerCommon.showErrorMessage(view.getMessageLabel(), "Edit value invalid!\n" + res);
        });

        view.getLastNameCol().setOnEditCommit(e -> {
            Author authorToEdit = e.getRowValue();
            Author editedAuthor = new Author(authorToEdit.getFirstName(), e.getNewValue());
            String res = editedAuthor.updateInFile(authorToEdit);
            if (res.matches("1"))
                ControllerCommon.showSuccessMessage(view.getMessageLabel(), "Edit Successful!");
            else
                ControllerCommon.showErrorMessage(view.getMessageLabel(), "Edit value invalid!\n" + res);
        });
    }

    public DeleteAuthorDialog getCurrentDeleteAuthorDialog(){
        return currentDeleteAuthorDialog;
    }

    public boolean isDeleteAuthorDialogIsOpen() {
        return deleteAuthorDialogIsOpen;
    }

    public void setDeleteAuthorDialogIsOpen(boolean deleteAuthorDialogIsOpen) {
        this.deleteAuthorDialogIsOpen = deleteAuthorDialogIsOpen;
    }
}
