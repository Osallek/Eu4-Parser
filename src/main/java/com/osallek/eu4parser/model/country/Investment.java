package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;

import java.util.ArrayList;
import java.util.List;

public class Investment {

    private final ClausewitzItem item;

    public Investment(ClausewitzItem item) {
        this.item = item;
    }

    public String getCountry() {
        return this.item.getVarAsString("tag");
    }

    public List<String> getInvestments() {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            return investmentsList.getValues();
        }

        return new ArrayList<>();
    }

    public void addInvestment(String investment) {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            if (!investmentsList.contains(investment)) {
                investmentsList.add(investment);
            }
        } else {
            this.item.addList("investments", investment);
        }
    }

    public void removeInvestment(String investment) {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            investmentsList.remove(investment);
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String country, String... investments) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "investments", parent.getOrder() + 1);
        toItem.addVariable("tag", ClausewitzUtils.addQuotes(country));
        toItem.addList("investments", investments);

        parent.addChild(toItem);

        return toItem;
    }
}
