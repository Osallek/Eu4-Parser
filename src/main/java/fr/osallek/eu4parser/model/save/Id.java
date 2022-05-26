package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.Objects;

public record Id(ClausewitzItem item) {

    public Integer getId() {
        return this.item.getVarAsInt("id");
    }

    public Integer incrementId() {
        return incrementId(1);
    }

    public Integer incrementId(int step) {
        int id = getId() + step;
        this.item.setVariable("id", id);

        return id;
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, int type) {
        return addToItem(parent, "id", id, type);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, int id, int type) {
        return addToItem(parent, name, id, type, parent.getOrder() + 1);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, int id, int type, int order) {
        ClausewitzItem toItem = new ClausewitzItem(parent, name, order);
        toItem.addVariable("id", id);
        toItem.addVariable("type", type);

        parent.addChild(toItem, true);

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
