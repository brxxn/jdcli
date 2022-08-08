package me.brian.jdcli.archive;

public class ArchiveException extends Exception {

    private final ArchiveExceptionType type;
    private final String message;

    public ArchiveException(ArchiveExceptionType type, String message) {
        this.type = type;
        this.message = message;
    }

    public ArchiveExceptionType getType() {
        return this.type;
    }

    public String getMessage() {
        return this.message;
    }

}
