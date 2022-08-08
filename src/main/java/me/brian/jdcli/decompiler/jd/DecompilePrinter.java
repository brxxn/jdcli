package me.brian.jdcli.decompiler.jd;

import org.jd.core.v1.api.printer.Printer;

public class DecompilePrinter implements Printer {

    private static final String NEWLINE = System.lineSeparator();
    private static final String TAB = "    "; // possible two-spaced tab option in future?

    private final StringBuilder stringBuilder;

    private int indentLevel;

    public DecompilePrinter() {
        this.stringBuilder = new StringBuilder();

        this.indentLevel = 0;
    }

    @Override
    public String toString() {
        return stringBuilder.toString();
    }

    @Override
    public void start(int i, int i1, int i2) {

    }

    @Override
    public void end() {
    }

    @Override
    public void printText(String text) {
        stringBuilder.append(text);
    }

    @Override
    public void printNumericConstant(String constant) {
        stringBuilder.append(constant);
    }

    @Override
    public void printStringConstant(String constant, String internalName) {
        stringBuilder.append(constant);
    }

    @Override
    public void printKeyword(String keyword) {
        stringBuilder.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
        stringBuilder.append(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor, String ownerInternalName) {
        stringBuilder.append(name);
    }

    @Override
    public void indent() {
        this.indentLevel++;
    }

    @Override
    public void unindent() {
        this.indentLevel--;
    }

    @Override
    public void startLine(int lineNumber) {
        // indent for each indentation count
        stringBuilder.append(TAB.repeat(Math.max(0, this.indentLevel)));
    }

    @Override
    public void endLine() {
        stringBuilder.append(NEWLINE);
    }

    @Override
    public void extraLine(int i) {
        // not printing extra line numbers to reduce storage usage
        stringBuilder.append(NEWLINE);
    }

    @Override
    public void startMarker(int type) { }

    @Override
    public void endMarker(int type) { }
}
