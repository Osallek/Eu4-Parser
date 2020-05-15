package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Collectors;

public class Army extends AbstractArmy {

    //Todo with provinces location -> 		unit={
    //			id=6520
    //			type=54
    //		}

    private List<Regiment> regiments;

    public Army(ClausewitzItem item, Country country) {
        super(item, country);
        refreshAttributes();
    }

    public List<Regiment> getRegiments() {
        return regiments;
    }

    public void addRegiment(String name, String type) {
        Integer home = 1;
        Double morale = 0.5d;
        Double drill = 0d;

        if (this.regiments != null && !this.regiments.isEmpty()) {
            home = this.regiments.get(0).getHome();
            morale = this.regiments.get(0).getMorale();
            drill = this.regiments.get(0).getDrill();
        }

        addRegiment(name, home, type, morale, drill);
    }

    public void addRegiment(String name, int home, String type, double morale, double drill) {
        Regiment.addToItem(this.item, this.country.getSave()
                                                  .getAndIncrementUnitIdCounter(), name, home, type, morale, drill);
        refreshAttributes();
    }

    public void removeRegiment(int index) {
        this.item.removeVariable("regiment", index);
        refreshAttributes();
    }

    public boolean isDrilling() {
        ClausewitzItem item = this.item.getChild("mission");

        if (item != null) {
            return item.getVar("drill_army_mission") != null;
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

    protected static ClausewitzItem addToItem(ClausewitzItem parent, long id, String name, int location,
                                              String graphicalCulture, long regimentId, String regimentName,
                                              int regimentHome, String regimentType, double regimentMorale,
                                              double regimentDrill) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "army", name, location, graphicalCulture, id);
        Regiment.addToItem(toItem, regimentId, regimentName, regimentHome, regimentType, regimentMorale, regimentDrill);

        return toItem;
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();

        List<ClausewitzItem> regimentsItems = this.item.getChildren("regiment");
        this.regiments = regimentsItems.stream()
                                       .map(Regiment::new)
                                       .collect(Collectors.toList());
    }
}
