package fr.osallek.eu4parser.model.game;

import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.save.country.Heir;
import fr.osallek.eu4parser.model.save.country.Monarch;
import fr.osallek.eu4parser.model.save.country.Queen;

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

    List<Culture> getAddAcceptedCultures();

    List<Culture> getRemoveAcceptedCultures();

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

    Heir getHeir();

    Monarch getMonarch();

    Queen Queen();
}
