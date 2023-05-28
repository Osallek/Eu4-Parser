package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.collections4.CollectionUtils;

import java.util.Optional;

public class Icon {

    private final ClausewitzItem item;

    public Icon(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<ConditionAnd> getAllow() {
        return this.item.getChild("allow").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return Optional.ofNullable(this.item.getVariables()).filter(CollectionUtils::isNotEmpty).map(Modifiers::new);
    }
}
