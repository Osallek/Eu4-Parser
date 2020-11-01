package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Modifier;
import com.osallek.eu4parser.common.ModifiersUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Modifiers {

    private final Set<String> enables;

    private final Map<Modifier, String> modifiers;

    public Modifiers(ClausewitzItem item) {
        this(item == null ? new ArrayList<>() : item.getVariables());
    }

    public Modifiers(List<ClausewitzVariable> variables) {
        this.enables = variables.stream()
                                .filter(var -> "enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getName()))
                                               || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue()))
                                               || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue())))
                                .map(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()))
                                .collect(Collectors.toSet());

        this.modifiers = variables.stream()
                                  .filter(var -> !"enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getName()))
                                                 && !"yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue()))
                                                 && !"no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue())))
                                  .collect(Collectors.toMap(ModifiersUtils::getModifier, ClausewitzVariable::getValue, (a, b) -> b));
    }

    public Modifiers(ClausewitzVariable... variables) {
        this.enables = Arrays.stream(variables)
                             .filter(var -> "enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getName()))
                                            || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue()))
                                            || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue())))
                             .map(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()))
                             .collect(Collectors.toSet());

        this.modifiers = Arrays.stream(variables)
                               .filter(var -> !"enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getName()))
                                              && !"yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue()))
                                              && !"no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(var.getValue())))
                               .collect(Collectors.toMap(ModifiersUtils::getModifier, ClausewitzVariable::getValue, (a, b) -> b));
    }

    public Modifiers(Set<String> enables, Map<Modifier, String> modifiers) {
        this.enables = enables;
        this.modifiers = modifiers;
    }

    public static Modifiers copy(Modifiers other) {
        return new Modifiers(other.enables, other.modifiers);
    }

    public boolean isEmpty() {
        return this.enables.isEmpty() && this.modifiers.isEmpty();
    }

    public void add(String name, String value) {
        if (StringUtils.isBlank(name)) {
            return;
        }

        if ("enable".equals(ClausewitzUtils.removeQuotes(name.toLowerCase())) || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(value))
            || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(value))) {
            this.enables.add(ClausewitzUtils.removeQuotes(name.toLowerCase()));
        } else {
            ModifiersUtils.sumModifiers(name, value, this);
        }
    }

    public void addModifier(Modifier modifier, String value) {
        ModifiersUtils.sumModifiers(modifier, value, this);
    }

    public void addAll(Modifiers modifiers) {
        this.enables.addAll(modifiers.enables);
        modifiers.modifiers.forEach(this::addModifier);
    }

    public Set<String> getEnables() {
        return enables;
    }

    public boolean enable(String enable) {
        return enables.contains(ClausewitzUtils.removeQuotes(enable.toLowerCase()));
    }

    public void removeEnable(String enable) {
        this.enables.remove(ClausewitzUtils.removeQuotes(enable.toLowerCase()));
    }

    public boolean hasModifier(String modifier) {
        return this.modifiers.containsKey(ModifiersUtils.getModifier(modifier));
    }

    public String getModifier(String modifier) {
        return this.modifiers.get(ModifiersUtils.getModifier(modifier));
    }

    public String getModifier(Modifier modifier) {
        return this.modifiers.get(modifier);
    }

    public void removeModifier(String modifier) {
        this.modifiers.remove(ModifiersUtils.getModifier(modifier));
    }

    public void removeModifier(Modifier modifier) {
        this.modifiers.remove(modifier);
    }

    public Map<Modifier, String> getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "enables=" + enables + ", modifiers=" + modifiers;
    }
}
