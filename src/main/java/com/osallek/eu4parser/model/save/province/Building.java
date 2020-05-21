package com.osallek.eu4parser.model.save.province;

import java.util.Comparator;
import java.util.Date;

public class Building implements Comparable<Building> {

    private final String name;

    private final String builder;

    private final Date date;

    public Building(String name, String builder, Date date) {
        this.name = name;
        this.builder = builder;
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public String getBuilder() {
        return builder;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compareTo(Building o) {
        return Comparator.nullsLast(Date::compareTo).compare(this.date, o.date);
    }
}
