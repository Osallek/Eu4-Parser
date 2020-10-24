package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveArea {

    private final ClausewitzItem item;

    private final Save save;

    private Map<Country, CountryState> countriesStates;

    private Map<Country, SaveInvestment> investments;

    private List<SaveProvince> provinces;

    private List<SupplyDepot> supplyDepots;

    public SaveArea(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getName();
    }

    public List<SaveProvince> getProvinces() {
        return provinces;
    }

    public CountryState getCountryState(Country country) {
        return this.countriesStates.get(country);
    }

    public Map<Country, CountryState> getCountriesStates() {
        return countriesStates;
    }

    public void addCountryState(Country country) {
        if (!this.countriesStates.containsKey(country)) {
            ClausewitzItem stateItem = this.item.getChild("state");

            if (stateItem == null) {
                stateItem = this.item.addChild("state");
                stateItem.addVariable("area", ClausewitzUtils.addQuotes(getName()));
            }

            CountryState.addToItem(stateItem, country);
            refreshAttributes();
        }
    }

    public void removeCountryState(Country country) {
        Iterator<Map.Entry<Country, CountryState>> iterator = this.countriesStates.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<Country, CountryState> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.getChild("state").removeChild("country_state", i);
                country.getStates().remove(ClausewitzUtils.removeQuotes(getName()));
                break;
            }
        }
        refreshAttributes();
    }

    public SaveInvestment getInvestment(Country country) {
        return this.investments.get(country);
    }

    public Map<Country, SaveInvestment> getInvestments() {
        return investments;
    }

    public void addInvestments(Country country, String... investments) {
        SaveInvestment investment = this.investments.get(country);

        if (investment != null) {
            for (String newInvestment : investments) {
                investment.addInvestment(newInvestment);
            }
        } else {
            SaveInvestment.addToItem(this.item, country, investments);
            refreshAttributes();
        }
    }

    public void removeInvestments(Country country) {
        Iterator<Map.Entry<Country, SaveInvestment>> iterator = this.investments.entrySet().iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Map.Entry<Country, SaveInvestment> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.removeChild("investments", i);
                break;
            }
        }
        refreshAttributes();
    }

    public void removeInvestments(Country country, String... investments) {
        SaveInvestment investment = this.investments.get(country);

        if (investment != null) {
            for (String newInvestment : investments) {
                investment.removeInvestment(newInvestment);
            }
        }
    }

    public List<SupplyDepot> getSupplyDepots() {
        return supplyDepots;
    }

    private void refreshAttributes() {
        this.provinces = this.save.getGame().getArea(getName()).getProvinces().stream().map(this.save::getProvince).collect(Collectors.toList());
        ClausewitzItem stateItem = this.item.getChild("state");

        if (stateItem != null) {
            List<ClausewitzItem> countryStateItems = stateItem.getChildren("country_state");
            this.countriesStates = countryStateItems.stream()
                                                    .map(child -> new CountryState(child, this.save))
                                                    .collect(Collectors.toMap(CountryState::getCountry, Function.identity(), (a, b) -> b, LinkedHashMap::new));
            this.countriesStates.forEach((country, countryState) -> country.getStates().put(this, countryState));
        }

        List<ClausewitzItem> investmentsItems = this.item.getChildren("investments");
        this.investments = investmentsItems.stream()
                                           .map(child -> new SaveInvestment(child, this.save))
                                           .collect(Collectors.toMap(SaveInvestment::getCountry, Function.identity(), (a, b) -> b, LinkedHashMap::new));

        ClausewitzItem supplyDepotsItem = this.item.getChild("supply_depots");
        if (supplyDepotsItem != null) {
            this.supplyDepots = supplyDepotsItem.getChildren()
                                                .stream()
                                                .map(child -> new SupplyDepot(child, this.save))
                                                .collect(Collectors.toList());
        }
    }
}
