package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class GreatProject {

    private final String name;

    private String localizedName;

    private final String ambientObject;

    private final int province;

    private final boolean isCanal;

    private final int time;

    private final Map<String, List<String>> modifiers;

    public GreatProject(ClausewitzItem item) {
        this.name = item.getName();
        this.ambientObject = item.getVarAsString("ambient_object");
        this.province = item.getVarAsInt("province");
        this.isCanal = BooleanUtils.toBoolean(item.getVarAsBool("is_canal"));
        this.time = item.getVarAsInt("time");

        ClausewitzItem child = item.getChild("modifier");
        this.modifiers = child == null ? null : child.getVariables()
                                                     .stream()
                                                     .collect(Collectors.groupingBy(ClausewitzVariable::getName,
                                                                                    Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));
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

    public String getAmbientObject() {
        return ambientObject;
    }

    public int getProvince() {
        return province;
    }

    public boolean isCanal() {
        return isCanal;
    }

    public int getTime() {
        return time;
    }

    public Map<String, List<String>> getModifiers() {
        return modifiers;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof GreatProject)) {
            return false;
        }

        GreatProject area = (GreatProject) o;

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
