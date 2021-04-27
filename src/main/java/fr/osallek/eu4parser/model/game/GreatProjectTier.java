package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class GreatProjectTier {

    private final String name;

    private Time time;

    private Integer buildCost;

    private Modifiers provinceModifiers;

    private Modifiers areaModifier;

    private Modifiers countryModifiers;

    public GreatProjectTier(ClausewitzItem item) {
        this.name = item.getName();

        if (item.hasChild("upgrade_time")) {
            this.time = new Time(item.getChild("upgrade_time"));
        }

        if (item.hasChild("cost_to_upgrade")) {
            this.buildCost = item.getChild("cost_to_upgrade").getVarAsInt("factor");
        }

        this.provinceModifiers = new Modifiers(item.getChild("province_modifiers"));
        this.areaModifier = new Modifiers(item.getChild("area_modifier"));
        this.countryModifiers = new Modifiers(item.getChild("country_modifiers"));
    }

    public String getName() {
        return name;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Integer getBuildCost() {
        return buildCost;
    }

    public void setBuildCost(Integer buildCost) {
        this.buildCost = buildCost;
    }

    public Modifiers getProvinceModifiers() {
        return provinceModifiers;
    }

    public void setProvinceModifiers(Modifiers provinceModifiers) {
        this.provinceModifiers = provinceModifiers;
    }

    public Modifiers getAreaModifier() {
        return areaModifier;
    }

    public void setAreaModifier(Modifiers areaModifier) {
        this.areaModifier = areaModifier;
    }

    public Modifiers getCountryModifiers() {
        return countryModifiers;
    }

    public void setCountryModifiers(Modifiers countryModifiers) {
        this.countryModifiers = countryModifiers;
    }
}
