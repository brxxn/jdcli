package me.brian.jdcli.decompiler;

public class DecompileException extends Exception {

    private final String message;

    public DecompileException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
