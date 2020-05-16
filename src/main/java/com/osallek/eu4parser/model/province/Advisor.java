package com.osallek.eu4parser.model.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Id;

import java.util.Date;

public class Advisor {

    protected final ClausewitzItem item;

    private Id id;

    public Advisor(ClausewitzItem item) {
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
        this.item.setVariable("lender", ClausewitzUtils.addQuotes(name));
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

    public Integer getLocation() {
        return this.item.getVarAsInt("location");
    }

    public void setLocation(int location) {
        this.item.setVariable("location", location);
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
