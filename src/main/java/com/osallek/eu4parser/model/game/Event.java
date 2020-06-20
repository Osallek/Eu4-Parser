package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Color;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Event {

    private final ClausewitzItem item;

    private String localizedName;

    public Event(ClausewitzItem item) {
        this.item = item;
    }

    public String getId() {
        return this.item.getVarAsString("id");
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getTitle() {
        return this.item.getVarAsString("title");
    }

    public String getDesc() {
        return this.item.getVarAsString("desc");
    }

    public String getPicture() {
        return this.item.getVarAsString("picture");
    }

    public EventType getType() {
        return EventType.getByType(this.item.getName());
    }

    public boolean fireOnlyOnce() {
        return Boolean.TRUE.equals(this.item.getVarAsBool("fire_only_once"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Event)) {
            return false;
        }

        Event tradeGood = (Event) o;
        return Objects.equals(getId(), tradeGood.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return this.localizedName;
    }
}
