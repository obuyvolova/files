package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Main {
    static Date date = new Date();
    static StringBuilder logs = new StringBuilder();

    public static void main(String[] args) {
        //создаем каталоги
        createFolder("C:\\Games", "src");
        createFolder("C:\\Games", "res");
        createFolder("C:\\Games", "save games");
        createFolder("C:\\Games", "temp");
        createFolder("C:\\Games\\src", "main");
        createFolder("C:\\Games\\src", "test");
        createFolder("C:\\Games\\res", "drawables");
        createFolder("C:\\Games\\res", "vectors");
        createFolder("C:\\Games\\res", "icons");

        //создаем файлы
        createFile("C:\\Games\\src\\main","Main.java");
        createFile("C:\\Games\\src\\main", "Utils.java");
        createFile("C:\\Games\\temp", "temp.txt");

        // записываем логи в файлик temp.txt
        try (FileWriter log = new FileWriter ("C:\\Games\\temp\\temp.txt")) {
            log.write(logs.toString());
            log.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

        GameProgress gameProgress1 = new GameProgress(60, 50, 8, 45.67);
        GameProgress gameProgress2 = new GameProgress(70, 40, 5, 67.65);
        GameProgress gameProgress3 = new GameProgress(90, 60, 3, 65.34);

        //сериализация состояния игры
        saveGame("C:\\Games\\save games\\save1.dat", gameProgress1);
        saveGame("C:\\Games\\save games\\save2.dat", gameProgress2);
        saveGame("C:\\Games\\save games\\save3.dat", gameProgress3);

        //запакуем ZIP
        zipFiles("C:\\Games\\save games\\saves.zip", new ArrayList<>(Arrays.asList(
                "C:\\Games\\save games\\save1.dat",
                "C:\\Games\\save games\\save2.dat",
                "C:\\Games\\save games\\save3.dat")));
        //создадим новую папку, в которую будем распаковывать zip-архив
        createFolder("C:\\Games\\save games", "open zip");

        // распакуем ZIP
        openZip("C:\\Games\\save games\\saves.zip", "C:\\Games\\save games\\open zip\\new");

        //десериализация состояния сохраненной игры
        System.out.println(openProgress("C:\\Games\\save games\\open zip\\newsave1.dat"));
        System.out.println(openProgress("C:\\Games\\save games\\open zip\\newsave2.dat"));
        System.out.println(openProgress("C:\\Games\\save games\\open zip\\newsave3.dat"));
    }

    public static void  createFile(String pathFolder, String nameFile) {
        File file = new File(pathFolder, nameFile);
        try {
            if (file.createNewFile()) {
                logs.append(date).append(" Фаил ").append(nameFile).append(" был создан в каталоге ").
                        append(pathFolder).append("\n");
            } else logs.append(date).append(" Ошибка при создании файла ").append(nameFile).append("\n");
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void createFolder(String pathFolder, String nameFolder) {
        File dir = new File (pathFolder, nameFolder);
        if (dir.mkdir()) {
            logs.append(date).append(" Создан новый каталог ").append(nameFolder).append(" в каталоге ").
                    append(pathFolder).append("\n");
        } else logs.append(date).append(" Ошибка при создании каталога ").append(nameFolder).append("\n");
    }

    public static void saveGame(String path,GameProgress gameProgress){
        try (FileOutputStream fileOutputStream = new FileOutputStream(path);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream)) {
            objectOutputStream.writeObject(gameProgress);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void zipFiles(String pathZip, ArrayList<String> pathFiles) {
            try (ZipOutputStream zipOutputStream = new ZipOutputStream(new
                    FileOutputStream(pathZip))) {
                for (int i = 0; i < pathFiles.size(); i ++) {
                    FileInputStream fileInputStream = new FileInputStream(pathFiles.get(i));
                    ZipEntry entry = new ZipEntry("save" + (i + 1) + ".dat");
                    zipOutputStream.putNextEntry(entry);
                    byte[] buffer = new byte[fileInputStream.available()];
                    fileInputStream.read(buffer);
                    zipOutputStream.write(buffer);
                    zipOutputStream.closeEntry();
                }
            } catch (Exception ex) {
                System.out.println(ex.getMessage());
            }

    }

    public static void openZip(String pathZip, String pathFolder) {
        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(pathZip))) {
            ZipEntry entry;
            String name;
            while ((entry  = zipInputStream.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fileOutputStream = new FileOutputStream(pathFolder + name);
                for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                    fileOutputStream.write(c);
                }
                fileOutputStream.flush();
                zipInputStream.closeEntry();
                fileOutputStream.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static GameProgress openProgress(String path) {
        try (FileInputStream fileInputStream = new FileInputStream(path);
            ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream)) {
            return (GameProgress) objectInputStream.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } return null;
    }

}


