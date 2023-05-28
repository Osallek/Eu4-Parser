package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
        return this.item.getList("mechanics").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setMechanics(List<String> mechanics) {
        if (CollectionUtils.isEmpty(mechanics)) {
            this.item.removeList("mechanics");
            return;
        }

        this.item.getList("mechanics").ifPresentOrElse(list -> list.setAll(mechanics.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                                       () -> this.item.addList("mechanics", mechanics.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null && getName() == null) {
            return true;
        }

        if (!(o instanceof GoldenBull goldenBull)) {
            return false;
        }
        return Objects.equals(getName(), goldenBull.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
