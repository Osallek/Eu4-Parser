package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.save.country.Losses;
import org.apache.commons.lang3.BooleanUtils;

import java.util.EnumMap;
import java.util.Map;

public class WarParticipant {

    private final ClausewitzItem item;

    public WarParticipant(ClausewitzItem item) {
        this.item = item;
    }

    public Double getValue() {
        return this.item.getVarAsDouble("value");
    }

    public void setValue(Double value) {
        this.item.setVariable("value", value);
    }

    public String getTag() {
        return this.item.getVarAsString("tag");
    }

    public boolean getPromisedLand() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("promised_land"));
    }

    public void setPromisedLand(boolean promisedLand) {
        this.item.setVariable("promised_land", promisedLand);
    }

    public Map<Losses, Integer> getLosses() {
        Map<Losses, Integer> lossesMap = new EnumMap<>(Losses.class);
        ClausewitzItem lossesItem = this.item.getChild("losses");

        if (lossesItem != null) {
            ClausewitzList list = lossesItem.getList("members");

            if (list == null) {
                return lossesMap;
            }

            for (Losses losses : Losses.values()) {
                lossesMap.put(losses, list.getAsInt(losses.ordinal()));
            }
        }

        return lossesMap;
    }
}
