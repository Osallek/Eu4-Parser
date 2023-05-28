package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Optional;

public class GreatProjectTier {

    private final ClausewitzItem item;

    public GreatProjectTier(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<Time> getTime() {
        return this.item.getChild("upgrade_time").map(Time::new);
    }

    public void setTime(int months) {
        ClausewitzItem child = item.getChild("upgrade_time").orElse(this.item.addChild("upgrade_time"));
        new Time(child).setMonths(months);
    }

    public int getBuildCost() {
        return this.item.getChild("cost_to_upgrade").flatMap(c -> c.getVarAsInt("factor")).orElse(0);
    }

    public void setBuildCost(int buildCost) {
        ClausewitzItem child = this.item.getChild("cost_to_upgrade").orElse(this.item.addChild("cost_to_upgrade"));
        child.setVariable("factor", buildCost);
    }

    public Optional<Modifiers> getProvinceModifiers() {
        return this.item.getChild("province_modifiers").map(Modifiers::new);
    }

    public Optional<Modifiers> getAreaModifier() {
        return this.item.getChild("area_modifier").map(Modifiers::new);
    }

    public Optional<Modifiers> getCountryModifiers() {
        return this.item.getChild("country_modifiers").map(Modifiers::new);
    }

    public Optional<Modifiers> getOnUpgraded() {
        return this.item.getChild("on_upgraded").map(Modifiers::new);
    }
}
