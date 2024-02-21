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

    public SaveArea(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getName());
    }

    public List<SaveProvince> getProvinces() {
        return this.save.getGame().getArea(getName()).getProvinces().stream().map(this.save::getProvince).toList();
    }

    public CountryState getCountryState(SaveCountry country) {
        return getCountriesStates().get(country);
    }

    public Map<SaveCountry, CountryState> getCountriesStates() {
        ClausewitzItem stateItem = this.item.getChild("state");
        Map<SaveCountry, CountryState> countriesStates = null;

        if (stateItem != null) {
            List<ClausewitzItem> countryStateItems = stateItem.getChildren("country_state");
            countriesStates = countryStateItems.stream()
                                               .map(child -> new CountryState(child, this.save))
                                               .collect(Collectors.toMap(CountryState::getCountry, Function.identity(), (a, b) -> b, LinkedHashMap::new));
        }

        return countriesStates;
    }

    public void addCountryState(SaveCountry country) {
        if (getCountryState(country) == null) {
            ClausewitzItem stateItem = this.item.getChild("state");

            if (stateItem == null) {
                stateItem = this.item.addChild("state");
                stateItem.addVariable("area", ClausewitzUtils.addQuotes(getName()));
            }

            CountryState.addToItem(stateItem, country);
        }
    }

    public void removeCountryState(SaveCountry country) {
        Iterator<Map.Entry<SaveCountry, CountryState>> iterator = getCountriesStates().entrySet().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Map.Entry<SaveCountry, CountryState> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.getChild("state").removeChild("country_state", i);
                country.getStates().remove(this.save.getAreas().get(ClausewitzUtils.removeQuotes(getName())));
                break;
            }
        }
    }

    public SaveInvestment getInvestment(SaveCountry country) {
        return getInvestments().get(country);
    }

    public Map<SaveCountry, SaveInvestment> getInvestments() {
        return this.item.getChildren("investments")
                        .stream()
                        .map(child -> new SaveInvestment(child, this.save))
                        .collect(Collectors.toMap(SaveInvestment::getCountry, Function.identity(), (a, b) -> b, LinkedHashMap::new));
    }

    public void addInvestments(SaveCountry country, Investment... investments) {
        SaveInvestment investment = getInvestment(country);

        if (investment != null) {
            Arrays.stream(investments).forEach(investment::addInvestment);
        } else {
            SaveInvestment.addToItem(this.item, country, investments);
        }
    }

    public void removeInvestments(SaveCountry country) {
        Iterator<Map.Entry<SaveCountry, SaveInvestment>> iterator = getInvestments().entrySet().iterator();
        int i = 0;

        while (iterator.hasNext()) {
            Map.Entry<SaveCountry, SaveInvestment> entry = iterator.next();
            if (entry.getValue().getCountry().equals(country)) {
                this.item.removeChild("investments", i);
                break;
            }
        }
    }

    public void removeInvestments(SaveCountry country, Investment... investments) {
        SaveInvestment investment = getInvestment(country);

        if (investment != null) {
            Arrays.stream(investments).forEach(investment::removeInvestment);
        }
    }

    public List<SupplyDepot> getSupplyDepots() {
        ClausewitzItem supplyDepotsItem = this.item.getChild("supply_depots");
        return supplyDepotsItem != null ? supplyDepotsItem.getChildren()
                                                          .stream()
                                                          .map(child -> new SupplyDepot(child, this.save))
                                                          .toList() : null;
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
