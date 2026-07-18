package com.example.singleworldmod;

import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class WorldTemplateUtils {

    private static final String TEMPLATE_RESOURCE_PATH = "/assets/singleworldmod/template_world.zip";

    public static void extractTemplateWorld(File targetDir) throws IOException {
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw new IOException("Не вдалося створити папку: " + targetDir.getAbsolutePath());
        }

        try (InputStream in = WorldTemplateUtils.class.getResourceAsStream(TEMPLATE_RESOURCE_PATH)) {
            if (in == null) {
                throw new IOException("Не знайдено template_world.zip в ресурсах мода. " +
                        "Поклади архів свого світу у src/main/resources/assets/singleworldmod/template_world.zip");
            }

            try (ZipInputStream zis = new ZipInputStream(in)) {
                ZipEntry entry;
                byte[] buffer = new byte[8192];
                while ((entry = zis.getNextEntry()) != null) {
                    File outFile = new File(targetDir, entry.getName());

                    if (!outFile.getCanonicalPath().startsWith(targetDir.getCanonicalPath() + File.separator)
                            && !outFile.getCanonicalPath().equals(targetDir.getCanonicalPath())) {
                        throw new IOException("Небезпечний шлях в архіві: " + entry.getName());
                    }

                    if (entry.isDirectory()) {
                        outFile.mkdirs();
                        continue;
                    }

                    File parent = outFile.getParentFile();
                    if (parent != null && !parent.exists()) {
                        parent.mkdirs();
                    }

                    try (FileOutputStream fos = new FileOutputStream(outFile)) {
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                    }
                    zis.closeEntry();
                }
            }
        }
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
