package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class EstatePrivilegeModifier {

    private final ClausewitzItem item;

    public EstatePrivilegeModifier(ClausewitzItem item) {
        this.item = item;
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    public Boolean isBad() {
        return this.item.getVarAsBool("is_bad");
    }

    public void setIsBad(Boolean isBad) {
        if (isBad == null) {
            this.item.removeVariable("is_bad");
        } else {
            this.item.setVariable("is_bad", isBad);
        }
    }
}
