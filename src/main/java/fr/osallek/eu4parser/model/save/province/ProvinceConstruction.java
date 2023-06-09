package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.Optional;

public record ProvinceConstruction(ClausewitzItem item, SaveProvince province) {

    public Optional<LocalDate> getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(LocalDate startDate) {
        if ((startDate.isBefore(this.province.getSave().getDate()) || startDate.equals(this.province.getSave().getDate()))) {
            this.item.setVariable("start_date", startDate);
        }
    }

    public Optional<Integer> getTotal() {
        return this.item.getVarAsInt("total");
    }

    public void setTotal(int total) {
        this.item.setVariable("total", total);
    }

    public Optional<Integer> getOriginalTotal() {
        return this.item.getVarAsInt("original_total");
    }

    public void setOriginalTotal(int total) {
        this.item.setVariable("original_total", total);
    }

    public Optional<Double> getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public Optional<Integer> getEnvoy() {
        return this.item.getVarAsInt("envoy");
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        if ((date.isAfter(this.province.getSave().getDate()))) {
            this.item.setVariable("date", date);
        }
    }

    public Optional<SaveCountry> getCountry() {
        return this.item.getVarAsString("country").map(s -> this.province.getSave().getCountry(s));
    }

    public void setCountry(SaveCountry country) {
        this.item.setVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
    }

    public Optional<Double> getCost() {
        return this.item.getVarAsDouble("cost");
    }

    public void setCost(double cost) {
        this.item.setVariable("cost", cost);
    }

    public Optional<Building> getBuilding() {
        return this.item.getVarAsInt("building").map(integer -> this.province.getSave().getGame().getBuildings().get(integer));
    }

    public void setBuilding(Building building) {
        this.item.setVariable("building", this.province.getSave().getGame().getBuildings().indexOf(building));
    }
}
