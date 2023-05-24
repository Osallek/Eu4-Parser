package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Power;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class CountryHistoryItems implements CountryHistoryItemI {

    private final List<CountryHistoryItemI> items;

    public CountryHistoryItems(List<CountryHistoryItemI> items) {
        this.items = items;
    }

    @Override
    public Optional<TechGroup> getTechnologyGroup() {
        return this.items.stream().map(CountryHistoryItemI::getTechnologyGroup).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<String> getUnitType() {
        return this.items.stream().map(CountryHistoryItemI::getUnitType).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getMercantilism() {
        return this.items.stream().map(CountryHistoryItemI::getMercantilism).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Province> getCapital() {
        return this.items.stream().map(CountryHistoryItemI::getCapital).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Country> getChangedTagFrom() {
        return this.items.stream().map(CountryHistoryItemI::getChangedTagFrom).flatMap(Optional::stream).findFirst();
    }

    public List<Country> getChangedTagsFrom() {
        return this.items.stream().map(CountryHistoryItemI::getChangedTagFrom).flatMap(Optional::stream).toList();
    }

    @Override
    public Optional<Province> getFixedCapital() {
        return this.items.stream().map(CountryHistoryItemI::getFixedCapital).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Government> getGovernment() {
        return this.items.stream().map(CountryHistoryItemI::getGovernment).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<String> getReligiousSchool() {
        return this.items.stream().map(CountryHistoryItemI::getReligiousSchool).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Power> getNationalFocus() {
        return this.items.stream().map(CountryHistoryItemI::getNationalFocus).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getGovernmentLevel() {
        return this.items.stream().map(CountryHistoryItemI::getGovernmentLevel).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<GovernmentRank> getGovernmentRank() {
        return this.items.stream().map(CountryHistoryItemI::getGovernmentRank).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Culture> getPrimaryCulture() {
        return this.items.stream().map(CountryHistoryItemI::getPrimaryCulture).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Religion> getReligion() {
        return this.items.stream().map(CountryHistoryItemI::getReligion).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Religion> getJoinLeague() {
        return this.items.stream().map(CountryHistoryItemI::getJoinLeague).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Double> getAddArmyProfessionalism() {
        return this.items.stream().map(CountryHistoryItemI::getAddArmyProfessionalism).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<String> getAddAcceptedCultures() {
        return this.items.stream().map(CountryHistoryItemI::getAddAcceptedCultures).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<String> getRemoveAcceptedCultures() {
        return this.items.stream()
                         .map(CountryHistoryItemI::getRemoveAcceptedCultures)
                         .filter(CollectionUtils::isNotEmpty)
                         .findFirst()
                         .orElse(new ArrayList<>());
    }

    @Override
    public List<String> getCumulatedAcceptedCultures() {
        return this.items.stream().map(CountryHistoryItemI::getCumulatedAcceptedCultures).flatMap(Collection::stream).distinct().toList();
    }

    @Override
    public List<Country> getHistoricalFriends() {
        return this.items.stream().map(CountryHistoryItemI::getHistoricalFriends).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Country> getHistoricalEnemies() {
        return this.items.stream().map(CountryHistoryItemI::getHistoricalEnemies).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Optional<Boolean> getElector() {
        return this.items.stream().map(CountryHistoryItemI::getElector).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Boolean> getRevolutionTarget() {
        return this.items.stream().map(CountryHistoryItemI::getRevolutionTarget).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Boolean> getClearScriptedPersonalities() {
        return this.items.stream().map(CountryHistoryItemI::getClearScriptedPersonalities).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<ChangeEstateLandShare> getChangeEstateLandShares() {
        return this.items.stream()
                         .map(CountryHistoryItemI::getChangeEstateLandShares)
                         .filter(CollectionUtils::isNotEmpty)
                         .findFirst()
                         .orElse(new ArrayList<>());
    }

    @Override
    public List<RulerPersonality> getAddHeirPersonalities() {
        return this.items.stream().map(CountryHistoryItemI::getAddHeirPersonalities).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<RulerPersonality> getAddRulerPersonalities() {
        return this.items.stream().map(CountryHistoryItemI::getAddRulerPersonalities).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<RulerPersonality> getAddQueenPersonalities() {
        return this.items.stream().map(CountryHistoryItemI::getAddQueenPersonalities).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<EstatePrivilege> getSetEstatePrivilege() {
        return this.items.stream().map(CountryHistoryItemI::getSetEstatePrivilege).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<GovernmentReform> getAddGovernmentReform() {
        return this.items.stream().map(CountryHistoryItemI::getAddGovernmentReform).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<String> getSetCountryFlag() {
        return this.items.stream().map(CountryHistoryItemI::getSetCountryFlag).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<String> getClearCountryFlag() {
        return this.items.stream().map(CountryHistoryItemI::getClearCountryFlag).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<String> getCumulatedCountryFlags() {
        return this.items.stream().map(CountryHistoryItemI::getCumulatedCountryFlags).flatMap(Collection::stream).distinct().toList();
    }

    @Override
    public Optional<Heir> getHeir() {
        return this.items.stream().map(CountryHistoryItemI::getHeir).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Monarch> getMonarch() {
        return this.items.stream().map(CountryHistoryItemI::getMonarch).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Queen> getQueen() {
        return this.items.stream().map(CountryHistoryItemI::getQueen).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<Leader> getLeaders() {
        return this.items.stream().map(CountryHistoryItemI::getLeaders).flatMap(Collection::stream).distinct().toList();
    }
}
