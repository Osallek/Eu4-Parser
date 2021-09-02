package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;

import java.time.LocalDate;

public class Define {

    private String category;

    private String name;

    private Object value;

    private FileNode fileNode;

    public Define(String category, String name, Object value, FileNode fileNode) {
        this.category = category;
        this.name = name;
        this.value = value;
        this.fileNode = fileNode;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public FileNode getFileNode() {
        return fileNode;
    }

    public void setFileNode(FileNode fileNode) {
        this.fileNode = fileNode;
    }

    public String getAsString() {
        return this.value.toString();
    }

    public LocalDate getAsLocalDate() {
        return LocalDate.parse(ClausewitzUtils.removeQuotes((String) this.value), ClausewitzUtils.DATE_FORMAT);
    }

    public double getAsDouble() {
        return (double) this.value;
    }

    public int getAsInt() {
        return (int) this.value;
    }

    public void changeValue(String value) {
        if (String.class.equals(this.value.getClass())) {
            this.value = ClausewitzUtils.addQuotes(value);
        } else if (Integer.class.equals(this.value.getClass())) {
            this.value = Integer.parseInt(value);
        } else if (Double.class.equals(this.value.getClass())) {
            this.value = Double.parseDouble(value);
        }
    }

    @Override
    public String toString() {
        return category + " - " + name + " - " + value;
    }
}
