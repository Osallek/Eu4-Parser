package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

public class Cardinal {

    private final ClausewitzItem item;

    private final Save save;

    private Id id;

    public Cardinal(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        refreshAttributes();
    }

    public SaveProvince getLocation() {
        return this.save.getProvince(this.item.getVarAsInt("location"));
    }

    public void setLocation(SaveProvince province) {
        this.item.setVariable("location", province.getId());
    }

    public Id getId() {
        return id;
    }

    private void refreshAttributes() {
        ClausewitzItem idItem = this.item.getChild("id");

        if (idItem != null) {
            this.id = new Id(idItem);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, int id, int location) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "cardinal", parent.getOrder() + 1);
        toItem.addVariable("location", location);
        Id.addToItem(toItem, id, 4713);

        parent.addChild(toItem);

        return toItem;
    }
}
