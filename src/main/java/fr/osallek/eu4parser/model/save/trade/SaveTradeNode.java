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
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class SaveTradeNode {

    private final ClausewitzItem item;

    private final Save save;

    private final int index;

    public SaveTradeNode(ClausewitzItem item, Save save, int index) {
        this.item = item;
        this.save = save;
        this.index = index;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("definitions"));
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
                        .toList();
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
        ClausewitzList topProvincesList = this.item.getList("top_provinces");
        ClausewitzList topProvincesValuesList = this.item.getList("top_provinces_values");
        Map<SaveCountry, Double> topProvinces = new LinkedHashMap<>();

        if (topProvincesList != null && topProvincesValuesList != null && topProvincesList.size() == topProvincesValuesList.size()) {
            for (int i = 0; i < topProvincesList.size(); i++) {
                topProvinces.put(this.save.getCountry(topProvincesList.get(i)), topProvincesValuesList.getAsDouble(i));
            }
        }

        return topProvinces;
    }

    public Map<SaveCountry, Double> getTopPower() {
        ClausewitzList topPowerList = this.item.getList("top_power");
        ClausewitzList topPowerValuesList = this.item.getList("top_power_values");
        Map<SaveCountry, Double> topPower = new LinkedHashMap<>();

        if (topPowerList != null && topPowerValuesList != null
            && topPowerList.size() == topPowerValuesList.size()) {
            for (int i = 0; i < topPowerList.size(); i++) {
                topPower.put(this.save.getCountry(topPowerList.get(i)), topPowerValuesList.getAsDouble(i));
            }
        }

        return topPower;
    }

    public LocalDate getMostRecentTreasureShipPassage() {
        LocalDate date = this.item.getVarAsDate("most_recent_treasure_ship_passage");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public Map<SaveCountry, TradeNodeCountry> getCountries() {
        return this.item.getChildren()
                        .stream()
                        .filter(child -> child.hasVar("max_demand"))
                        .map(child -> new TradeNodeCountry(this.save.getGame(), child))
                        .collect(Collectors.toMap(tnc -> this.save.getCountry(ClausewitzUtils.removeQuotes(tnc.getCountry())), Function.identity()));
    }

    public TradeNodeCountry getCountry(SaveCountry country) {
        return Optional.ofNullable(this.item.getChild(country.getTag())).map(child -> new TradeNodeCountry(this.save.getGame(), child)).orElse(null);
    }

    public List<TradeNodeIncoming> getIncoming() {
        return this.item.getChildren("incoming").stream().map(child -> new TradeNodeIncoming(child, this.save)).toList();
    }
}
