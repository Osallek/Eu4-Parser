package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.model.game.Estate;
import fr.osallek.eu4parser.model.game.EstateLoyaltyLevel;
import fr.osallek.eu4parser.model.game.EstateModifier;
import fr.osallek.eu4parser.model.game.EstatePrivilege;
import fr.osallek.eu4parser.model.game.EstatePrivilegeModifier;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Modifier;
import fr.osallek.eu4parser.model.game.ModifierDefinition;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import org.apache.commons.collections4.CollectionUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SaveEstate {

    private final Game game;

    private final ClausewitzItem item;

    private final SaveCountry country;

    private final Estate estateGame;

    public SaveEstate(ClausewitzItem item, SaveCountry country) {
        this.country = country;
        this.game = this.country.getSave().getGame();
        this.item = item;
        this.estateGame = this.game.getEstate(ClausewitzUtils.removeQuotes(getType()));
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public double getLoyalty() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("loyalty"));
    }

    public void setLoyalty(Double loyalty) {
        this.item.setVariable("loyalty", loyalty);
    }

    public EstateLoyaltyLevel getLoyaltyLevel() {
        if (getLoyalty() < this.game.getEstateAngryThreshold()) {
            return EstateLoyaltyLevel.ANGRY;
        } else if (getLoyalty() >= this.game.getEstateHappyThreshold()) {
            return EstateLoyaltyLevel.HAPPY;
        } else {
            return EstateLoyaltyLevel.NEUTRAL;
        }
    }

    public int getInfluenceLevel() {
        if (getInfluence() < this.game.getEstateInfluenceLevel1()) {
            return 1;
        } else if (getInfluence() < this.game.getEstateInfluenceLevel2()) {
            return 2;
        } else if (getInfluence() < this.game.getEstateInfluenceLevel3()) {
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
            modifiers.add(this.estateGame.getLandOwnershipModifier().getModifier(modifier) * getTerritory() / 100);
        }

        if (CollectionUtils.isNotEmpty(getGrantedPrivileges())) {
            getGrantedPrivileges().stream().map(EstateInteraction::getPrivilege).filter(Objects::nonNull).forEach(privilege -> {
                if (privilege.getModifiers().hasModifier(modifier)) {
                    modifiers.add(privilege.getModifiers().getModifier(modifier));
                }

                if (privilege.getModifierByLandOwnership().hasModifier(modifier)) {
                    modifiers.add(privilege.getModifierByLandOwnership().getModifier(modifier) * getTerritory() / 100);
                }
                modifiers.addAll(privilege.getConditionalModifiers()
                                          .stream()
                                          .filter(m -> m.getTrigger() != null && m.getTrigger().apply(this.country, this.country))
                                          .map(EstatePrivilegeModifier::getModifiers)
                                          .filter(m -> m.hasModifier(modifier))
                                          .map(m -> m.getModifier(modifier))
                                          .toList());
            });
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }

    public double getInfluence() {
        return NumbersUtils.doubleOrDefault(getEstateGame().getBaseInfluence()) +
               getInfluenceModifiers().stream()
                                      .mapToDouble(SaveEstateModifier::getValue)
                                      .filter(Objects::nonNull)
                                      .sum() +
               getGrantedPrivileges().stream()
                                     .map(EstateInteraction::getPrivilege)
                                     .filter(Objects::nonNull)
                                     .map(EstatePrivilege::getInfluence)
                                     .filter(Objects::nonNull)
                                     .mapToDouble(Double::doubleValue)
                                     .sum() +
               getInfluenceFromTerritory() +
               this.estateGame.getInfluenceModifiers()
                              .stream()
                              .filter(estateModifier -> estateModifier.getTrigger().apply(this.country, this.country))
                              .map(EstateModifier::getAmount)
                              .mapToDouble(Double::doubleValue)
                              .sum() +
               ((getInfluenceModifierName() == null || ModifiersUtils.getModifier(getInfluenceModifierName()) == null) ? 0 :
                NumbersUtils.doubleOrDefault(this.country.getModifier(ModifiersUtils.getModifier(getInfluenceModifierName()))) * 100);
    }

    public double getInfluenceFromTerritory() {
        return Math.min(this.game.getEstateMaxInfluenceFromDev(),
                        getTerritory() * NumbersUtils.doubleOrDefault(this.estateGame.getInfluenceFromDevModifier()) * this.game.getEstateInfluencePerDev());
    }

    public String getInfluenceModifierName() {
        return this.estateGame.getModifierDefinitions() == null ? null : this.estateGame.getModifierDefinitions()
                                                                                        .stream()
                                                                                        .filter(modifierDefinition -> "influence".equalsIgnoreCase(
                                                                                                modifierDefinition.getType()) &&
                                                                                                                      modifierDefinition.getTrigger()
                                                                                                                                        .apply(this.country,
                                                                                                                                               this.country))
                                                                                        .findFirst()
                                                                                        .map(ModifierDefinition::getKey)
                                                                                        .orElse(getType());
    }

    public double getTerritory() {
        return NumbersUtils.doubleOrDefault(this.item.getVarAsDouble("territory"));
    }

    public void setTerritory(Double territory) {
        this.item.setVariable("territory", territory);
    }

    public List<EstateInteraction> getGrantedPrivileges() {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem != null) {
            return grantedPrivilegesItem.getLists().stream().map(list -> new EstateInteraction(game, list)).toList();
        }

        return new ArrayList<>();
    }

    public void addGrantedPrivilege(EstatePrivilege privilege, LocalDate date) {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem == null) {
            grantedPrivilegesItem = this.item.addChild("granted_privileges");
        }

        EstateInteraction.addToItem(grantedPrivilegesItem, privilege.getName(), date);
    }

    public void removeGrantedPrivilege(EstatePrivilege privilege) {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges");

        if (grantedPrivilegesItem == null) {
            return;
        }

        Integer index = null;
        List<ClausewitzList> lists = grantedPrivilegesItem.getLists();
        for (int i = 0; i < lists.size(); i++) {
            if (privilege.getName().equals(lists.get(i).get(0))) {
                index = i;
                break;
            }
        }

        if (index != null) {
            grantedPrivilegesItem.removeList(index);
        }
    }

    public void addInteraction(String name, LocalDate date) {
        if (this.item != null) {
            EstateInteraction.addToItem(this.item, name, date);
        }
    }

    public void removeInteraction(Integer index) {
        if (this.item != null) {
            this.item.removeChild("interaction_use", index);
        }
    }

    public List<SaveEstateModifier> getInfluenceModifiers() {
        return this.item.getChildren("influence_modifier").stream().map(SaveEstateModifier::new).toList();
    }

    public void addInfluenceModifier(Double value, String desc, LocalDate date) {
        if (this.item != null) {
            SaveEstateModifier.addToItem(this.item, "influence_modifier", value, desc, date);
        }
    }

    public void removeInfluenceModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("influence_modifier", index);
        }
    }

    public List<SaveEstateModifier> getLoyaltyModifiers() {
        return this.item.getChildren("loyalty_modifier").stream().map(SaveEstateModifier::new).toList();
    }

    public void addLoyaltyModifier(Double value, String desc, LocalDate date) {
        if (this.item != null) {
            SaveEstateModifier.addToItem(this.item, "loyalty_modifier", value, desc, date);
        }
    }

    public void removeLoyaltyModifier(Integer index) {
        if (this.item != null) {
            this.item.removeChild("loyalty_modifier", index);
        }
    }

    public List<Integer> getActiveInfluences() {
        ClausewitzList list = this.item.getList("active_influences");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(Integer::parseInt).toList();
    }

    public List<Integer> getActiveLoyalties() {
        ClausewitzList list = this.item.getList("active_loyalties");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues().stream().map(Integer::parseInt).toList();
    }

    public Estate getEstateGame() {
        return estateGame;
    }
}
