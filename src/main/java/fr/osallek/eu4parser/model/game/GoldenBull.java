package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

public class GoldenBull {

    private final ClausewitzItem item;

    public GoldenBull(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public List<String> getMechanics() {
        ClausewitzList list = this.item.getList("mechanics");
        return list == null ? null : list.getValues();
    }

    public void setMechanics(List<String> mechanics) {
        if (CollectionUtils.isEmpty(mechanics)) {
            this.item.removeList("mechanics");
            return;
        }

        ClausewitzList list = this.item.getList("mechanics");

        if (list != null) {
            list.setAll(mechanics.stream().filter(Objects::nonNull).toArray(String[]::new));
        } else {
            this.item.addList("mechanics", mechanics.stream().filter(Objects::nonNull).toArray(String[]::new));
        }
    }

    public Modifiers getModifiers() {
        return new Modifiers(this.item.getChild("modifier"));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && getName() == null) {
            return true;
        }

        if (!(o instanceof GoldenBull)) {
            return false;
        }
        GoldenBull decree = (GoldenBull) o;
        return Objects.equals(getName(), decree.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
