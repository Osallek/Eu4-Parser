package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Id;

import java.time.LocalDate;

public class MercenaryCompany {

    protected final ClausewitzItem item;

    protected final SaveCountry country;

    public MercenaryCompany(ClausewitzItem item, SaveCountry country) {
        this.item = item;
        this.country = country;
    }

    public Id getId() {
        ClausewitzItem idItem = this.item.getChild("id");

        return idItem != null ? new Id(idItem) : null;
    }

    public String getTag() {
        return this.item.getVarAsString("tag");
    }

    public void setTag(String tag) {
        this.item.setVariable("tag", ClausewitzUtils.addQuotes(tag));
    }

    public Boolean getProgress() {
        return this.item.getVarAsBool("progress");
    }

    public Id getLeader() {
        ClausewitzItem leaderItem = this.item.getChild("leader");

        return leaderItem != null ? new Id(leaderItem) : null;
    }

    public void removeLeader() {
        this.item.removeChild("leader");
    }

    public Double getManpower() {
        return this.item.getVarAsDouble("manpower");
    }

    public void setManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("manpower", manpower);
    }

    public Double getStartingManpower() {
        return this.item.getVarAsDouble("starting_manpower");
    }

    public void setStartingManpower(Double manpower) {
        if (manpower < 0d) {
            manpower = 0d;
        }

        this.item.setVariable("starting_manpower", manpower);
    }

    public LocalDate getHiringDate() {
        return this.item.getVarAsDate("hiring_date");
    }

    public LocalDate getDisbandDate() {
        return this.item.getVarAsDate("disband_date");
    }
}
