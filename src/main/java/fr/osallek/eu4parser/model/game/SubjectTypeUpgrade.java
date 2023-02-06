package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

public class SubjectTypeUpgrade extends Nodded {

    private final ClausewitzItem item;

    public SubjectTypeUpgrade(ClausewitzItem item, FileNode fileNode) {
        super(fileNode);
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Integer getCost() {
        return this.item.getVarAsInt("cost");
    }

    public void setCost(Integer cost) {
        this.item.setVariable("cost", cost);
    }

    public ConditionAnd getCanUpgradeTrigger() {
        ClausewitzItem child = this.item.getChild("can_upgrade_trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getModifiersOverlord() {
        return new Modifiers(this.item.getChild("modifier_overlord"));
    }

    public Modifiers getModifiersSubject() {
        return new Modifiers(this.item.getChild("modifier_subject"));
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

        if (!(o instanceof SubjectTypeUpgrade upgrade)) {
            return false;
        }

        return Objects.equals(getName(), upgrade.getName());
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
