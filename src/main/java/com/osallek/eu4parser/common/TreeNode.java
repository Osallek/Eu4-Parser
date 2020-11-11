package com.osallek.eu4parser.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TreeNode<T extends Comparable<T>> implements Comparable<TreeNode<T>> {

    private T data;

    private final SortedSet<TreeNode<T>> children = new TreeSet<>();

    private TreeNode<T> parent = null;

    private Integer depth;

    public TreeNode(T data) {
        this.depth = 1;
        this.data = data;
    }

    public TreeNode(TreeNode<T> parent, T data, Function<T, Collection<T>> getChildrenFunction) {
        setParent(parent);
        this.data = data;
        this.children.addAll(getChildrenFunction.apply(data)
                                                .stream()
                                                .map(t -> new TreeNode<>(this, t, getChildrenFunction))
                                                .collect(Collectors.toCollection(TreeSet::new)));
    }

    public boolean isRoot() {
        return parent == null;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public TreeNode<T> addChild(TreeNode<T> child) {
        child.setParent(this);

        this.children.remove(child);
        this.children.add(child);

        return child;
    }

    public void addChildren(Collection<TreeNode<T>> children) {
        children.forEach(each -> each.setParent(this));
        this.children.addAll(children);
    }

    public void removeChildrenIf(Predicate<T> predicate) {
        this.children.removeIf(t -> predicate.test(t.data));
        this.children.forEach(t -> t.removeChildrenIf(predicate));
    }

    public T getDataRecursive(Predicate<T> predicate) {
        if (predicate.test(this.data)) {
            return this.data;
        }

        for (TreeNode<T> child : this.children) {
            T t;

            if ((t = child.getDataRecursive(predicate)) != null) {
                return t;
            }
        }

        return null;
    }

    public TreeNode<T> getRecursive(Predicate<T> predicate) {
        if (predicate.test(this.data)) {
            return this;
        }

        for (TreeNode<T> child : this.children) {
            TreeNode<T> node;

            if ((node = child.getRecursive(predicate)) != null) {
                return node;
            }
        }

        return null;
    }

    @SafeVarargs
    public final List<TreeNode<T>> getLeaves(Predicate<T>... predicates) {
        List<TreeNode<T>> list = new ArrayList<>();
        getLeaves(list, predicates);
        return list;
    }

    @SafeVarargs
    public final void getLeaves(List<TreeNode<T>> list, Predicate<T>... predicates) {
        if (this.isLeaf()) {
            if (predicates.length == 0 || Arrays.stream(predicates).allMatch(predicate -> predicate.test(this.data))) {
                list.add(this);
            }
        } else {
            this.children.forEach(child -> child.getLeaves(list, predicates));
        }
    }

    public SortedSet<TreeNode<T>> getChildren() {
        return children;
    }

    public SortedSet<T> getChildrenData(Predicate<T> predicate) {
        return this.children.stream().filter(t -> predicate.test(t.data)).map(TreeNode::getData).collect(Collectors.toCollection(TreeSet::new));
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    private void setParent(TreeNode<T> parent) {
        if (parent != null) {
            this.parent = parent;
            this.depth = parent.depth + 1;
        } else {
            this.depth = 1;
        }
    }

    public TreeNode<T> getParent() {
        return parent;
    }

    public Integer getDepth() {
        return depth;
    }

    public Map<T, Integer> getDataAsListSorted() {
        Map<T, Integer> map = new LinkedHashMap<>();
        map.put(this.data, this.depth);

        for (TreeNode<T> child : this.children) {
            map.putAll(child.getDataAsListSorted());
        }

        return map;
    }

    public int getMaxDepth() {
        if (this.children.isEmpty()) {
            return this.depth;
        } else {
            return this.children.stream().map(TreeNode::getMaxDepth).max(Integer::compareTo).orElse(0);
        }
    }

    public void merge(TreeNode<T> other) {
        if (this.equals(other)) {
            if (this.isLeaf()) {
                this.parent.addChild(other);
            } else {
                other.getChildren()
                     .forEach(child -> this.children.stream()
                                                    .filter(o -> o.equals(child))
                                                    .findFirst()
                                                    .ifPresentOrElse(o -> o.merge(child), () -> this.addChild(child)));
            }
        }
    }

    @Override
    public int compareTo(TreeNode<T> o) {
        return data.compareTo(o.data);
    }

    @Override
    public String toString() {
        return this.data.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        TreeNode<?> that = (TreeNode<?>) obj;
        return Objects.equals(this.data, that.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }
}
