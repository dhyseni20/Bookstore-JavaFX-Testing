package application.bookstore.models;

import application.bookstore.auxiliaries.CustomObjectOutputStream;
import application.bookstore.auxiliaries.FileHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Arrays;

public abstract class BaseModel<V extends BaseModel> {
    public static String FOLDER_PATH = "data/";

    public static final Logger LOGGER = LogManager.getLogger(BaseModel.class);

    public static <T extends BaseModel> ObservableList<T> getData(File file, ObservableList<T> data) {
        if (data.size() == 0) {
            FileInputStream fileInputStream = null;
            ObjectInputStream inputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                inputStream = new ObjectInputStream(fileInputStream);
                while (true) {
                    T temp = (T) inputStream.readObject();
                    if (temp == null)
                        break;
                    data.add(temp);
                }
            } catch (FileNotFoundException ex) {
                LOGGER.info(String.format("File %s not found, the file will be created when data is entered.\n", file) + Arrays.toString(ex.getStackTrace()));
            } catch (EOFException eofException) {
                LOGGER.info(String.format("End of %s file reached!", file));
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (fileInputStream != null)
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        }
        return data;
    }


    public String save(File file, ObservableList<V> data) {
        if (!(isValid().matches("1")))
            return isValid();
        ObjectOutputStream outputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(file, true);
            if (file.length() == 0)
                outputStream = new ObjectOutputStream(fileOutputStream);
            else
                outputStream = new CustomObjectOutputStream(fileOutputStream);
            outputStream.writeObject((V) this);
            data.add((V) this);
        } catch (IOException e) {
            LOGGER.info("Could Not Save to File." + Arrays.toString(e.getStackTrace()));
            return "Could Not Save to File. Please check Logs.";
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ignored) {
                }
            }
            if (fileOutputStream != null)
                try {
                    fileOutputStream.close();
                } catch (IOException ignored) {
                }
        }
        return "1";
    }

    public static <T extends BaseModel> String clearData(File file, ObservableList<T> list) {
        try {
            FileHandler.overwriteCurrentListToFile(file, FXCollections.observableArrayList());
            list.clear();
        } catch (Exception e) {
            LOGGER.info("Could Not Save to File." + Arrays.toString(e.getStackTrace()));
            return "Could Not Save to File. Please check Logs.";
        }
        return "1";
    }

    public String delete(File file, ObservableList<V> list) {
        try {
            list.remove((V) this);
            FileHandler.overwriteCurrentListToFile(file, list);
        } catch (Exception e) {
            list.set(list.indexOf(((V) this)), (V) this);
            LOGGER.info("Could Not Save to File." + Arrays.toString(e.getStackTrace()));
            return "Could Not Save to File. Please check Logs.";
        }
        return "1";
    }

    public String update(File file, ObservableList<V> list, V old) {
        if (!(isValid().matches("1"))) {
            list.set(list.indexOf(old), old);
            return isValid();
        }
        try {
            list.set(list.indexOf(old), (V) this);
            FileHandler.overwriteCurrentListToFile(file, list);
        } catch (Exception e) {
            list.set(list.indexOf(((V) this)), old);
            LOGGER.info("Could Not Save to File." + Arrays.toString(e.getStackTrace()));
            return "Could Not Save to File. Please check Logs.";
        }
        return "1";
    }

    public abstract String isValid();
    // return "1" if valid, else return a "message" describing the error


    public abstract String saveInFile();

    public abstract String deleteFromFile();

    public abstract String updateInFile(V old);

}
