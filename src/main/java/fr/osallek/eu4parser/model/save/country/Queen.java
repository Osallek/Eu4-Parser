package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Country;

import java.time.LocalDate;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public Queen(ClausewitzItem item, SaveCountry country, LocalDate date) {
        super(item, country, date);
    }

    public Queen(ClausewitzItem item, Country country) {
        super(item, country);
    }

    public Queen(ClausewitzItem item, Country country, LocalDate date) {
        super(item, country, date);
    }

    public Boolean getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Boolean getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public SaveCountry getSaveCountryOfOrigin() {
        return this.saveCountry.getSave().getCountry(this.item.getVarAsString("country_of_origin"));
    }

    public Country getCountryOfOrigin() {
        return this.game.getCountry(this.item.getVarAsString("country_of_origin"));
    }

    public void setCountryOfOrigin(SaveCountry countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin.getTag());
    }
}
