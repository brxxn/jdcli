package me.brian.jdcli.command.commands;

import me.brian.jdcli.command.CommandException;
import me.brian.jdcli.command.ICommandHandler;

import java.io.IOException;
import java.util.Properties;

public class VersionCommand implements ICommandHandler {

    public VersionCommand() {}

    public int run(String[] args) throws CommandException {
        try {
            final Properties versionProperties = new Properties();
            versionProperties.load(this.getClass().getClassLoader().getResourceAsStream("version.properties"));
            String version = versionProperties.getProperty("version", "none");
            String releasesPage = versionProperties.getProperty("releases", "[unavailable]");
            System.out.println("jdcli version " + version);
            System.out.println("Check the releases page for newer versions: " + releasesPage);
        } catch (IOException exception) {
            exception.printStackTrace();
            throw new CommandException("failed to load version properties");
        }
        return 0;
    }

    public boolean shouldPrintUsage(int argumentCount) {
        return false;
    }

    public String getUsage() {
        return "jdcli version";
    }

}
