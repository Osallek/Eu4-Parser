package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.UnitType;
import com.osallek.eu4parser.model.game.Unit;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.Save;
import org.luaj.vm2.ast.Str;

public class Ship {

    protected final Save save;

    protected final ClausewitzItem item;

    private Id id;

    private Id lastTarget;

    public Ship(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Integer getHome() {
        return this.item.getVarAsInt("home");
    }

    public void setHome(Integer home) {
        this.item.setVariable("home", home);
    }

    public Unit getType() {
        return this.save.getGame().getUnit(getTypeName());
    }

    public String getTypeName() {
        return this.item.getVarAsString("type");
    }

    public UnitType getUnitType() {
        return getType().getType();
    }

    public void setType(Unit unit) {
        this.item.setVariable("type", ClausewitzUtils.addQuotes(unit.getName()));
    }

    public Double getMorale() {
        return this.item.getLastVarAsDouble("morale");
    }

    public void setMorale(Double morale) {
        if (morale < 0d) {
            morale = 0d;
        }

        this.item.setVariable("morale", morale);
    }

    public Id getLastTarget() {
        return lastTarget;
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int home, String type, double morale) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "ship", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);

        parent.addChild(toItem);

        return toItem;
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }

        ClausewitzItem lastTargetItem = this.item.getChild("last_target");

        if (lastTargetItem != null) {
            this.lastTarget = new Id(lastTargetItem);
        }
    }
}
