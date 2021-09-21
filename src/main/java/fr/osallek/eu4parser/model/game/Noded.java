package fr.osallek.eu4parser.model.game;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.Comparator;

public abstract class Noded implements Comparable<Noded> {

    protected FileNode fileNode;

    protected Noded() {
    }

    protected Noded(FileNode fileNode) {
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
    public int compareTo(Noded o) {
        return Comparator.comparing(Noded::getName).compare(this, o);
    }
}
