package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.effects.Effects;

import java.util.Optional;

public class GreatProjectTier {

    private final ClausewitzItem item;

    private final Game game;

    public GreatProjectTier(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Time getTime() {
        ClausewitzItem child = item.getChild("upgrade_time");
        return child == null ? null : new Time(child);
    }

    public void setTime(int months) {
        ClausewitzItem child = item.getChild("upgrade_time");

        if (child == null) {
            child = this.item.addChild("upgrade_time");
        }

        new Time(child).setMonths(months);
    }

    public int getBuildCost() {
        return this.item.getChild("cost_to_upgrade").getVarAsInt("factor");
    }

    public void setBuildCost(int buildCost) {
        ClausewitzItem child = this.item.getChild("cost_to_upgrade");

        if (child == null) {
            child = this.item.addChild("cost_to_upgrade");
        }

        child.setVariable("factor", buildCost);
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province_modifiers"));
    }

    public Modifiers getAreaModifier() {
        return new Modifiers(this.item.getChild("area_modifier"));
    }

    public Modifiers getCountryModifiers() {
        return new Modifiers(this.item.getChild("country_modifiers"));
    }

    public Effects getOnUpgraded() {
        return Optional.ofNullable(this.item.getChild("on_upgraded")).map(i -> new Effects(i, this.game)).orElse(null);
    }
}
