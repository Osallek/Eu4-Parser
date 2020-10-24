package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Investment {

    private final Game game;

    private final String name;

    private String localizedName;

    private final String category;

    private final Double cost;

    private final String upgradesTo;

    private final String sprite;

    private final Condition allow;

    private final Map<String, String> ownerModifier;

    private final Map<String, String> companyProvinceAreaModifier;

    private final Map<String, String> areaModifier;

    private final Map<String, String> ownerCompanyRegionModifier;

    private final Map<String, String> companyRegionModifier;

    public Investment(ClausewitzItem item, Game game) {
        this.game = game;
        this.name = item.getName();
        this.category = item.getVarAsString("category");
        this.cost = item.getVarAsDouble("cost");
        this.upgradesTo = item.getVarAsString("upgrades_to");
        this.sprite = ClausewitzUtils.removeQuotes(item.getVarAsString("sprite"));

        ClausewitzItem child = item.getChild("company_province_area_modifier");
        this.companyProvinceAreaModifier = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                                        .stream()
                                                                                        .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                                  ClausewitzVariable::getValue,
                                                                                                                  (a, b) -> b,
                                                                                                                  LinkedHashMap::new));

        child = item.getChild("owner_modifier");
        this.ownerModifier = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                          .stream()
                                                                          .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                    ClausewitzVariable::getValue,
                                                                                                    (a, b) -> b,
                                                                                                    LinkedHashMap::new));

        child = item.getChild("area_modifier");
        this.areaModifier = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                         .stream()
                                                                         .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                   ClausewitzVariable::getValue,
                                                                                                   (a, b) -> b,
                                                                                                   LinkedHashMap::new));

        child = item.getChild("owner_company_region_modifier");
        this.ownerCompanyRegionModifier = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                                       .stream()
                                                                                       .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                                 ClausewitzVariable::getValue,
                                                                                                                 (a, b) -> b,
                                                                                                                 LinkedHashMap::new));

        child = item.getChild("company_region_modifier");
        this.companyRegionModifier = child == null ? new LinkedHashMap<>() : child.getVariables()
                                                                                  .stream()
                                                                                  .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                            ClausewitzVariable::getValue,
                                                                                                            (a, b) -> b,
                                                                                                            LinkedHashMap::new));

        child = item.getChild("allow");
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

    public Map<String, String> getOwnerModifier() {
        return ownerModifier;
    }

    public Map<String, String> getCompanyProvinceAreaModifier() {
        return companyProvinceAreaModifier;
    }

    public Map<String, String> getAreaModifier() {
        return areaModifier;
    }

    public Map<String, String> getOwnerCompanyRegionModifier() {
        return ownerCompanyRegionModifier;
    }

    public Map<String, String> getCompanyRegionModifier() {
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
