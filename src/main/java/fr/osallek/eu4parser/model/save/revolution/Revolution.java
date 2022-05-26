package fr.osallek.eu4parser.model.save.revolution;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.List;

public record Revolution(ClausewitzItem item, Save save) {

    public SaveCountry getRevolutionTarget() {
        return this.save.getCountry(this.item.getVarAsString("revolution_target"));
    }

    public void setRevolutionTarget(SaveCountry country) {
        this.item.setVariable("revolution_target", country.getTag());
        setHasFirstRevolutionStarted(true);
        setDismantleDate(null);
    }

    public String getRevolutionTargetOriginalName() {
        return this.item.getVarAsString("revolution_target_original_name");
    }

    public void setRevolutionTargetOriginalName(String revolutionTargetOriginalName) {
        this.item.setVariable("revolution_target_original_name", revolutionTargetOriginalName);
    }

    public Boolean hasFirstRevolutionStarted() {
        return this.item.getVarAsBool("has_first_revolution_started");
    }

    void setHasFirstRevolutionStarted(boolean hasFirstRevolutionStarted) {
        this.item.setVariable("has_first_revolution_started", hasFirstRevolutionStarted);
    }

    public LocalDate getDismantleDate() {
        LocalDate date = this.item.getVarAsDate("dismantle_date");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setDismantleDate(LocalDate dismantleDate) {
        if (dismantleDate == null) {
            dismantleDate = Eu4Utils.DEFAULT_DATE;
        }

        this.item.setVariable("dismantle_date", dismantleDate);
    }

    public LocalDate getClaimed() {
        LocalDate date = this.item.getVarAsDate("claimed");

        if (date == null || this.save.getStartDate().equals(date)) {
            return null;
        }

        return date;
    }

    public void setClaimed(LocalDate claimed) {
        if (claimed == null) {
            claimed = this.save.getStartDate();
        }

        this.item.setVariable("claimed", claimed);
    }

    public List<SaveCountry> getPastTargets() {
        return this.item.getVarsAsStrings("past_targets").stream().map(this.save::getCountry).toList();
    }

    public void addPastTarget(SaveCountry country) {
        ClausewitzList list = this.item.getList("past_targets");

        if (list == null) {
            this.item.addList("past_targets", country.getTag());
        } else {
            list.add(country.getTag());
        }
    }

    public void removePastTarget(SaveCountry country) {
        ClausewitzList list = this.item.getList("past_targets");

        if (list != null) {
            list.remove(country.getTag());
        }
    }
}
