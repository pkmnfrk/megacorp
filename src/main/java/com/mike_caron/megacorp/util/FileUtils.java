package com.mike_caron.megacorp.util;

import com.mike_caron.megacorp.MegaCorpMod;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class FileUtils
{
    /**
     * Copy a file from source to destination.
     *
     * @param source
     *        the source
     * @param destination
     *        the destination
     * @return True if succeeded , False if not
     */
    public static boolean copy(InputStream source , String destination) {
        boolean succeess = true;

        //System.out.println("Copying ->" + source + "\n\tto ->" + destination);

        try {
            Files.copy(source, Paths.get(destination), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException ex) {
            MegaCorpMod.logger.error(ex);
            succeess = false;
        }

        return succeess;

    }

    public static void exportResource(String resource, File destination)
    {
        copy(FileUtils.class.getResourceAsStream(resource),destination.getPath());
    }

    public static void exportResourceOnce(String resource, File destination)
    {
        if(destination.exists()) return;
        exportResource(resource, destination);
    }
}
