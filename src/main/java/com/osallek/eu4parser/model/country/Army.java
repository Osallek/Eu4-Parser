package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Collectors;

public class Army extends AbstractArmy {

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

    public void addRegiment(String name, Integer home, String type, Double morale, Double drill) {
        Regiment.addToItem(this.item, this.country.getSave().incrementUnitIdCounter(), name, home, type, morale, drill);
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

        if (drilling) {
            ClausewitzItem child = this.item.getChild("mission");

            if (child == null) {
                child = this.item.addChild("mission");
            }

            child.addChild("drill_army_mission");
        } else {
            ClausewitzItem child = this.item.getChild("mission");

            if (child != null) {
                child.removeChild("drill_army_mission");
            }

        }
    }

    protected void refreshAttributes() {
        super.refreshAttributes();

        List<ClausewitzItem> regimentsItems = this.item.getChildren("regiment");
        this.regiments = regimentsItems.stream()
                                       .map(Regiment::new)
                                       .collect(Collectors.toList());
    }
}
