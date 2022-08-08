package me.brian.jdcli.decompiler.jd;

import me.brian.jdcli.archive.ArchiveFileUtility;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;

import java.io.*;
import java.nio.file.Files;

public class DecompileLoader implements Loader {

    private final File baseDirectory;

    public DecompileLoader(File baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    private File getFileForClassName(String name) throws IOException {
        return ArchiveFileUtility.getZeroConflictFile(baseDirectory.getPath() + name + ".class");
    }

    public byte[] load(String name) throws LoaderException {
        try {
            return Files.readAllBytes(getFileForClassName(name).toPath());
        } catch (IOException exception) {
            throw new LoaderException(exception);
        }
    }

    public boolean canLoad(String className) {
        try {
            File file = getFileForClassName(className);
            return file.exists() && file.isFile() && file.canRead();
        } catch (IOException exception) {
            exception.printStackTrace();
            return false;
        }
    }
}
