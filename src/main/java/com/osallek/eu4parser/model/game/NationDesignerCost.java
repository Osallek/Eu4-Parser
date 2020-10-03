package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class NationDesignerCost {

    private final Condition trigger;

    private final int value;

    public NationDesignerCost(ClausewitzItem item, ClausewitzItem triggerItem) {
        this.trigger = triggerItem != null ? new Condition(triggerItem) : item.getChild("trigger") == null ? null : new Condition(item.getChild("trigger"));
        this.value = item.getVarAsInt("value");
    }

    public Condition getTrigger() {
        return trigger;
    }

    public int getValue() {
        return value;
    }
}
