package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Navy extends Army {

    private List<Ship> ships;

    public Navy(ClausewitzItem item, Country country) {
        super(item, country);
    }

    public List<Ship> getShips() {
        return ships;
    }

    public void addShip(String name, String type) {
        Integer home = 1;
        Double morale = 0.5d;

        if (this.ships != null && !this.ships.isEmpty()) {
            home = this.ships.get(0).getHome();
            morale = this.ships.get(0).getMorale();
        }

        addShip(name, home, type, morale);
    }

    public void addShip(String name, Integer home, String type, Double morale) {
        AbstractRegiment.addToItem(this.item, this.country.getSave().getAndIncrementUnitIdCounter(), name, home, type, morale);
        refreshAttributes();
    }

    public void removeShip(int index) {
        this.item.removeVariable("ship", index);
        refreshAttributes();
    }

    //Todo mission details
    public String getMission() {
        ClausewitzItem missionItem = this.item.getChild("mission");

        if (missionItem != null) {
            return missionItem.getChild(0).getName();
        }

        return null;
    }

    public void removeMission() {
        ClausewitzItem missionItem = this.item.getChild("mission");

        if (missionItem != null) {
            missionItem.removeChild(0);
        }
    }

    public Date getLastAtSea() {
        return this.item.getVarAsDate("last_at_sea");
    }

    public Double getBlockageEfficiency() {
        return this.item.getVarAsDouble("blockade_efficiency");
    }

    public List<Integer> getBlockade() {
        ClausewitzList list = this.item.getList("blockade");

        if (list != null) {
            return list.getValuesAsInt();
        }

        return new ArrayList<>();
    }

    public Double getActiveFractionLastMonth() {
        return this.item.getVarAsDouble("active_fraction_last_month");
    }

    protected static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int location,
                                              String graphicalCulture, int shipId, String shipName,
                                              int shipHome, String shipType, double shipMorale) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "navy", name, location, graphicalCulture, id);
        AbstractRegiment.addToItem(toItem, shipId, shipName, shipHome, shipType, shipMorale);

        return toItem;
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();

        List<ClausewitzItem> shipsItems = this.item.getChildren("ship");
        this.ships = shipsItems.stream()
                               .map(child -> new Ship(child, this.country.getSave(), this))
                               .collect(Collectors.toList());
    }
}
