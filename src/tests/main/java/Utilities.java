import application.bookstore.models.BaseModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Utilities {
    static void deleteDir(File file) {
        File[] contents = file.listFiles();
        if (contents != null) {
            for (File f : contents) {
                deleteDir(f);
            }
        }
        file.delete();
    }

    public static <T extends BaseModel> List<T> getData(File file) {
        List<T> data = new ArrayList<>();
        try (FileInputStream fileInputStream = new FileInputStream(file); ObjectInputStream inputStream = new ObjectInputStream(fileInputStream)) {
            while (true) {
                T temp = (T) inputStream.readObject();
                if (temp == null)
                    break;
                data.add(temp);
            }
        } catch (EOFException eofException) {
            BaseModel.LOGGER.info(String.format("End of %s file reached!", file));
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Test setup failed, could read data.", e);
        }
        return data;
    }
}
