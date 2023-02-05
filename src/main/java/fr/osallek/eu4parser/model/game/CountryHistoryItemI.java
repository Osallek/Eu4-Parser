package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Power;

import java.util.List;

public interface CountryHistoryItemI {

    TechGroup getTechnologyGroup();

    String getUnitType();

    Integer getMercantilism();

    Province getCapital();

    Country getChangedTagFrom();

    Province getFixedCapital();

    Government getGovernment();

    String getReligiousSchool();

    Power getNationalFocus();

    Integer getGovernmentLevel();

    GovernmentRank getGovernmentRank();

    Culture getPrimaryCulture();

    Religion getReligion();

    Religion getJoinLeague();

    Double getAddArmyProfessionalism();

    List<String> getAddAcceptedCultures();

    List<String> getRemoveAcceptedCultures();

    List<String> getCumulatedAcceptedCultures();

    List<Country> getHistoricalFriends();

    List<Country> getHistoricalEnemies();

    Boolean getElector();

    Boolean getRevolutionTarget();

    Boolean getClearScriptedPersonalities();

    List<ChangeEstateLandShare> getChangeEstateLandShares();

    List<RulerPersonality> getAddHeirPersonalities();

    List<RulerPersonality> getAddRulerPersonalities();

    List<RulerPersonality> getAddQueenPersonalities();

    List<EstatePrivilege> getSetEstatePrivilege();

    List<GovernmentReform> getAddGovernmentReform();

    List<String> getSetCountryFlag();

    List<String> getClearCountryFlag();

    List<String> getCumulatedCountryFlags();

    Heir getHeir();

    Monarch getMonarch();

    Queen getQueen();

    List<Leader> getLeaders();
}
