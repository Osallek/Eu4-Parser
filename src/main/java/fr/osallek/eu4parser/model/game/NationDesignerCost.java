package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Optional;

public class NationDesignerCost {

    private final ClausewitzItem item;

    public NationDesignerCost(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public void setTrigger(ConditionAnd condition) {
        if (condition == null) {
            this.item.removeChild("trigger");
            return;
        }

//        ClausewitzItem triggerChild = this.item.getChild("trigger");
        //Todo Condition => item
    }

    public int getValue() {
        return this.item.getVarAsInt("value").orElse(0);
    }

    public void setValue(int value) {
        this.item.setVariable("value", value);
    }
}
