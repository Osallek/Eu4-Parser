package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Power;

import java.util.List;
import java.util.Optional;

public interface CountryHistoryItemI {

    Optional<TechGroup> getTechnologyGroup();

    Optional<String> getUnitType();

    Optional<Integer> getMercantilism();

    Optional<Province> getCapital();

    Optional<Country> getChangedTagFrom();

    Optional<Province> getFixedCapital();

    Optional<Government> getGovernment();

    Optional<String> getReligiousSchool();

    Optional<Power> getNationalFocus();

    Optional<Integer> getGovernmentLevel();

    Optional<GovernmentRank> getGovernmentRank();

    Optional<Culture> getPrimaryCulture();

    Optional<Religion> getReligion();

    Optional<Religion> getJoinLeague();

    Optional<Double> getAddArmyProfessionalism();

    List<String> getAddAcceptedCultures();

    List<String> getRemoveAcceptedCultures();

    List<String> getCumulatedAcceptedCultures();

    List<Country> getHistoricalFriends();

    List<Country> getHistoricalEnemies();

    Optional<Boolean> getElector();

    Optional<Boolean> getRevolutionTarget();

    Optional<Boolean> getClearScriptedPersonalities();

    List<ChangeEstateLandShare> getChangeEstateLandShares();

    List<RulerPersonality> getAddHeirPersonalities();

    List<RulerPersonality> getAddRulerPersonalities();

    List<RulerPersonality> getAddQueenPersonalities();

    List<EstatePrivilege> getSetEstatePrivilege();

    List<GovernmentReform> getAddGovernmentReform();

    List<String> getSetCountryFlag();

    List<String> getClearCountryFlag();

    List<String> getCumulatedCountryFlags();

    Optional<Heir> getHeir();

    Optional<Monarch> getMonarch();

    Optional<Queen> getQueen();

    List<Leader> getLeaders();
}
