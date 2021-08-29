package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item, Save save, SaveCountry country) {
        super(item, save, country);
    }

    public Queen(ClausewitzItem item, Save save, SaveCountry country, LocalDate date) {
        super(item, save, country, date);
    }

    public Boolean getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Boolean getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public SaveCountry getCountryOfOrigin() {
        return this.save.getCountry(this.item.getVarAsString("country_of_origin"));
    }

    public void setCountryOfOrigin(SaveCountry countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin.getTag());
    }
}
