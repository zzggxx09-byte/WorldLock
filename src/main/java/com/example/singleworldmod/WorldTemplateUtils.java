package com.example.singleworldmod;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;
import java.nio.file.Files;

public class WorldTemplateUtils {

    /** Назва папки поруч із .minecraft, куди гравець кидає свою карту напряму. */
    private static final String TEMPLATE_FOLDER_NAME = "singleworldmod_template";

    /** Повертає папку .minecraft/singleworldmod_template (створює її, якщо нема). */
    public static File getTemplateFolder(File mcDataDir) {
        File dir = new File(mcDataDir, TEMPLATE_FOLDER_NAME);
        if (!dir.exists()) {
            dir.mkdirs();
            writeReadme(dir);
        }
        return dir;
    }

    private static void writeReadme(File dir) {
        File readme = new File(dir, "СЮДИ_КИДАЙ_СВІЙ_СВІТ.txt");
        try (FileWriter fw = new FileWriter(readme)) {
            fw.write("Кидай сюди файли своєї карти НАПРЯМУ (без zip):\n" +
                    "level.dat, region/, playerdata/, DIM-1/, DIM1/, data/ і т.д.\n" +
                    "Тобто вміст папки світу, а не саму папку.\n");
        } catch (IOException ignored) {
        }
    }

    /** Чи є у папці шаблону хоча б level.dat (тобто карта реально покладена). */
    public static boolean isTemplateReady(File mcDataDir) {
        File dir = getTemplateFolder(mcDataDir);
        return new File(dir, "level.dat").exists();
    }

    /** Копіює папку-шаблон світу в нову папку в saves/. */
    public static void copyTemplateWorld(File mcDataDir, File targetDir) throws IOException {
        File templateDir = getTemplateFolder(mcDataDir);
        if (!new File(templateDir, "level.dat").exists()) {
            throw new IOException("У папці " + templateDir.getAbsolutePath() +
                    " немає level.dat. Поклади туди файли своєї карти.");
        }
        copyRecursively(templateDir.toPath(), targetDir.toPath());
    }

    private static void copyRecursively(java.nio.file.Path source, java.nio.file.Path target) throws IOException {
        Files.walk(source).forEach(path -> {
            try {
                java.nio.file.Path rel = source.relativize(path);
                java.nio.file.Path destPath = target.resolve(rel.toString());
                // Пропускаємо наш README
                if (path.getFileName().toString().equals("СЮДИ_КИДАЙ_СВІЙ_СВІТ.txt")) {
                    return;
                }
                if (Files.isDirectory(path)) {
                    Files.createDirectories(destPath);
                } else {
                    Files.createDirectories(destPath.getParent());
                    Files.copy(path, destPath, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void setLevelName(File worldDir, String newName) throws IOException {
        File levelDat = new File(worldDir, "level.dat");
        if (!levelDat.exists()) {
            return;
        }

        NBTTagCompound root;
        try (FileInputStream fis = new FileInputStream(levelDat)) {
            root = CompressedStreamTools.readCompressed(fis);
        }

        NBTTagCompound data = root.getCompoundTag("Data");
        data.setString("LevelName", newName);
        root.setTag("Data", data);

        try (FileOutputStream fos = new FileOutputStream(levelDat)) {
            CompressedStreamTools.writeCompressed(root, fos);
        }
    }

    public static String sanitizeFolderName(String name) {
        String sanitized = name.replaceAll("[\\\\/:*?\"<>|]", "_").trim();
        if (sanitized.isEmpty()) {
            sanitized = "world";
        }
        return sanitized;
    }

    public static File getUniqueSaveFolder(File savesDir, String baseName) {
        File candidate = new File(savesDir, baseName);
        int i = 1;
        while (candidate.exists()) {
            candidate = new File(savesDir, baseName + "-" + i);
            i++;
        }
        return candidate;
    }
}
