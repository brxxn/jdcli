package me.brian.jdcli.command.commands;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.ICommandHandler;
import me.brian.jdcli.decompiler.DecompileException;
import me.brian.jdcli.decompiler.DecompileUtility;

import java.io.File;

public class DecompileJarCommand implements ICommandHandler {

    public DecompileJarCommand() {}

    public int run(String[] args) throws CommandException {
        String jarFileString = args[0];
        String outputDirectoryString = (args.length >= 2 && !args[1].startsWith("-")) ? args[1] : jarFileString + ".classes/";

        File jarFile = new File(jarFileString);
        File outputDirectory = new File(outputDirectoryString);

        if (!jarFile.exists() || !jarFile.isFile()) {
            throw new CommandException("jar file does not exist");
        }

        if (!jarFile.canRead()) {
            throw new CommandException("cannot read the jar file, check your permissions and try again.");
        }

        if (outputDirectory.exists()) {
            throw new CommandException("the targeted output directory (" + outputDirectoryString + ") already exists; delete it and retry");
        }

        if (!outputDirectory.mkdirs()) {
            throw new CommandException("failed to create output directory due to an i/o error.");
        }

        boolean verbose = false;
        boolean skipCleanup = false;

        // parse command line options
        for (int i = 1; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "-sc", "--skip-cleanup" -> skipCleanup = true;
                case "-v", "--verbose" -> verbose = true;
            }
        }

        System.out.println("Extracting files from jar...");
        try {
            DecompileUtility.extractJar(jarFile, outputDirectory);
        } catch (DecompileException exception) {
            throw new CommandException("error extracting files from jar - " + exception.getMessage());
        }
        System.out.println("Successfully extracted all files from jar into " + outputDirectoryString);

        System.out.println("Decompiling all .class files to .java files...");
        try {
            DecompileUtility.decompileAllClasses(outputDirectory, verbose);
        } catch (DecompileException exception) {
            throw new CommandException("error decompiling classes - " + exception.getMessage());
        }
        System.out.println("Finished decompiling .class files to .java files");

        if (!skipCleanup) {
            System.out.println("Cleaning up...");
            try {
                DecompileUtility.cleanupDirectory(outputDirectory);
            } catch (DecompileException exception) {
                throw new CommandException("cleanup failed - " + exception.getMessage());
            }
        }

        System.out.println("Done!");

        return 0;
    }

    public boolean shouldPrintUsage(int argumentCount) {
        return argumentCount < 1;
    }

    public String getUsage() {
        return "jdcli dj|decompile-jar <jar file> [output directory] [--verbose] [--skip-cleanup]";
    }

}
