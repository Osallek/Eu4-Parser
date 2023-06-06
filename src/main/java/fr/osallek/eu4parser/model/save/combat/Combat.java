package fr.osallek.eu4parser.model.save.combat;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Optional;
import java.util.function.BiFunction;

public abstract class Combat<C extends Combatant> {

    protected final Save save;

    protected final ClausewitzItem item;

    private final BiFunction<ClausewitzItem, Save, C> supplier;

    protected Id id;

    protected C attacker;

    protected C defender;

    protected Combat(ClausewitzItem item, Save save, BiFunction<ClausewitzItem, Save, C> supplier) {
        this.save = save;
        this.item = item;
        this.supplier = supplier;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public Optional<SaveProvince> getLocation() {
        return this.item.getVarAsInt("location").map(this.save::getProvince);
    }

    public Optional<Integer> getPhase() {
        return this.item.getVarAsInt("phase");
    }

    public Optional<Integer> getDay() {
        return this.item.getVarAsInt("day");
    }

    public void setDay(int day) {
        if (day < 0) {
            day = 0;
        }

        this.item.setVariable("day", day);
    }

    public Optional<Integer> getDuration() {
        return this.item.getVarAsInt("duration");
    }

    public C getAttacker() {
        return attacker;
    }

    public C getDefender() {
        return defender;
    }

    private void refreshAttributes() {
        this.id = this.item.getChild("id").map(Id::new).orElse(null);
        this.attacker = this.item.getChild("attacker").map(i -> this.supplier.apply(i, this.save)).orElse(null);
        this.defender = this.item.getChild("defender").map(i -> this.supplier.apply(i, this.save)).orElse(null);
    }
}
