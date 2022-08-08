package me.brian.jdcli.archive;

import java.io.File;
import java.io.IOException;

public class ArchiveFileUtility {

    private static String getDuplicatePathString(String originalPath, int number) {
        String filename = getFileNameInPath(originalPath);
        String[] parts = filename.split("\\.");
        if (parts.length == 1) {
            return parts[0] + "." + number;
        }
        StringBuilder newFilename = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            if (i == parts.length - 1) {
                // on the last element, add "." + number to the string
                newFilename.append(".").append(number);
            }
            // continue building string
            if (i != 0) {
                newFilename.append(".");
            }
            newFilename.append(parts[i]);
        }
        return getParentPath(originalPath) + newFilename;
    }

    private static String getParentPath(String path) {
        // my IDE goes weird if i try to escape the path separator through string concatenation in regex
        String[] parts = path.split(File.separator.equals("/") ? "/" : "\\\\");
        StringBuilder parentPath = new StringBuilder();
        for (int i = 0; i < parts.length - 1; i++) {
            parentPath.append(parts[i]);
            if (i != parts.length - 1) {
                parentPath.append(File.separator);
            }
        }
        return parentPath + File.separator;
    }

    private static String getFileNameInPath(String path) {
        String[] parts = path.split(File.separator.equals("/") ? "/" : "\\\\");
        return parts[parts.length - 1];
    }

    private static boolean isFileCaseSensitiveMatch(File file, String fileName) throws IOException {
        return getFileNameInPath(file.getCanonicalPath()).equals(fileName);
    }

    private static File getFileForCaseSensitivePath(File file, String fileName) throws IOException {
        String filePath = file.getPath();
        String idealFilePath = getParentPath(filePath) + fileName;
        File caseInsensitiveFile = file;
        for (int i = 1; caseInsensitiveFile.exists() && !isFileCaseSensitiveMatch(caseInsensitiveFile, fileName); i++) {
            caseInsensitiveFile = new File(getDuplicatePathString(idealFilePath, i));
        }
        return caseInsensitiveFile;
    }

    /**
     * There are instances where a jar can have multiple files that would match on a case-insensitive file system
     * and overwrite each other. This method finds a name that the file should be renamed to so that other files
     * are not overwritten. If the file already exists with the same case-sensitive name, that file will be returned.
     *
     * @param target The ideal target destination
     * @return A file modified to resolve any case-insensitive conflicts
     */
    public static File getZeroConflictFile(String target) throws IOException {
        File idealFile = new File(target);
        String idealFileName = getFileNameInPath(target);
        return getFileForCaseSensitivePath(idealFile, idealFileName);
    }

}
