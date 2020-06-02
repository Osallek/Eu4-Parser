package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.Id;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class AbstractArmy {

    protected final ClausewitzItem item;

    protected final Country country;

    protected Id id;

    protected Id leader;

    public AbstractArmy(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        ClausewitzVariable var = this.item.getVar("name");
        name = ClausewitzUtils.addQuotes(name);

        if (var != null) {
            var.setValue(name);
        } else {
            this.item.addVariable("name", name);
        }
    }

    public Id getLeader() {
        return leader;
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
        ClausewitzVariable var = this.item.getVar("location");

        if (var != null) {
            var.setValue(location);
        } else {
            this.item.addVariable("location", location);
        }
    }

    public Date getMovementProgressLastUpdated() {
        return this.item.getVarAsDate("movement_progress_last_updated");
    }

    public String getGraphicalCulture() {
        return this.item.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        ClausewitzVariable var = this.item.getVar("graphical_culture");
        graphicalCulture = ClausewitzUtils.addQuotes(graphicalCulture);

        if (var != null) {
            var.setValue(graphicalCulture);
        } else {
            this.item.addVariable("graphical_culture", graphicalCulture);
        }
    }

    public Boolean getMainArmy() {
        return this.item.getVarAsBool("main_army");
    }

    /**
     * The AI can also reserve some of its forces to act as "Hunter-Killer" armies. In EUIII, AI armies often settled
     * into a siege and refused to move, even if there was (to a human eye), urgent events taking place nearby. Now,
     * Hunter-Killer armies will seek out enemy armies and attempt to engage them in direct battle. They will also
     * protect their own besieging forces, intercepting enemies and acting as defensive screen.
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

    public void setCanAttach(Boolean canAttach) {
        ClausewitzVariable var = this.item.getVar("can_attach");

        if (var != null) {
            var.setValue(canAttach);
        } else {
            this.item.addVariable("can_attach", canAttach);
        }
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
        toItem.addVariable("graphical_culture", graphicalCulture);
        parent.addChild(toItem);

        return toItem;
    }

    protected void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }

        ClausewitzItem leaderItem = this.item.getChild("leader");

        if (leaderItem != null) {
            this.leader = new Id(leaderItem);
        }
    }
}
