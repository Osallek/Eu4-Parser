package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Modifier;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.model.Power;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdeaGroup {

    private final String name;

    private String localizedName;

    private final Power category;

    private final boolean free;

    private final Condition trigger;

    private final Modifiers start;

    private final Modifiers bonus;

    private final Map<String, Modifiers> ideas;

    public IdeaGroup(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzVariable var = item.getVar("category");
        this.category = var == null ? null : Power.valueOf(var.getValue().toUpperCase());
        this.free = BooleanUtils.toBoolean(item.getVarAsBool("free"));
        this.ideas = item.getChildrenNot("start", "bonus", "trigger", "ai_will_do")
                         .stream()
                         .collect(Collectors.toMap(ClausewitzItem::getName, Modifiers::new, (a, b) -> b, LinkedHashMap::new));

        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);

        this.start = new Modifiers(item.getChild("start"));
        this.bonus = new Modifiers(item.getChild("bonus"));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public Power getCategory() {
        return category;
    }

    public boolean isFree() {
        return free;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public Modifiers getStart() {
        return start;
    }

    public Modifiers getBonus() {
        return bonus;
    }

    public Map<String, Modifiers> getIdeas() {
        return ideas;
    }

    public Double getModifier(int level, Modifier modifier) {
        List<Double> modifiers = new ArrayList<>();
        level = Math.min(level, this.ideas.size());

        if (level >= 0 && this.start.hasModifier(modifier)) {
            modifiers.add(this.start.getModifier(modifier));
        }

        Iterator<Modifiers> modifiersIterator = this.ideas.values().iterator();
        for (int i = level; i > 0; i--) {
            Modifiers m = modifiersIterator.next();

            if (m.hasModifier(modifier)) {
                modifiers.add(m.getModifier(modifier));
            }
        }

        if (level >= this.ideas.size() && this.bonus.hasModifier(modifier)) {
            modifiers.add(this.bonus.getModifier(modifier));
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

        IdeaGroup area = (IdeaGroup) o;

        return Objects.equals(name, area.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
