package me.brian.jdcli.command.commands;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.ICommandHandler;
import me.brian.jdcli.decompiler.DecompileException;
import me.brian.jdcli.decompiler.DecompileUtility;

import java.io.File;

public class DecompileClassCommand implements ICommandHandler {

    public DecompileClassCommand() {}

    public int run(String[] args) throws CommandException {
        String baseDirectoryPath = args[0];
        String targetClassFilePath = args[1];

        File baseDirectory = new File(baseDirectoryPath);
        File targetClassFile = new File(targetClassFilePath);

        if (!baseDirectory.exists() || !baseDirectory.isDirectory()) {
            throw new CommandException("base directory does not exist");
        }

        if (!targetClassFile.exists() || !targetClassFile.isFile()) {
            throw new CommandException("the targeted class file (" + targetClassFilePath + ") does not exist.");
        }

        System.out.println("Decompiling...");
        try {
            DecompileUtility.decompileClass(baseDirectory, targetClassFile);
        } catch (DecompileException exception) {
            throw new CommandException("decompile error - " + exception.getMessage());
        }
        System.out.println("Done!");

        return 0;
    }

    public boolean shouldPrintUsage(int argumentCount) {
        return argumentCount < 1;
    }

    public String getUsage() {
        return "jdcli dj|decompile-jar <jar file> [--verbose]";
    }

}
