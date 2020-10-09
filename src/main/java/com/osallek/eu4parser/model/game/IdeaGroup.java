package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;
import org.apache.commons.lang3.BooleanUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class IdeaGroup {

    private final String name;

    private String localizedName;

    private final Power category;

    private final boolean free;

    private final Condition trigger;

    private final Map<String, String> start;

    private final Map<String, String> bonus;

    private final Map<String, Map<String, String>> ideas;

    public IdeaGroup(ClausewitzItem item) {
        this.name = item.getName();
        ClausewitzVariable var = item.getVar("category");
        this.category = var == null ? null : Power.valueOf(var.getValue().toUpperCase());
        this.free = BooleanUtils.toBoolean(item.getVarAsBool("free"));
        this.ideas = item.getChildrenNot("start", "bonus", "trigger", "ai_will_do")
                         .stream()
                         .collect(Collectors.toMap(ClausewitzItem::getName,
                                                   child -> child.getVariables()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                           ClausewitzVariable::getValue,
                                                                                           (a, b) -> b,
                                                                                           LinkedHashMap::new))));
        ClausewitzItem child = item.getChild("trigger");
        this.trigger = child == null ? null : new Condition(child);
        child = item.getChild("start");
        this.start = child == null ? null : child.getVariables()
                                                 .stream()
                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                           ClausewitzVariable::getValue,
                                                                           (a, b) -> b,
                                                                           LinkedHashMap::new));
        child = item.getChild("bonus");
        this.bonus = child == null ? null : child.getVariables()
                                                 .stream()
                                                 .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                           ClausewitzVariable::getValue,
                                                                           (a, b) -> b,
                                                                           LinkedHashMap::new));
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

    public Map<String, String> getStart() {
        return start;
    }

    public Map<String, String> getBonus() {
        return bonus;
    }

    public Map<String, Map<String, String>> getIdeas() {
        return ideas;
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
