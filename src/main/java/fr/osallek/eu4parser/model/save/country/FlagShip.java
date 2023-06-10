package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.util.List;
import java.util.Optional;

public record FlagShip(ClausewitzItem item, Save save) {

    public String getName() {
        return this.item.getVarAsString("name").orElse("");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<SaveCountry> getOriginalOwner() {
        return this.item.getVarAsString("original_owner").map(this.save::getCountry);
    }

    public void setOriginalOwner(SaveCountry originalOwner) {
        this.item.setVariable("original_owner", ClausewitzUtils.addQuotes(originalOwner.getTag()));
    }

    public Optional<Boolean> isCaptured() {
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
