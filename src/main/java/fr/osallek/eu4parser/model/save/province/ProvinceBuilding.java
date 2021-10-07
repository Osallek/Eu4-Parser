package fr.osallek.eu4parser.model.save.province;

import fr.osallek.eu4parser.model.game.Building;

import java.time.LocalDate;
import java.util.Comparator;

public class ProvinceBuilding implements Comparable<ProvinceBuilding> {

    private final Building building;

    private final String name;

    private final String builder;

    private final LocalDate date;

    public ProvinceBuilding(ProvinceBuilding other, Building building) {
        this.name = other.name;
        this.builder = other.builder;
        this.date = other.date;
        this.building = building;
    }

    public ProvinceBuilding(String name, String builder, LocalDate date, Building building) {
        this.name = name;
        this.builder = builder;
        this.date = date;
        this.building = building;
    }

    public Building getBuilding() {
        return building;
    }

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
        return this.getName();
    }
}
