package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CountryHistoryItems implements CountryHistoryItemI {

    private final List<CountryHistoryItemI> items;

    public CountryHistoryItems(List<CountryHistoryItemI> items) {
        this.items = items;
    }

    @Override
    public TechGroup getTechnologyGroup() {
        return this.items.stream().map(CountryHistoryItemI::getTechnologyGroup).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public String getUnitType() {
        return this.items.stream().map(CountryHistoryItemI::getUnitType).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getMercantilism() {
        return this.items.stream().map(CountryHistoryItemI::getMercantilism).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Province getCapital() {
        return this.items.stream().map(CountryHistoryItemI::getCapital).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Country getChangedTagFrom() {
        return this.items.stream().map(CountryHistoryItemI::getChangedTagFrom).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Province getFixedCapital() {
        return this.items.stream().map(CountryHistoryItemI::getFixedCapital).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Government getGovernment() {
        return this.items.stream().map(CountryHistoryItemI::getGovernment).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public String getReligiousSchool() {
        return this.items.stream().map(CountryHistoryItemI::getReligiousSchool).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Power getNationalFocus() {
        return this.items.stream().map(CountryHistoryItemI::getNationalFocus).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getGovernmentLevel() {
        return this.items.stream().map(CountryHistoryItemI::getGovernmentLevel).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public GovernmentRank getGovernmentRank() {
        return this.items.stream().map(CountryHistoryItemI::getGovernmentRank).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Culture getPrimaryCulture() {
        return this.items.stream().map(CountryHistoryItemI::getPrimaryCulture).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Religion getReligion() {
        return this.items.stream().map(CountryHistoryItemI::getReligion).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Religion getJoinLeague() {
        return this.items.stream().map(CountryHistoryItemI::getJoinLeague).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Double getAddArmyProfessionalism() {
        return this.items.stream().map(CountryHistoryItemI::getAddArmyProfessionalism).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public List<Culture> getAddAcceptedCultures() {
        return this.items.stream().map(CountryHistoryItemI::getAddAcceptedCultures).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Culture> getRemoveAcceptedCultures() {
        return this.items.stream()
                         .map(CountryHistoryItemI::getRemoveAcceptedCultures)
                         .filter(CollectionUtils::isNotEmpty)
                         .findFirst()
                         .orElse(new ArrayList<>());
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
    public Boolean getElector() {
        return this.items.stream().map(CountryHistoryItemI::getElector).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Boolean getRevolutionTarget() {
        return this.items.stream().map(CountryHistoryItemI::getRevolutionTarget).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Boolean getClearScriptedPersonalities() {
        return this.items.stream().map(CountryHistoryItemI::getClearScriptedPersonalities).filter(Objects::nonNull).findFirst().orElse(null);
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
    public Heir getHeir() {
        return this.items.stream().map(CountryHistoryItemI::getHeir).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Monarch getMonarch() {
        return this.items.stream().map(CountryHistoryItemI::getMonarch).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Queen getQueen() {
        return this.items.stream().map(CountryHistoryItemI::getQueen).filter(Objects::nonNull).findFirst().orElse(null);
    }
}
