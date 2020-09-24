package com.osallek.eu4parser.model.save.revolution;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Revolution {

    private final Save save;

    private final ClausewitzItem item;

    public Revolution(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Country getRevolutionTarget() {
        return this.save.getCountry(this.item.getVarAsString("revolution_target"));
    }

    public void setRevolutionTarget(Country country) {
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

    public Date getDismantleDate() {
        Date date = this.item.getVarAsDate("dismantle_date");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public void setDismantleDate(Date dismantleDate) {
        if (dismantleDate == null) {
            dismantleDate = Eu4Utils.DEFAULT_DATE;
        }

        this.item.setVariable("dismantle_date", dismantleDate);
    }

    public Date getClaimed() {
        Date date = this.item.getVarAsDate("claimed");

        if (date == null || this.save.getStartDate().equals(date)) {
            return null;
        }

        return date;
    }

    public void setClaimed(Date claimed) {
        if (claimed == null) {
            claimed = this.save.getStartDate();
        }

        this.item.setVariable("claimed", claimed);
    }

    public List<Country> getPastTargets() {
        return this.item.getVarsAsStrings("past_targets").stream().map(this.save::getCountry).collect(Collectors.toList());
    }

    public void addPastTarget(Country country) {
        ClausewitzList list = this.item.getList("past_targets");

        if (list == null) {
            this.item.addList("past_targets", country.getTag());
        } else {
            list.add(country.getTag());
        }
    }

    public void removePastTarget(Country country) {
        ClausewitzList list = this.item.getList("past_targets");

        if (list != null) {
            list.remove(country.getTag());
        }
    }
}
