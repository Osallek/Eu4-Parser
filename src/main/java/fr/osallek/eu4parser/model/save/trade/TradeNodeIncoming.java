package fr.osallek.eu4parser.model.save.trade;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

public record TradeNodeIncoming(ClausewitzItem item, Save save) {

    public SaveTradeNode getFrom() {
        return this.save.getTradeNode(getFromIndex());
    }

    public Integer getFromIndex() {
        return this.item.getVarAsInt("from");
    }

    public Double getValue() {
        return this.item.getVarAsDouble("value");
    }

    public Double getAdd() {
        return this.item.getVarAsDouble("add");
    }
}
