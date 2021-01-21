package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;

public class Regiment extends AbstractRegiment {

    private final Army army;

    public Regiment(ClausewitzItem item, Save save, Army army) {
        super(item, save);
        this.army = army;
    }

    @Override
    public Army getArmy() {
        return army;
    }

    public Double getDrill() {
        return this.item.getLastVarAsDouble("drill");
    }

    public void setDrill(Double drill) {
        if (drill < 0d) {
            drill = 0d;
        } else if (drill > 100d) {
            drill = 100d;
        }

        this.item.setVariable("drill", drill);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, String name, int home, String type, double morale, double drill) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "regiment", parent.getOrder() + 1);
        Id.addToItem(toItem, id, 54);
        toItem.addVariable("name", name);
        toItem.addVariable("home", home);
        toItem.addVariable("type", ClausewitzUtils.addQuotes(type));
        toItem.addVariable("morale", morale);
        toItem.addVariable("drill", drill);

        parent.addChild(toItem);

        return toItem;
    }
}
