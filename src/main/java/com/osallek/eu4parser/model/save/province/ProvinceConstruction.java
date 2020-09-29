package com.osallek.eu4parser.model.save.province;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Building;
import com.osallek.eu4parser.model.save.country.Country;

import java.util.Date;

public class ProvinceConstruction {

    protected final ClausewitzItem item;

    protected final SaveProvince province;

    public ProvinceConstruction(ClausewitzItem item, SaveProvince province) {
        this.item = item;
        this.province = province;
    }

    public Date getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(Date startDate) {
        if ((startDate.before(this.province.getSave().getDate()) || startDate.equals(this.province.getSave().getDate()))) {
            this.item.setVariable("start_date", startDate);
        }
    }

    public Integer getTotal() {
        return this.item.getVarAsInt("total");
    }

    public void setTotal(int total) {
        this.item.setVariable("total", total);
    }

    public Integer getOriginalTotal() {
        return this.item.getVarAsInt("original_total");
    }

    public void setOriginalTotal(int total) {
        this.item.setVariable("original_total", total);
    }

    public Double getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public Integer getEnvoy() {
        return this.item.getVarAsInt("envoy");
    }

    public Date getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(Date date) {
        if ((date.after(this.province.getSave().getDate()))) {
            this.item.setVariable("date", date);
        }
    }

    public Country getCountry() {
        return this.province.getSave().getCountry(this.item.getVarAsString("country"));
    }

    public void setCountry(Country country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Double getCost() {
        return this.item.getVarAsDouble("cost");
    }

    public void setCost(double cost) {
        this.item.setVariable("cost", cost);
    }

    public Building getBuilding() {
        return this.province.getSave().getGame().getBuildings().get(this.item.getVarAsInt("building"));
    }

    public void setBuilding(Building building) {
        this.item.setVariable("building", this.province.getSave().getGame().getBuildings().indexOf(building));
    }
}
