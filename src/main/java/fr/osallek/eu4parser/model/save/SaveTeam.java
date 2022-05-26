package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public record SaveTeam(ClausewitzItem item, Save save) {

    public String name() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("name"));
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public List<String> countries() {
        return this.item.getVarsAsStrings("member").stream().filter(StringUtils::isNotBlank).map(ClausewitzUtils::removeQuotes).toList();
    }

    public List<SaveCountry> saveCountries() {
        return countries().stream().map(save::getCountry).toList();
    }

    public void addCountry(SaveCountry country) {
        addCountry(country.getTag());
    }

    public void addCountry(String tag) {
        if (!countries().contains(tag)) {
            this.item.addVariable("member", ClausewitzUtils.addQuotes(tag));
        }
    }

    public void removeCountry(SaveCountry country) {
        removeCountry(country.getTag());
    }

    public void removeCountry(String tag) {
        this.item.removeVariable("name", ClausewitzUtils.addQuotes(tag));
    }
}
