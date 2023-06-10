package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public abstract class AbstractArmy {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    protected AbstractArmy(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public SaveCountry getCountry() {
        return country;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<Id> getLeader() {
        return this.item.getChild("leader").map(Id::new);
    }

    public void removeLeader() {
        this.item.removeChild("leader");
    }

    public Optional<Integer> getPrevious() {
        return this.item.getVarAsInt("previous");
    }

    public Optional<Integer> getPreviousWar() {
        return this.item.getVarAsInt("previous_war");
    }

    public Optional<Double> getMovementProgress() {
        return this.item.getVarAsDouble("movement_progress");
    }

    public List<Integer> getPath() {
        return this.item.getList("path").map(ClausewitzList::getValuesAsInt).orElse(new ArrayList<>());
    }

    public Optional<Integer> getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(int location) {
        this.item.setVariable("location", location);
    }

    public Optional<LocalDate> getMovementProgressLastUpdated() {
        return this.item.getVarAsDate("movement_progress_last_updated");
    }

    public Optional<String> getGraphicalCulture() {
        return this.item.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        this.item.setVariable("graphical_culture", ClausewitzUtils.addQuotes(graphicalCulture));
    }

    public Optional<Boolean> getMainArmy() {
        return this.item.getVarAsBool("main_army");
    }

    /**
     * The AI can also reserve some of its forces to act as "Hunter-Killer" armies. In EUIII, AI armies often settled into a siege and refused to move, even if
     * there was (to a human eye), urgent events taking place nearby. Now, Hunter-Killer armies will seek out enemy armies and attempt to engage them in direct
     * battle. They will also protect their own besieging forces, intercepting enemies and acting as defensive screen.
     * https://eu4.paradoxwikis.com/Artificial_intelligence#Improvements_over_EUIII
     */
    public Optional<Boolean> getHunterKiller() {
        return this.item.getVarAsBool("hunter_killer");
    }

    public Optional<Double> getAttritionLevel() {
        return this.item.getVarAsDouble("attrition_level");
    }

    public Optional<Boolean> getVisibleToAi() {
        return this.item.getVarAsBool("visible_to_ai");
    }

    public Optional<Boolean> canAttach() {
        return this.item.getVarAsBool("can_attach");
    }

    public void setCanAttach(boolean canAttach) {
        this.item.setVariable("can_attach", canAttach);
    }

    public Optional<Boolean> getMovementLocked() {
        return this.item.getVarAsBool("movement_locked");
    }

    public Optional<Boolean> getAttrition() {
        return this.item.getVarAsBool("attrition");
    }

    protected static ClausewitzItem addToItem(ClausewitzItem parent, String itemName, String name, int location, String graphicalCulture, int id) {
        ClausewitzItem toItem = new ClausewitzItem(parent, itemName, parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(name));
        toItem.addVariable("location", location);
        toItem.addVariable("graphical_culture", graphicalCulture);
        parent.addChild(toItem);

        return toItem;
    }
}
