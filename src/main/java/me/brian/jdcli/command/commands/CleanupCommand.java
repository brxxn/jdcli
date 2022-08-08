package me.brian.jdcli.command.commands;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.ICommandHandler;
import me.brian.jdcli.decompiler.DecompileException;
import me.brian.jdcli.decompiler.DecompileUtility;

import java.io.File;

public class CleanupCommand implements ICommandHandler {

    public CleanupCommand() {}

    public int run(String[] args) throws CommandException {
        String outputDirectoryString = args[0];
        File outputDirectory = new File(outputDirectoryString);

        if (!outputDirectory.exists()) {
            throw new CommandException("the target directory (" + outputDirectoryString + ") does not exist");
        }

        System.out.println("Cleaning up...");
        try {
            DecompileUtility.cleanupDirectory(outputDirectory);
        } catch (DecompileException exception) {
            throw new CommandException("error cleaning up - " + exception.getMessage());
        }

        System.out.println("Done!");

        return 0;
    }

    public boolean shouldPrintUsage(int argumentCount) {
        return argumentCount < 1;
    }

    public String getUsage() {
        return "jdcli cleanup <directory>";
    }

}
