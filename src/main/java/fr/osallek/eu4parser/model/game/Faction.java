package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Objects;

public class Faction {

    private final ClausewitzItem item;

    public Faction(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        ClausewitzVariable variable = item.getVar("monarch_power");
        return variable == null ? null : Power.byName(variable.getValue());
    }

    public void setCategory(Power category) {
        this.item.setVariable("monarch_power", category.name());
    }

    public Condition getTrigger() {
        return new Condition(this.item, "monarch_power", "modifier", "triggered_faction_name");
    }

    public List<Names> getNames() {
        List<ClausewitzItem> namesItems = this.item.getChildren("triggered_faction_name");
        return namesItems.stream().map(Names::new).toList();
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Faction faction)) {
            return false;
        }

        return Objects.equals(getName(), faction.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
