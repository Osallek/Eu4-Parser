package com.osallek.eu4parser.model.save.trade;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.common.Eu4Utils;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.TradeGood;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TradeNode {

    private final ClausewitzItem item;

    private final Game game;

    private Map<String, TradeNodeCountry> countries;

    private Map<String, Double> topProvinces;

    private Map<String, Double> topPower;

    public TradeNode(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
        refreshAttributes();
    }

    public String getName() {
        return this.item.getVarAsString("definitions");
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
                productionLeaders.put(this.game.getTradeGood(i), tradeGoodsSizeList.getAsDouble(i));
            }
        }

        return productionLeaders;
    }

    public Map<String, Double> getTopProvinces() {
        return topProvinces;
    }

    public Map<String, Double> getTopPower() {
        return topPower;
    }

    public Date getMostRecentTreasureShipPassage() {
        Date date = this.item.getVarAsDate("most_recent_treasure_ship_passage");

        if (date == null || Eu4Utils.DEFAULT_DATE.equals(date)) {
            return null;
        }

        return date;
    }

    public Map<String, TradeNodeCountry> getCountries() {
        return countries;
    }

    private void refreshAttributes() {
        this.countries = this.item.getChildren()
                                  .stream()
                                  .filter(child -> child.getVar("max_demand") != null)
                                  .map(TradeNodeCountry::new)
                                  .collect(Collectors.toMap(TradeNodeCountry::getCountry, Function.identity()));

        ClausewitzList topProvincesList = this.item.getList("top_provinces");
        ClausewitzList topProvincesValuesList = this.item.getList("top_provinces_values");

        if (topProvincesList != null && topProvincesValuesList != null
            && topProvincesList.size() == topProvincesValuesList.size()) {
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
    }
}
