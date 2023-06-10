package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Optional;

public class Cardinal {

    private final ClausewitzItem item;

    private final Save save;

    public Cardinal(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
    }

    public Optional<SaveProvince> getLocation() {
        return this.item.getVarAsInt("location").map(this.save::getProvince);
    }

    public void setLocation(SaveProvince province) {
        this.item.setVariable("location", province.getId());
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, int location) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "cardinal", parent.getOrder() + 1);
        toItem.addVariable("location", location);
        Id.addToItem(toItem, id, 4713);

        parent.addChild(toItem);

        return toItem;
    }
}
