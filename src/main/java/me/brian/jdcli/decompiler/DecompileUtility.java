package me.brian.jdcli.decompiler;

import me.brian.jdcli.archive.ArchiveException;
import me.brian.jdcli.archive.ArchiveFileUtility;
import me.brian.jdcli.archive.ArchiveUtility;
import me.brian.jdcli.decompiler.jd.DecompileLoader;
import me.brian.jdcli.decompiler.jd.DecompilePrinter;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.*;
import java.util.stream.Stream;

public class DecompileUtility {

    public static void extractJar(File jar, File outputDirectory) throws DecompileException {

        try {
            File tempJarFile = new File(outputDirectory.getPath() + "/" + jar.getName());
            boolean success = tempJarFile.createNewFile();

            if (!success) {
                throw new DecompileException("failed to create temporary jar file in output directory");
            }

            Files.copy(jar.toPath(), tempJarFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
            /*
            // call the `unzip -B [jar]` command to extract .class files for us in the temporary directory
            ProcessBuilder processBuilder = new ProcessBuilder();
            processBuilder.command("unzip", "-B", "-o", tempJarFile.getAbsolutePath());

            processBuilder.directory(outputDirectory);
            Process jarExtractionProcess = processBuilder.start();
            jarExtractionProcess.getInputStream().transferTo(System.out);
            int exitCode;

            // wait for the jar xf command to finish.
            try {
                exitCode = jarExtractionProcess.waitFor();
            } catch (InterruptedException exception) {
                throw new DecompileException("encountered an interrupt while waiting for jar extraction to complete");
            }

            if (exitCode != 0) {
                throw new DecompileException("encountered a non-zero exit code (" + exitCode + ") while extracting jar");
            }
            */

            try {
                ArchiveUtility.unarchive(tempJarFile, outputDirectory);
            } catch (ArchiveException exception) {
                throw new DecompileException("encountered archive exception of type " + exception.getType() + " with message: " + exception.getMessage());
            }

            // cleanup and delete the temporary .jar file
            if (tempJarFile.exists() && !tempJarFile.delete()) {
                // we shouldn't throw a DecompileException since this isn't fatal, just print a message instead.
                System.out.println("failed to delete temporary jar file, continuing anyways");
            }
        } catch (IOException exception) {
            System.out.println("IOException occurred while preparing jar file for extraction, printing stack trace...");
            exception.printStackTrace();
            throw new DecompileException("encountered an IOException");
        }

    }

    public static void decompileClass(File baseDirectory, File targetedFile) throws DecompileException {
        decompileClass(baseDirectory, targetedFile, true);
    }

    public static void decompileClass(File baseDirectory, File targetedFile, boolean printExceptions) throws DecompileException {
        String baseDirectoryPath = baseDirectory.getAbsolutePath();
        String targetedFilePath = targetedFile.getAbsolutePath();

        // this changes something like "/directory/A/B.class" to "A/B.class"
        String targetedInternalClassName = targetedFilePath.substring(baseDirectoryPath.length());
        // change ".class" to ""
        targetedInternalClassName = targetedInternalClassName.replaceAll("\\.class", "");
        decompileClass(baseDirectory, targetedInternalClassName, printExceptions);
    }

    public static void decompileClass(File baseDirectory, String classInternalName, boolean printExceptions) throws DecompileException {
        ClassFileToJavaSourceDecompiler jdDecompiler = new ClassFileToJavaSourceDecompiler();
        DecompilePrinter printer = new DecompilePrinter();

        try {
            jdDecompiler.decompile(new DecompileLoader(baseDirectory), printer, classInternalName);
        } catch (Exception exception) {
            if (printExceptions) {
                exception.printStackTrace();
            }
            throw new DecompileException("encountered exception while decompiling: " + exception.getMessage());
        }

        try {
            String output = printer.toString();
            File outputFile = ArchiveFileUtility.getZeroConflictFile(baseDirectory.getPath() + classInternalName + ".java");

            if (!outputFile.createNewFile()) {
                throw new DecompileException("failed to create file at decompiled class path");
            }

            FileWriter fileWriter = new FileWriter(outputFile);
            fileWriter.write(output);
            fileWriter.close();
        } catch (IOException exception) {
            if (printExceptions) {
                exception.printStackTrace();
            }

            throw new DecompileException("encountered an IOException while writing decompiled class file");
        }

    }

    private static boolean isClassFileName(String fileName) {
        return fileName.endsWith(".class");
    }

    private static void _writeErrorFile(File classFile, String error) {
        try (FileWriter writer = new FileWriter(classFile)) {
            writer.write(error);
        } catch (IOException exception) {
            System.out.println("Unhandled IO exception while writing error file:");
            exception.printStackTrace();
        }
    }

    private static void _handleCleanupForFile(File classFile) {
        if (isClassFileName(classFile.getName())) {
            if (!classFile.delete()) {
                System.out.println("failed to delete " + classFile.getPath() + " during cleanup");
            }
        }
    }

    private static void _handleClassDecompile(File baseDirectory, File classFile, boolean verbose) {
        if (!isClassFileName(classFile.getName())) {
            return;
        }

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                if (verbose) {
                    System.out.println("[verbose] decompiling " + classFile.getPath() + "...");
                }
                decompileClass(baseDirectory, classFile, verbose);

                if (verbose) {
                    System.out.println("[verbose] successfully decompiled " + classFile.getPath());
                }
            } catch (DecompileException exception) {
                if (verbose) {
                    System.out.println("[verbose] encountered error while decompiling " + classFile + ": " + exception.getMessage());
                }
                _writeErrorFile(classFile, "/* " + exception.getMessage() + " */");
            }
        });

        try {
            future.get(1, TimeUnit.SECONDS);
        } catch (InterruptedException exception) {
            // not doing anything here, continue exiting process
        } catch (ExecutionException exception) {
            exception.printStackTrace();
        } catch (TimeoutException exception) {
            System.out.println("Timeout occurred while trying to decompile " + classFile + ", skipping...");
            _writeErrorFile(classFile, "/* timeout occurred while trying to decompile */");
        }
    }

    public static void decompileAllClasses(File baseDirectory, boolean verbose) throws DecompileException {
        try (Stream<Path> pathStream = Files.walk(baseDirectory.toPath())) {
            pathStream.forEach(path -> _handleClassDecompile(baseDirectory, new File(path.toString()), verbose));
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new DecompileException("encountered an unexpected IOException while decompiling all classes");
        }
    }

    public static void cleanupDirectory(File baseDirectory) throws DecompileException {
        try (Stream<Path> pathStream = Files.walk(baseDirectory.toPath())) {
            pathStream.forEach(path -> _handleCleanupForFile(new File(path.toString())));
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new DecompileException("encountered IOException during cleanup");
        }
    }

}
