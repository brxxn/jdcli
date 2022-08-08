package me.brian.jdcli.archive;

import java.io.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

public class ArchiveUtility {

    private static File _prepareFile(File directory, JarEntry zipEntry) throws ArchiveException, IOException {
        File destination = ArchiveFileUtility.getZeroConflictFile(directory.getPath() + File.separator + zipEntry.getName());

        // protect against malicious jar files using zip slips
        String directoryPath = directory.getAbsolutePath();
        String destinationPath = destination.getAbsolutePath();

        if (!destinationPath.startsWith(directoryPath + File.separator)) {
            throw new ArchiveException(ArchiveExceptionType.GENERIC, "potentially malicious jar file attempted to zip slip");
        }

        return destination;
    }

    public static void unarchive(File pathToArchive, File destination) throws ArchiveException {
        if (!pathToArchive.exists() || !pathToArchive.isFile()) {
            throw new ArchiveException(ArchiveExceptionType.GENERIC, "archive path does not exist");
        }

        if (!destination.exists() || !destination.isDirectory()) {
            throw new ArchiveException(ArchiveExceptionType.GENERIC, "archive destination does not exist");
        }

        try (FileInputStream inputStream = new FileInputStream(pathToArchive)) {
            JarInputStream jarInputStream = new JarInputStream(inputStream);
            JarEntry entry = null;
            try {
                entry = jarInputStream.getNextJarEntry();
            } catch (EOFException exception) {
                // do nothing, see https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6519463
            }
            while (entry != null) {
                File target = _prepareFile(destination, entry);
                if (entry.isDirectory()) {
                    if (!target.isDirectory() && !target.mkdirs()) {
                        throw new ArchiveException(ArchiveExceptionType.IO, "Could not make or find directory " + target.getPath());
                    }
                } else {
                    // some archives put some file entries before the directory, this allows it to be properly handled
                    File parentDirectory = target.getParentFile();
                    if (!parentDirectory.exists()) {
                        // make sure no zip slips allow directories to be made outside of destination
                        if (!parentDirectory.getAbsolutePath().startsWith(destination.getAbsolutePath() + File.separator)) {
                            throw new ArchiveException(ArchiveExceptionType.GENERIC, "malicious jar attempted to zip slip with invalid parent directory");
                        }
                        if (!parentDirectory.mkdirs()) {
                            throw new ArchiveException(ArchiveExceptionType.IO, "Could not make parent directory " + parentDirectory.getName());
                        }
                    }

                    FileOutputStream outputStream = new FileOutputStream(target);

                    int size;
                    byte[] buffer = new byte[1024];

                    while ((size = jarInputStream.read(buffer)) > 0) {
                        outputStream.write(buffer, 0, size);
                    }

                    outputStream.close();
                }

                try {
                    entry = jarInputStream.getNextJarEntry();
                } catch (EOFException exception) {
                    // do nothing, see https://bugs.java.com/bugdatabase/view_bug.do?bug_id=6519463
                }
            }
            jarInputStream.close();
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new ArchiveException(ArchiveExceptionType.IO, exception.toString());
        }
    }

}
