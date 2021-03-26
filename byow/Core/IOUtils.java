package byow.Core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class IOUtils {
    public static void write(File filename, String writing) {
        if (filename.exists()) {
            try {
                FileWriter myWriter = new FileWriter(filename);
                myWriter.write(writing);
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
    }

    public static String read(File file) {
        if (file.exists()) {
            try {
                Scanner myReader = new Scanner(file);
                if (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
                    return data;
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        return null;
    }
}
