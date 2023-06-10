package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Optional;

public class Army extends AbstractArmy {

    public Army(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public Optional<Id> getMercenaryCompany() {
        return this.item.getChild("mercenary_company").map(Id::new);
    }

    public List<Regiment> getRegiments() {
        return this.item.getChildren("regiment").stream().map(regimentItem -> new Regiment(regimentItem, this.country.getSave(), this)).toList();
    }

    public void addRegiment(String name, String type) {
        int home = 1;
        double morale = 0.5d;
        double drill = 0d;

        List<Regiment> regiments = getRegiments();
        if (CollectionUtils.isNotEmpty(regiments)) {
            home = regiments.get(0).getHome().orElse(home);
            morale = regiments.get(0).getMorale().orElse(morale);
            drill = regiments.get(0).getDrill().orElse(drill);
        }

        addRegiment(name, home, type, morale, drill);
    }

    public void addRegiment(String name, int home, String type, double morale, double drill) {
        Regiment.addToItem(this.item, this.country.getSave().getAndIncrementUnitIdCounter(), name, home, type, morale, drill);
    }

    public void removeRegiment(int index) {
        this.item.removeVariable("regiment", index);
    }

    public boolean isDrilling() {
        return this.item.getChild("mission").map(i -> i.hasChild("drill_army_mission")).orElse(false);
    }

    public void setDrilling(boolean drilling) {
        if (drilling == isDrilling()) {
            return;
        }

        Optional<ClausewitzItem> child = this.item.getChild("mission");

        if (drilling) {
            if (child.isEmpty()) {
                child = Optional.of(this.item.addChild("mission"));
            }

            child.get().addChild("drill_army_mission");
        } else {
            child.ifPresent(clausewitzItem -> clausewitzItem.removeChild("drill_army_mission"));
        }
    }

    protected static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int location, String graphicalCulture, int regimentId,
                                              String regimentName, int regimentHome, String regimentType, double regimentMorale, double regimentDrill) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "army", name, location, graphicalCulture, id);
        Regiment.addToItem(toItem, regimentId, regimentName, regimentHome, regimentType, regimentMorale, regimentDrill);

        return toItem;
    }
}
