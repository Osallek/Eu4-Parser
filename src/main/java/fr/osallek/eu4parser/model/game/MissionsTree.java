package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MissionsTree extends Nodded {

    private final ClausewitzItem item;

    private final Game game;

    public MissionsTree(ClausewitzItem item, Game game, FileNode fileNode) {
        super(fileNode);
        this.item = item;
        this.game = game;
    }

    @Override
    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Integer getSlot() {
        return this.item.getVarAsInt("slot");
    }

    public void setSlot(Integer slot) {
        if (slot == null) {
            this.item.removeVariable("slot");
        } else {
            this.item.setVariable("slot", slot);
        }
    }

    public Boolean isGeneric() {
        return this.item.getVarAsBool("generic");
    }

    public void setGeneric(Boolean generic) {
        if (generic == null) {
            this.item.removeVariable("generic");
        } else {
            this.item.setVariable("generic", generic);
        }
    }

    public Boolean isAi() {
        return this.item.getVarAsBool("ai");
    }

    public void setAi(Boolean ai) {
        if (ai == null) {
            this.item.removeVariable("ai");
        } else {
            this.item.setVariable("ai", ai);
        }
    }

    public Boolean hasCountryShield() {
        return this.item.getVarAsBool("has_country_shield");
    }

    public void setHasCountryShield(Boolean hasCountryShield) {
        if (hasCountryShield == null) {
            this.item.removeVariable("has_country_shield");
        } else {
            this.item.setVariable("has_country_shield", hasCountryShield);
        }
    }

    public ConditionAnd getPotentialOnLoad() {
        ClausewitzItem child = this.item.getChild("potential_on_load");
        return child == null ? null : new ConditionAnd(child);
    }

    public ConditionAnd getPotential() {
        ClausewitzItem child = this.item.getChild("potential");
        return child == null ? null : new ConditionAnd(child);
    }

    public List<Mission> getMissions() {
        return this.item.getChildrenNot("potential_on_load", "potential")
                        .stream()
                        .map(item1 -> new Mission(item1, game, this))
                        .toList();
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

        if (!(o instanceof MissionsTree missionsTree)) {
            return false;
        }

        return Objects.equals(getName(), missionsTree.getName());
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
