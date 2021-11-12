package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileNode implements Comparable<FileNode> {

    private Path root;

    private Path path;

    private final Path relativePath;

    private Mod mod;

    public FileNode(Mod mod) {
        this(mod.getPath(), mod);
    }

    public FileNode(Path path, Mod mod) {
        this.root = path;
        this.path = path;
        this.mod = mod;
        this.relativePath = this.root.relativize(this.path);
    }

    public FileNode(Path root, Path relativePath) {
        this.root = root;
        this.relativePath = relativePath;
        this.path = this.root.resolve(this.relativePath);
    }

    public FileNode(Path root, Path path, Mod mod) {
        this.root = root;
        this.path = path;
        this.mod = mod;
        this.relativePath = this.root.relativize(this.path);
    }

    public FileNode(Mod mod, Path relativePath) {
        this.root = mod.getPath();
        this.path = this.root.resolve(relativePath);
        this.mod = mod;
        this.relativePath = relativePath;
    }

    public Path getRoot() {
        return root;
    }

    public Path getPath() {
        return path;
    }

    public Path getRelativePath() {
        return this.relativePath;
    }

    public static List<FileNode> getChildren(FileNode fileNode) {
        if (Files.isDirectory(fileNode.path)) {
            try (Stream<Path> stream = Files.list(fileNode.path)) {
                return stream.map(p -> new FileNode(fileNode.root, p, fileNode.mod)).collect(Collectors.toList());
            } catch (IOException e) {
                return new ArrayList<>();
            }
        } else {
            return new ArrayList<>();
        }
    }

    public boolean fromMod() {
        return mod != null;
    }

    public Mod getMod() {
        return mod;
    }

    public void setMod(Mod mod) {
        this.mod = mod;
        this.root = mod.getPath();
        this.path = this.root.resolve(relativePath);
    }

    @Override
    public int compareTo(FileNode o) {
        return Comparator.comparing(FileNode::getRelativePath).compare(this, o);
    }

    @Override
    public String toString() {
        return this.relativePath.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        FileNode fileNode = (FileNode) o;
        return Objects.equals(this.relativePath, fileNode.relativePath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.relativePath);
    }
}
