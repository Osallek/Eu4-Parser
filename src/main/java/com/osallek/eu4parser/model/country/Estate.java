package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Estate {

    private final ClausewitzItem item;

    private final Country country;

    private List<EstateInteraction> interactions;

    private List<EstateInfluenceModifier> influenceModifiers;

    public Estate(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        refreshAttributes();
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        ClausewitzVariable var = this.item.getVar("type");

        if (var != null) {
            var.setValue(type);
        } else {
            this.item.addVariable("type", type);
        }
    }

    public Double getLoyalty() {
        return this.item.getVarAsDouble("loyalty");
    }

    public void setLoyalty(Double loyalty) {
        ClausewitzVariable var = this.item.getVar("loyalty");

        if (var != null) {
            var.setValue(loyalty);
        } else {
            this.item.addVariable("loyalty", loyalty);
        }
    }

    public List<Integer> getProvinces() {
        ClausewitzList list = this.item.getList("provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(Integer::parseInt).collect(Collectors.toList());
    }

    public void addProvince(Integer provinceId) {
        if (this.country.getSave().getProvince(provinceId).getOwner().equalsIgnoreCase(this.country.getTag())) {
            ClausewitzList list = this.item.getList("provinces");

            if (list != null) {
                list.add(provinceId);
            } else {
                this.item.addList("provinces", provinceId);
            }
        }
    }

    public void removeProvince(Integer provinceId) {
        ClausewitzList list = this.item.getList("provinces");

        if (list != null) {
            list.remove(String.valueOf(provinceId));
        }
    }

    public List<EstateInteraction> getInteractions() {
        return interactions;
    }

    public void addInteraction(Integer interaction, Date date) {
        if (this.item != null) {
            EstateInteraction.addToItem(this.item, interaction, date);
            refreshAttributes();
        }
    }

    public void removeInteraction(Integer index) {
        if (this.item != null) {
            this.item.removeChild("interaction_use", index);
            refreshAttributes();
        }
    }

    public List<EstateInfluenceModifier> getInfluenceModifiers() {
        return influenceModifiers;
    }

    public void addInfluenceModifier(Double value, String desc, Date date) {
        if (this.item != null) {
            EstateInfluenceModifier.addToItem(this.item, value, desc, date);
            refreshAttributes();
        }
    }

    public void removeInfluenceModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("influence_modifier", index);
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


    private void refreshAttributes() {
        List<ClausewitzItem> interactionUse = this.item.getChildren("interaction_use");
        this.interactions = interactionUse.stream()
                                          .map(EstateInteraction::new)
                                          .collect(Collectors.toList());
        List<ClausewitzItem> influenceModifierItems = this.item.getChildren("influence_modifier");
        this.influenceModifiers = influenceModifierItems.stream()
                                                        .map(EstateInfluenceModifier::new)
                                                        .collect(Collectors.toList());
    }
}
