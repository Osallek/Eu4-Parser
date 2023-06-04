package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.util.Optional;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item, Country country) {
        super(item, country);
    }

    public Optional<Boolean> getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Optional<Boolean> getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public Optional<String> getCountryOfOrigin() {
        return this.item.getVarAsString("country_of_origin");
    }

    public void setCountryOfOrigin(SaveCountry countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin.getTag());
    }
}
