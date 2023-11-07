package com.cortezromeo.taixiu.util;

public class FilenameUtil {

    public static String removeExtension(String fileName) {

        String separator = System.getProperty("file.separator");
        String filename;
        int lastSeparatorIndex = fileName.lastIndexOf(separator);
        if (lastSeparatorIndex == -1) {
            filename = fileName;
        } else {
            filename = fileName.substring(lastSeparatorIndex + 1);
        }

        int extensionIndex = filename.lastIndexOf(".");
        if (extensionIndex == -1)
            return filename;

        return filename.substring(0, extensionIndex);
    }

}
