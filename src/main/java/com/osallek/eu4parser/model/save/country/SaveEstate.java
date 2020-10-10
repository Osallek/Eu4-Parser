package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.model.game.Estate;
import com.osallek.eu4parser.model.game.Game;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class SaveEstate {

    private final ClausewitzItem item;

    private List<EstateInteraction> grantedPrivileges;

    private List<EstateModifier> influenceModifiers;

    private List<EstateModifier> loyaltyModifiers;

    private final Estate estateGame;

    public SaveEstate(ClausewitzItem item, Game game) {
        this.item = item;
        this.estateGame = game.getEstate(ClausewitzUtils.removeQuotes(getType()));
        refreshAttributes();
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public Double getLoyalty() {
        return this.item.getVarAsDouble("loyalty");
    }

    public void setLoyalty(Double loyalty) {
        this.item.setVariable("loyalty", loyalty);
    }

    public Double getTerritory() {
        return this.item.getVarAsDouble("territory");
    }

    public List<EstateInteraction> getGrantedPrivileges() {
        return grantedPrivileges;
    }

    public void addGrantedPrivilege(String name, Date date) {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem == null) {
            grantedPrivilegesItem = this.item.addChild("granted_privileges");
        }

        EstateInteraction.addToItem(grantedPrivilegesItem, name, date);
        refreshAttributes();
    }

    public void addInteraction(String name, Date date) {
        if (this.item != null) {
            EstateInteraction.addToItem(this.item, name, date);
            refreshAttributes();
        }
    }

    public void removeInteraction(Integer index) {
        if (this.item != null) {
            this.item.removeChild("interaction_use", index);
            refreshAttributes();
        }
    }

    public List<EstateModifier> getInfluenceModifiers() {
        return influenceModifiers;
    }

    public void addInfluenceModifier(Double value, String desc, Date date) {
        if (this.item != null) {
            EstateModifier.addToItem(this.item, "influence_modifier", value, desc, date);
            refreshAttributes();
        }
    }

    public void removeInfluenceModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("influence_modifier", index);
            refreshAttributes();
        }
    }

    public List<EstateModifier> getLoyaltyModifiers() {
        return loyaltyModifiers;
    }

    public void addLoyaltyModifier(Double value, String desc, Date date) {
        if (this.item != null) {
            EstateModifier.addToItem(this.item, "loyalty_modifier", value, desc, date);
            refreshAttributes();
        }
    }

    public void removeLoyaltyModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("loyalty_modifier", index);
            refreshAttributes();
        }
    }

    public List<Integer> getActiveInfluences() {
        ClausewitzList list = this.item.getList("active_influences");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public List<Integer> getActiveLoyalties() {
        ClausewitzList list = this.item.getList("active_loyalties");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public Estate getEstateGame() {
        return estateGame;
    }

    private void refreshAttributes() {
        List<ClausewitzItem> modifierItems = this.item.getChildren("influence_modifier");
        this.influenceModifiers = modifierItems.stream().map(EstateModifier::new).collect(Collectors.toList());

        modifierItems = this.item.getChildren("loyalty_modifier");
        this.loyaltyModifiers = modifierItems.stream().map(EstateModifier::new).collect(Collectors.toList());

        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem != null) {
            this.grantedPrivileges = grantedPrivilegesItem.getLists().stream()
                                                          .map(EstateInteraction::new)
                                                          .collect(Collectors.toList());
        }
    }
}
