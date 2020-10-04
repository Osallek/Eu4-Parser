package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class FetishistCult {

    private final String name;

    private String localizedName;

    private final Map<String, String> modifiers;

    private final Condition allow;

    private final int sprite;

    public FetishistCult(ClausewitzItem item) {
        this.name = item.getName();
        this.allow = item.getChild("allow") == null ? null : new Condition(item.getChild("allow"));
        this.sprite = item.getVarAsInt("sprite");
        this.modifiers = item.getVarsNot("sprite").stream().collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                     ClausewitzVariable::getValue,
                                                                                     (a, b) -> b,
                                                                                     LinkedHashMap::new));

        if (this.allow != null) {
            this.allow.removeCondition("has_unlocked_cult", this.name); //Prevent endless recursive
        }
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

    public Map<String, String> getModifiers() {
        return modifiers;
    }

    public Condition getAllow() {
        return allow;
    }

    public int getSprite() {
        return sprite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof FetishistCult)) {
            return false;
        }

        FetishistCult that = (FetishistCult) o;
        return Objects.equals(name, that.name);
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
