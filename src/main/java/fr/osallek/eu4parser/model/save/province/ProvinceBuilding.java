package fr.osallek.eu4parser.model.save.province;

import fr.osallek.eu4parser.model.game.todo.Building;

import java.time.LocalDate;
import java.util.Comparator;

public class ProvinceBuilding extends Building implements Comparable<ProvinceBuilding> {

    private final String name;

    private final String builder;

    private final LocalDate date;

    public ProvinceBuilding(ProvinceBuilding other, Building building) {
        super(building);
        this.name = other.name;
        this.builder = other.builder;
        this.date = other.date;
    }

    public ProvinceBuilding(String name, String builder, LocalDate date, Building building) {
        super(building);
        this.name = name;
        this.builder = builder;
        this.date = date;
    }

    @Override
    public String getName() {
        return name;
    }

    public String getBuilder() {
        return builder;
    }

    public LocalDate getDate() {
        return date;
    }

    @Override
    public int compareTo(ProvinceBuilding o) {
        return Comparator.nullsLast(LocalDate::compareTo).compare(this.date, o.date);
    }

    @Override
    public String toString() {
        return this.getLocalizedName();
    }
}
