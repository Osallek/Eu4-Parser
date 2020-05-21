package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public class Id {

    private final ClausewitzItem item;

    public Id(ClausewitzItem item) {
        this.item = item;
    }

    public Long getId() {
        return this.item.getVarAsLong("id");
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, long id, int type) {
        return addToItem(parent, "id", id, type);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, long id, int type) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, parent.getOrder() + 1);
        toItem.addVariable("id", id);
        toItem.addVariable("type", type);

        parent.addChild(toItem);

        return toItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof Id)) {
            return false;
        }

        Id id = (Id) o;
        return Objects.equals(getId(), id.getId()) && Objects.equals(getType(), id.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getType());
    }
}
