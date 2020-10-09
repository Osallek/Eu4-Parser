package com.osallek.eu4parser.model.save.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Advisor;
import com.osallek.eu4parser.model.game.Culture;
import com.osallek.eu4parser.model.save.Id;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.SaveReligion;

import java.util.Date;

public class SaveAdvisor {

    protected final Save save;

    protected final ClausewitzItem item;

    private final Advisor gameAdvisor;

    private Id id;

    public SaveAdvisor(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        this.gameAdvisor = this.save.getGame().getAdvisor(getName());
        refreshAttributes();
    }

    public Id getId() {
        return id;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("lender", ClausewitzUtils.addQuotes(name));
    }

    public Advisor getGameAdvisor() {
        return gameAdvisor;
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        this.item.setVariable("type", type);
    }

    public Integer getSkill() {
        return this.item.getVarAsInt("skill");
    }

    public void setSkill(int skill) {
        this.item.setVariable("skill", skill);
    }

    public SaveProvince getLocation() {
        return this.save.getProvince(this.item.getVarAsInt("location"));
    }

    public void setLocation(SaveProvince location) {
        this.item.setVariable("location", location.getId());
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Culture getCulture() {
        return this.save.getGame().getCulture(this.item.getVarAsString("culture"));
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("religion"));
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        this.item.setVariable("date", date);
    }

    public Date getHireDate() {
        return this.item.getVarAsDate("hire_date");
    }

    public void setHireDate(Date hireDate) {
        this.item.setVariable("hire_date", hireDate);
    }

    private void refreshAttributes() {
        ClausewitzItem idChild = this.item.getChild("id");

        if (idChild != null) {
            this.id = new Id(idChild);
        }
    }
}
