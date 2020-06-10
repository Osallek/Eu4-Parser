package com.osallek.eu4parser.model.save.empire;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.List;

public class Hre extends Empire {

    //	active_incident={
    //		incident="incident_shadow_kingdom"
    //		expiry_date=1461.3.27
    //		option=1

    private HreIncident activeIncident;

    public Hre(ClausewitzItem item) {
        super(item);
    }

    @Override
    public void dismantle() {
        super.dismantle();
        this.item.removeList("electors");
    }

    @Override
    public void setReformLevel(int reformLevel) {
        super.setReformLevel(reformLevel);

        this.setImperialBanAllowed(reformLevel >= 1);
        this.setInternalHreCb(reformLevel < 5);
        this.setHreInheritable(reformLevel >= 6);
    }

    public Integer getContinent() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsInt("continent");
    }

    public void setContinent(int continent) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("continent", continent);
    }

    public Boolean getImperialBanAllowed() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("imperial_ban_allowed");
    }

    public void setImperialBanAllowed(boolean imperialBanAllowed) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("imperial_ban_allowed", imperialBanAllowed);
    }

    public Boolean getInternalHreCb() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("internal_hre_cb");
    }

    public void setInternalHreCb(boolean internalHreCb) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("internal_hre_cb", internalHreCb);
    }

    public Boolean getHreInheritable() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("hre_inheritable");
    }

    public void setHreInheritable(boolean hreInheritable) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("hre_inheritable", hreInheritable);
    }

    public Boolean getAllowsFemaleEmperor() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("allows_female_emperor");
    }

    public void setAllowsFemaleEmperor(boolean allowsFemaleEmperor) {
        if (dismantled()) {
            return;
        }

        ClausewitzVariable var = this.item.setVariable("allows_female_emperor", allowsFemaleEmperor);
    }

    public List<String> getElectors() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        return this.item.getList("electors").getValues();
    }

    public void addElector(String tag) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            if (!list.contains(tag)) {
                list.add(tag);
            }
        } else {
            this.item.addList("electors", tag);
        }
    }

    public void removeElector(String tag) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.remove(tag);
        }
    }

    public void removeElector(int id) {
        if (dismantled()) {
            return;
        }

        ClausewitzList list = this.item.getList("electors");

        if (list != null) {
            list.remove(id);
        }
    }

    public List<String> getPassedReforms() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        return this.item.getVarsAsStrings("passed_reform");
    }

    public void addPassedReform(String reform) {
        if (dismantled()) {
            return;
        }

        reform = ClausewitzUtils.addQuotes(reform);
        List<String> passedReform = this.item.getVarsAsStrings("passed_reform");

        if (!passedReform.contains(reform)) {
            this.item.addVariable("passed_reform", reform);
        }
    }

    public void removePassedReform(int index) {
        if (dismantled()) {
            return;
        }

        this.item.removeVariable("passed_reform", index);
    }

    public void removePassedReform(String reform) {
        if (dismantled()) {
            return;
        }

        this.item.removeVariable("passed_reform", reform);
    }

    public Integer getEmperorPreviousRank() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsInt("emperor_previous_rank");
    }

    public void setEmperorPreviousRank(int id) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("emperor_previous_rank", id);
    }

    public Boolean getImperialRealmWar() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsBool("imperial_realm_war");
    }

    public void setImperialRealmWar(boolean imperialRealmWar) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("imperial_realm_war", imperialRealmWar);
    }

    public HreIncident getActiveIncident() {
        return activeIncident;
    }

    public void removeIncident() {
        this.item.removeChild("active_incident");
        refreshAttributes();
    }

    protected void refreshAttributes() {
        ClausewitzItem incidentItem = this.item.getChild("active_incident");

        if (incidentItem != null) {
            this.activeIncident = new HreIncident(incidentItem);
        }
    }
}
