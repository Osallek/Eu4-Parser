package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Optional;

public class EstatePrivilegeModifier {

    private final ClausewitzItem item;

    public EstatePrivilegeModifier(ClausewitzItem item) {
        this.item = item;
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public Optional<Modifiers> getModifiers() {
        return this.item.getChild("modifier").map(Modifiers::new);
    }

    public Optional<Boolean> isBad() {
        return this.item.getVarAsBool("is_bad");
    }

    public void setIsBad(Boolean isBad) {
        if (isBad == null) {
            this.item.removeVariable("is_bad");
        } else {
            this.item.setVariable("is_bad", isBad);
        }
    }
}
