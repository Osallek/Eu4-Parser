package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import fr.osallek.eu4parser.model.save.country.Leader;

import java.util.Objects;

public class LeaderPersonality {

    private final ClausewitzItem item;

    public LeaderPersonality(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = this.item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item);
    }

    public boolean isLeaderValid(Leader leader) {
        return getAllow() == null || !getAllow().apply(leader);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LeaderPersonality personality)) {
            return false;
        }

        return Objects.equals(getName(), personality.getName());
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
