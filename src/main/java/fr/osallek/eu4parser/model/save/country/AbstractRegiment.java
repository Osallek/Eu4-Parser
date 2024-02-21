package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.UnitType;
import fr.osallek.eu4parser.model.game.Unit;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;

import java.util.ArrayList;

public abstract class AbstractRegiment {

    protected final Save save;

    protected final ClausewitzItem item;

    protected AbstractRegiment(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Id getId() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            return new Id(idItem);
        }

        return null;
    }

    public abstract AbstractArmy getArmy();

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

    //1=Mercenary
    //2=Banner
    //3=Streltsy
    //4=Cossacks
    //5=Janissaries
    //6=Rajputs
    //7=Marines
    //?=Revolutionary Guard
    public Integer getCategory() {
        return this.item.getVarAsInt("category");
    }

    public Id getLastTarget() {
        ClausewitzItem lastTargetItem = this.item.getChild("last_target");

        if (lastTargetItem != null) {
            return new Id(lastTargetItem);
        }

        return null;
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
}
