package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Map;
import java.util.Objects;

public class Investment {

    private final Game game;

    private final String name;

    private String localizedName;

    private final String category;

    private final Double cost;

    private final String upgradesTo;

    private final String sprite;

    private final Condition allow;

    private final Modifiers ownerModifier;

    private final Modifiers companyProvinceAreaModifier;

    private final Modifiers areaModifier;

    private final Modifiers ownerCompanyRegionModifier;

    private final Modifiers companyRegionModifier;

    public Investment(ClausewitzItem item, Game game) {
        this.game = game;
        this.name = item.getName();
        this.category = item.getVarAsString("category");
        this.cost = item.getVarAsDouble("cost");
        this.upgradesTo = item.getVarAsString("upgrades_to");
        this.sprite = ClausewitzUtils.removeQuotes(item.getVarAsString("sprite"));
        this.companyProvinceAreaModifier = new Modifiers(item.getChild("company_province_area_modifier"));
        this.ownerModifier = new Modifiers(item.getChild("owner_modifier"));
        this.areaModifier = new Modifiers(item.getChild("area_modifier"));
        this.ownerCompanyRegionModifier = new Modifiers(item.getChild("owner_company_region_modifier"));
        this.companyRegionModifier = new Modifiers(item.getChild("company_region_modifier"));

        ClausewitzItem child = item.getChild("allow");
        this.allow = child == null ? null : new Condition(child);
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getCategory() {
        return category;
    }

    public Double getCost() {
        return cost;
    }

    public Investment getUpgradesTo() {
        return this.upgradesTo == null ? null : this.game.getInvestment(this.upgradesTo);
    }

    public String getSprite() {
        return sprite;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getOwnerModifier() {
        return ownerModifier;
    }

    public Modifiers getCompanyProvinceAreaModifier() {
        return companyProvinceAreaModifier;
    }

    public Modifiers getAreaModifier() {
        return areaModifier;
    }

    public Modifiers getOwnerCompanyRegionModifier() {
        return ownerCompanyRegionModifier;
    }

    public Modifiers getCompanyRegionModifier() {
        return companyRegionModifier;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Investment that = (Investment) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
