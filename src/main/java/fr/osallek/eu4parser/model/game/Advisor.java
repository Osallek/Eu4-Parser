package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.Power;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class Advisor extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public Advisor(ClausewitzItem item, Game game, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getPower() {
        return Power.byName(this.item.getVarAsString("monarch_power"));
    }

    public void setPower(Power power) {
        this.item.setVariable("monarch_power", power.name());
    }

    public Boolean allowOnlyMale() {
        return this.item.getVarAsBool("allow_only_male");
    }

    public void setAllowOnlyMale(Boolean onlyMale) {
        if (onlyMale == null) {
            this.item.removeVariable("allow_only_male");
        } else {
            this.item.setVariable("allow_only_male", onlyMale);
        }
    }

    public Boolean allowOnlyFemale() {
        return this.item.getVarAsBool("allow_only_female");
    }

    public void setAllowOnlyFemale(Boolean onlyFemale) {
        if (onlyFemale == null) {
            this.item.removeVariable("allow_only_female");
        } else {
            this.item.setVariable("allow_only_female", onlyFemale);
        }
    }

    public Modifiers getSkillScaledModifier() {
        return new Modifiers(this.item.getChild("skill_scaled_modifier"));
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getVarsNot("monarch_power", "allow_only_male", "allow_only_female"));
    }

    public String getDefaultSpriteName() {
        return "GFX_advisor_" + this.getName();
    }

    public SpriteType getDefaultSprite() {
        return this.game.getSpriteType(getDefaultSpriteName());
    }

    @Override
    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Advisor)) {
            return false;
        }

        Advisor area = (Advisor) o;

        return Objects.equals(getName(), area.getName());
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
