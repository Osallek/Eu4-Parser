package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class EstatePrivilegeModifier {

    private final Condition trigger;

    private final Map<String, String> modifiers;

    private final boolean isBad;

    public EstatePrivilegeModifier(ClausewitzItem item) {
        this.isBad = BooleanUtils.toBoolean(item.getVarAsBool("is_bad"));
        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
        child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables().stream().collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                                       ClausewitzVariable::getValue,
                                                                                                       (a, b) -> b,
                                                                                                       LinkedHashMap::new));
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Map<String, String> getModifiers() {
        return modifiers;
    }

    public boolean isBad() {
        return isBad;
    }
}
