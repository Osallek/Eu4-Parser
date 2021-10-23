package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.common.Eu4Utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;

public class Define extends Nodded {

    private String category;

    private String name;

    private Object value;

    public Define(String category, String name, Object value, FileNode fileNode) {
        super(fileNode);
        this.category = category;
        this.name = name;
        this.value = value;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
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
    public void write(BufferedWriter writer) throws IOException {
        writer.write(Eu4Utils.DEFINE_KEY + "." + this.category + "." + this.name + " = " + this.value);
    }

    @Override
    public String toString() {
        return category + " - " + name + " - " + value;
    }
}
