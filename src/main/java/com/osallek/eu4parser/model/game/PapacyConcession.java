package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class PapacyConcession {

    private String name;

    private String harshLocalizedName;

    private String concilatoryLocalizedName;

    private Modifiers harshModifiers;

    private Modifiers concilatoryModifiers;

    public PapacyConcession(ClausewitzItem item) {
        this.name = item.getName();
        this.harshModifiers = new Modifiers(item.getChild("harsh"));
        this.concilatoryModifiers = new Modifiers(item.getChild("concilatory"));
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHarshLocalizedName() {
        return harshLocalizedName;
    }

    void setHarshLocalizedName(String harshLocalizedName) {
        this.harshLocalizedName = harshLocalizedName;
    }

    public String getConcilatoryLocalizedName() {
        return concilatoryLocalizedName;
    }

    void setConcilatoryLocalizedName(String concilatoryLocalizedName) {
        this.concilatoryLocalizedName = concilatoryLocalizedName;
    }

    public Modifiers getHarshModifiers() {
        return this.harshModifiers;
    }

    public void addHarshModifier(String modifier, String quantity) {
        if (this.harshModifiers == null) {
            this.harshModifiers = new Modifiers();
        }

        this.harshModifiers.add(modifier, quantity);
    }

    public void removeHarshModifier(String modifier) {
        if (this.harshModifiers != null) {
            this.harshModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getConcilatoryModifiers() {
        return this.concilatoryModifiers;
    }

    public void addConcilatoryModifier(String modifier, String quantity) {
        if (this.concilatoryModifiers == null) {
            this.concilatoryModifiers = new Modifiers();
        }

        this.concilatoryModifiers.add(modifier, quantity);
    }

    public void removeConcilatoryModifier(String modifier) {
        if (this.concilatoryModifiers != null) {
            this.concilatoryModifiers.removeModifier(modifier);
        }
    }
}
