package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class NationDesignerCost {

    private final ClausewitzItem item;

    public NationDesignerCost(ClausewitzItem item) {
        this.item = item;
    }

    public Condition getTrigger() {
        return this.item.getChild("trigger") == null ? null : new Condition(item.getChild("trigger"));
    }

    public void setTrigger(Condition condition) {
        if (condition == null) {
            this.item.removeChild("trigger");
            return;
        }

        ClausewitzItem triggerChild = this.item.getChild("trigger");
        //Todo Condition => item
    }

    public int getValue() {
        return this.item.getVarAsInt("value");
    }

    public void setValue(int value) {
        this.item.setVariable("value", value);
    }
}
