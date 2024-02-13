package com.epam.mjc.nio;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileReader {

    public Profile getDataFromFile(File file) {
        String name = "";
        String email = "";
        int age = 0;
        long phone = 0;

        Logger logger = Logger.getLogger(FileReader.class.getName());

        try (RandomAccessFile accessFile = new RandomAccessFile(file, "r")) {
            FileChannel channel = accessFile.getChannel();

            long fileSize = channel.size();

            ByteBuffer buffer = ByteBuffer.allocate((int) fileSize);
            channel.read(buffer);
            buffer.flip();

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < fileSize; i++) {
                char ch = (char) buffer.get();
                if (ch == '\n') {
                    String[] values = sb.toString().split(": ");
                    switch (values[0]) {
                        case "Name":
                            name = values[1];
                            break;
                        case "Age":
                            age = Integer.parseInt(values[1]);
                            break;
                        case "Email":
                            email = values[1];
                            break;
                        case "Phone":
                            phone = Long.parseLong(values[1]);
                            break;
                        default:
                            logger.log(Level.SEVERE, "No such field");
                    }
                    sb.setLength(0);
                } else {
                    sb.append(ch);
                }

            }

        } catch (FileNotFoundException e) {
            logger.log(Level.SEVERE, "file not found");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new Profile(name, age, email, phone);
    }

    public static void main(String[] args) {

        try {
            ClassLoader loader = FileReader.class.getClassLoader();
            File file = new File(loader.getResource("Profile.txt").toURI());
            FileReader reader = new FileReader();
            reader.getDataFromFile(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
