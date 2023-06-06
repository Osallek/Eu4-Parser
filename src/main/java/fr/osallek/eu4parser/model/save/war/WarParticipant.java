package fr.osallek.eu4parser.model.save.war;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.country.Losses;
import org.apache.commons.lang3.BooleanUtils;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public record WarParticipant(ClausewitzItem item) {

    public Optional<Double> getValue() {
        return this.item.getVarAsDouble("value");
    }

    public void setValue(Double value) {
        this.item.setVariable("value", value);
    }

    public Optional<String> getTag() {
        return this.item.getVarAsString("tag").map(ClausewitzUtils::removeQuotes);
    }

    public boolean getPromisedLand() {
        return this.item.getVarAsBool("promised_land").map(BooleanUtils::toBoolean).orElse(false);
    }

    public void setPromisedLand(boolean promisedLand) {
        this.item.setVariable("promised_land", promisedLand);
    }

    public Map<Losses, Integer> getLosses() {
        return this.item.getChild("losses").flatMap(i -> i.getList("members")).map(list -> {
            Map<Losses, Integer> lossesMap = new EnumMap<>(Losses.class);

            for (Losses losses : Losses.values()) {
                list.getAsInt(losses.ordinal()).ifPresent(integer -> lossesMap.put(losses, integer));
            }

            return lossesMap;
        }).orElse(new EnumMap<>(Losses.class));
    }
}
