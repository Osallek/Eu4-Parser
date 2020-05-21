package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class Queen extends Monarch {

    public Queen(ClausewitzItem item) {
        super(item);
    }

    public Boolean getConsort() {
        return this.item.getVarAsBool("consort");
    }

    public Boolean getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Boolean getQueenRegent() {
        return this.item.getVarAsBool("queen_regent");
    }

    public String getCountryOfOrigin() {
        return this.item.getVarAsString("country_of_origin");
    }

    public void setCountryOfOrigin(String countryOfOrigin) {
        this.item.setVariable("country_of_origin", countryOfOrigin);
    }
}
