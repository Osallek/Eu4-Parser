package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Decree;
import fr.osallek.eu4parser.model.save.Save;

public class CelestialEmpire extends Empire {

    private SaveDecree decree;

    public CelestialEmpire(ClausewitzItem item, Save save) {
        super(item, save);
        refreshAttributes();
    }

    @Override
    protected String getId() {
        return "celestial_empire";
    }

    @Override
    public void dismantle() {
        super.dismantle();
        ClausewitzItem decreeItem = this.item.getChild("decree");

        if (decreeItem != null) {
            decreeItem.removeAll();
        }

        refreshAttributes();
    }

    public SaveDecree getDecree() {
        return decree;
    }

    public void setDecree(Decree decree) {
        if (this.decree != null) {
            this.decree.setDecree(decree);
        } else {
            SaveDecree.addToItem(this.item, decree.getName(), this.save.getDate());
            refreshAttributes();
        }
    }

    @Override
    protected void refreshAttributes() {
        super.refreshAttributes();
        ClausewitzItem decreeItem = this.item.getChild("decree");

        if (decreeItem != null) {
            this.decree = new SaveDecree(decreeItem, this.save);
        }
    }
}
