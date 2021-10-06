package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

public class ImperialReformEffect {

    private final ClausewitzItem item;

    public ImperialReformEffect(ClausewitzItem item) {
        this.item = item;
    }

    public Boolean isImperialBanAllowed() {
        return this.item.getVarAsBool("imperial_ban_allowed");
    }

    public void setImperialBanAllowed(Boolean imperialBanAllowed) {
        if (imperialBanAllowed == null) {
            this.item.removeVariable("imperial_ban_allowed");
        } else {
            this.item.setVariable("imperial_ban_allowed", imperialBanAllowed);
        }
    }

    public Boolean isInternalHreCb() {
        return this.item.getVarAsBool("internal_hre_cb");
    }

    public void setInternalHreCb(Boolean internalHreCb) {
        if (internalHreCb == null) {
            this.item.removeVariable("internal_hre_cb");
        } else {
            this.item.setVariable("internal_hre_cb", internalHreCb);
        }
    }

    public Boolean enableImperialRealmWar() {
        return this.item.getVarAsBool("enable_imperial_realm_war");
    }

    public void setEnableImperialRealmWar(Boolean enableImperialRealmWar) {
        if (enableImperialRealmWar == null) {
            this.item.removeVariable("enable_imperial_realm_war");
        } else {
            this.item.setVariable("enable_imperial_realm_war", enableImperialRealmWar);
        }
    }

    public Boolean hreInheritable() {
        return this.item.getVarAsBool("hre_inheritable");
    }

    public void setHreInheritable(Boolean hreInheritable) {
        if (hreInheritable == null) {
            this.item.removeVariable("hre_inheritable");
        } else {
            this.item.setVariable("hre_inheritable", hreInheritable);
        }
    }
}
