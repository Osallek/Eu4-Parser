package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.NumbersUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Modifiers {

    private final Set<String> enables;

    private final Map<Modifier, Double> modifiers;

    public Modifiers(ClausewitzItem item) {
        this(item == null ? new ArrayList<>() : item.getVariables());
    }

    public Modifiers(List<ClausewitzVariable> variables) {
        this(variables, "");
    }

    public Modifiers(List<ClausewitzVariable> variables, String prefix) {
        this.enables = variables.stream()
                                .filter(variable -> "enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getName()))
                                                    || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                    || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue())))
                                .map(variable -> ClausewitzUtils.removeQuotes(variable.getName().toLowerCase()))
                                .collect(Collectors.toSet());

        this.modifiers = variables.stream()
                                  .filter(variable -> !"enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getName()))
                                                      && !"yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                      && !"no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                      && NumbersUtils.toDouble(variable.getValue()) != null)
                                  .filter(variable -> ModifiersUtils.getModifier(variable) != null)
                                  .collect(Collectors.toMap(variable -> ModifiersUtils.getModifier(prefix + variable.getName()),
                                                            ClausewitzVariable::getAsDouble, (a, b) -> b));
    }

    public Modifiers(ClausewitzVariable... variables) {
        this.enables = Arrays.stream(variables)
                             .filter(variable -> "enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getName()))
                                                 || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                 || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue())))
                             .map(variable -> ClausewitzUtils.removeQuotes(variable.getName().toLowerCase()))
                             .collect(Collectors.toSet());

        this.modifiers = Arrays.stream(variables)
                               .filter(variable -> !"enable".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getName()))
                                                   && !"yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                   && !"no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(variable.getValue()))
                                                   && NumbersUtils.toDouble(variable.getValue()) != null)
                               .filter(variable -> ModifiersUtils.getModifier(variable) != null)
                               .collect(Collectors.toMap(ModifiersUtils::getModifier, ClausewitzVariable::getAsDouble, (a, b) -> b));
    }

    public Modifiers(Set<String> enables, Map<Modifier, Double> modifiers) {
        this.enables = enables;
        this.modifiers = modifiers;
    }

    public static Modifiers copy(Modifiers other) {
        return new Modifiers(new HashSet<>(other.enables), new HashMap<>(other.modifiers));
    }

    public boolean isEmpty() {
        return CollectionUtils.isEmpty(this.enables) && MapUtils.isEmpty(this.modifiers);
    }

    public void add(String name, String value) {
        if (StringUtils.isBlank(name)) {
            return;
        }

        if ("enable".equals(ClausewitzUtils.removeQuotes(name.toLowerCase())) || "yes".equalsIgnoreCase(ClausewitzUtils.removeQuotes(value))
            || "no".equalsIgnoreCase(ClausewitzUtils.removeQuotes(value))) {
            this.enables.add(ClausewitzUtils.removeQuotes(name.toLowerCase()));
        } else if (NumbersUtils.toDouble(value) != null) {
            ModifiersUtils.sumModifiers(name, NumbersUtils.toDouble(value), this);
        }
    }

    public void addModifier(Modifier modifier, Double value) {
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

    public boolean hasModifier(Modifier modifier) {
        return this.modifiers.containsKey(modifier);
    }

    public Double getModifier(String modifier) {
        return this.modifiers.get(ModifiersUtils.getModifier(modifier));
    }

    public Double getModifier(Modifier modifier) {
        return this.modifiers.get(modifier);
    }

    public void removeModifier(String modifier) {
        this.modifiers.remove(ModifiersUtils.getModifier(modifier));
    }

    public void removeModifier(Modifier modifier) {
        this.modifiers.remove(modifier);
    }

    public Map<Modifier, Double> getModifiers() {
        return modifiers;
    }

    public Modifiers getModifiers(String name) {
        return getModifiers(ModifiersUtils.getModifier(name));
    }

    public Modifiers getModifiers(Modifier modifier) {
        return new Modifiers(new HashSet<>(), this.modifiers.entrySet()
                                                            .stream()
                                                            .filter(entry -> entry.getKey().equals(modifier))
                                                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Modifiers getCountryModifiers() {
        return new Modifiers(this.enables, this.modifiers.entrySet()
                                                         .stream()
                                                         .filter(entry -> ModifierScope.COUNTRY.equals(entry.getKey().getScope()))
                                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.enables, this.modifiers.entrySet()
                                                         .stream()
                                                         .filter(entry -> ModifierScope.PROVINCE.equals(entry.getKey().getScope()))
                                                         .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    public File getImage(Game game) {
        AtomicReference<File> image = new AtomicReference<>(null);

        if (MapUtils.isNotEmpty(this.modifiers)) {
            this.modifiers.keySet()
                          .stream()
                          .map(m -> m.getImage(game))
                          .filter(Objects::nonNull)
                          .findFirst()
                          .ifPresent(image::set);
        }

        return image.get();
    }

    @Override
    public String toString() {
        return "enables=" + enables + ", modifiers=" + modifiers;
    }
}
