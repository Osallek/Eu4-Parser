package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;

public class FlagShip {

    private final Save save;

    private final ClausewitzItem item;

    public FlagShip(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Country getOriginalOwner() {
        return this.save.getCountry(this.item.getVarAsString("original_owner"));
    }

    public void setOriginalOwner(Country originalOwner) {
        this.item.setVariable("original_owner", ClausewitzUtils.addQuotes(originalOwner.getTag()));
    }

    public Boolean isCaptured() {
        return this.item.getVarAsBool("is_captured");
    }

    public List<String> getModifications() {
        return this.item.getVarsAsStrings("modification");
    }

    public void addModification(String modification) {
        List<String> modifications = this.item.getVarsAsStrings("modification");

        if (!modifications.contains(ClausewitzUtils.addQuotes(modification))) {
            this.item.addVariable("modification", ClausewitzUtils.addQuotes(modification));
        }
    }

    public void removeModification(int index) {
        this.item.removeVariable("modification", index);
    }

    public void removeModification(String modification) {
        this.item.removeVariable("modification", ClausewitzUtils.addQuotes(modification));
    }
}
