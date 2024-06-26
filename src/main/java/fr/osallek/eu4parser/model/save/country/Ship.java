package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;

public class Ship extends AbstractRegiment {

    private final Navy navy;

    public Ship(ClausewitzItem item, Save save, Navy navy) {
        super(item, save);
        this.navy = navy;
    }

    @Override
    public AbstractArmy getArmy() {
        return getNavy();
    }

    public Navy getNavy() {
        return navy;
    }

    public Boolean hasDisengaged() {
        return this.item.getVarAsBool("has_disengaged");
    }

    public void setHasDisengaged(boolean hasDisengaged) {
        this.item.setVariable("has_disengaged", hasDisengaged);
    }

    public FlagShip getFlagShip() {
        ClausewitzItem flagShipItem = this.item.getChild("flagship");

        if (flagShipItem != null) {
            return new FlagShip(flagShipItem, this.save);
        }
        return null;
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int home, String type, double morale, boolean hasDisengaged) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "regiment", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);
        toItem.addVariable("has_disengaged", hasDisengaged);

        parent.addChild(toItem);

        return toItem;
    }
}
