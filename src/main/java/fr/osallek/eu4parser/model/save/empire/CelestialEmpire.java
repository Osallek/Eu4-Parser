package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Decree;
import fr.osallek.eu4parser.model.save.Save;

public class CelestialEmpire extends Empire {

    public CelestialEmpire(ClausewitzItem item, Save save) {
        super(item, save);
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
    }

    public SaveDecree getDecree() {
        ClausewitzItem decreeItem = this.item.getChild("decree");

        return decreeItem != null ? new SaveDecree(decreeItem, this.save) : null;
    }

    public void setDecree(Decree decree) {
        SaveDecree saveDecree = getDecree();
        if (saveDecree != null) {
            saveDecree.setDecree(decree);
        } else {
            SaveDecree.addToItem(this.item, decree.getName(), this.save.getDate());
        }
    }
}
