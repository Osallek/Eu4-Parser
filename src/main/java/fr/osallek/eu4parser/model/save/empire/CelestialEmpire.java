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
        this.item.getChild("decree").ifPresent(ClausewitzItem::removeAll);

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
        this.decree = this.item.getChild("decree").map(i -> new SaveDecree(i, this.save)).orElse(null);
    }
}
