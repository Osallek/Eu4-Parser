package fr.osallek.eu4parser.model.game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;

public abstract class Nodded implements Comparable<Nodded> {

    protected FileNode fileNode;

    protected Nodded() {
    }

    protected Nodded(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public FileNode getFileNode() {
        return fileNode;
    }

    public void setFileNode(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public abstract String getName();

    public abstract void write(BufferedWriter writer) throws IOException;

    @Override
    public int compareTo(Nodded o) {
        return Comparator.comparing(Nodded::getName).compare(this, o);
    }
}
