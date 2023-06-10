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
import java.util.function.Predicate;
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
        return this.item.getVarAsString("definitions").orElse("");
    }

    public int getIndex() {
        return index;
    }

    public Optional<Double> getCurrent() {
        return this.item.getVarAsDouble("current");
    }

    public Optional<Double> getVal() {
        return this.item.getVarAsDouble("val");
    }

    public Optional<Double> getLocalValue() {
        return this.item.getVarAsDouble("local_value");
    }

    public Optional<Double> getOutgoing() {
        return this.item.getVarAsDouble("outgoing");
    }

    public Optional<Double> getValueAddedOutgoing() {
        return this.item.getVarAsDouble("value_added_outgoing");
    }

    public Optional<Double> getRetention() {
        return this.item.getVarAsDouble("retention");
    }

    public List<Double> getSteerPower() {
        return this.item.getVariables("steer_power")
                        .stream()
                        .map(ClausewitzVariable::getAsDouble)
                        .toList();
    }

    public Optional<Integer> getNbCollectors() {
        return this.item.getVarAsInt("num_collectors");
    }

    public Optional<Double> getTotal() {
        return this.item.getVarAsDouble("total");
    }

    public Optional<Double> getProvincePower() {
        return this.item.getVarAsDouble("p_pow");
    }

    public Optional<Double> getMax() {
        return this.item.getVarAsDouble("max");
    }

    public Optional<Double> getCollectorPower() {
        return this.item.getVarAsDouble("collector_power");
    }

    public Optional<Double> getPullPower() {
        return this.item.getVarAsDouble("pull_power");
    }

    public Optional<Double> getRetainPower() {
        return this.item.getVarAsDouble("retain_power");
    }

    public Optional<Double> getHighestPower() {
        return this.item.getVarAsDouble("highest_power");
    }

    public Map<TradeGood, Double> getTradeGoodsSize() {
        Map<TradeGood, Double> productionLeaders = new LinkedHashMap<>();
        this.item.getList("trade_goods_size").ifPresent(tradeGoodsSizeList -> {
            for (int i = 0; i < tradeGoodsSizeList.size(); i++) {
                productionLeaders.put(this.save.getGame().getTradeGood(i - 1), tradeGoodsSizeList.getAsDouble(i).orElse(0d));
            }
        });

        return productionLeaders;
    }

    public Map<SaveCountry, Double> getTopProvinces() {
        return topProvinces.entrySet().stream().collect(Collectors.toMap(entry -> this.save.getCountry(entry.getKey()), Map.Entry::getValue));
    }

    public Map<SaveCountry, Double> getTopPower() {
        return topPower.entrySet().stream().collect(Collectors.toMap(entry -> this.save.getCountry(entry.getKey()), Map.Entry::getValue));
    }

    public Optional<LocalDate> getMostRecentTreasureShipPassage() {
        return this.item.getVarAsDate("most_recent_treasure_ship_passage").filter(Predicate.not(Eu4Utils.DEFAULT_DATE::equals));
    }

    public Map<SaveCountry, TradeNodeCountry> getCountries() {
        return countries.entrySet()
                        .stream()
                        .collect(Collectors.toMap(entry -> this.save.getCountry(ClausewitzUtils.removeQuotes(entry.getKey())), Map.Entry::getValue));
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

        Optional<ClausewitzList> topProvincesList = this.item.getList("top_provinces");
        Optional<ClausewitzList> topProvincesValuesList = this.item.getList("top_provinces_values");

        if (topProvincesList.isPresent() && topProvincesValuesList.isPresent() && topProvincesList.get().size() == topProvincesValuesList.get().size()) {
            this.topProvinces = new LinkedHashMap<>();
            for (int i = 0; i < topProvincesList.get().size(); i++) {
                this.topProvinces.put(ClausewitzUtils.removeQuotes(topProvincesList.get().get(i).orElse("")),
                                      topProvincesValuesList.get().getAsDouble(i).orElse(0d));
            }
        }

        Optional<ClausewitzList> topPowerList = this.item.getList("top_power");
        Optional<ClausewitzList> topPowerValuesList = this.item.getList("top_power_values");

        if (topPowerList.isPresent() && topPowerValuesList.isPresent() && topPowerList.get().size() == topPowerValuesList.get().size()) {
            this.topPower = new LinkedHashMap<>();
            for (int i = 0; i < topPowerList.get().size(); i++) {
                this.topPower.put(ClausewitzUtils.removeQuotes(topPowerList.get().get(i).orElse("")), topPowerValuesList.get().getAsDouble(i).orElse(0d));
            }
        }

        this.incoming = this.item.getChildren("incoming").stream().map(child -> new TradeNodeIncoming(child, this.save)).toList();
    }
}
