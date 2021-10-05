package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Power;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdeaGroup {

    private final ClausewitzItem item;

    public IdeaGroup(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Power getCategory() {
        ClausewitzVariable variable = item.getVar("category");
        return variable == null ? null : Power.byName(variable.getValue());
    }

    public void setCategory(Power power) {
        this.item.setVariable("category", power.name());
    }

    public Boolean isFree() {
        return this.item.getVarAsBool("free");
    }

    public void setFree(Boolean free) {
        if (free == null) {
            this.item.removeVariable("free");
        } else {
            this.item.setVariable("free", free);
        }
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public Modifiers getStart() {
        return new Modifiers(this.item.getChild("start"));
    }

    public Modifiers getBonus() {
        return new Modifiers(this.item.getChild("bonus"));
    }

    public Map<String, Modifiers> getIdeas() {
        return this.item.getChildrenNot("start", "bonus", "trigger", "ai_will_do")
                        .stream()
                        .collect(Collectors.toMap(ClausewitzItem::getName, Modifiers::new, (a, b) -> b, LinkedHashMap::new));
    }

    public Double getModifier(int level, Modifier modifier) {
        List<Double> modifiers = new ArrayList<>();
        level = Math.min(level, getIdeas().size());

        if (level >= 0 && getStart().hasModifier(modifier)) {
            modifiers.add(getStart().getModifier(modifier));
        }

        Iterator<Modifiers> modifiersIterator = getIdeas().values().iterator();
        for (int i = level; i > 0; i--) {
            Modifiers m = modifiersIterator.next();

            if (m.hasModifier(modifier)) {
                modifiers.add(m.getModifier(modifier));
            }
        }

        if (level >= getIdeas().size() && getBonus().hasModifier(modifier)) {
            modifiers.add(getBonus().getModifier(modifier));
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof IdeaGroup)) {
            return false;
        }

        IdeaGroup ideaGroup = (IdeaGroup) o;

        return Objects.equals(getName(), ideaGroup.getName());
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
