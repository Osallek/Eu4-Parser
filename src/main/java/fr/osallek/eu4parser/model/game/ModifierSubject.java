package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;

import java.util.Optional;

public class ModifierSubject {

    private final ClausewitzItem item;

    public ModifierSubject(ClausewitzItem item) {
        this.item = item;
    }
    
    public Optional<String> getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public void setModifier(String modifier) {
        this.item.setVariable("modifier", modifier);
    }

    public Optional<ConditionAnd> getTrigger() {
        return this.item.getChild("trigger").map(ConditionAnd::new);
    }

    public Optional<String> getExpirationMessageOverlord() {
        return this.item.getVarAsString("expiration_message_overlord");
    }

    public void setExpirationMessageOverlord(String expirationMessageOverlord) {
        this.item.setVariable("expiration_message_overlord", expirationMessageOverlord);
    }

    public Optional<String> getExpirationMessageSubject() {
        return this.item.getVarAsString("expiration_message_subject");
    }

    public void setExpirationMessageSubject(String expirationMessageSubject) {
        this.item.setVariable("expiration_message_subject", expirationMessageSubject);
    }
}
