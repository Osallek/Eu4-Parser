package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.ModifiersUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Modifiers {

    private final Set<String> enables;

    private final Map<String, String> modifiers;

    public Modifiers(ClausewitzItem item) {
        this.enables = item == null ? null : item.getVariables("enable")
                                                 .stream()
                                                 .map(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()))
                                                 .collect(Collectors.toSet());

        this.modifiers = item == null ? null : item.getVarsNot("enable")
                                                   .stream()
                                                   .collect(Collectors.toMap(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()),
                                                                             ClausewitzVariable::getValue,
                                                                             (a, b) -> b));
    }

    public Modifiers(List<ClausewitzVariable> variables) {
        this.enables = variables.stream()
                                .map(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()))
                                .filter("enable"::equals)
                                .collect(Collectors.toSet());

        this.modifiers = variables.stream()
                                  .filter(var -> !"enable".equals(ClausewitzUtils.removeQuotes(var.getName().toLowerCase())))
                                  .collect(Collectors.toMap(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()),
                                                            ClausewitzVariable::getValue,
                                                            (a, b) -> b));
    }

    public Modifiers(ClausewitzVariable... variables) {
        this.enables = Arrays.stream(variables)
                             .map(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()))
                             .filter("enable"::equals)
                             .collect(Collectors.toSet());

        this.modifiers = Arrays.stream(variables)
                               .filter(var -> !"enable".equals(ClausewitzUtils.removeQuotes(var.getName().toLowerCase())))
                               .collect(Collectors.toMap(var -> ClausewitzUtils.removeQuotes(var.getName().toLowerCase()),
                                                         ClausewitzVariable::getValue,
                                                         (a, b) -> b));
    }

    public Modifiers(Map<String, String> variables) {
        this.enables = variables.keySet()
                                .stream()
                                .map(s -> ClausewitzUtils.removeQuotes(s.toLowerCase()))
                                .filter("enable"::equals)
                                .collect(Collectors.toSet());

        this.modifiers = variables;
        this.modifiers.entrySet().removeIf(entry -> "enable".equals(ClausewitzUtils.removeQuotes(entry.getKey().toLowerCase())));
    }

    public Modifiers(Set<String> enables, Map<String, String> modifiers) {
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

        if ("enable".equals(ClausewitzUtils.removeQuotes(name.toLowerCase()))) {
            this.enables.add(ClausewitzUtils.removeQuotes(name.toLowerCase()));
        } else {
            ModifiersUtils.sumModifiers(name, value, this);
        }
    }

    public void addAll(Modifiers modifiers) {
        this.enables.addAll(modifiers.enables);
        modifiers.modifiers.forEach(this::add);
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
        return this.modifiers.containsKey(ClausewitzUtils.removeQuotes(modifier.toLowerCase()));
    }

    public String getModifier(String modifier) {
        return this.modifiers.get(ClausewitzUtils.removeQuotes(modifier.toLowerCase()));
    }

    public void removeModifier(String modifier) {
        this.modifiers.remove(ClausewitzUtils.removeQuotes(modifier.toLowerCase()));
    }

    public Map<String, String> getModifiers() {
        return modifiers;
    }

    @Override
    public String toString() {
        return "enables=" + enables + ", modifiers=" + modifiers;
    }
}
