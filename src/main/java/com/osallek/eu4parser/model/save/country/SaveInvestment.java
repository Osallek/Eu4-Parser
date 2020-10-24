package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.save.Save;

import java.util.ArrayList;
import java.util.List;

public class SaveInvestment {

    private final Save save;

    private final ClausewitzItem item;

    public SaveInvestment(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
    }

    public Country getCountry() {
        return this.save.getCountry(this.item.getVarAsString("tag"));
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

    public static ClausewitzItem addToItem(ClausewitzItem parent, Country country, String... investments) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "investments", parent.getOrder() + 1);
        toItem.addVariable("tag", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addList("investments", investments);

        parent.addChild(toItem);

        return toItem;
    }
}
