package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;
import java.util.Optional;

public class MercenaryCompany {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    public MercenaryCompany(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public Optional<String> getTag() {
        return this.item.getVarAsString("tag");
    }

    public void setTag(String tag) {
        this.item.setVariable("tag", ClausewitzUtils.addQuotes(tag));
    }

    public Optional<Boolean> getProgress() {
        return this.item.getVarAsBool("progress");
    }

    public Optional<Id> getLeader() {
        return this.item.getChild("leader").map(Id::new);
    }

    public void removeLeader() {
        this.item.removeChild("leader");
    }

    public Optional<Double> getManpower() {
        return this.item.getVarAsDouble("manpower");
    }

    public void setManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("manpower", manpower);
    }

    public Optional<Double> getStartingManpower() {
        return this.item.getVarAsDouble("starting_manpower");
    }

    public void setStartingManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("starting_manpower", manpower);
    }

    public Optional<LocalDate> getHiringDate() {
        return this.item.getVarAsDate("hiring_date");
    }

    public Optional<LocalDate> getDisbandDate() {
        return this.item.getVarAsDate("disband_date");
    }
}
