package com.osallek.eu4parser.model.save.country;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.eu4parser.common.Modifier;
import com.osallek.eu4parser.common.ModifiersUtils;
import com.osallek.eu4parser.common.NumbersUtils;
import com.osallek.eu4parser.model.game.Estate;
import com.osallek.eu4parser.model.game.EstateLoyaltyLevel;
import com.osallek.eu4parser.model.game.EstateModifier;
import com.osallek.eu4parser.model.game.EstatePrivilege;
import com.osallek.eu4parser.model.game.EstatePrivilegeModifier;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.game.ModifierDefinition;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SaveEstate {

    private final Game game;

    private final ClausewitzItem item;

    private final Country country;

    private List<EstateInteraction> grantedPrivileges;

    private List<SaveEstateModifier> influenceModifiers;

    private List<SaveEstateModifier> loyaltyModifiers;

    private final Estate estateGame;

    public SaveEstate(ClausewitzItem item, Country country) {
        this.country = country;
        this.game = this.country.getSave().getGame();
        this.item = item;
        this.estateGame = this.game.getEstate(ClausewitzUtils.removeQuotes(getType()));
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

    public EstateLoyaltyLevel getLoyaltyLevel() {
        if (NumbersUtils.doubleOrDefault(getLoyalty()) < this.game.getEstateAngryThreshold()) {
            return EstateLoyaltyLevel.ANGRY;
        } else if (NumbersUtils.doubleOrDefault(getLoyalty()) >= this.game.getEstateHappyThreshold()) {
            return EstateLoyaltyLevel.HAPPY;
        } else {
            return EstateLoyaltyLevel.NEUTRAL;
        }
    }

    public int getInfluenceLevel() {
        if (NumbersUtils.doubleOrDefault(getInfluence()) < this.game.getEstateInfluenceLevel1()) {
            return 1;
        } else if (NumbersUtils.doubleOrDefault(getInfluence()) < this.game.getEstateInfluenceLevel2()) {
            return 2;
        } else if (NumbersUtils.doubleOrDefault(getInfluence()) < this.game.getEstateInfluenceLevel3()) {
            return 3;
        } else {
            return 4;
        }
    }

    public Double getModifiers(Modifier modifier) {
        List<Double> modifiers = new ArrayList<>();

        if (!modifier.getName().equalsIgnoreCase(getInfluenceModifierName())) {
            switch (getLoyaltyLevel()) {
                case ANGRY:
                    if (this.estateGame.getCountryModifierAngry().hasModifier(modifier)) {
                        modifiers.add(this.estateGame.getCountryModifierAngry().getModifier(modifier) * getInfluenceLevel() / 4);
                    }
                    break;
                case NEUTRAL:
                    if (this.estateGame.getCountryModifierNeutral().hasModifier(modifier)) {
                        modifiers.add(this.estateGame.getCountryModifierNeutral().getModifier(modifier) * getInfluenceLevel() / 4);
                    }
                    break;
                case HAPPY:
                    if (this.estateGame.getCountryModifierHappy().hasModifier(modifier)) {
                        modifiers.add(this.estateGame.getCountryModifierHappy().getModifier(modifier) * getInfluenceLevel() / 4);
                    }
                    break;
            }
        }

        if (this.estateGame.getLandOwnershipModifier().hasModifier(modifier)) {
            modifiers.add(this.estateGame.getLandOwnershipModifier().getModifier(modifier) * NumbersUtils.doubleOrDefault(getTerritory()) / 100);
        }

        if (CollectionUtils.isNotEmpty(getGrantedPrivileges())) {
            getGrantedPrivileges().stream()
                                  .map(EstateInteraction::getPrivilege)
                                  .forEach(privilege -> {
                                      if (privilege.getModifiers().hasModifier(modifier)) {
                                          modifiers.add(privilege.getModifiers().getModifier(modifier));
                                      }

                                      if (privilege.getModifierByLandOwnership().hasModifier(modifier)) {
                                          modifiers.add(
                                                  privilege.getModifierByLandOwnership().getModifier(modifier) * NumbersUtils.doubleOrDefault(getTerritory())
                                                  / 100);
                                      }
                                      modifiers.addAll(privilege.getConditionalModifiers()
                                                                .stream()
                                                                .filter(m -> m.getTrigger().apply(this.country, this.country))
                                                                .map(EstatePrivilegeModifier::getModifiers)
                                                                .filter(m -> m.hasModifier(modifier))
                                                                .map(m -> m.getModifier(modifier))
                                                                .collect(Collectors.toList()));
                                  });
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }

    public Double getInfluence() {
        return NumbersUtils.doubleOrDefault(getEstateGame().getBaseInfluence())
               + getInfluenceModifiers().stream().mapToDouble(SaveEstateModifier::getValue).sum()
               + (getGrantedPrivileges() == null ? 0
                                                 : getGrantedPrivileges().stream()
                                                                         .map(EstateInteraction::getPrivilege)
                                                                         .mapToDouble(EstatePrivilege::getInfluence)
                                                                         .sum())
               + getInfluenceFromTerritory()
               + this.estateGame.getInfluenceModifiers()
                                .values()
                                .stream()
                                .filter(estateModifier -> estateModifier.getTrigger().apply(this.country, this.country))
                                .mapToDouble(EstateModifier::getAmount)
                                .sum()
               + NumbersUtils.doubleOrDefault(this.country.getModifier(ModifiersUtils.getModifier(getInfluenceModifierName()))) * 100;
    }

    public Double getInfluenceFromTerritory() {
        return Math.min(this.game.getEstateMaxInfluenceFromDev(),
                        getTerritory() * this.estateGame.getInfluenceFromDevModifier() * this.game.getEstateInfluencePerDev());
    }

    public String getInfluenceModifierName() {
        return this.estateGame.getModifierDefinitions()
                              .stream()
                              .filter(modifierDefinition -> "influence".equalsIgnoreCase(modifierDefinition.getType())
                                                            && modifierDefinition.getTrigger().apply(this.country, this.country))
                              .findFirst()
                              .map(ModifierDefinition::getKey)
                              .orElse(getType());
    }

    public Double getTerritory() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("territory"));
    }

    public List<EstateInteraction> getGrantedPrivileges() {
        return grantedPrivileges;
    }

    public void addGrantedPrivilege(String name, LocalDate date) {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem == null) {
            grantedPrivilegesItem = this.item.addChild("granted_privileges");
        }

        EstateInteraction.addToItem(grantedPrivilegesItem, name, date);
        refreshAttributes();
    }

    public void addInteraction(String name, LocalDate date) {
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

    public List<SaveEstateModifier> getInfluenceModifiers() {
        return influenceModifiers;
    }

    public void addInfluenceModifier(Double value, String desc, LocalDate date) {
        if (this.item != null) {
            SaveEstateModifier.addToItem(this.item, "influence_modifier", value, desc, date);
            refreshAttributes();
        }
    }

    public void removeInfluenceModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("influence_modifier", index);
            refreshAttributes();
        }
    }

    public List<SaveEstateModifier> getLoyaltyModifiers() {
        return loyaltyModifiers;
    }

    public void addLoyaltyModifier(Double value, String desc, LocalDate date) {
        if (this.item != null) {
            SaveEstateModifier.addToItem(this.item, "loyalty_modifier", value, desc, date);
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
        this.influenceModifiers = modifierItems.stream().map(SaveEstateModifier::new).collect(Collectors.toList());

        modifierItems = this.item.getChildren("loyalty_modifier");
        this.loyaltyModifiers = modifierItems.stream().map(SaveEstateModifier::new).collect(Collectors.toList());

        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem != null) {
            this.grantedPrivileges = grantedPrivilegesItem.getLists().stream()
                                                          .map(list -> new EstateInteraction(game, list))
                                                          .collect(Collectors.toList());
        }
    }
}
