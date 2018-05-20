package pl.edu.mimuw.students.fouriersphone;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class FileTranslator {

    /* Checks if external storage is available for read and write */
    /* Code from https://developer.android.com/training/ */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    /* Code from https://developer.android.com/training/ */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    /* Get directory for document */
    public static File getPublicDocumentDir(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), fileName);
        try {
            if (!file.createNewFile()) {
                Log.e("File", "File exists");
            } else {
                Log.e("File", "File created");
            }
        } catch (IOException e) {
            Log.e("File", "Exception");
            return null;
        }
        return file;
    }

    /* Reads file into character array and return number of characters in file
     * (-1 in case of error) */
    public static int prepareFile(String fileName, char[] cbuf) {
        if (!isExternalStorageReadable()) {
            Log.e("File", "Problems with reading");
            return -1;
        }

        File file = getPublicDocumentDir(fileName);
        FileReader reader = null;

        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            Log.e("File", "Exception");
            return -1;
        }

        try {
            return reader.read(cbuf);
        } catch (IOException e) {
            Log.e("File", "Error in read");
            return -1;
        }
    }

    /* Saves array of characters to a file */
    public static boolean saveFile(String filename, char[] cbuf) {
        if (!isExternalStorageWritable()) {
            Log.e("File", "Can't write");
            return false;
        }

        File file = getPublicDocumentDir(filename);
        FileWriter writer = null;

        try {
            writer = new FileWriter(file);
        } catch (IOException e) {
            Log.e("File", "Exception");
            return false;
        }

        try {
            writer.write(cbuf);
        } catch (IOException e) {
            Log.e("File", "Error in write");
            return false;
        }

        return true;
    }
}
