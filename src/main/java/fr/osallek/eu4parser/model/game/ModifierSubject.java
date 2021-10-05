package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ModifierSubject {

    private final ClausewitzItem item;

    public ModifierSubject(ClausewitzItem item) {
        this.item = item;
    }
    
    public String getModifier() {
        return this.item.getVarAsString("modifier");
    }

    public void setModifier(String modifier) {
        this.item.setVariable("modifier", modifier);
    }

    public Condition getTrigger() {
        ClausewitzItem child = this.item.getChild("trigger");
        return child == null ? null : new Condition(child);
    }

    public String getExpirationMessageOverlord() {
        return this.item.getVarAsString("expiration_message_overlord");
    }

    public void setExpirationMessageOverlord(String expirationMessageOverlord) {
        this.item.setVariable("expiration_message_overlord", expirationMessageOverlord);
    }

    public String getExpirationMessageSubject() {
        return this.item.getVarAsString("expiration_message_subject");
    }

    public void setExpirationMessageSubject(String expirationMessageSubject) {
        this.item.setVariable("expiration_message_subject", expirationMessageSubject);
    }
}
