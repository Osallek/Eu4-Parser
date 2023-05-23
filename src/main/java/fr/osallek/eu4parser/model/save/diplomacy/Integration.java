package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;

import java.time.LocalDate;

public class Integration extends DatableRelation {

    public Integration(ClausewitzItem item, Save save) {
        super(item, save);
    }

    public Double getProgress() {
        return this.item.getVarAsDouble("progress");
    }

    public void setProgress(Double progress) {
        this.item.setVariable("progress", progress);
    }

    public Integer getEnvoy() {
        return this.item.getVarAsInt("envoy");
    }

    public void setEnvoy(Integer envoy) {
        this.item.setVariable("envoy", envoy);
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, String name, String first, String second, LocalDate startDate, double progress) {
        ClausewitzItem toItem = DatableRelation.addToItem(parent, name, first, second, startDate);
        toItem.addVariable("progress", progress);

        return toItem;
    }
}
