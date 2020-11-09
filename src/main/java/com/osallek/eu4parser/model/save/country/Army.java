package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;

import java.util.List;
import java.util.stream.Collectors;

public class Army extends AbstractArmy {

    private Id mercenaryCompany;

    private List<Regiment> regiments;

    public Army(ClausewitzItem item, Country country) {
        super(item, country);
        refreshAttributes();
    }

    public Id getMercenaryCompany() {
        return mercenaryCompany;
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

    protected static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int location,
                                              String graphicalCulture, int regimentId, String regimentName,
                                              int regimentHome, String regimentType, double regimentMorale,
                                              double regimentDrill) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "army", name, location, graphicalCulture, id);
        Regiment.addToItem(toItem, regimentId, regimentName, regimentHome, regimentType, regimentMorale, regimentDrill);

        return toItem;
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();

        ClausewitzItem child = this.item.getChild("mercenary_company");
        if (child != null) {
            this.mercenaryCompany = new Id(child);
        }

        List<ClausewitzItem> regimentsItems = this.item.getChildren("regiment");
        this.regiments = regimentsItems.stream()
                                       .map(regimentItem -> new Regiment(regimentItem, this.country.getSave(), this))
                                       .collect(Collectors.toList());
    }
}
