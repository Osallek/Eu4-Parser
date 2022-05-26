package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.save.Save;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public record SaveInvestment(ClausewitzItem item, Save save) {

    public SaveCountry getCountry() {
        return this.save.getCountry(this.item.getVarAsString("tag"));
    }

    public List<Investment> getInvestments() {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            return investmentsList.getValues().stream().map(s -> this.save.getGame().getInvestment(s)).filter(Objects::nonNull).toList();
        }

        return new ArrayList<>();
    }

    public void addInvestment(Investment investment) {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            if (!investmentsList.contains(investment.getName())) {
                investmentsList.add(investment.getName());
            }
        } else {
            this.item.addList("investments", investment.getName());
        }
    }

    public void removeInvestment(Investment investment) {
        ClausewitzList investmentsList = this.item.getList("investments");

        if (investmentsList != null) {
            investmentsList.remove(investment.getName());
        }
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country, Investment... investments) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "investments", parent.getOrder() + 1);
        toItem.addVariable("tag", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addList("investments", Arrays.stream(investments).map(Investment::getName).toList());

        parent.addChild(toItem);

        return toItem;
    }
}
