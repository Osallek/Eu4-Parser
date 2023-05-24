package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Objects;

public class CustomizableLocalizationText {

    private final ClausewitzItem item;

    public CustomizableLocalizationText(ClausewitzItem item) {
        this.item = item;
    }

    public String getLocalisationKey() {
        return this.item.getVarAsString("localisation_key");
    }

    public void setLocalisationKey(String localisationKey) {
        this.item.setVariable("localisation_key", localisationKey);
    }

    public ConditionAnd getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new ConditionAnd(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof CustomizableLocalizationText text)) {
            return false;
        }

        return Objects.equals(getLocalisationKey(), text.getLocalisationKey()) && Objects.equals(getTrigger(), text.getTrigger());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLocalisationKey(), getTrigger());
    }

    @Override
    public String toString() {
        return getLocalisationKey();
    }
}
