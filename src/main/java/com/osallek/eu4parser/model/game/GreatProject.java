package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class GreatProject {

    private final String name;

    private String localizedName;

    private final String ambientObject;

    private final int province;

    private final boolean isCanal;

    private final int time;

    private final Modifiers modifiers;

    public GreatProject(ClausewitzItem item) {
        this.name = item.getName();
        this.ambientObject = item.getVarAsString("ambient_object");
        this.province = item.getVarAsInt("province");
        this.isCanal = BooleanUtils.toBoolean(item.getVarAsBool("is_canal"));
        this.time = item.getVarAsInt("time");

        this.modifiers = new Modifiers(item.getChild("modifier"));
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

    public Modifiers getModifiers() {
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
