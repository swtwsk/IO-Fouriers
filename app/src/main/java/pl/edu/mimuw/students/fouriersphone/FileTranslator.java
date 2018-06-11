package pl.edu.mimuw.students.fouriersphone;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Klasa FileTranslator ma za zadanie zapewnić interfejs do komunikacji
 * funkcji wywoływanych przez użytkownika z systemem plików. Jej podstawowe
 * funkcjonalności to:
 * getDownloadsFiles - wypisanie wszystkich plików o rozszerzeniu .txt z
 * folderu Downloads;
 * prepareFile - odczytanie danych z pliku to tablicy charów;
 * saveFile - zapisanie Stringa do pliku.
 * Należy pamiętać, że do poprawnego używania klasy użytkownik musi udzielić
 * aplikacji zgody na zarządzanie pamięcią.
 */
public class FileTranslator {

    /**
     * Checks if external storage is available for read and write.
     * Code adapted from https://developer.android.com/training/
     * @return can we write to external storage
     */
    private static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /**
     * Checks if external storage is available to at least read.
     * Code adapted from https://developer.android.com/training/
     * @return can we read from external storage
     */
    private static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /**
     * Function getting array of files from Downloads directory which
     * are in .txt format.
     * @return array of Strings - names of .txt format files in Downloads
     */
    public static ArrayList<String> getDownloadsFiles() {
        Log.d("File","Getting download files from " + Environment.DIRECTORY_DOWNLOADS);
        String formatEnding = ".txt";

        File downloads = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS);

        String[] files = downloads.list();
        ArrayList<String> goodFiles = new ArrayList<>();

        if (files == null) {
            Log.d("File", "There are no files in Downloads directory");
        } else {
            for (String file : files) {
                if (file.endsWith(formatEnding)) {
                    goodFiles.add(file);
                }
            }
        }
        return goodFiles;
    }

    /**
     * Returns File object of a given file name in DIRECTORY_DOWNLOADS
     * directory. If such file doesn't exist creates such file.
     * @param fileName file name of wanted File object
     * @return File object representing wanted file in Downloads or null
     *         if unsuccessful
     */
    private static File getPublicDownloadDir(String fileName) {
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            if (!file.createNewFile()) {
                Log.d("File", "File already existed");
            } else {
                Log.d("File", "File created");
            }
        } catch (IOException e) {
            Log.e("File", "Exception in createNewFile");
            return null;
        }
        return file;
    }

    /**
     * When given a file name and array of chars, tries to find file of
     * a given file name in Downloads and then reads its first bytes into
     * given array of chars. It reads no more chars then size of file and
     * no more then length of this array.
     * @param fileName name of file in directory Downloads function should read
     * @param cbuf array of chars function puts read chars into
     * @return number of read chars or -1 in case of error
     */
    public static int prepareFile(String fileName, char[] cbuf, int count) {
        Log.d("File", "Attempting to open file " + fileName);

        if (!isExternalStorageReadable()) {
            Log.e("File", "Error - external storage is not readable");
            return -1;
        }

        File file = getPublicDownloadDir(fileName);
        FileReader reader;

        if (file == null) {
            Log.e("File", "Problems with opening file");
            return -1;
        }

        try {
            reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            Log.e("File", "Exception in creating FileReader");
            return -1;
        }

        try {
            int ret = reader.read(cbuf, 0, count);
            if (ret == -1) {
                Log.d("File", "File empty");
            }
            return ret;
        } catch (IOException e) {
            Log.e("File", "Exception in read");
            return -1;
        }
    }

    /**
     * When given a file name of file in Downloads and a string it tries
     * to write this string to this file. If file of given file name doesn't
     * exist it creates it. On the other hand, if such file exists, it's
     * overwritten.
     * @param fileName name of file in Downloads function should write to
     * @param s string function should write to file
     * @return true if everything was successful and false if something went wrong
     */
    public static boolean saveFile(String fileName, String s) {
        if (!isExternalStorageWritable()) {
            Log.e("File", "Error - external storage is not writable");
            return false;
        }

        File file = getPublicDownloadDir(fileName);
        if (file == null) return false;

        FileOutputStream stream = null;
        boolean wasError = false;

        try {
            stream = new FileOutputStream(file);
            stream.write(s.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("File", "Exception in constructor of FileOutputStream");
            wasError = true;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("File", "Exception in function write");
            wasError = true;
        }

        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("File", "Exception in function close");
                wasError = true;
            }
        }

        return !wasError;
    }
}
