package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import org.apache.commons.lang3.BooleanUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public class ProvinceHistoryItem {

    private final ClausewitzItem item;

    private final Game game;

    public ProvinceHistoryItem(Province province, LocalDate date) {
        Optional<ClausewitzItem> nextHistoryItem = province.getDefaultHistoryItem()
                .item
                .getChildren()
                .stream()
                .filter(child -> Eu4Utils.DATE_PATTERN.matcher(child.getName()).matches())
                .sorted(Comparator.comparing(ClausewitzItem::getName))
                .filter(child -> Eu4Utils.stringToDate(child.getName()).isAfter(date))
                .findFirst();
        this.item = new ClausewitzItem(province.getDefaultHistoryItem().item, ClausewitzUtils.dateToString(date),
                                       nextHistoryItem.map(ClausewitzItem::getOrder).orElse(province.getDefaultHistoryItem().item.getNbObjects()), true, true);
        this.game = province.getDefaultHistoryItem().game;
    }

    public ProvinceHistoryItem(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public Country getOwner() {
        ClausewitzVariable variable = this.item.getVar("owner");
        return variable == null ? null : this.game.getCountry(variable.getValue());
    }

    public void setOwner(Country owner) {
        setOwner(owner.getTag());
    }

    public void setOwner(String owner) {
        if (owner == null) {
            this.item.removeVariable("owner");
        } else {
            this.item.setVariable("owner", owner, 0);
        }
    }

    public Country getController() {
        ClausewitzVariable variable = this.item.getVar("controller");
        return variable == null ? null : this.game.getCountry(variable.getValue());
    }

    public void setController(Country controller) {
        setController(controller.getTag());
    }

    public void setController(String controller) {
        if (controller == null) {
            this.item.removeVariable("controller");
        } else {
            this.item.setVariable("controller", controller, 1);
        }
    }

    public List<Country> getAddCores() {
        return this.item.getVarsAsStrings("add_core").stream().map(this.game::getCountry).toList();
    }

    public void addAddCore(String addCore) {
        this.item.addVariable("add_core", addCore.toUpperCase(),
                              this.item.getVars("add_core").stream().mapToInt(ClausewitzVariable::getOrder).max().orElse(this.item.getNbObjects()), true);
    }

    public void addAddCore(Country country) {
        addAddCore(country.getTag());
    }

    public void removeAddCore(String addCore) {
        this.item.removeVariable("add_core", addCore.toUpperCase());
    }

    public void removeAddCore(Country country) {
        removeAddCore(country.getTag());
    }

    public List<Country> getRemoveCores() {
        return this.item.getVarsAsStrings("remove_core").stream().map(this.game::getCountry).toList();
    }

    public void addRemoveCore(String removeCore) {
        this.item.addVariable("remove_core", removeCore.toUpperCase(),
                              this.item.getVars("remove_core").stream().mapToInt(ClausewitzVariable::getOrder).max().orElse(this.item.getNbObjects()), true);
    }

    public void addRemoveCore(Country country) {
        addRemoveCore(country.getTag());
    }

    public void removeRemoveCore(String removeCore) {
        this.item.removeVariable("remove_core", removeCore.toUpperCase());
    }

    public void removeRemoveCore(Country country) {
        removeRemoveCore(country.getTag());
    }

    public Boolean getCity() {
        return this.item.getVarAsBool("is_city");
    }

    public void setIsCity(Boolean isCity) {
        if (isCity == null) {
            this.item.removeVariable("is_city");
        } else {
            this.item.setVariable("is_city", isCity);
        }
    }

    public Culture getCulture() {
        return this.game.getCulture(this.item.getVarAsString("culture"));
    }

    public void setCulture(String culture) {
        this.item.setVariable("culture", culture);
    }

    public void setCulture(Culture culture) {
        setCulture(culture.getName());
    }

    public Religion getReligion() {
        return this.game.getReligion(this.item.getVarAsString("religion"));
    }

    public void setReligion(String religion) {
        this.item.setVariable("religion", religion);
    }

    public void setReligion(Religion religion) {
        setReligion(religion.getName());
    }

    public Integer getBaseTax() {
        return this.item.getVarAsInt("base_tax");
    }

    public void setBaseTax(Integer baseTax) {
        if (baseTax == null) {
            this.item.removeVariable("base_tax");
        } else {
            this.item.setVariable("base_tax", baseTax);
        }
    }

    public Integer getBaseProduction() {
        return this.item.getVarAsInt("base_production");
    }

    public void setBaseProduction(Integer baseProduction) {
        if (baseProduction == null) {
            this.item.removeVariable("base_production");
        } else {
            this.item.setVariable("base_production", baseProduction);
        }
    }

    public Integer getBaseManpower() {
        return this.item.getVarAsInt("base_manpower");
    }

    public void setBaseManpower(Integer baseManpower) {
        if (baseManpower == null) {
            this.item.removeVariable("base_manpower");
        } else {
            this.item.setVariable("base_manpower", baseManpower);
        }
    }

    public TradeGood getTradeGoods() {
        return this.game.getTradeGood(this.item.getVarAsString("trade_goods"));
    }

    public void setTradeGood(String tradeGood) {
        this.item.setVariable("trade_goods", tradeGood);
    }

    public void setTradeGood(TradeGood tradeGood) {
        setTradeGood(tradeGood.getName());
    }

    public Boolean getHre() {
        return this.item.getVarAsBool("hre");
    }

    public void setHre(Boolean hre) {
        if (hre == null) {
            this.item.removeVariable("hre");
        } else {
            this.item.setVariable("hre", hre);
        }
    }

    public String getCapital() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("capital"));
    }

    public void setCapital(String capital) {
        this.item.setVariable("capital", ClausewitzUtils.addQuotes(capital));
    }

    public List<TechGroup> getDiscoveredBy() {
        return this.item.getVarsAsStrings("discovered_by").stream().map(this.game::getTechGroup).toList();
    }

    public void addDiscoveredBy(String discoveredBy) {
        this.item.addVariable("discovered_By", discoveredBy.toUpperCase());
    }

    public void addDiscoveredBy(Country country) {
        addDiscoveredBy(country.getTag());
    }

    public void removeDiscoveredBy(String discoveredBy) {
        this.item.removeVariable("discovered_By", discoveredBy.toUpperCase());
    }

    public void removeDiscoveredBy(Country country) {
        removeDiscoveredBy(country.getTag());
    }

    public Boolean getReformationCenter() {
        return this.item.getVarAsBool("reformation_center");
    }

    public void setReformationCenter(Boolean reformationCenter) {
        if (reformationCenter == null) {
            this.item.removeVariable("reformation_center");
        } else {
            this.item.setVariable("reformation_center", reformationCenter);
        }
    }

    public Boolean getSeatInParliament() {
        return this.item.getVarAsBool("seat_in_parliament");
    }

    public void setSeatInParliament(Boolean seatInParliament) {
        if (seatInParliament == null) {
            this.item.removeVariable("seat_in_parliament");
        } else {
            this.item.setVariable("seat_in_parliament", seatInParliament);
        }
    }

    public Integer getUnrest() {
        return this.item.getVarAsInt("unrest");
    }

    public void setUnrest(Integer unrest) {
        if (unrest == null) {
            this.item.removeVariable("unrest");
        } else {
            this.item.setVariable("unrest", unrest);
        }
    }

    public Integer getCenterOfTrade() {
        return this.item.getVarAsInt("center_of_trade");
    }

    public void setCenterOfTrade(Integer centerOfTrade) {
        if (centerOfTrade == null) {
            this.item.removeVariable("center_of_trade");
        } else {
            this.item.setVariable("center_of_trade", centerOfTrade);
        }
    }

    public Integer getExtraCost() {
        return this.item.getVarAsInt("extra_cost");
    }

    public void setExtraCost(Integer extraCost) {
        if (extraCost == null) {
            this.item.removeVariable("extra_cost");
        } else {
            this.item.setVariable("extra_cost", extraCost);
        }
    }

    public Integer getNativeSize() {
        return this.item.getVarAsInt("native_size");
    }

    public void setNativeSize(Integer nativeSize) {
        if (nativeSize == null) {
            this.item.removeVariable("native_size");
        } else {
            this.item.setVariable("native_size", nativeSize, this.item.getVar("base_tax").getOrder());
        }
    }

    public Integer getNativeHostileness() {
        return this.item.getVarAsInt("native_hostileness");
    }

    public void setNativeHostileness(Integer nativeHostileness) {
        if (nativeHostileness == null) {
            this.item.removeVariable("native_hostileness");
        } else {
            this.item.setVariable("native_hostileness", nativeHostileness, this.item.getVar("base_tax").getOrder());
        }
    }

    public Integer getNativeFerocity() {
        return this.item.getVarAsInt("native_ferocity");
    }

    public void setNativeFerocity(Integer nativeFerocity) {
        if (nativeFerocity == null) {
            this.item.removeVariable("native_ferocity");
        } else {
            this.item.setVariable("native_ferocity", nativeFerocity, this.item.getVar("base_tax").getOrder());
        }
    }

    public List<ModifierApply> getPermanentModifier() {
        if (this.item.hasChild("add_permanent_province_modifier")) {
            return this.item.getChildren("add_permanent_province_modifier").stream().map(ModifierApply::new).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public List<ModifierApply> getRemoveModifier() {
        if (this.item.hasChild("remove_province_modifier")) {
            return this.item.getChildren("remove_province_modifier").stream().map(ModifierApply::new).toList();
        } else {
            return new ArrayList<>();
        }
    }

    public ProvinceRevolt getRevolt() {
        if (this.item.hasChild("revolt")) {
            return new ProvinceRevolt(this.item.getChild("revolt"));
        } else {
            return null;
        }
    }

    public List<Building> getBuildings() {
        return this.game.getBuildings()
                        .stream()
                        .filter(building -> this.item.hasVar(building.getName()) && BooleanUtils.toBoolean(this.item.getVarAsBool(building.getName())))
                        .toList();
    }

    public void addBuilding(String building) {
        this.item.addVariable(building, true);
    }

    public void addBuilding(Building building) {
        addBuilding(building);
    }

    public void removeBuilding(String building) {
        this.item.addVariable(building, false);
    }

    public void removeBuilding(Building building) {
        removeBuilding(building);
    }

    public void write(BufferedWriter writer) throws IOException {
        this.item.write(writer, true, 0, new HashMap<>());
    }
}
