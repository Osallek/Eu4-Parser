package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;

public class ModifierSubject {

    private final String modifier;

    private final Condition trigger;

    private final String expirationMessageOverlord;

    private final String expirationMessageSubject;

    public ModifierSubject(ClausewitzItem item) {
        this.modifier = item.getVarAsString("modifier");
        this.trigger = item.getChild("trigger") == null ? null : new Condition(item.getChild("trigger"));
        this.expirationMessageOverlord = item.getVarAsString("expiration_message_overlord");
        this.expirationMessageSubject = item.getVarAsString("expiration_message_subject");
    }

    public String getModifier() {
        return modifier;
    }

    public Condition getTrigger() {
        return trigger;
    }

    public String getExpirationMessageOverlord() {
        return expirationMessageOverlord;
    }

    public String getExpirationMessageSubject() {
        return expirationMessageSubject;
    }
}
