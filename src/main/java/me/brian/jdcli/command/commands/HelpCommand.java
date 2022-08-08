package me.brian.jdcli.command.commands;

import me.brian.jdcli.JDCliRunner;
import me.brian.jdcli.command.ICommandHandler;

public class HelpCommand implements ICommandHandler {

    public HelpCommand() {}

    public int run(String[] args) {
        JDCliRunner.printHelp();
        return 0;
    }

    public boolean shouldPrintUsage(int argumentCount) {
        return false;
    }

    public String getUsage() {
        return "jdcli help";
    }

}
