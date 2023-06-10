package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;
import java.util.Optional;

public class QuantifyDatableRelation extends DatableRelation {

    public QuantifyDatableRelation(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Optional<Double> getAmount() {
        return this.item.getVarAsDouble("amount");
    }

    public void setAmount(Double amount) {
        this.item.setVariable("amount", amount);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate, double amount) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, name, first, second, startDate);
        toItem.addVariable("amount", amount);

        return toItem;
    }
}
