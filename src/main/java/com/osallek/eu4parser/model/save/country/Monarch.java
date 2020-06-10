package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.ListOfDates;

import java.util.Date;

public class Monarch {

    protected final ClausewitzItem item;

    private Id id;

    private Personalities personalities;

    private HasDeclaredWar hasDeclaredWar;

    private ListOfDates rulerFlags;

    private Id leaderId;

    private Leader leader;

    public Monarch(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getCountry() {
        return this.item.getVarAsString("country");
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("ADM");
    }

    public void setAdm(int adm) {
        this.item.setVariable("ADM", adm);
    }

    public Integer getDip() {
        return this.item.getVarAsInt("DIP");
    }

    public void setDip(int dip) {
        this.item.setVariable("DIP", dip);
    }

    public Integer getMil() {
        return this.item.getVarAsInt("MIL");
    }

    public void setMil(int mil) {
        this.item.setVariable("MIL", mil);
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public String getCulture() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(String culture) {
        this.item.setVariable("culture", culture);
    }

    public String getReligion() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(String religion) {
        this.item.setVariable("religion", religion);
    }

    public String getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));
    }

    public Date getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(Date birthDate) {
        this.item.setVariable("birth_date", birthDate);
    }

    public Date getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(Date deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public String getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));
    }

    public Personalities getPersonalities() {
        return personalities;
    }

    public HasDeclaredWar getHasDeclaredWar() {
        return hasDeclaredWar;
    }

    public Id getLeaderId() {
        return leaderId;
    }

    public Leader getLeader() {
        return leader;
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }

        ClausewitzItem personalitiesItem = this.item.getChild("personalities");

        if (personalitiesItem != null) {
            this.personalities = new Personalities(personalitiesItem);
        }

        ClausewitzItem hasDeclaredWarItem = this.item.getChild("has_declared_war");

        if (hasDeclaredWarItem != null) {
            this.hasDeclaredWar = new HasDeclaredWar(hasDeclaredWarItem);
        }

        ClausewitzItem rulerFlagsItem = this.item.getChild("ruler_flags");

        if (rulerFlagsItem != null) {
            this.rulerFlags = new ListOfDates(rulerFlagsItem);
        }

        ClausewitzItem leaderIdChild = this.item.getChild("leader_id");

        if (leaderIdChild != null) {
            this.leaderId = new Id(leaderIdChild);
        }

        ClausewitzItem leaderChild = this.item.getChild("leader");

        if (leaderChild != null) {
            this.leader = new Leader(leaderChild);
        }
    }
}
