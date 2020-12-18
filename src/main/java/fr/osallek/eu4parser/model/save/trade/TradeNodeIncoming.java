package fr.osallek.eu4parser.model.save.trade;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

public class TradeNodeIncoming {

    private final ClausewitzItem item;

    private final Save save;

    public TradeNodeIncoming(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
    }

    public TradeNode getFrom() {
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
