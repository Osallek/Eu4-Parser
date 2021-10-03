package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Leader;

import java.util.Objects;

public class LeaderPersonality {

    private final String name;

    private String localizedName;

    private final Condition allow;

    private final Modifiers modifiers;

    public LeaderPersonality(ClausewitzItem item) {
        this.name = item.getName();

        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
        this.modifiers = new Modifiers(item);
    }

    public LeaderPersonality(LeaderPersonality other) {
        this.name = other.name;
        this.localizedName = other.localizedName;
        this.allow = other.allow;
        this.modifiers = other.modifiers;
    }

    public Condition getAllow() {
        return allow;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean isLeaderValid(Leader leader) {
        return getAllow() == null || !getAllow().apply(leader);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof LeaderPersonality)) {
            return false;
        }

        LeaderPersonality personality = (LeaderPersonality) o;

        return Objects.equals(name, personality.name);
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
