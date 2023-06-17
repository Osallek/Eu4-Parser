package fr.osallek.eu4parser.model.save.country;

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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.apache.commons.collections4.CollectionUtils;

public class SaveEstate {

    private final Game game;

    private final ClausewitzItem item;

    private final SaveCountry country;

    private List<EstateInteraction> grantedPrivileges;

    private List<SaveEstateModifier> influenceModifiers;

    private List<SaveEstateModifier> loyaltyModifiers;

    private final Estate estateGame;

    public SaveEstate(ClausewitzItem item, SaveCountry country) {
        this.country = country;
        this.game = this.country.getSave().getGame();
        this.item = item;
        this.estateGame = getType().map(this.game::getEstate).orElse(null);
        refreshAttributes();
    }

    public Optional<String> getType() {
        return this.item.getVarAsString("type");
    }

    public double getLoyalty() {
        return this.item.getVarAsDouble("loyalty").orElse(0d);
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

        if (getInfluenceModifierName().filter(s -> !modifier.getName().equalsIgnoreCase(s)).isPresent()) {
            switch (getLoyaltyLevel()) {
                case ANGRY -> this.estateGame.getCountryModifierAngry().map(m -> m.getModifier(modifier)).ifPresent(m -> {
                    modifiers.add(m * getInfluenceLevel() / 4);
                });
                case NEUTRAL -> this.estateGame.getCountryModifierNeutral().map(m -> m.getModifier(modifier)).ifPresent(m -> {
                    modifiers.add(m * getInfluenceLevel() / 4);
                });
                case HAPPY -> this.estateGame.getCountryModifierHappy().map(m -> m.getModifier(modifier)).ifPresent(m -> {
                    modifiers.add(m * getInfluenceLevel() / 4);
                });
            }
        }

        this.estateGame.getLandOwnershipModifier().map(m -> m.getModifier(modifier)).ifPresent(m -> modifiers.add(m * getTerritory() / 100));

        if (CollectionUtils.isNotEmpty(getGrantedPrivileges())) {
            getGrantedPrivileges().stream()
                                  .map(EstateInteraction::getPrivilege)
                                  .filter(Objects::nonNull)
                                  .forEach(privilege -> {
                                      privilege.map(EstatePrivilege::getModifiers).map(m -> m.getModifier(modifier)).ifPresent(modifiers::add);
                                      privilege.flatMap(EstatePrivilege::getModifierByLandOwnership)
                                               .map(m -> m.getModifier(modifier))
                                               .ifPresent(m -> modifiers.add(m * getTerritory() / 100));

                                      modifiers.addAll(privilege.map(EstatePrivilege::getConditionalModifiers)
                                                                .stream()
                                                                .flatMap(Collection::stream)
                                                                .filter(m -> m.getTrigger().isEmpty() || m.getTrigger().get().apply(this.country, this.country))
                                                                .map(EstatePrivilegeModifier::getModifiers)
                                                                .filter(Optional::isPresent)
                                                                .map(Optional::get)
                                                                .map(m -> m.getModifier(modifier))
                                                                .toList());
                                  });
        }

        return ModifiersUtils.sumModifiers(modifier, modifiers);
    }

    public double getInfluence() {
        return getEstateGame().getBaseInfluence().orElse(0d)
               + getInfluenceModifiers().stream()
                                        .map(SaveEstateModifier::getValue)
                                        .filter(Optional::isPresent)
                                        .mapToDouble(Optional::get)
                                        .filter(Objects::nonNull)
                                        .sum()
               + getGrantedPrivileges().stream()
                                       .map(EstateInteraction::getPrivilege)
                                       .filter(Optional::isPresent)
                                       .map(Optional::get)
                                       .map(EstatePrivilege::getInfluence)
                                       .filter(Optional::isPresent)
                                       .mapToDouble(Optional::get)
                                       .sum()
               + getInfluenceFromTerritory()
               + this.estateGame.getInfluenceModifiers()
                                .stream()
                                .filter(estateModifier -> estateModifier.getTrigger().isEmpty() ||
                                                          estateModifier.getTrigger().get().apply(this.country, this.country))
                                .map(EstateModifier::getAmount)
                                .mapToDouble(Double::doubleValue)
                                .sum()
               + ((getInfluenceModifierName().isEmpty() || ModifiersUtils.getModifier(getInfluenceModifierName().get()) == null) ? 0 :
                  NumbersUtils.doubleOrDefault(this.country.getModifier(ModifiersUtils.getModifier(getInfluenceModifierName().get()))) * 100);
    }

    public double getInfluenceFromTerritory() {
        return Math.min(this.game.getEstateMaxInfluenceFromDev(),
                        getTerritory() * this.estateGame.getInfluenceFromDevModifier().orElse(0d) * this.game.getEstateInfluencePerDev());
    }

    public Optional<String> getInfluenceModifierName() {
        return this.estateGame.getModifierDefinitions() == null ? null :
               this.estateGame.getModifierDefinitions()
                              .stream()
                              .filter(modifierDefinition -> modifierDefinition.getType().filter("influence"::equalsIgnoreCase).isPresent()
                                                            && modifierDefinition.getTrigger().filter(c -> c.apply(this.country, this.country)).isPresent())
                              .findFirst()
                              .flatMap(ModifierDefinition::getKey)
                              .or(this::getType);
    }

    public double getTerritory() {
        return this.item.getVarAsDouble("territory").orElse(0d);
    }

    public void setTerritory(Double territory) {
        this.item.setVariable("territory", territory);
    }

    public List<EstateInteraction> getGrantedPrivileges() {
        return this.grantedPrivileges == null ? new ArrayList<>() : this.grantedPrivileges;
    }

    public void addGrantedPrivilege(EstatePrivilege privilege, LocalDate date) {
        ClausewitzItem grantedPrivilegesItem = this.item.getChild("granted_privileges").orElse(this.item.addChild("granted_privileges"));

        EstateInteraction.addToItem(grantedPrivilegesItem, privilege.getName(), date);
        refreshAttributes();
    }

    public void removeGrantedPrivilege(EstatePrivilege privilege) {
        this.item.getChild("granted_privileges").ifPresent(grantedPrivilegesItem -> {
            Integer index = null;
            List<ClausewitzList> lists = grantedPrivilegesItem.getLists();
            for (int i = 0; i < lists.size(); i++) {
                if (privilege.getName().equals(lists.get(i).get(0).get())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                grantedPrivilegesItem.removeList(index);
            }
        });
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
        return this.item.getList("active_influences").map(ClausewitzList::getValuesAsInt).orElse(new ArrayList<>());
    }

    public List<Integer> getActiveLoyalties() {
        return this.item.getList("active_loyalties").map(ClausewitzList::getValuesAsInt).orElse(new ArrayList<>());
    }

    public Estate getEstateGame() {
        return estateGame;
    }

    private void refreshAttributes() {
        this.influenceModifiers = this.item.getChildren("influence_modifier").stream().map(SaveEstateModifier::new).toList();
        this.loyaltyModifiers = this.item.getChildren("loyalty_modifier").stream().map(SaveEstateModifier::new).toList();
        this.grantedPrivileges = this.item.getChild("granted_privileges")
                                          .map(ClausewitzItem::getLists)
                                          .stream()
                                          .flatMap(Collection::stream)
                                          .map(list -> new EstateInteraction(game, list))
                                          .toList();
    }
}
