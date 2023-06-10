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
import java.util.Optional;
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
        return ClausewitzUtils.removeQuotes(this.item.getName());
    }

    public Optional<Integer> getType() {
        return this.item.getVarAsInt("type");
    }

    public Optional<Double> getVal() {
        return this.item.getVarAsDouble("val");
    }

    public Optional<Double> getPotential() {
        return this.item.getVarAsDouble("potential");
    }

    public Optional<Double> getPrev() {
        return this.item.getVarAsDouble("prev");
    }

    public Optional<Double> getMaxPower() {
        return this.item.getVarAsDouble("max_pow");
    }

    public Optional<Double> getMaxDemand() {
        return this.item.getVarAsDouble("max_demand");
    }

    public Optional<Double> getProvincePower() {
        return this.item.getVarAsDouble("province_power");
    }

    public Optional<Double> getShipPower() {
        return this.item.getVarAsDouble("ship_power");
    }

    public Optional<Double> getSteerPower() {
        return this.item.getVarAsDouble("steer_power");
    }

    public Optional<Double> getPowerFaction() {
        return this.item.getVarAsDouble("power_fraction");
    }

    public Optional<Double> getMoney() {
        return this.item.getVarAsDouble("money");
    }

    public Optional<Double> getTotal() {
        return this.item.getVarAsDouble("total");
    }

    public Optional<Double> getAdd() {
        return this.item.getVarAsDouble("add");
    }

    public boolean hasTrader() {
        return this.item.getVarAsBool("has_trader").map(BooleanUtils::toBoolean).orElse(false);
    }

    public boolean hasCapital() {
        return this.item.getVarAsBool("has_capital").map(BooleanUtils::toBoolean).orElse(false);
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

    public Optional<Integer> getNbLightShip() {
        return this.item.getVarAsInt("light_ship");
    }

    public Optional<Double> getTotalPowerFromTransfer() {
        return this.item.getVarAsDouble("t_in");
    }

    public Optional<Double> getTotalPowerTransferred() {
        return this.item.getVarAsDouble("t_out");
    }

    public Map<String, Double> getPowerFromTransfer() {
        Map<String, Double> powerFromTransfer = new HashMap<>();
        this.item.getChild("t_from").ifPresent(item -> item.getVariables().forEach(v -> powerFromTransfer.put(v.getName(), v.getAsDouble())));
        return powerFromTransfer;
    }

    public Map<String, Double> getPowerTransferred() {
        Map<String, Double> powerTransferred = new HashMap<>();
        this.item.getChild("t_to").ifPresent(item -> item.getVariables().forEach(v -> powerTransferred.put(v.getName(), v.getAsDouble())));
        return powerTransferred;
    }

    public Optional<Double> getPrivateerMission() {
        return this.item.getVarAsDouble("privateer_mission");
    }

    public Optional<Double> getPrivateerMoney() {
        return this.item.getVarAsDouble("privateer_money");
    }

    public Optional<Double> getAlreadySent() {
        return this.item.getVarAsDouble("already_sent");
    }

    public Optional<TradePolicy> getTradePolicy() {
        return this.item.getVarAsString("trading_policy").map(ClausewitzUtils::removeQuotes).map(this.game::getTradePolicy);
    }

    public Optional<LocalDate> getTradePolicyDate() {
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
