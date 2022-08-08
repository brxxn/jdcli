package me.brian.jdcli;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.commands.*;
import me.brian.jdcli.command.ICommandHandler;

public class JDCliRunner {

    public static void printHelp() {
        System.out.println("usage: jdcli <command> [arguments...]");
        System.out.println();
        System.out.println("valid commands:");
        System.out.println("  jdcli dj|decompile-jar <jar file> [output directory] [--verbose] [--skip-cleanup] - Decompile a jar file into a directory");
        System.out.println("  jdcli dcs|decompile-classes <directory> [--verbose] [--delete-class-files] - Decompile classes in a directory");
        System.out.println("  jdcli dc|decompile-class <base directory> <file> [output name] - Decompile a .class file");
        System.out.println("  jdcli cleanup <directory> - removes all .class files in a directory");
        System.out.println("  jdcli h|help - Show this menu");
        System.out.println("  jdcli v|version - Show version information");
        System.out.println();
        System.out.println("options:");
        System.out.println("  -v --verbose | Log additional debug information");
    }

    public static void runCommand(ICommandHandler commandHandler, String[] args, boolean shouldExit) {
        String[] commandArguments = new String[args.length - 1];
        System.arraycopy(args, 1, commandArguments, 0, commandArguments.length);

        if (commandHandler.shouldPrintUsage(commandArguments.length)) {
            System.out.println("usage: " + commandHandler.getUsage());
            System.exit(1);
            return;
        }
        int status = 1;
        try {
            status = commandHandler.run(commandArguments);
        } catch (CommandException exception) {
            System.out.println("fatal error: " + exception.getMessage());
        }

        if (shouldExit) {
            System.exit(status);
        }
    }

    public static void main(String[] args) {
        if (args.length == 0) {
            printHelp();
            return;
        }

        String commandName = args[0].toLowerCase();
        switch (commandName) {
            case "dj", "decompile-jar" -> runCommand(new DecompileJarCommand(), args, true);
            case "dcs", "decompile-classes" -> runCommand(new DecompileClassesCommand(), args, true);
            case "dc", "decompile-class" -> runCommand(new DecompileClassCommand(), args, true);
            case "cleanup" -> runCommand(new CleanupCommand(), args, true);
            case "h", "help" -> runCommand(new HelpCommand(), args, true);
            case "v", "version" -> runCommand(new VersionCommand(), args, true);
            default -> System.out.println("invalid jdcli usage, use jdcli help for help.");
        }
    }
}
