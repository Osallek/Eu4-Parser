package fr.osallek.eu4parser.model.game;

import org.jetbrains.annotations.NotNull;

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

    private final Path root;

    private final Path path;

    private final Path relativePath;

    public FileNode(Path path) {
        this.root = path;
        this.path = path;
        this.relativePath = this.root.relativize(this.path);
    }

    public FileNode(Path root, Path path) {
        this.root = root;
        this.path = path;
        this.relativePath = this.root.relativize(this.path);
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
        try (Stream<Path> stream = Files.list(fileNode.path)) {
            return stream.map(p -> new FileNode(fileNode.root, p)).collect(Collectors.toList());
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    @Override
    public int compareTo(@NotNull FileNode o) {
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
