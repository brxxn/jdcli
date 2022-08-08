package me.brian.jdcli.command.commands;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.ICommandHandler;
import me.brian.jdcli.decompiler.DecompileException;
import me.brian.jdcli.decompiler.DecompileUtility;

import java.io.File;

public class DecompileClassesCommand implements ICommandHandler {

    public DecompileClassesCommand() {}

    public int run(String[] args) throws CommandException {
        String outputDirectoryString = args[0];
        File outputDirectory = new File(outputDirectoryString);

        if (!outputDirectory.exists()) {
            throw new CommandException("the target directory (" + outputDirectoryString + ") does not exist");
        }

        boolean verbose = false;
        boolean cleanup = false;

        // parse command line options
        for (int i = 1; i < args.length; i++) {
            switch (args[i].toLowerCase()) {
                case "--delete-class-files" -> cleanup = true;
                case "-v", "--verbose" -> verbose = true;
            }
        }

        System.out.println("Decompiling all .class files to .java files...");
        try {
            DecompileUtility.decompileAllClasses(outputDirectory, verbose);
        } catch (DecompileException exception) {
            throw new CommandException("error decompiling classes - " + exception.getMessage());
        }
        System.out.println("Finished decompiling .class files to .java files");

        if (cleanup) {
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
        return "jdcli dcs|decompile-classes <directory> [--verbose] [--delete-class-files]";
    }

}
