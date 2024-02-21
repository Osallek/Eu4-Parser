package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;

import java.util.List;

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

    public void addRegiment(String name, String type) {
        Integer home = 1;
        Double morale = 0.5d;
        Double drill = 0d;

        List<Regiment> regiments = getRegiments();
        if (regiments != null && !regiments.isEmpty()) {
            home = regiments.getFirst().getHome();
            morale = regiments.getFirst().getMorale();
            drill = regiments.getFirst().getDrill();
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

    protected static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int location, String graphicalCulture, int regimentId,
                                              String regimentName, int regimentHome, String regimentType, double regimentMorale, double regimentDrill) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "army", name, location, graphicalCulture, id);
        Regiment.addToItem(toItem, regimentId, regimentName, regimentHome, regimentType, regimentMorale, regimentDrill);

        return toItem;
    }
}
