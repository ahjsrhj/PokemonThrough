package cn.imrhj.PokemonThrough.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Created by rhj on 16/7/19.
 */
public class FileIO {
    public static String read(String path) {
        File file = new File(path);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                String line = reader.readLine();
                reader.close();
                if (line == null || line.isEmpty()) {
                    return "0";
                }
                return line;
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return "0";
    }

    public static void write(String path, String value) {
        Log.d(FileIO.class.getName(), "write: path = " + path + ", value = " + value);
        File file = new File(path);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            writer.write(value);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
