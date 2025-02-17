package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Unit;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.counters.Counter;

import java.util.List;
import java.util.stream.IntStream;

public class Army extends AbstractArmy {

    public Army(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public Id getMercenaryCompany() {
        ClausewitzItem child = this.item.getChild("mercenary_company");

        return child != null ? new Id(child) : null;
    }

    public List<Regiment> getRegiments() {
        return this.item.getChildren("regiment").stream().map(regimentItem -> new Regiment(regimentItem, this.country.getSave(), this)).toList();
    }

    public void addRegiment(Unit type) {
        Integer home = 1;
        Double morale = 0.5d;
        Double drill = 0d;

        List<Regiment> regiments = getRegiments();
        if (regiments != null && !regiments.isEmpty()) {
            home = regiments.getFirst().getHome();
            morale = regiments.getFirst().getMorale();
            drill = regiments.getFirst().getDrill();
        }

        addRegiment("Regiment", home, type, morale, drill);
    }

    public void addRegiment(String name, int home, Unit type, double morale, double drill) {
        Regiment.addToItem(this.item, this.country.getSave().getIdCounters().getAndIncrement(Counter.REGIMENT), name, home, type.getName(), morale, drill);
    }

    public void removeRegiment(int id) {
        List<Regiment> regiments = getRegiments();
        IntStream.range(0, regiments.size())
                 .filter(i -> id == regiments.get(i).getId().getId())
                 .findFirst()
                 .ifPresent(index -> this.item.removeChild("regiment", index));
    }

    public boolean isDrilling() {
        ClausewitzItem item = this.item.getChild("mission");

        if (item != null) {
            return item.hasChild("drill_army_mission");
        }

        return false;
    }

    public void setDrilling(boolean drilling) {
        if (drilling == isDrilling()) {
            return;
        }

        ClausewitzItem child = this.item.getChild("mission");

        if (drilling) {

            if (child == null) {
                child = this.item.addChild("mission");
            }

            child.addChild("drill_army_mission");
        } else {

            if (child != null) {
                child.removeChild("drill_army_mission");
            }

        }
    }
}
