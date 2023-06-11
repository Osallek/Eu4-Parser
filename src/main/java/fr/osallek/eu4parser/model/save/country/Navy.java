package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Navy extends Army {

    public Navy(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public List<Ship> getShips() {
        return this.item.getChildren("ship").stream().map(child -> new Ship(child, this.country.getSave(), this)).toList();
    }

    public void addShip(String name, String type) {
        int home = 1;
        double morale = 0.5d;

        List<Ship> ships = getShips();
        if (CollectionUtils.isNotEmpty(ships)) {
            home = ships.get(0).getHome().orElse(home);
            morale = ships.get(0).getMorale().orElse(morale);
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
    public Optional<String> getMission() {
        return this.item.getChild("mission").flatMap(missionItem -> missionItem.getChild(0)).map(ClausewitzObject::getName);
    }

    public void removeMission() {
        this.item.getChild("mission").ifPresent(i -> i.removeChild(0));
    }

    public Optional<LocalDate> getLastAtSea() {
        return this.item.getVarAsDate("last_at_sea");
    }

    public Optional<Double> getBlockageEfficiency() {
        return this.item.getVarAsDouble("blockade_efficiency");
    }

    public List<Integer> getBlockade() {
        return this.item.getList("blockade").map(ClausewitzList::getValuesAsInt).orElse(new ArrayList<>());
    }

    public Optional<Double> getActiveFractionLastMonth() {
        return this.item.getVarAsDouble("active_fraction_last_month");
    }

    public boolean isProtecting() {
        return this.item.getChild("mission").filter(i -> i.hasChild("protect_mission")).isPresent();
    }

    public boolean isPrivateering() {
        return this.item.getChild("mission").filter(i -> i.hasChild("privateer_mission_2")).isPresent();
    }

    protected static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int location, String graphicalCulture, int shipId, String shipName,
                                              int shipHome, String shipType, double shipMorale) {
        ClausewitzItem toItem = AbstractArmy.addToItem(parent, "navy", name, location, graphicalCulture, id);
        AbstractRegiment.addToItem(toItem, shipId, shipName, shipHome, shipType, shipMorale);

        return toItem;
    }
}
