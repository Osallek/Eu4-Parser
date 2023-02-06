package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;
import java.io.File;
import java.util.Objects;

public class RulerPersonality {

    private final ClausewitzItem item;

    private final Game game;

    public RulerPersonality(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public ConditionAnd getRulerAllow() {
        ClausewitzItem child = item.getChild("ruler_allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setRulerAllow(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("ruler_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("ruler_allow");
        //Todo Condition => item
    }

    public ConditionAnd getHeirAllow() {
        ClausewitzItem child = item.getChild("heir_allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setHeirAllow(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("heir_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("heir_allow");
        //Todo Condition => item
    }

    public ConditionAnd getConsortAllow() {
        ClausewitzItem child = item.getChild("consort_allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setConsortAllow(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("consort_allow");
            return;
        }

        ClausewitzItem child = this.item.getChild("consort_allow");
        //Todo Condition => item
    }

    public ConditionAnd getAllow() {
        ClausewitzItem child = item.getChild("allow");
        return child == null ? null : new ConditionAnd(child);
    }

    public void setAllow(ConditionAnd condition) {
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

    public File getImage() {
        return getModifiers().getImage(this.game);
    }

    public Game getGame() {
        return game;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof RulerPersonality personality)) {
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
