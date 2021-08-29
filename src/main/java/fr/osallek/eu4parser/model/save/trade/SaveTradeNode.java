package fr.osallek.eu4parser.model.save.trade;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveTradeNode {

    private final ClausewitzItem item;

    private final Save save;

    private final int index;

    private Map<String, TradeNodeCountry> countries;

    private List<TradeNodeIncoming> incoming;

    private Map<String, Double> topProvinces;

    private Map<String, Double> topPower;

    public SaveTradeNode(ClausewitzItem item, Save save, int index) {
        this.item = item;
        this.save = save;
        this.index = index;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("definitions");
    }

    public int getIndex() {
        return index;
    }

    public Double getCurrent() {
        return this.item.getVarAsDouble("current");
    }

    public Double getVal() {
        return this.item.getVarAsDouble("val");
    }

    public Double getLocalValue() {
        return this.item.getVarAsDouble("local_value");
    }

    public Double getOutgoing() {
        return this.item.getVarAsDouble("outgoing");
    }

    public Double getValueAddedOutgoing() {
        return this.item.getVarAsDouble("value_added_outgoing");
    }

    public Double getRetention() {
        return this.item.getVarAsDouble("retention");
    }

    public List<Double> getSteerPower() {
        return this.item.getVariables("steer_power")
                        .stream()
                        .map(ClausewitzVariable::getAsDouble)
                        .collect(Collectors.toList());
    }

    public Integer getNbCollectors() {
        return this.item.getVarAsInt("num_collectors");
    }

    public Double getTotal() {
        return this.item.getVarAsDouble("total");
    }

    public Double getProvincePower() {
        return this.item.getVarAsDouble("p_pow");
    }

    public Double getMax() {
        return this.item.getVarAsDouble("max");
    }

    public Double getCollectorPower() {
        return this.item.getVarAsDouble("collector_power");
    }

    public Double getPullPower() {
        return this.item.getVarAsDouble("pull_power");
    }

    public Double getRetainPower() {
        return this.item.getVarAsDouble("retain_power");
    }

    public Double getHighestPower() {
        return this.item.getVarAsDouble("highest_power");
    }

    public Map<TradeGood, Double> getTradeGoodsSize() {
        Map<TradeGood, Double> productionLeaders = new LinkedHashMap<>();
        ClausewitzList tradeGoodsSizeList = this.item.getList("trade_goods_size");

        if (tradeGoodsSizeList != null) {
            for (int i = 0; i < tradeGoodsSizeList.size(); i++) {
                productionLeaders.put(this.save.getGame().getTradeGood(i - 1), tradeGoodsSizeList.getAsDouble(i));
            }
        }

        return productionLeaders;
    }

    public Map<SaveCountry, Double> getTopProvinces() {
        return topProvinces.entrySet().stream().collect(Collectors.toMap(entry -> this.save.getCountry(entry.getKey()), Map.Entry::getValue));
    }

    public Map<SaveCountry, Double> getTopPower() {
        return topPower.entrySet().stream().collect(Collectors.toMap(entry -> this.save.getCountry(entry.getKey()), Map.Entry::getValue));
    }

    public LocalDate getMostRecentTreasureShipPassage() {
        LocalDate date = this.item.getVarAsDate("most_recent_treasure_ship_passage");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public Map<SaveCountry, TradeNodeCountry> getCountries() {
        return countries.entrySet().stream().collect(Collectors.toMap(entry -> this.save.getCountry(entry.getKey()), Map.Entry::getValue));
    }

    public TradeNodeCountry getCountry(SaveCountry country) {
        return this.countries.get(country.getTag());
    }

    public List<TradeNodeIncoming> getIncoming() {
        return incoming;
    }

    private void refreshAttributes() {
        this.countries = this.item.getChildren()
                                  .stream()
                                  .filter(child -> child.hasVar("max_demand"))
                                  .map(child -> new TradeNodeCountry(this.save.getGame(), child))
                                  .collect(Collectors.toMap(TradeNodeCountry::getCountry, Function.identity()));

        ClausewitzList topProvincesList = this.item.getList("top_provinces");
        ClausewitzList topProvincesValuesList = this.item.getList("top_provinces_values");

        if (topProvincesList != null && topProvincesValuesList != null && topProvincesList.size() == topProvincesValuesList.size()) {
            this.topProvinces = new LinkedHashMap<>();
            for (int i = 0; i < topProvincesList.size(); i++) {
                this.topProvinces.put(ClausewitzUtils.removeQuotes(topProvincesList.get(i)), topProvincesValuesList.getAsDouble(i));
            }
        }

        ClausewitzList topPowerList = this.item.getList("top_power");
        ClausewitzList topPowerValuesList = this.item.getList("top_power_values");

        if (topPowerList != null && topPowerValuesList != null
            && topPowerList.size() == topPowerValuesList.size()) {
            this.topPower = new LinkedHashMap<>();
            for (int i = 0; i < topPowerList.size(); i++) {
                this.topPower.put(ClausewitzUtils.removeQuotes(topPowerList.get(i)), topPowerValuesList.getAsDouble(i));
            }
        }

        this.incoming = this.item.getChildren("incoming").stream().map(child -> new TradeNodeIncoming(child, this.save)).collect(Collectors.toList());
    }
}
