package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Save;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Area {

    private final ClausewitzItem item;

    private final Save save;

    private Map<String, CountryState> countriesStates;

    private Map<String, Investment> investments;

    public Area(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getName();
    }

    public CountryState getCountryState(String country) {
        return this.countriesStates.get(country);
    }

    public Map<String, CountryState> getCountriesStates() {
        return countriesStates;
    }

    public void addCountryState(String country) {
        if (!this.countriesStates.containsKey(ClausewitzUtils.removeQuotes(country))) {
            ClausewitzItem stateItem = this.item.getChild("state");

            if (stateItem == null) {
                stateItem = this.item.addChild("state");
                stateItem.addVariable("area", ClausewitzUtils.addQuotes(getName()));
            }

            CountryState.addToItem(stateItem, country);
            refreshAttributes();
        }
    }

    public void removeCountryState(String country) {
        Iterator<Map.Entry<String, CountryState>> iterator = this.countriesStates.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, CountryState> entry = iterator.next();
            if (ClausewitzUtils.addQuotes(entry.getValue().getCountry()).equals(ClausewitzUtils.addQuotes(country))) {
                this.item.getChild("state").removeChild("country_state", i);
                this.save.getCountry(ClausewitzUtils.removeQuotes(country))
                         .getStates()
                         .remove(ClausewitzUtils.removeQuotes(getName()));
                break;
            }
        }
        refreshAttributes();
    }

    public Investment getInvestment(String country) {
        return this.investments.get(country);
    }

    public Map<String, Investment> getInvestments() {
        return investments;
    }

    public void addInvestments(String country, String... investments) {
        Investment investment = this.investments.get(ClausewitzUtils.removeQuotes(country));

        if (investment != null) {
            for (String newInvestment : investments) {
                investment.addInvestment(newInvestment);
            }
        } else {
            Investment.addToItem(this.item, country, investments);
            refreshAttributes();
        }
    }

    public void removeInvestments(String country) {
        Iterator<Map.Entry<String, Investment>> iterator = this.investments.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<String, Investment> entry = iterator.next();
            if (ClausewitzUtils.addQuotes(entry.getValue().getCountry()).equals(ClausewitzUtils.addQuotes(country))) {
                this.item.removeChild("investments", i);
                break;
            }
        }
        refreshAttributes();
    }

    public void removeInvestments(String country, String... investments) {
        Investment investment = this.investments.get(ClausewitzUtils.removeQuotes(country));

        if (investment != null) {
            for (String newInvestment : investments) {
                investment.removeInvestment(newInvestment);
            }
        }
    }

    private void refreshAttributes() {
        ClausewitzItem stateItem = this.item.getChild("state");

        if (stateItem != null) {
            List<ClausewitzItem> countryStateItems = stateItem.getChildren("country_state");
            this.countriesStates = countryStateItems.stream()
                                                    .map(CountryState::new)
                                                    .collect(Collectors.toMap(countryState -> ClausewitzUtils.removeQuotes(countryState
                                                                                                                                   .getCountry()),
                                                                              Function.identity(),
                                                                              (a, b) -> b,
                                                                              LinkedHashMap::new));
            this.countriesStates.forEach((country, countryState) -> this.save.getCountry(country)
                                                                             .getStates()
                                                                             .put(ClausewitzUtils.removeQuotes(getName()),
                                                                                  countryState));
        }

        List<ClausewitzItem> investmentsItems = this.item.getChildren("investments");
        this.investments = investmentsItems.stream()
                                           .map(Investment::new)
                                           .collect(Collectors.toMap(investment -> ClausewitzUtils.removeQuotes(investment
                                                                                                                        .getCountry()),
                                                                     Function.identity(),
                                                                     (a, b) -> b,
                                                                     LinkedHashMap::new));
    }
}
