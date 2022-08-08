package me.brian.jdcli.command;

/**
 * An interface that manages some subcommands of jdcli
 */
public interface ICommandHandler {

    /**
     * Runs the command
     *
     * @param arguments arguments supplied by the user
     * @return the error status code that should be used on exit. (0 = success, 1 = error...)
     */
    int run(String[] arguments) throws CommandException;

    /**
     * Determines if command usage should be printed
     *
     * @param argumentCount number of arguments supplied by the user
     * @return a boolean that is true when the amount of arguments supplied is invalid
     */
    boolean shouldPrintUsage(int argumentCount);

    /**
     * Get usage for the command
     * @return a string containing how the command should be used
     */
    String getUsage();

}
