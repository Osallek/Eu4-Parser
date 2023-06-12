package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.save.Save;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public record SaveInvestment(ClausewitzItem item, Save save) {

    public Optional<SaveCountry> getCountry() {
        return this.item.getVarAsString("tag").map(this.save::getCountry);
    }

    public List<Investment> getInvestments() {
        return this.item.getList("investments")
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(s -> this.save.getGame().getInvestment(s))
                        .filter(Objects::nonNull)
                        .toList();
    }

    public void addInvestment(Investment investment) {
        this.item.getList("investments").ifPresentOrElse(investmentsList -> {
            if (!investmentsList.contains(investment.getName())) {
                investmentsList.add(investment.getName());
            }
        }, () -> this.item.addList("investments", investment.getName()));
    }

    public void removeInvestment(Investment investment) {
        this.item.getList("investments").ifPresent(investmentsList -> investmentsList.remove(investment.getName()));
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country, Investment... investments) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "investments", parent.getOrder() + 1);
        toItem.addVariable("tag", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addList("investments", Arrays.stream(investments).map(Investment::getName).toList());

        parent.addChild(toItem);

        return toItem;
    }
}
