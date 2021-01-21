package fr.osallek.eu4parser.model.save.trade;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.TradePolicy;
import org.apache.commons.lang3.BooleanUtils;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TradeNodeCountry {

    private final Game game;

    private final ClausewitzItem item;

    private Map<String, TradeNodeModifier> modifiers;

    public TradeNodeCountry(Game game, ClausewitzItem item) {
        this.game = game;
        this.item = item;
        refreshAttributes();
    }

    public String getCountry() {
        return this.item.getName();
    }

    public Integer getType() {
        return this.item.getVarAsInt("type");
    }

    public Double getVal() {
        return this.item.getVarAsDouble("val");
    }

    public Double getPotential() {
        return this.item.getVarAsDouble("potential");
    }

    public Double getPrev() {
        return this.item.getVarAsDouble("prev");
    }

    public Double getMaxPower() {
        return this.item.getVarAsDouble("max_pow");
    }

    public Double getMaxDemand() {
        return this.item.getVarAsDouble("max_demand");
    }

    public Double getProvincePower() {
        return this.item.getVarAsDouble("province_power");
    }

    public Double getShipPower() {
        return this.item.getVarAsDouble("ship_power");
    }

    public Double getSteerPower() {
        return this.item.getVarAsDouble("steer_power");
    }

    public Double getPowerFaction() {
        return this.item.getVarAsDouble("power_fraction");
    }

    public Double getMoney() {
        return this.item.getVarAsDouble("money");
    }

    public Double getTotal() {
        return this.item.getVarAsDouble("total");
    }

    public Double getAdd() {
        return this.item.getVarAsDouble("add");
    }

    public boolean hasTrader() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("has_trader"));
    }

    public boolean hasCapital() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("has_capital"));
    }

    public Map<String, TradeNodeModifier> getModifiers() {
        return modifiers;
    }

    public void addModifier(String key, int duration, double power, double powerModifier) {
        TradeNodeModifier.addToItem(this.item, key, duration, power, powerModifier);
        refreshAttributes();
    }

    public void removeModifier(String key) {
        int i = 0;
        for (String modifier : this.modifiers.keySet()) {
            if (modifier.equals(ClausewitzUtils.addQuotes(key))) {
                this.item.removeChild("modifier", i);
                break;
            }
            i++;
        }

        refreshAttributes();
    }

    public Integer getNbLightShip() {
        return this.item.getVarAsInt("light_ship");
    }

    public Double getTotalPowerFromTransfer() {
        return this.item.getVarAsDouble("t_in");
    }

    public Double getTotalPowerTransferred() {
        return this.item.getVarAsDouble("t_out");
    }

    public Map<String, Double> getPowerFromTransfer() {
        Map<String, Double> powerFromTransfer = new HashMap<>();

        ClausewitzItem powerFromTransferItem = this.item.getChild("t_from");

        if (powerFromTransferItem != null) {
            powerFromTransferItem.getVariables()
                                 .forEach(var -> powerFromTransfer.put(var.getName(), var.getAsDouble()));
        }

        return powerFromTransfer;
    }

    public Map<String, Double> getPowerTransferred() {
        Map<String, Double> powerTransferred = new HashMap<>();

        ClausewitzItem powerFromTransferItem = this.item.getChild("t_to");

        if (powerFromTransferItem != null) {
            powerFromTransferItem.getVariables()
                                 .forEach(var -> powerTransferred.put(var.getName(), var.getAsDouble()));
        }

        return powerTransferred;
    }

    public Double getPrivateerMission() {
        return this.item.getVarAsDouble("privateer_mission");
    }

    public Double getPrivateerMoney() {
        return this.item.getVarAsDouble("privateer_money");
    }

    public Double getAlreadySent() {
        return this.item.getVarAsDouble("already_sent");
    }

    public TradePolicy getTradePolicy() {
        return this.game.getTradePolicy(ClausewitzUtils.removeQuotes(this.item.getVarAsString("trading_policy")));
    }

    public LocalDate getTradePolicyDate() {
        return this.item.getVarAsDate("trading_policy_date");
    }

    public void setTradePolicy(TradePolicy name, LocalDate date) {
        this.item.setVariable("trading_policy", ClausewitzUtils.addQuotes(name.getName()));
        this.item.setVariable("trading_policy_date", date);
    }

    public void removeTradePolicy() {
        this.item.removeVariable("trading_policy");
        this.item.removeVariable("trading_policy_date");
    }

    private void refreshAttributes() {
        List<ClausewitzItem> modifiersItems = this.item.getChildren("modifier");
        this.modifiers = modifiersItems.stream()
                                       .map(TradeNodeModifier::new)
                                       .collect(Collectors.toMap(modifier -> ClausewitzUtils.removeQuotes(modifier.getKey()),
                                                                 Function.identity(), (a, b) -> a, LinkedHashMap::new));
    }
}
