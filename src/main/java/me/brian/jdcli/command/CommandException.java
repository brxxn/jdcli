package me.brian.jdcli.command;

public class CommandException extends Exception {

    private final String message;

    public CommandException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

}
