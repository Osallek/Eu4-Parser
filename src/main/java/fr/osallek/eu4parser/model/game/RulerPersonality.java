package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;
import java.io.File;
import java.util.Objects;
import java.util.Optional;

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

    public Optional<ConditionAnd> getRulerAllow() {
        return this.item.getChild("ruler_allow").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getHeirAllow() {
        return this.item.getChild("heir_allow").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getConsortAllow() {
        return this.item.getChild("consort_allow").map(ConditionAnd::new);
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item);
    }

    public boolean isMonarchValid(Monarch monarch) {
        if (getAllow().isPresent() && !getAllow().get().apply(monarch.getCountry(), monarch.getCountry())) {
            return false;
        }

        if (Monarch.class.equals(monarch.getClass()) && (getRulerAllow().isEmpty() || getRulerAllow().get().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Heir.class.equals(monarch.getClass()) && (getHeirAllow().isEmpty() || getHeirAllow().get().apply(monarch.getCountry(), monarch.getCountry()))) {
            return false;
        }

        if (Queen.class.equals(monarch.getClass()) && (getConsortAllow().isEmpty() || getConsortAllow().get().apply(monarch.getCountry(), monarch.getCountry()))) {
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
