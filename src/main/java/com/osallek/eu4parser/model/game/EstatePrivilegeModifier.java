package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EstatePrivilegeModifier {

    private final Condition trigger;

    private final Modifiers modifiers;

    private final boolean isBad;

    public EstatePrivilegeModifier(ClausewitzItem item) {
        this.isBad = BooleanUtils.toBoolean(item.getVarAsBool("is_bad"));

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        this.modifiers = new Modifiers( item.getChild("modifier"));
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Modifiers getModifiers() {
        return modifiers;
    }

    public boolean isBad() {
        return isBad;
    }
}
