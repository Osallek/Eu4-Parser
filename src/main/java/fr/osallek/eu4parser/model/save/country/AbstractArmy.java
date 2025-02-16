package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractArmy {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    protected AbstractArmy(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public Id getId() {
        ClausewitzItem idItem = this.item.getChild("id");
        return idItem == null ? null : new Id(idItem);
    }

    public SaveCountry getCountry() {
        return country;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Id getLeader() {
        ClausewitzItem leaderItem = this.item.getChild("leader");

        return leaderItem != null ? new Id(leaderItem) : null;
    }

    public void removeLeader() {
        this.item.removeChild("leader");
    }

    public Integer getPrevious() {
        return this.item.getVarAsInt("previous");
    }

    public Integer getPreviousWar() {
        return this.item.getVarAsInt("previous_war");
    }

    public Double getMovementProgress() {
        return this.item.getVarAsDouble("movement_progress");
    }

    public List<Integer> getPath() {
        ClausewitzList list = this.item.getList("path");

        if (list != null) {
            return list.getValuesAsInt();
        }

        return new ArrayList<>();
    }

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(int location) {
        this.item.setVariable("location", location);
    }

    public LocalDate getMovementProgressLastUpdated() {
        return this.item.getVarAsDate("movement_progress_last_updated");
    }

    public String getGraphicalCulture() {
        return this.item.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        this.item.setVariable("graphical_culture", ClausewitzUtils.addQuotes(graphicalCulture));
    }

    public Boolean getMainArmy() {
        return this.item.getVarAsBool("main_army");
    }

    /**
     * The AI can also reserve some of its forces to act as "Hunter-Killer" armies. In EUIII, AI armies often settled into a siege and refused to move, even if
     * there was (to a human eye), urgent events taking place nearby. Now, Hunter-Killer armies will seek out enemy armies and attempt to engage them in direct
     * battle. They will also protect their own besieging forces, intercepting enemies and acting as defensive screen.
     * https://eu4.paradoxwikis.com/Artificial_intelligence#Improvements_over_EUIII
     */
    public Boolean getHunterKiller() {
        return this.item.getVarAsBool("hunter_killer");
    }

    public Double getAttritionLevel() {
        return this.item.getVarAsDouble("attrition_level");
    }

    public Boolean getVisibleToAi() {
        return this.item.getVarAsBool("visible_to_ai");
    }

    public Boolean canAttach() {
        return this.item.getVarAsBool("can_attach");
    }

    public void setCanAttach(boolean canAttach) {
        this.item.setVariable("can_attach", canAttach);
    }

    public Boolean getMovementLocked() {
        return this.item.getVarAsBool("movement_locked");
    }

    public Boolean getAttrition() {
        return this.item.getVarAsBool("attrition");
    }

    protected static ClausewitzItem addToItem(ClausewitzItem parent, String itemName, String name, int location, String graphicalCulture, int id) {
        ClausewitzItem toItem = new ClausewitzItem(parent, itemName, parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(name));
        toItem.addVariable("location", location);
        toItem.addVariable("graphical_culture", ClausewitzUtils.addQuotes(graphicalCulture));
        parent.addChild(toItem);

        return toItem;
    }
}
