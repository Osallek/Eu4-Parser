package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Navy extends Army {

    public Navy(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public List<Ship> getShips() {
        return this.item.getChildren("ship").stream().map(child -> new Ship(child, this.country.getSave(), this)).toList();
    }

    public void addShip(String name, String type) {
        Integer home = 1;
        Double morale = 0.5d;

        List<Ship> ships = getShips();
        if (ships != null && !ships.isEmpty()) {
            home = ships.getFirst().getHome();
            morale = ships.getFirst().getMorale();
        }

        addShip(name, home, type, morale);
    }

    public void addShip(String name, Integer home, String type, Double morale) {
        AbstractRegiment.addToItem(this.item, this.country.getSave().getAndIncrementUnitIdCounter(), name, home, type, morale);
    }

    public void removeShip(int index) {
        this.item.removeVariable("ship", index);
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

    public LocalDate getLastAtSea() {
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

    public boolean isProtecting() {
        ClausewitzItem item = this.item.getChild("mission");

        if (item != null) {
            return item.hasChild("protect_mission");
        }

        return false;
    }

    public boolean isPrivateering() {
        ClausewitzItem item = this.item.getChild("mission");

        if (item != null) {
            return item.hasChild("privateer_mission_2");
        }

        return false;
    }
}
