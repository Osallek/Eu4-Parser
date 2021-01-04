package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.util.Objects;

public class HolyOrder {

    private final String name;

    private String localizedName;

    private final String icon;

    private final Power category;

    private final Condition trigger;

    private final Modifiers modifiers;

    public HolyOrder(ClausewitzItem item) {
        this.name = item.getName();
        this.icon = item.getVarAsString("icon");
        this.category = Power.byName(item.getVarAsString("cost_type").replace("_power", ""));
        this.modifiers = new Modifiers(item.getChild("modifier"));

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
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

    public String getIcon() {
        return icon;
    }

    public Power getCategory() {
        return category;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof HolyOrder)) {
            return false;
        }

        HolyOrder area = (HolyOrder) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
