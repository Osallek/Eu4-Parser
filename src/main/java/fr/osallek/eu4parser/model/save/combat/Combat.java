package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.function.BiFunction;

public abstract class Combat<C extends Combatant> {

    protected final Save save;

    protected final ClausewitzItem item;

    private final BiFunction<ClausewitzItem, Save, C> supplier;

    protected Combat(ClausewitzItem item, Save save, BiFunction<ClausewitzItem, Save, C> supplier) {
        this.save = save;
        this.item = item;
        this.supplier = supplier;
    }

    public Id getId() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            return new Id(idChild);
        }

        return null;
    }

    public SaveProvince getLocation() {
        return this.save.getProvince(this.item.getVarAsInt("location"));
    }

    public Integer getPhase() {
        return this.item.getVarAsInt("phase");
    }

    public Integer getDay() {
        return this.item.getVarAsInt("day");
    }

    public void setDay(int day) {
        if (day < 0) {
            day = 0;
        }

        this.item.setVariable("day", day);
    }

    public Integer getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public C getAttacker() {
        ClausewitzItem attackerItem = this.item.getChild("attacker");

        if (attackerItem != null) {
            return supplier.apply(attackerItem, this.save);
        }

        return null;
    }

    public C getDefender() {
        ClausewitzItem defenderItem = this.item.getChild("defender");

        if (defenderItem != null) {
            return supplier.apply(defenderItem, this.save);
        }

        return null;
    }
}
