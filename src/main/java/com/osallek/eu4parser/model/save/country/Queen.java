package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Boolean getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Boolean getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public Country getCountryOfOrigin() {
        return this.save.getCountry(this.item.getVarAsString("country_of_origin"));
    }

    public void setCountryOfOrigin(Country countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin.getTag());
    }
}
