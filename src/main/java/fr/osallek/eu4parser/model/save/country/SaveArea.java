package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Investment;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveArea {

    private final ClausewitzItem item;

    private final Save save;

    private Map<SaveCountry, CountryState> countriesStates;

    private Map<SaveCountry, SaveInvestment> investments;

    private List<SaveProvince> provinces;

    private List<SupplyDepot> supplyDepots;

    public SaveArea(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        refreshAttributes();
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getName());
    }

    public List<SaveProvince> getProvinces() {
        return provinces;
    }

    public CountryState getCountryState(SaveCountry country) {
        return this.countriesStates == null ? null : this.countriesStates.get(country);
    }

    public Map<SaveCountry, CountryState> getCountriesStates() {
        return countriesStates;
    }

    public void addCountryState(SaveCountry country) {
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

    public void removeCountryState(SaveCountry country) {
        Iterator<Map.Entry<SaveCountry, CountryState>> iterator = this.countriesStates.entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<SaveCountry, CountryState> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.getChild("state").removeChild("country_state", i);
                country.getStates().remove(ClausewitzUtils.removeQuotes(getName()));
                break;
            }
        }
        refreshAttributes();
    }

    public SaveInvestment getInvestment(SaveCountry country) {
        return this.investments.get(country);
    }

    public Map<SaveCountry, SaveInvestment> getInvestments() {
        return investments;
    }

    public void addInvestments(SaveCountry country, Investment... investments) {
        SaveInvestment investment = this.investments.get(country);

        if (investment != null) {
            Arrays.stream(investments).forEach(investment::addInvestment);
        } else {
            SaveInvestment.addToItem(this.item, country, investments);
            refreshAttributes();
        }
    }

    public void removeInvestments(SaveCountry country) {
        Iterator<Map.Entry<SaveCountry, SaveInvestment>> iterator = this.investments.entrySet().iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Map.Entry<SaveCountry, SaveInvestment> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.removeChild("investments", i);
                break;
            }
        }
        refreshAttributes();
    }

    public void removeInvestments(SaveCountry country, Investment... investments) {
        SaveInvestment investment = this.investments.get(country);

        if (investment != null) {
            Arrays.stream(investments).forEach(investment::removeInvestment);
        }
    }

    public List<SupplyDepot> getSupplyDepots() {
        return supplyDepots;
    }

    private void refreshAttributes() {
        this.provinces = this.save.getGame().getArea(getName()).getProvinces().stream().map(this.save::getProvince).toList();
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
                                                .toList();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        SaveArea saveArea = (SaveArea) o;
        return Objects.equals(getName(), saveArea.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }
}
