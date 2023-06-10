package fr.osallek.eu4parser.model.save.trade;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.util.Optional;

public record TradeNodeIncoming(ClausewitzItem item, Save save) {

    public Optional<SaveTradeNode> getFrom() {
        return getFromIndex().map(this.save::getTradeNode);
    }

    public Optional<Integer> getFromIndex() {
        return this.item.getVarAsInt("from");
    }

    public Optional<Double> getValue() {
        return this.item.getVarAsDouble("value");
    }

    public Optional<Double> getAdd() {
        return this.item.getVarAsDouble("add");
    }
}
