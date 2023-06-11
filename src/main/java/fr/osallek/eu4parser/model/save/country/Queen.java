package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item, SaveCountry country) {
        super(item, country);
    }

    public Queen(ClausewitzItem item, SaveCountry country, LocalDate date) {
        super(item, country, date);
    }

    public Optional<Boolean> getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Optional<Boolean> getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public Optional<SaveCountry> getSaveCountryOfOrigin() {
        return this.item.getVarAsString("country_of_origin").map(s -> this.saveCountry.getSave().getCountry(s));
    }

    public void setCountryOfOrigin(SaveCountry countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin.getTag());
    }
}
