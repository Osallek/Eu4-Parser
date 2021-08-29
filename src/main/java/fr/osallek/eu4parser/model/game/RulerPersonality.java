package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;

import java.util.Objects;

public class RulerPersonality {

    private final ClausewitzItem item;

    private String localizedName;

    public RulerPersonality(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Condition getRulerAllow() {
        ClausewitzItem child = item.getChild("ruler_allow");
        return child == null ? null : new Condition(child);
    }

    public void setRulerAllow(Condition condition) {
        if (condition == null) {
            this.item.removeChild("ruler_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("ruler_allow");
        //Todo Condition => item
    }

    public Condition getHeirAllow() {
        ClausewitzItem child = item.getChild("heir_allow");
        return child == null ? null : new Condition(child);
    }

    public void setHeirAllow(Condition condition) {
        if (condition == null) {
            this.item.removeChild("heir_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("heir_allow");
        //Todo Condition => item
    }

    public Condition getConsortAllow() {
        ClausewitzItem child = item.getChild("consort_allow");
        return child == null ? null : new Condition(child);
    }

    public void setConsortAllow(Condition condition) {
        if (condition == null) {
            this.item.removeChild("consort_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("consort_allow");
        //Todo Condition => item
    }

    public Condition getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new Condition(child);
    }

    public void setAllow(Condition condition) {
        if (condition == null) {
            this.item.removeChild("allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("allow");
        //Todo Condition => item
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item);
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public boolean isMonarchValid(Monarch monarch) {
        if (getAllow() != null && !getAllow().apply(monarch.getCountry(), monarch.getCountry())) {
            return false;
        }

        if (Monarch.class.equals(monarch.getClass()) && (getRulerAllow() == null || getRulerAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Heir.class.equals(monarch.getClass()) && (getHeirAllow() == null || getHeirAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Queen.class.equals(monarch.getClass()) && (getConsortAllow() == null || getConsortAllow().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RulerPersonality)) {
            return false;
        }

        RulerPersonality personality = (RulerPersonality) o;

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
