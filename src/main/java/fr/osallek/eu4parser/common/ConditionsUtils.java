package fr.osallek.eu4parser.common;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.UnitType;
import fr.osallek.eu4parser.model.game.Age;
import fr.osallek.eu4parser.model.game.Building;
import fr.osallek.eu4parser.model.game.Country;
import fr.osallek.eu4parser.model.game.CountryHistoryItemI;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.CultureGroup;
import fr.osallek.eu4parser.model.game.Government;
import fr.osallek.eu4parser.model.game.Institution;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.game.Policy;
import fr.osallek.eu4parser.model.game.Province;
import fr.osallek.eu4parser.model.game.ProvinceHistoryItemI;
import fr.osallek.eu4parser.model.game.ProvinceList;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.SubjectType;
import fr.osallek.eu4parser.model.game.TradeGood;
import fr.osallek.eu4parser.model.game.condition.ConditionAbstract;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.TradeLeague;
import fr.osallek.eu4parser.model.save.country.ActivePolicy;
import fr.osallek.eu4parser.model.save.country.Income;
import fr.osallek.eu4parser.model.save.country.Leader;
import fr.osallek.eu4parser.model.save.country.LeaderType;
import fr.osallek.eu4parser.model.save.country.Queen;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.country.SaveEstate;
import fr.osallek.eu4parser.model.save.country.SaveEstateModifier;
import fr.osallek.eu4parser.model.save.country.SaveFaction;
import fr.osallek.eu4parser.model.save.country.SaveModifier;
import fr.osallek.eu4parser.model.save.diplomacy.QuantifyDatableRelation;
import fr.osallek.eu4parser.model.save.empire.HreReligionStatus;
import fr.osallek.eu4parser.model.save.gameplayoptions.NationSetup;
import fr.osallek.eu4parser.model.save.gameplayoptions.ProvinceTaxManpower;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import fr.osallek.eu4parser.model.save.trade.SaveTradeNode;
import fr.osallek.eu4parser.model.save.war.ActiveWar;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class ConditionsUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConditionsUtils.class.getName());

    public static final ConditionAnd ALWAYS_CONDITION = new ConditionAnd(Pair.of("always", "yes"));

    private ConditionsUtils() {}

    public static boolean applyConditionToCountry(SaveCountry country, SaveCountry root, SaveCountry from, String condition, String rawValue) {
        TradeGood tradeGood;
        SaveCountry other;
        SaveProvince saveProvince;
        Integer integer;
        Double aDouble;
        String value;
        SubjectType subjectType;
        Religion religion;
        ConditionAnd scriptedTrigger;

        if ("ROOT".equals(rawValue)) {
            value = root.getTag().toUpperCase();
        } else if ("FROM".equals(rawValue)) {
            value = from.getTag().toUpperCase();
        } else {
            value = rawValue;
        }

        if (country.getSave().getGame().getAdvisor(condition) != null) {
            return country.getActiveAdvisors()
                          .stream()
                          .anyMatch(advisor -> condition.equals(advisor.getName()) && advisor.getSkill() >= NumbersUtils.toInt(value));
        }

        if (country.getSave().getGame().getBuilding(condition) != null) {
            return country.getOwnedProvinces()
                          .stream()
                          .filter(province -> province.getBuildings().stream().anyMatch(b -> b.getName().equals(value)))
                          .count() >= NumbersUtils.toInt(value);
        }

        if (country.getSave().getGame().getIdeaGroup(condition) != null) {
            return country.getIdeaGroups().getIdeaGroupsNames().getOrDefault(condition, -1) >= NumbersUtils.toInt(value);
        }

        if ((subjectType = country.getSave().getGame().getSubjectType(condition)) != null) {
            return country.getSubjects().stream().map(SaveCountry::getSubjectType).filter(subjectType::equals).count() >= NumbersUtils.toInt(value);
        }

        if ((religion = country.getSave().getGame().getReligion(condition)) != null) {
            return country.getTolerance(religion) >= NumbersUtils.toDouble(value);
        }

        if ((tradeGood = country.getSave().getGame().getTradeGood(condition)) != null) {
            return country.getOwnedProvinces()
                          .stream()
                          .filter(province -> tradeGood.equals(province.getTradeGood()))
                          .count() >= NumbersUtils.toInt(value);
        }

        if ((scriptedTrigger = country.getSave().getGame().getScriptedTrigger(condition)) != null) {
            return scriptedTrigger.apply(country, from);
        }

        switch (condition.toLowerCase()) {
            case "absolutism":
                return NumbersUtils.intOrDefault(country.getAbsolutism()) >= NumbersUtils.toInt(value);
            case "accepted_culture":
                return country.getAcceptedCulturesNames().stream().anyMatch(s -> s.equalsIgnoreCase(rawValueToCulture(rawValue, root, from)));
            case "adm":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getMonarch() != null && country.getMonarch().getAdm() != null && country.getMonarch().getAdm() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getMonarch() == null
                           || other.getMonarch().getAdm() == null
                           || (country.getMonarch() != null
                               && country.getMonarch().getAdm() != null
                               && country.getMonarch().getAdm() >= other.getMonarch().getAdm());
                }
            case "adm_power":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getPowers() != null && country.getPowers().get(Power.ADM) != null && country.getPowers().get(Power.ADM) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getPowers() == null
                           || other.getPowers().get(Power.ADM) == null
                           || (country.getPowers() != null
                               && country.getPowers().get(Power.ADM) != null
                               && country.getPowers().get(Power.ADM) >= other.getPowers().get(Power.ADM));
                }
            case "adm_tech":
                return country.getTech().getAdm() >= Integer.parseInt(value);
            case "advisor":
                return country.getActiveAdvisors().stream().anyMatch(advisor -> advisor.getType().equals(value));
            case "advisor_exists":
                return country.getSave().getAdvisor(Integer.parseInt(value)) != null;
            case "ai":
                return "yes".equalsIgnoreCase(value) == !country.isHuman();
            case "alliance_with":
                return country.getAllies().stream().map(SaveCountry::getTag).anyMatch(tag -> tag.equalsIgnoreCase(value));
            case "allows_female_emperor":
                return country.getSave().getHre() != null && BooleanUtils.toBoolean(country.getSave().getHre().getAllowsFemaleEmperor());
            case "always":
                return "yes".equalsIgnoreCase(value);
            case "army_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getArmySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getArmySize() >= other.getArmySize();
                }
            case "army_size_percentage":
                return BigDecimal.valueOf(country.getArmySize()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLandForceLimit())))
                       >= 0;
            case "army_professionalism":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) >= aDouble;
                }
                break;
            case "army_tradition":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getArmyTradition()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getArmyTradition()) >= NumbersUtils.doubleOrDefault(other.getArmyTradition());
                }
            case "artillery_fraction":
                return BigDecimal.valueOf(country.getNbArtillery()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize()))) >= 0;
            case "at_war_with_religious_enemy":
                return country.getCurrentAtWarWith().stream().anyMatch(c -> !Objects.equals(c.getReligion(), country.getReligion()));
            case "authority":
                return NumbersUtils.doubleOrDefault(country.getAuthority()) >= NumbersUtils.toDouble(value);
            case "average_autonomy":
                return NumbersUtils.doubleOrDefault(country.getAverageAutonomy()) >= NumbersUtils.toDouble(value);
            case "average_autonomy_above_min":
                return NumbersUtils.doubleOrDefault(country.getAverageAutonomyAboveMin()) >= NumbersUtils.toDouble(value);
            case "average_effective_unrest":
                return NumbersUtils.doubleOrDefault(country.getAverageEffectiveUnrest()) >= NumbersUtils.toDouble(value);
            case "average_home_autonomy":
                return NumbersUtils.doubleOrDefault(country.getAverageHomeAutonomy()) >= NumbersUtils.toDouble(value);
            case "average_unrest":
                return NumbersUtils.doubleOrDefault(country.getAverageUnrest()) >= NumbersUtils.toDouble(value);
            case "blockade":
                return NumbersUtils.doubleOrDefault(country.getBlockadedPercent()) >= NumbersUtils.toDouble(value);
            case "call_for_peace":
                return NumbersUtils.doubleOrDefault(country.getCallForPeace()) >= NumbersUtils.toDouble(value);
            case "can_create_vassals":
                return country.getOverlord() == null;
            case "can_migrate":
                return country.getGovernment() != null &&
                       country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isAllowMigration().getKey()
                                                                                        && (reform.isAllowMigration().getValue() == null
                                                                                            || reform.isAllowMigration().getValue().apply(country, country)))
                       && country.getOwnedProvinces().size() == 1 && !country.isAtWar() &&
                       (country.getLastMigration() == null || country.getLastMigration().isBefore(country.getSave().getDate().plusYears(-5)));
            case "capital":
                return country.getCapitalId() != null && country.getCapitalId().equals(NumbersUtils.toInt(value));
            case "capital_trade_node":
                return Objects.equals(country.getTradePort().getId(), NumbersUtils.toInt(value));
            case "cavalry_fraction":
                return BigDecimal.valueOf(country.getNbCavalry()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize()))) >= 0;
            case "church_power":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getChurch() == null || NumbersUtils.doubleOrDefault(country.getChurch().getPower()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getChurch() == null || other.getChurch() == null
                           || NumbersUtils.doubleOrDefault(country.getChurch().getPower()) >= NumbersUtils.doubleOrDefault(other.getChurch().getPower());
                }
            case "coalition_target":
                return CollectionUtils.isNotEmpty(country.getSave().getCountry(value).getCoalition());
            case "colony":
                return country.getSubjects().stream().filter(SaveCountry::isColony).count() >= NumbersUtils.toInt(value);
            case "colony_claim":
                return country.getClaimProvinces().stream().anyMatch(province -> province.isColony() && province.getOwner().getTag().equals(value));
            case "consort_adm":
                return country.getConsort() != null && country.getConsort().getAdm() >= NumbersUtils.toInt(value);
            case "consort_age":
                return country.getConsort() != null
                       && country.getConsort().getBirthDate().plusYears(NumbersUtils.toInt(value)).isBefore(country.getSave().getDate());
            case "consort_culture":
                return country.getConsort() != null && rawValueToCulture(rawValue, root, from).equals(country.getConsort().getCultureName());
            case "consort_dip":
                return country.getConsort() != null && country.getConsort().getDip() >= NumbersUtils.toInt(value);
            case "consort_has_personality":
                return country.getConsort() != null && country.getConsort().getPersonalities() != null
                       && country.getConsort()
                                 .getPersonalities()
                                 .getPersonalities()
                                 .stream()
                                 .anyMatch(rulerPersonality -> value.equalsIgnoreCase(rulerPersonality.getName()));
            case "consort_mil":
                return country.getConsort() != null && country.getConsort().getMil() >= NumbersUtils.toInt(value);
            case "consort_religion":
                return country.getConsort() != null && country.getConsort().getReligion().getName().equals(rawValueToReligion(rawValue, root, from));
            case "controls":
                return country.getSave().getProvince(NumbersUtils.toInt(value)).getController().equals(country);
            case "core_claim":
                return country.getCoreProvinces().stream().anyMatch(province -> value.equalsIgnoreCase(province.getOwnerTag()));
            case "core_percentage":
                return BigDecimal.valueOf(country.getCoreProvinces().size())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOwnedProvinces().size()))) >= 0;
            case "corruption":
                return NumbersUtils.doubleOrDefault(country.getCorruption()) >= NumbersUtils.toDouble(value);
            case "culture_group":
                return country.getPrimaryCulture().getCultureGroup().getName().equals(rawValueToCultureGroup(rawValue, root, from));
            case "culture_group_claim":
                other = country.getSave().getCountry(value);
                return other.getOwnedProvinces()
                            .stream()
                            .anyMatch(province -> province.getCulture() != null
                                                  && province.getCulture().getCultureGroup().equals(country.getPrimaryCulture().getCultureGroup()));
            case "current_age":
                return country.getSave().getCurrentAge().getName().equals(value);
            case "current_debate":
                return country.getParliament() != null && country.getParliament().getActiveParliamentIssue() != null
                       && value.equalsIgnoreCase(country.getParliament().getActiveParliamentIssue().getWhich().getName());
            case "current_icon":
                return country.getReligion() != null && country.getCurrentIcon() != null
                       && value.equalsIgnoreCase(country.getReligion().getGameReligion().getIcons().get(country.getCurrentIcon()).getName());
            case "current_income_balance":
                return country.getLedger() != null && country.getLedger().getLastMonthIncome() != null && country.getLedger().getLastMonthExpense() != null
                       && new BigDecimal(value).add(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))
                                               .subtract(BigDecimal.valueOf(country.getLedger().getLastMonthExpense())).compareTo(BigDecimal.ZERO) >= 0;
            case "current_institution_growth": //Todo
                break;
            case "current_size_of_parliament":
                return country.getParliament() != null
                       && country.getOwnedProvinces().stream().map(SaveProvince::getSeatInParliament).filter(Objects::nonNull).count() >=
                          NumbersUtils.toInt(value);
            case "custom_nation_setup":
                return "yes".equalsIgnoreCase(value) == NationSetup.CUSTOM.equals(country.getSave().getGameplayOptions().getNationSetup());
            case "days": //Used for scope had_active_policy
                return true;
            case "defensive_war_with":
                other = country.getSave().getCountry(value);
                return country.getActiveWars().stream().anyMatch(activeWar -> activeWar.getDefender(country) != null && activeWar.getDefender(other) != null);
            case "development_of_overlord_fraction":
                return country.getOverlord() != null
                       && BigDecimal.valueOf(country.getDevelopment())
                                    .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOverlord().getDevelopment()))) >= 0;
            case "devotion":
                if (country.getGovernment() != null &&
                    country.getGovernment().getReforms().stream().noneMatch(reform -> reform.isHasDevotion().getKey()
                                                                                      && (reform.isHasDevotion().getValue() == null
                                                                                          || reform.isHasDevotion().getValue().apply(country, country)))) {
                    return false;
                }

                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getDevotion()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getChurch() == null || other.getChurch() == null
                           || NumbersUtils.doubleOrDefault(country.getDevotion()) >= NumbersUtils.doubleOrDefault(other.getDevotion());
                }
            case "difficulty":
                return value.equalsIgnoreCase(country.getSave().getGameplayOptions().getDifficulty().name());
            case "dip":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getMonarch() != null && country.getMonarch().getDip() != null && country.getMonarch().getDip() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getMonarch() == null || other.getMonarch().getDip() == null
                           || (country.getMonarch() != null
                               && country.getMonarch().getDip() != null
                               && country.getMonarch().getDip() >= other.getMonarch().getDip());
                }
            case "diplomatic_reputation":
                return NumbersUtils.doubleOrDefault(country.getDiplomaticReputation()) >= NumbersUtils.toDouble(value);
            case "dip_power":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getPowers() != null && country.getPowers().get(Power.DIP) != null && country.getPowers().get(Power.DIP) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getPowers() == null
                           || other.getPowers().get(Power.DIP) == null
                           || (country.getPowers() != null
                               && country.getPowers().get(Power.DIP) != null
                               && country.getPowers().get(Power.DIP) >= other.getPowers().get(Power.DIP));
                }
            case "dip_tech":
                return country.getTech().getDip() >= Integer.parseInt(value);
            case "disaster": //Used for scope has_disaster_progress
                return true;
            case "dominant_culture":
                return country.getDominantCulture() != null && country.getDominantCulture().getName().equals(rawValueToCulture(rawValue, root, from));
            case "dominant_religion":
                return country.getDominantReligion() != null && country.getDominantReligion().getName().equals(rawValueToReligion(rawValue, root, from));
            case "doom":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getDoom()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getDoom()) >= NumbersUtils.doubleOrDefault(other.getDoom());
                }
            case "dynasty":
                if (Eu4Utils.isTag(value)) {
                    other = country.getSave().getCountry(value);
                    return other.getMonarch() == null || other.getMonarch().getDynasty() == null
                           || (country.getMonarch() != null
                               && country.getMonarch().getDynasty() != null
                               && country.getMonarch().getDynasty().equals(other.getMonarch().getDynasty()));
                } else {
                    return country.getMonarch() != null && country.getMonarch().getDynasty() != null &&
                           ClausewitzUtils.removeQuotes(value).equals(ClausewitzUtils.removeQuotes(country.getMonarch().getDynasty()));
                }
            case "empire_of_china_reform_level":
                return country.getSave().getCelestialEmpire() != null
                       && country.getSave().getCelestialEmpire().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "estate": //Used for scope estate_influence, estate_loyalty, estate_territory
                return true;
            case "exists":
                if ("yes".equalsIgnoreCase(value)) {
                    return true;
                } else {
                    return country.getSave().getCountry(value) != null;
                }
            case "faction": //Used for scope faction_influence
                return true;
            case "faction_in_power":
                return CollectionUtils.isNotEmpty(country.getFactions())
                       && value.equalsIgnoreCase(country.getFactions().stream().max(Comparator.comparing(SaveFaction::getInfluence)).get().getType().getName());
            case "federation_size":
                return country.getFederationFriends().size() >= NumbersUtils.toInt(value);
            case "fervor":
                return country.getFervor() != null && NumbersUtils.doubleOrDefault(country.getFervor().getValue()) >= NumbersUtils.toDouble(value);
            case "flag": //Used for scope had_consort_flag, had_country_flag, had_global_flag, had_heir_flag, had_province_flag, had_ruler_flag
                return true;
            case "full_idea_group":
                return country.getIdeaGroups().getIdeaGroupsNames().getOrDefault(value, -1) == 7;
            case "galley_fraction":
                return BigDecimal.valueOf(country.getNbGalleys()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize()))) >= 0;
            case "gold_income":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().containsKey(Income.GOLD)
                       && country.getLedger().getLastMonthIncomeTable().get(Income.GOLD) >= NumbersUtils.toDouble(value);
            case "gold_income_percentage":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().containsKey(Income.GOLD)
                       && BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.GOLD))
                                    .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))) >= 0;
            case "government":
                return country.getGovernment() != null && country.getGovernment().getType().getName().equals(ClausewitzUtils.addQuotes(value));
            case "government_rank":
                return country.getGovernmentLevel() >= NumbersUtils.toInt(value);
            case "grown_by_development":
                return country.getHistoryStatsCache() != null &&
                       BigDecimal.valueOf(NumbersUtils.doubleOrDefault(country.getHistoryStatsCache().getStartingDevelopment()))
                                 .subtract(BigDecimal.valueOf(NumbersUtils.doubleOrDefault(country.getDevelopment()))).compareTo(BigDecimal.ZERO) >= 0;
            case "great_power_rank":
                return NumbersUtils.intOrDefault(country.getGreatPowerRank(), country.getSave().getGame().getNbGreatPowers() + 1) <= NumbersUtils.toInt(value);
            case "guaranteed_by":
                other = country.getSave().getCountry(value);
                return country.getGuarantees().contains(other);
            case "had_recent_war":
                return country.getLastWarEnded().plusYears(NumbersUtils.toInt(value)).isAfter(country.getSave().getDate());
            case "harmonization_progress":
                return NumbersUtils.doubleOrDefault(country.getHarmonyProgress()) >= NumbersUtils.toDouble(value);
            case "harmony":
                return NumbersUtils.doubleOrDefault(country.getHarmony()) >= NumbersUtils.toDouble(value);
            case "has_active_debate":
                return country.getParliament() != null && country.getParliament().getActiveParliamentIssue() != null;
            case "has_active_fervor":
                return country.getFervor() != null && "yes".equalsIgnoreCase(value) != country.getFervor().getActives().isEmpty();
            case "has_active_policy":
                return country.getActivePolicies().stream().map(ActivePolicy::getPolicy).map(Policy::getName).anyMatch(value::equals);
            case "has_adopted_cult":
                return country.getFetishistCult() != null && country.getFetishistCult().getName().equalsIgnoreCase(value);
            case "has_advisor":
                return !country.getActiveAdvisors().isEmpty();
            case "has_age_ability":
                return country.getActiveAgeAbility().stream().anyMatch(ageAbility -> ageAbility.getName().equalsIgnoreCase(ClausewitzUtils.addQuotes(value)));
            case "has_any_disaster":
                return country.getActiveDisaster() != null;
            case "has_border_with_religious_enemy":
                return country.getBorderProvinces()
                              .stream()
                              .map(SaveProvince::getOwner)
                              .filter(Objects::nonNull)
                              .anyMatch(c -> !Objects.equals(c.getReligion(), country.getReligion()));
            case "has_casus_belli_against":
                return country.getSave()
                              .getDiplomacy()
                              .getCasusBellis()
                              .stream()
                              .anyMatch(casusBelli -> casusBelli.getFirst().equals(country) && casusBelli.getSecond().getTag().equals(value));
            case "has_changed_nation":
                return BooleanUtils.toBoolean(country.hasSwitchedNation());
            case "has_church_aspect":
                return country.getChurch() != null && country.getChurch().getAspects().stream().anyMatch(churchAspect -> value.equals(churchAspect.getName()));
            case "has_colonial_parent":
                return country.isColony() && country.getOverlord().getTag().equals(value);
            case "has_commanding_three_star":
                return country.getLeaders().values().stream().anyMatch(leader -> leader.getNbStars() == 3);
            case "has_consort":
                return country.getConsort() != null;
            case "has_consort_flag":
                return country.getConsort() != null && country.getConsort().getRulerFlags().contains(value);
            case "has_consort_regency":
                return country.getConsort() != null && BooleanUtils.toBoolean(country.getConsort().getRegent());
            case "has_country_flag":
                return country.getFlags() != null && country.getFlags().contains(value);
            case "has_country_modifier":
                return country.getModifiers().stream().anyMatch(modifier -> modifier.getModifierName().equals(value));
            case "has_custom_ideas":
                return CollectionUtils.isNotEmpty(country.getCustomNationalIdeas());
            case "has_disaster":
                return value.equalsIgnoreCase(country.getActiveDisaster());
            case "has_discovered":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getSave().getProvince(integer) != null && country.getSave().getProvince(integer).getDiscoveredBy().contains(country);
                }
                break;
            case "has_dlc":
                return country.getSave().getDlcEnabled().contains(value);
            case "has_divert_trade":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(6));
            case "has_embargo_rivals":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(1));
            case "has_estate":
                return country.getEstates().stream().map(SaveEstate::getType).map(ClausewitzUtils::removeQuotes).anyMatch(value::equals);
            case "has_faction":
                return CollectionUtils.isNotEmpty(country.getFactions()) && country.getFactions()
                                                                                   .stream()
                                                                                   .map(SaveFaction::getType)
                                                                                   .anyMatch(faction -> value.equalsIgnoreCase(faction.getName()));
            case "has_factions":
                return CollectionUtils.isNotEmpty(country.getFactions());
            case "has_female_consort":
                return country.getConsort() != null && BooleanUtils.toBoolean(country.getConsort().getFemale());
            case "has_female_heir":
                return country.getHeir() != null && BooleanUtils.toBoolean(country.getHeir().getFemale());
            case "has_first_revolution_started":
                return country.getSave().getRevolution() != null && BooleanUtils.toBoolean(country.getSave().getRevolution().hasFirstRevolutionStarted());
            case "has_foreign_consort":
                return country.getConsort() != null && country.getConsort().getWho() != null;
            case "has_foreign_heir":
                return country.getHeir() != null && country.getHeir().getWho() != null;
            case "has_friendly_reformation_center":
                return country.getOwnedProvinces()
                              .stream()
                              .filter(province -> province.getReligion() != null && province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> BooleanUtils.toBoolean(province.centerOfReligion()));
            case "has_game_started":
                return !country.getSave().getDate().equals(country.getSave().getStartDate());
            case "has_government_ability":
                return country.getGovernment() != null && country.getGovernment().getReforms() != null
                       && country.getGovernment().getReforms().stream().anyMatch(reform -> CollectionUtils.isNotEmpty(reform.getGovernmentAbilities())
                                                                                           && reform.getGovernmentAbilities().contains(value));
            case "has_given_consort_to":
                other = country.getSave().getCountry(value);
                return other.getConsort() != null && Queen.class.equals(other.getConsort().getClass())
                       && ((Queen) other.getConsort()).getSaveCountryOfOrigin().equals(country);
            case "has_guaranteed":
                other = country.getSave().getCountry(value);
                return other.getGuarantees().contains(country);
            case "has_global_flag":
                return country.getSave().getFlags().contains(value);
            case "has_government_attribute": //Todo not only custom attributes but also native ie: has_government_attribute = heir
                return country.getGovernment() != null &&
                       country.getGovernment()
                              .getReforms()
                              .stream()
                              .filter(reform -> reform.getCustomAttributes() != null)
                              .anyMatch(reform -> reform.getCustomAttributes()
                                                        .entrySet()
                                                        .stream()
                                                        .anyMatch(entry -> value.equalsIgnoreCase(entry.getKey())
                                                                           && "yes".equalsIgnoreCase(entry.getValue())));
            case "has_government_mechanic":
                return country.getGovernment() != null && country.getGovernment().hasMechanic(value);
            case "has_harmonized_with":
                return country.getHarmonizedReligionGroups().stream().anyMatch(rel -> value.equalsIgnoreCase(rel.getName()))
                       || country.getHarmonizedReligions().stream().anyMatch(rel -> value.equalsIgnoreCase(rel.getName()));
            case "has_horde_unity":
                return "yes".equalsIgnoreCase(value) ==
                       (country.getGovernment() != null &&
                        country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isTribal().getKey()
                                                                                         && (reform.isTribal().getValue() == null
                                                                                             || reform.isTribal().getValue().apply(country, country)))
                        && country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isNomad().getKey()
                                                                                            && (reform.isNomad().getValue() == null
                                                                                                || reform.isNomad().getValue().apply(country, country))));
            case "has_heir":
                return "yes".equalsIgnoreCase(value) == (country.getHeir() != null);
            case "has_heir_flag":
                return country.getHeir() != null && country.getHeir().getRulerFlags().contains(value);
            case "has_hostile_reformation_center":
                return country.getOwnedProvinces()
                              .stream()
                              .filter(province -> province.getReligion() != null && !province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> BooleanUtils.toBoolean(province.centerOfReligion()));
            case "has_idea":
                return country.getIdeaGroups().hasIdea(value);
            case "has_idea_group":
                return country.getIdeaGroups().getIdeaGroupsNames().containsKey(value);
            case "has_institution":
                return country.getEmbracedInstitutions().stream().anyMatch(institution -> value.equalsIgnoreCase(institution.getName()));
            case "has_leader":
                return country.getLeaders().values().stream().map(Leader::getName).anyMatch(value::equals);
            case "has_matching_religion":
                if (Eu4Utils.isTag(value)) {
                    other = country.getSave().getCountry(value);
                    return other.getReligion() != null && other.getReligion().equals(country.getReligion())
                           || (country.getSecondaryReligion() != null && other.getSecondaryReligion() != null
                               && country.getSecondaryReligion().equals(other.getSecondaryReligion()));
                } else {
                    return country.getReligion() != null && country.getReligion().getName().equals(value)
                           || (country.getSecondaryReligion() != null && country.getSecondaryReligion().getName().equals(value));
                }
            case "has_meritocracy":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment()
                                                                .getReforms()
                                                                .stream()
                                                                .anyMatch(reform -> reform.isHasMeritocracy().getKey()
                                                                                    && (reform.isHasMeritocracy().getValue() == null
                                                                                        || reform.isHasMeritocracy().getValue().apply(country, country))));
            case "has_militarised_society":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment()
                                                                .getReforms()
                                                                .stream()
                                                                .anyMatch(reform -> reform.isMilitarisedSociety().getKey()
                                                                                    && (reform.isMilitarisedSociety().getValue() == null
                                                                                        || reform.isMilitarisedSociety().getValue().apply(country, country))));
            case "has_mission":
                return country.getCountryMissions().getMissions().stream().anyMatch(mission -> value.equalsIgnoreCase(mission.getName()));
            case "has_new_dynasty":
                return country.getMonarch() != null && country.getPreviousMonarchs().size() >= 2 && country.getHistory().getMonarchs().size() >= 2
                       && !country.getHistory()
                                  .getMonarch(country.getPreviousMonarchs().get(country.getPreviousMonarchs().size() - 2).getId())
                                  .getDynasty()
                                  .equals(country.getMonarch().getDynasty());
            case "has_parliament":
                return country.getParliament() != null;
            case "has_patriarchs":
                return country.getReligion() != null && ("yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().hasPatriarchs()));
            case "has_personal_deity":
                return value.equalsIgnoreCase(country.getPersonalDeity().getName());
            case "has_privateers":
                return "yes".equalsIgnoreCase(value) == NumbersUtils.intOrDefault(country.getNumShipsPrivateering()) > 0;
            case "has_promote_investments":
                return CollectionUtils.isNotEmpty(country.getTradeCompanies())
                       && country.getTradeCompanies()
                                 .stream()
                                 .anyMatch(tradeCompany -> value.equalsIgnoreCase(tradeCompany.getName()) && tradeCompany.promoteInvestments());
            case "has_regency":
                return country.getMonarch() != null && BooleanUtils.toBoolean(country.getMonarch().getRegent());
            case "has_reform":
                return country.getGovernment() != null && country.getGovernment()
                                                                 .getReforms()
                                                                 .stream()
                                                                 .anyMatch(reform -> value.equalsIgnoreCase(reform.getName()));
            case "have_had_reform":
                return country.getGovernment() != null && country.getGovernment().getHistory().stream().anyMatch(value::equalsIgnoreCase);
            case "government_reform_progress":
                return NumbersUtils.doubleOrDefault(country.getGovernmentReformProgress()) >= NumbersUtils.toDouble(value);
            case "has_ruler":
                return country.getMonarch() != null && country.getMonarch().getName().equals(value);
            case "has_ruler_flag":
                return country.getMonarch() == null || (country.getMonarch().getRulerFlags() != null && country.getMonarch().getRulerFlags().contains(value));
            case "has_ruler_modifier":
                return country.getModifiers()
                              .stream()
                              .filter(modifier -> BooleanUtils.toBoolean(modifier.rulerModifier()))
                              .map(SaveModifier::getModifierName)
                              .anyMatch(value::equals);
            case "has_saved_event_target": //Todo ???
                break;
            case "has_scutage":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(4));
            case "has_secondary_religion":
                return country.getSecondaryReligion() != null;
            case "has_send_officers":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(5));
            case "has_spawned_rebels":
                return country.getSave()
                              .getRebelFactions()
                              .values()
                              .stream()
                              .anyMatch(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getType().equals(ClausewitzUtils.removeQuotes(value)));
            case "has_spawned_supported_rebels":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getRebelFactions()
                              .values()
                              .stream()
                              .anyMatch(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getSupportiveCountry() != null && rebelFaction.getSupportiveCountry().equals(other));
            case "has_subsidize_armies":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(3));
            case "has_support_loyalists":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(2));
            case "has_switched_nation":
                return BooleanUtils.toBoolean(country.hasSwitchedNation());
            case "has_truce":
            case "truce_with":
                other = country.getSave().getCountry(value);
                return other != null && other.isAlive() && country.getActiveRelation(other) != null
                       && BooleanUtils.toBoolean(country.getActiveRelation(other).truce());
            case "has_unconditional_surrender":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(country.hasUnconditionalSurrender());
            case "has_unembraced_institution":
                return country.getNotEmbracedInstitutions().stream().anyMatch(institution -> value.equalsIgnoreCase(institution.getName()));
            case "has_unified_culture_group":
                return country.getSave()
                              .getProvinces()
                              .values()
                              .stream()
                              .filter(province -> province.getCulture() != null
                                                  && province.getCulture().getCultureGroup().equals(country.getPrimaryCulture().getCultureGroup()))
                              .allMatch(province -> province.getOwner().equals(country));
            case "has_unit_type":
                return country.getUnitType().equals(value);
            case "has_unlocked_cult":
                return country.getReligion() != null && country.getReligion().getGameReligion().useFetishistCult()
                       && (country.getSave().getGame().getFetishistCult(value).getAllow() == null
                           || country.getSave().getGame().getFetishistCult(value).getAllow().apply(country, from));
            case "has_wartaxes":
                return BooleanUtils.toBoolean(country.warTaxes());
            case "heavy_ship_fraction":
                return BigDecimal.valueOf(country.getNbHeavyShips()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize()))) >= 0;
            case "heir_adm":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getAdm()) >= NumbersUtils.toInt(value);
            case "heir_age":
                return country.getHeir() != null
                       && ChronoUnit.YEARS.between(country.getHeir().getBirthDate(), country.getSave().getDate()) >= NumbersUtils.toInt(value);
            case "heir_dip":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getDip()) >= NumbersUtils.toInt(value);
            case "heir_claim":
                return country.getHeir() != null && NumbersUtils.doubleOrDefault(country.getHeir().getClaim()) >= NumbersUtils.toInt(value);
            case "heir_culture":
                return country.getHeir() != null && rawValueToCulture(rawValue, root, from).equals(country.getHeir().getCultureName());
            case "heir_has_consort_dynasty":
                return country.getHeir() != null && country.getConsort() != null && country.getHeir().getDynasty().equals(country.getConsort().getDynasty());
            case "heir_has_personality":
                return country.getHeir() != null && country.getHeir()
                                                           .getPersonalities()
                                                           .getPersonalities()
                                                           .stream()
                                                           .anyMatch(rulerPersonality -> value.equalsIgnoreCase(rulerPersonality.getName()));
            case "heir_has_ruler_dynasty":
                return country.getHeir() != null && country.getMonarch() != null && country.getHeir().getDynasty().equals(country.getMonarch().getDynasty());
            case "heir_mil":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getMil()) >= NumbersUtils.toInt(value);
            case "heir_nationality":
                other = country.getSave().getCountry(value);
                return country.getHeir() != null && country.getHeir().getWho().equals(other);
            case "heir_religion":
                return country.getHeir() != null && country.getHeir().getReligion().getName().equals(rawValueToReligion(rawValue, root, from));
            case "high_army_professionalism":
                return "yes".equalsIgnoreCase(value) == (
                        NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) >= country.getSave().getGame().getHighArmyProfessionalismMinRange()
                        && NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) <= country.getSave().getGame().getHighArmyProfessionalismMaxRange());
            case "historical_friend_with":
                other = country.getSave().getCountry(value);
                return country.getHistoricalFriends().contains(other);
            case "historical_rival_with":
                other = country.getSave().getCountry(value);
                return country.getHistoricalRivals().contains(other);
            case "horde_unity":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getHordeUnity()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getHordeUnity() != null && country.getHordeUnity() != null && country.getHordeUnity() >= other.getHordeUnity();
                }
            case "hre_heretic_religion":
                if ((other = country.getSave().getCountry(value)) != null) {
                    return country.isAlive() && !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(other.getReligion().isHreHereticReligion());
                } else {
                    return country.isAlive() && !country.getSave().getHre().dismantled()
                           && country.getSave().getReligions().getReligion(rawValueToReligion(rawValue, root, from)) != null
                           && BooleanUtils.toBoolean(country.getSave()
                                                            .getReligions()
                                                            .getReligion(rawValueToReligion(rawValue, root, from))
                                                            .isHreHereticReligion());
                }
            case "hre_leagues_enabled":
                return !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(country.getSave().getHreLeaguesActive());
            case "hre_reform_level":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "hre_reform_passed":
                return !country.getSave().getHre().dismantled()
                       && country.getSave().getHre().getPassedReforms().stream().anyMatch(reform -> ClausewitzUtils.addQuotes(value).equals(reform.getName()));
            case "hre_religion":
                return !country.getSave().getHre().dismantled()
                       && BooleanUtils.toBoolean(country.getSave().getReligions().getReligion(rawValueToReligion(rawValue, root, from)).isHreReligion());
            case "hre_religion_locked":
                return "yes".equalsIgnoreCase(value) == (!country.getSave().getHre().dismantled()
                                                         && !HreReligionStatus.PEACE.equals(country.getSave().getHreReligionStatus()));
            case "hre_religion_treaty":
                return "yes".equalsIgnoreCase(value) == (!country.getSave().getHre().dismantled()
                                                         && HreReligionStatus.PEACE.equals(country.getSave().getHreReligionStatus()));
            case "hre_size":
                return !country.getSave().getHre().dismantled()
                       && country.getSave().getCountries().values().stream().filter(c -> c.getCapital().inHre()).count() >= NumbersUtils.toInt(value);
            case "imperial_influence":
                return !country.getSave().getHre().dismantled()
                       && NumbersUtils.doubleOrDefault(country.getSave().getHre().getImperialInfluence()) >= NumbersUtils.toDouble(value);
            case "imperial_mandate":
                return !country.getSave().getCelestialEmpire().dismantled()
                       && NumbersUtils.doubleOrDefault(country.getSave().getCelestialEmpire().getImperialInfluence()) >= NumbersUtils.toDouble(value);
            case "in_golden_age":
                return country.getGoldenEraDate() != null
                       && ChronoUnit.YEARS.between(country.getGoldenEraDate(), country.getSave().getDate()) < country.getSave()
                                                                                                                     .getGame()
                                                                                                                     .getGoldenEraDuration();
            case "incident": //Used for scope incident_variable_value
                return true;
            case "infantry_fraction":
                return BigDecimal.valueOf(country.getNbInfantry()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize()))) >= 0;
            case "inflation":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getInflation()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getInflation()) >= NumbersUtils.doubleOrDefault(other.getInflation());
                }
            case "influence": //Used for scope faction_influence
                return true;
            case "innovativeness":
                return NumbersUtils.doubleOrDefault(country.getInnovativeness()) >= NumbersUtils.toDouble(value);
            case "invasion_nation":
                return "yes".equalsIgnoreCase(value) == country.getModifiers()
                                                               .stream()
                                                               .anyMatch(modifier -> "\"invasion_nation\"".equals(modifier.getModifierName()));
            case "invested_papal_influence":
                return country.getPapalInfluence() != null && country.getPapalInfluence() >= NumbersUtils.toDouble(value);
            case "in_league":
                return country.getSave().getReligions().getReligion(value).getInLeague().contains(country);
            case "ironman":
                return false;
            case "is_advisor_employed":
                return country.getSave()
                              .getCountries()
                              .values()
                              .stream()
                              .anyMatch(c -> c.getActiveAdvisors().stream().anyMatch(advisor -> NumbersUtils.toInt(value).equals(advisor.getId().getId())));
            case "is_at_war":
                return "yes".equalsIgnoreCase(value) == country.isAtWar();
            case "is_bankrupt":
                return "yes".equalsIgnoreCase(value) == (country.lastBankrupt() != null &&
                                                         ChronoUnit.YEARS.between(country.lastBankrupt(), country.getSave().getDate())
                                                         < country.getSave().getGame().getBankruptcyDuration());
            case "is_claim":
                return country.getClaimProvinces().stream().map(SaveProvince::getId).anyMatch(NumbersUtils.toInt(value)::equals);
            case "is_client_nation":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null
                                                         && country.getSubjectType().getName().equalsIgnoreCase(Eu4Utils.SUBJECT_TYPE_CLIENT_VASSAL));
            case "is_client_nation_of":
                other = country.getSave().getCountry(value);
                return other.equals(country.getOverlord()) && country.getSubjectType().getName().equalsIgnoreCase(Eu4Utils.SUBJECT_TYPE_CLIENT_VASSAL);
            case "is_colonial_nation":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null
                                                         && country.getSubjectType().getName().equalsIgnoreCase(Eu4Utils.SUBJECT_TYPE_COLONY));
            case "is_colonial_nation_of":
                other = country.getSave().getCountry(value);
                return other.equals(country.getOverlord()) && country.getSubjectType().getName().equalsIgnoreCase(Eu4Utils.SUBJECT_TYPE_COLONY);
            case "is_core":
                return country.getCoreProvinces().stream().map(SaveProvince::getId).anyMatch(NumbersUtils.toInt(value)::equals);
            case "is_crusade_target":
                return "yes".equalsIgnoreCase(value) == country.getSave()
                                                               .getReligions()
                                                               .getReligions()
                                                               .values()
                                                               .stream()
                                                               .anyMatch(saveReligion -> saveReligion.hasPapacy()
                                                                                         && saveReligion.getPapacy().getCrusadeTarget() != null
                                                                                         && saveReligion.getPapacy().getCrusadeTarget().equals(country));
            case "is_defender_of_faith":
                return country.isAlive() && country.getReligion() != null
                       && ("yes".equalsIgnoreCase(value) == (country.getReligion().hasDefenderOfFaith() && country.equals(
                        country.getReligion().getDefender())));
            case "is_dynamic_tag":
                return "yes".equalsIgnoreCase(value) == country.isClientState();
            case "is_elector":
                return "yes".equalsIgnoreCase(value) == country.isElector();
            case "is_emperor":
                return "yes".equalsIgnoreCase(value) == country.isEmperor();
            case "is_emperor_of_china":
                return "yes".equalsIgnoreCase(value) == (!country.getSave().getCelestialEmpire().dismantled()
                                                         && country.equals(country.getSave().getCelestialEmpire().getEmperor()));
            case "is_enemy":
                other = country.getSave().getCountry(value);
                return "yes".equalsIgnoreCase(value) == country.getEnemies().contains(other);
            case "is_excommunicated":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(country.isExcommunicated());
            case "is_federation_leader":
                return "yes".equalsIgnoreCase(value) == country.equals(country.getFederationLeader());
            case "is_female":
                return "yes".equalsIgnoreCase(value) == (country.getMonarch() != null && BooleanUtils.toBoolean(country.getMonarch().getFemale()));
            case "is_force_converted":
                return "yes".equalsIgnoreCase(value) == (country.getForceConvert() != null);
            case "is_former_colonial_nation":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() == null && country.getColonialParent() != null);
            case "is_free_city":
                return "yes".equalsIgnoreCase(value) == country.isFreeCity();
            case "is_free_or_tributary_trigger":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() == null || country.getSubjectType().isVoluntary());
            case "is_great_power":
                return "yes".equalsIgnoreCase(value) == country.isGreatPower();
            case "is_harmonizing_with":
                return country.getHarmonizingWithReligion() != null && value.equalsIgnoreCase(country.getHarmonizingWithReligion().getName());
            case "is_heir_leader":
                return "yes".equalsIgnoreCase(value) == (country.getHeir() != null && country.getHeir().getLeader() != null);
            case "is_hegemon":
                return "yes".equalsIgnoreCase(value) == (country.getHegemon() != null);
            case "is_imperial_ban_allowed":
                return "yes".equalsIgnoreCase(value) == (!country.getSave().getHre().dismantled()
                                                         && BooleanUtils.toBoolean(country.getSave().getHre().getImperialBanAllowed()));
            case "is_incident_active":
                switch (value) {
                    case "any":
                        return !country.getActiveIncidents().isEmpty();
                    case "none":
                        return country.getActiveIncidents().isEmpty();
                    default:
                        return country.getActiveIncidents().contains(ClausewitzUtils.addQuotes(value));
                }
            case "is_incident_happened":
                return country.getPastIncidents().contains(ClausewitzUtils.addQuotes(value));
            case "is_incident_possible": //Fixme ???
            case "is_incident_potential":
                return country.getPotentialIncidents().contains(ClausewitzUtils.addQuotes(value));
            case "is_institution_enabled":
                return country.getSave().getInstitutions().isAvailable(country.getSave().getGame().getInstitution(value));
            case "is_in_coalition":
                return "yes".equalsIgnoreCase(value) == (country.getCoalitionTarget() != null);
            case "is_in_coalition_war":
                return "yes".equalsIgnoreCase(value) == (country.getActiveWars().stream().anyMatch(ActiveWar::isCoalition));
            case "is_in_deficit":
                return "yes".equalsIgnoreCase(value) == (country.getLedger().getLastMonthIncome() < country.getLedger().getLastMonthExpense());
            case "is_in_league_war":
                return "yes".equalsIgnoreCase(value) == (country.getActiveWars().stream().anyMatch(war -> war.getWarGoal() != null
                                                                                                          && war.getWarGoal()
                                                                                                                .getCasusBelli(country.getSave().getGame())
                                                                                                                .isLeague()));
            case "is_in_trade_league":
                return "yes".equalsIgnoreCase(value) == (country.getSave().getTradeLeagues().stream().anyMatch(tradeLeague -> tradeLeague.hasMember(country)));
            case "is_in_trade_league_with":
                other = country.getSave().getCountry(value);
                return country.getSave().getTradeLeagues().stream().anyMatch(tradeLeague -> tradeLeague.hasMember(country) && tradeLeague.hasMember(other));
            case "is_league_enemy":
                other = country.getSave().getCountry(value);
                return country.isAlive() && !country.getReligion().equals(other.getReligion()) && country.getReligion().getInLeague().contains(country)
                       && country.getReligion().getInLeague().contains(other);
            case "is_lacking_institutions":
                return "yes".equalsIgnoreCase(value) == (country.getSave().getInstitutions().getNbInstitutions() > country.getNbEmbracedInstitutions());
            case "is_league_friend":
                other = country.getSave().getCountry(value);
                return country.isAlive() && country.getReligion().equals(other.getReligion()) && country.getReligion().getInLeague().contains(country)
                       && country.getReligion().getInLeague().contains(other);
            case "is_league_leader":
                return country.isAlive() && ("yes".equalsIgnoreCase(value) == (BooleanUtils.toBoolean(country.getSave().getHreLeaguesActive())
                                                                               && (country.equals(country.getSave().getHre().getEmperor())
                                                                                   || CollectionUtils.isNotEmpty(country.getReligion().getInLeague()) && country
                        .getReligion()
                        .getInLeague()
                        .get(0)
                        .equals(country))));
            case "is_lesser_in_union":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null
                                                         && Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equals(country.getSubjectType().getName()));
            case "is_monarch_leader":
                return "yes".equalsIgnoreCase(value) == (country.getMonarch() != null && (country.getMonarch().getLeader() != null));
            case "is_month":
            case "real_month_of_year":
                return country.getSave().getDate().getMonthValue() == NumbersUtils.toInt(value);
            case "is_march":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null
                                                         && Eu4Utils.SUBJECT_TYPE_MARCH.equals(country.getSubjectType().getName()));
            case "is_neighbor_of":
                other = country.getSave().getCountry(value);
                return country.getNeighbours().contains(other);
            case "is_nomad":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isNomad().getKey()
                                                                                                                          && (reform.isNomad().getValue()
                                                                                                                              == null
                                                                                                                              || reform.isNomad()
                                                                                                                                       .getValue()
                                                                                                                                       .apply(country,
                                                                                                                                              country))));
            case "is_orangists_in_power":
                return "yes".equalsIgnoreCase(value) == (country.getStatistsVsMonarchists() != null && country.getStatistsVsMonarchists() > 0);
            case "is_origin_of_consort":
                other = country.getSave().getCountry(value);
                return country.getConsort() != null && country.getConsort().getWho().equals(other);
            case "is_overseas_subject":
                if (country.getOverlord() == null) {
                    return !"yes".equalsIgnoreCase(value);
                }

                Set<ProvinceList> continents = country.getOwnedProvinces().stream().map(SaveProvince::getContinent).collect(Collectors.toSet());
                continents.retainAll(country.getOverlord().getOwnedProvinces().stream().map(SaveProvince::getContinent).collect(Collectors.toSet()));
                return "yes".equalsIgnoreCase(value) == continents.isEmpty();
            case "is_papal_controller":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && country.getReligion().getPapacy() != null
                                                         && country.equals(country.getReligion().getPapacy().getController()));
            case "is_part_of_hre":
                return "yes".equalsIgnoreCase(value) == (country.getCapital() != null && country.getCapital().inHre());
            case "is_playing_custom_nation":
                return "yes".equalsIgnoreCase(value) == country.isCustom();
            case "is_possible_march":
                break;
            case "is_possible_vassal": //todo
                break;
            case "is_previous_papal_controller":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && country.getReligion().getPapacy() != null
                                                         && country.equals(country.getReligion().getPapacy().getPreviousController()));
            case "is_protectorate":
                return false;
            case "is_random_new_world":
                return "yes".equalsIgnoreCase(value) == country.getSave().isRandomNewWorld();
            case "is_religion_enabled":
                SaveReligion saveReligion = country.getSave().getReligions().getReligion(rawValueToReligion(rawValue, root, from));
                return saveReligion != null && (saveReligion.getGameReligion().getDate() == null
                                                || (saveReligion.getEnable() != null && country.getSave().getDate().isAfter(saveReligion.getEnable())));
            case "is_religion_reformed":
                return "yes".equalsIgnoreCase(value) == (BooleanUtils.toBoolean(country.hasReformedReligion()));
            case "is_republic":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isRepublic().getKey()
                                                                                                                          && (reform.isRepublic().getValue()
                                                                                                                              == null
                                                                                                                              || reform.isRepublic()
                                                                                                                                       .getValue()
                                                                                                                                       .apply(country,
                                                                                                                                              country))));
            case "is_revolutionary":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isRevolutionary().getKey()
                                                                                                                          && (reform.isRevolutionary()
                                                                                                                                    .getValue()
                                                                                                                              == null
                                                                                                                              || reform.isRevolutionary()
                                                                                                                                       .getValue()
                                                                                                                                       .apply(country,
                                                                                                                                              country))));
            case "is_revolution_target":
                return "yes".equalsIgnoreCase(value) == (country.getSave().getRevolution() != null
                                                         && country.equals(country.getSave().getRevolution().getRevolutionTarget()));
            case "is_revolutionary_republic_trigger":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment()
                                                                .getReforms()
                                                                .stream()
                                                                .anyMatch(reform -> "revolutionary_republic_reform".equalsIgnoreCase(reform.getName())));
            case "is_rival":
                other = country.getSave().getCountry(value);
                return country.getRivals().containsKey(ClausewitzUtils.addQuotes(other.getTag()));
            case "is_state_core":
                integer = NumbersUtils.toInt(value);
                saveProvince = country.getSave().getProvince(integer);
                return country.getCoreProvinces().contains(saveProvince)
                       && country.getStates().keySet().stream().anyMatch(area -> area.getProvinces().contains(saveProvince));
            case "is_statists_in_power":
                return "yes".equalsIgnoreCase(value) == country.isStatistsInPower();
            case "is_monarchists_in_power":
                return "yes".equalsIgnoreCase(value) == country.isMonarchistsInPower();
            case "is_subject":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null);
            case "is_subject_of":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other);
            case "is_subject_of_type":
                return country.getOverlord() != null && value.equalsIgnoreCase(country.getSubjectType().getName());
            case "is_subject_other_than_tributary_trigger":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null && !country.getSubjectType().isVoluntary());
            case "is_territorial_core":
                integer = NumbersUtils.toInt(value);
                saveProvince = country.getSave().getProvince(integer);
                return country.getCoreProvinces().contains(saveProvince)
                       && country.getStates().keySet().stream().noneMatch(area -> area.getProvinces().contains(saveProvince));
            case "is_threat":
                other = country.getSave().getCountry(value);
                return "attitude_threatened".equals(other.getActiveRelation(country).getAttitude());
            case "is_trade_league_leader":
                return "yes".equalsIgnoreCase(value) == (country.getTradeLeague() != null && country.getTradeLeague().getMembers().get(0).equals(country));
            case "is_tribal":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isTribal().getKey()
                                                                                                                          && (reform.isTribal().getValue()
                                                                                                                              == null
                                                                                                                              || reform.isTribal()
                                                                                                                                       .getValue()
                                                                                                                                       .apply(country,
                                                                                                                                              country))));
            case "is_tributary":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null && country.getSubjectType().isVoluntary());
            case "is_vassal":
                return "yes".equalsIgnoreCase(value) == (country.getOverlord() != null
                                                         && Eu4Utils.SUBJECT_TYPE_VASSAL.equalsIgnoreCase(country.getSubjectType().getName()));
            case "is_year":
                return country.getSave().getDate().getYear() == NumbersUtils.toInt(value);
            case "isolationism":
                return country.getIsolationism() != null && country.getIsolationismLevel() >= NumbersUtils.toInt(value);
            case "janissary_percentage":
                return BigDecimal.valueOf(country.getNbRegimentOfCategory(5))
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize()))) >= 0;
            case "junior_union_with":
                other = country.getSave().getCountry(value);
                return other.equals(country.getOverlord()) && Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equals(country.getSubjectType().getName());
            case "karma":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return NumbersUtils.intOrDefault(country.getKarma()) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.intOrDefault(country.getKarma()) >= NumbersUtils.intOrDefault(other.getKarma());
                }
            case "knowledge_sharing":
                return "yes".equalsIgnoreCase(value) == country.getSave()
                                                               .getDiplomacy()
                                                               .getKnowledgeSharing()
                                                               .stream()
                                                               .anyMatch(knowledgeSharing -> knowledgeSharing.getFirst().equals(country));
            case "knows_country":
                other = country.getSave().getCountry(value);
                return (other.getCapitalId() != null && other.getCapital().getDiscoveredBy().contains(country));
            case "land_forcelimit":
                return country.getLandForceLimit() > NumbersUtils.toDouble(value);
            case "land_maintenance":
                return NumbersUtils.doubleOrDefault(country.getLandMaintenance()) >= NumbersUtils.toDouble(value);
            case "land_morale":
                return country.getLandMorale() > NumbersUtils.toDouble(value);
            case "last_mission": //Outdated was for old missions mechanism
                break;
            case "legitimacy":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getLegitimacy()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getLegitimacy() != null
                           && NumbersUtils.doubleOrDefault(country.getLegitimacy()) >= NumbersUtils.doubleOrDefault(other.getLegitimacy());
                }
            case "legitimacy_equivalent":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getLegitimacyEquivalent()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getLegitimacyEquivalent()) >= NumbersUtils.doubleOrDefault(other.getLegitimacyEquivalent());
                }
            case "legitimacy_or_horde_unity":
                if (country.getLegitimacyOrHordeUnity() == null) {
                    return false;
                }

                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getLegitimacyOrHordeUnity()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    if (other.getLegitimacyOrHordeUnity() == null) {
                        return false;
                    }

                    return country.getLegitimacyOrHordeUnity() >= other.getLegitimacyOrHordeUnity();
                }
            case "liberty_desire":
                return country.getOverlord() != null || NumbersUtils.doubleOrDefault(country.getLibertyDesire()) >= NumbersUtils.toDouble(value);
            case "light_ship_fraction":
                return BigDecimal.valueOf(country.getNbLightShips()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize()))) >= 0;
            case "loyalty": //Used for scope estate_loyalty
                return true;
            case "low_army_professionalism":
                return "yes".equalsIgnoreCase(value) == (
                        NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) >= country.getSave().getGame().getLowArmyProfessionalismMinRange()
                        && NumbersUtils.doubleOrDefault(country.getArmyProfessionalism()) <= country.getSave().getGame().getLowArmyProfessionalismMaxRange());
            case "luck":
                return "yes".equalsIgnoreCase(value) == (!country.wasPlayer() && !country.isHuman() && BooleanUtils.toBoolean(country.isLucky()));
            case "march_of":
                other = country.getSave().getCountry(value);
                return other.equals(country.getOverlord()) && Eu4Utils.SUBJECT_TYPE_MARCH.equals(country.getSubjectType().getName());
            case "manpower":
                return country.getManpower() >= NumbersUtils.toInt(value) * 1000;
            case "manpower_percentage":
                return BigDecimal.valueOf(country.getManpower()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getMaxManpower()))) >= 0;
            case "marriage_with":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getDiplomacy()
                              .getRoyalMarriage()
                              .stream()
                              .anyMatch(marriage -> (marriage.getFirst().equals(country) && marriage.getSecond().equals(other)
                                                     || marriage.getFirst().equals(other) && marriage.getSecond().equals(country)));
            case "max_manpower":
                return country.getMaxManpower() >= NumbersUtils.toInt(value) * 1000;
            case "mercantilism":
                return country.getMercantilism() >= NumbersUtils.toDouble(value);
            case "meritocracy":
                return country.getMeritocracy() != null && country.getMeritocracy() >= NumbersUtils.toDouble(value);
            case "mil":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getMonarch() != null && country.getMonarch().getMil() != null && country.getMonarch().getMil() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getMonarch() == null || other.getMonarch().getMil() == null
                           || (country.getMonarch() != null
                               && country.getMonarch().getMil() != null
                               && country.getMonarch().getMil() >= other.getMonarch().getMil());
                }
            case "militarised_society":
                return country.getMilitarisedSociety() != null && country.getMilitarisedSociety() >= NumbersUtils.toDouble(value);
            case "mil_power":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getPowers() != null && country.getPowers().get(Power.MIL) != null && country.getPowers().get(Power.MIL) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return other.getPowers() == null
                           || other.getPowers().get(Power.MIL) == null
                           || (country.getPowers() != null
                               && country.getPowers().get(Power.MIL) != null
                               && country.getPowers().get(Power.MIL) >= other.getPowers().get(Power.MIL));
                }
            case "mil_tech":
                return country.getTech().getMil() >= Integer.parseInt(value);
            case "mission_completed":
                return country.getCompletedMissions().stream().anyMatch(mission -> mission.getName().equals(ClausewitzUtils.removeQuotes(value)));
            case "modifier": //Used for scope has_estate_influence_modifier, has_estate_loyalty_modifier
                return true;
            case "monthly_income":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getEstimatedMonthlyIncome() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getEstimatedMonthlyIncome() >= other.getEstimatedMonthlyIncome();
                }
            case "months_of_ruling":
                if (country.getMonarch() == null) {
                    return false;
                }

                return ChronoUnit.MONTHS.between(country.getSave().getDate(),
                                                 country.getHistory().getMonarch(country.getMonarch().getId().getId()).getMonarchDate())
                       >= NumbersUtils.toInt(value);
            case "national_focus":
                return country.getNationalFocus().equals(Power.byName(value));
            case "nation_designer_points":
                return country.getCustomNationPoints() != null && country.getCustomNationPoints() >= NumbersUtils.toDouble(value);
            case "native_policy":
                return country.getNativePolicy() != null && country.getNativePolicy().equals(NumbersUtils.toInt(value));
            case "naval_forcelimit":
                return country.getNavalForceLimit() >= NumbersUtils.toDouble(value);
            case "naval_maintenance":
                return NumbersUtils.doubleOrDefault(country.getNavalMaintenance()) >= NumbersUtils.toDouble(value);
            case "naval_morale":
                return country.getNavalMorale() >= NumbersUtils.toDouble(value);
            case "navy_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNavySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNavySize() >= other.getNavySize();
                }
            case "navy_size_percentage":
                return BigDecimal.valueOf(country.getNavySize()).compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavalForceLimit())))
                       >= 0;
            case "navy_tradition":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getNavyTradition()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getNavyTradition()) >= NumbersUtils.doubleOrDefault(other.getNavyTradition());
                }
            case "nomad_development_scale":
                return "yes".equalsIgnoreCase(value) ==
                       (country.getGovernment() != null &&
                        country.getGovernment().getReforms().stream().anyMatch(reform -> reform.isNomad().getKey()
                                                                                         && (reform.isNomad().getValue() == null
                                                                                             || reform.isNomad().getValue().apply(country, country)))
                        && NumbersUtils.doubleOrDefault(country.getRawDevelopment()) >= country.getSave().getGame().getNomadDevelopmentScale());
            case "normal_or_historical_nations":
                return "yes".equalsIgnoreCase(value) == (NationSetup.NORMAL.equals(country.getSave().getGameplayOptions().getNationSetup())
                                                         || NationSetup.HISTORICAL.equals(country.getSave().getGameplayOptions().getNationSetup()));
            case "normal_province_values":
                return "yes".equalsIgnoreCase(value) == (ProvinceTaxManpower.HISTORICAL.equals(
                        country.getSave().getGameplayOptions().getProvinceTaxManpower()));
            case "num_accepted_cultures":
                return country.getAcceptedCultures().size() >= NumbersUtils.toInt(value);
            case "num_expanded_administration":
                return NumbersUtils.intOrDefault(country.getNumExpandedAdministration()) >= NumbersUtils.toInt(value);
            case "num_of_active_blessings":
                return country.getBlessings().size() >= NumbersUtils.toInt(value);
            case "num_of_admirals":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getLeadersOfType(LeaderType.ADMIRAL).size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getLeadersOfType(LeaderType.ADMIRAL).size() >= other.getLeadersOfType(LeaderType.ADMIRAL).size();
                }
            case "num_of_admirals_with_traits":
                return country.getLeadersOfType(LeaderType.ADMIRAL).stream().filter(leader -> leader.getPersonality() != null).count()
                       >= NumbersUtils.toInt(value);
            case "num_of_age_objectives":
                return NumbersUtils.intOrDefault(country.getNumOfAgeObjectives()) >= NumbersUtils.toInt(value);
            case "num_of_allies":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfAllies() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfAllies() >= other.getNumOfAllies();
                }
            case "num_of_artillery":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbArtillery() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbArtillery() >= other.getNbArtillery();
                }
            case "num_of_aspects":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getChurch() != null && country.getChurch().getAspects().size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);

                    if (country.getChurch() == null) {
                        return false;
                    }

                    if (other.getChurch() == null) {
                        return true;
                    }

                    return country.getChurch().getAspects().size() >= other.getChurch().getAspects().size();
                }
            case "num_of_banners":
                return country.getNbRegimentOfCategory(2) >= NumbersUtils.toInt(value);
            case "num_of_captured_ships_with_boarding_doctrine":
                return country.getNumOfCapturedShipsWithBoardingDoctrine() >= NumbersUtils.toInt(value);
            case "num_of_cardinals":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfCardinals() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfCardinals() >= other.getNumOfCardinals();
                }
            case "num_of_cavalry":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbCavalry() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbCavalry() >= other.getNbCavalry();
                }
            case "num_of_cities":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfCities() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfCities() >= other.getNumOfCities();
                }
            case "num_of_coalition_members":
                return country.getCoalition().size() >= NumbersUtils.toInt(value);
            case "num_of_colonies":
                return country.getOwnedProvinces().stream().filter(province -> province.getColonySize() != null && !province.isCity()).count()
                       >= NumbersUtils.toInt(value);
            case "num_of_colonists":
                return country.getColonists().size() >= NumbersUtils.toInt(value);
            case "num_of_conquistadors":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getLeadersOfType(LeaderType.CONQUISTADOR).size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getLeadersOfType(LeaderType.CONQUISTADOR).size() >= other.getLeadersOfType(LeaderType.CONQUISTADOR).size();
                }
            case "num_of_consorts":
                return country.getNumOfConsorts() >= NumbersUtils.toInt(value);
            case "num_of_continents":
                return country.getContinents().size() >= NumbersUtils.toInt(value);
            case "num_of_cossacks":
                return country.getNbRegimentOfCategory(4) >= NumbersUtils.toInt(value);
            case "num_of_custom_nations":
                return country.getSave().getCountries().values().stream().filter(SaveCountry::isCustom).count() >= NumbersUtils.toInt(value);
            case "num_of_daimyos":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getSubjects()
                                  .stream()
                                  .filter(subject -> Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equals(subject.getSubjectType().getName()))
                                  .count()
                           >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getSubjects()
                                  .stream()
                                  .filter(dependency -> Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equals(dependency.getSubjectType().getName()))
                                  .count() >=
                           other.getSubjects()
                                .stream()
                                .filter(dependency -> Eu4Utils.SUBJECT_TYPE_DAIMYO_VASSAL.equals(dependency.getSubjectType().getName()))
                                .count();
                }
            case "num_of_diplomatic_relations":
                return country.getNumOfRelations() >= NumbersUtils.toInt(value);
            case "num_of_diplomats":
                return country.getDiplomats().size() >= NumbersUtils.toInt(value);
            case "num_of_electors":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getElectors().size() >= NumbersUtils.toInt(value);
            case "num_of_explorers":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getLeadersOfType(LeaderType.EXPLORER).size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getLeadersOfType(LeaderType.EXPLORER).size() >= other.getLeadersOfType(LeaderType.EXPLORER).size();
                }
            case "num_of_foreign_hre_provinces":
                return !country.getSave().getHre().dismantled()
                       && country.getSave()
                                 .getProvinces()
                                 .values()
                                 .stream()
                                 .filter(province -> province.inHre()
                                                     && ((province.getOwner().getOverlord() == null && !province.getOwner().getCapital().inHre())
                                                         || (province.getOwner().getOverlord() != null && !province.getOwner()
                                                                                                                   .getOverlord()
                                                                                                                   .getCapital()
                                                                                                                   .inHre())))
                                 .count() >= NumbersUtils.toInt(value);
            case "num_of_free_diplomatic_relations":
                return country.getNbFreeDiplomaticRelations() >= NumbersUtils.toInt(value);
            case "num_of_galley":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbGalleys() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbGalleys() >= other.getNbGalleys();
                }
            case "num_of_generals":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getLeadersOfType(LeaderType.GENERAL).size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getLeadersOfType(LeaderType.GENERAL).size() >= other.getLeadersOfType(LeaderType.GENERAL).size();
                }
            case "num_of_generals_with_traits":
                return country.getLeadersOfType(LeaderType.GENERAL).stream().filter(leader -> leader.getPersonality() != null).count()
                       >= NumbersUtils.toInt(value);
            case "num_of_harmonized":
                return country.getHarmonizedReligionGroups().size() >= NumbersUtils.toInt(value);
            case "num_of_heavy_ship":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbHeavyShips() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbHeavyShips() >= other.getNbHeavyShips();
                }
            case "num_of_infantry":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbInfantry() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbInfantry() >= other.getNbInfantry();
                }
            case "num_of_large_colonial_nation":
                return country.getNumOfLargeColonies() >= NumbersUtils.toInt(value);
            case "num_of_light_ship":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbLightShips() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbLightShips() >= other.getNbLightShips();
                }
            case "num_of_loans":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getLoans().size() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getLoans().size() >= other.getLoans().size();
                }
            case "num_of_marches":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_MARCH) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_MARCH) >= other.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_MARCH);
                }
            case "num_of_mercenaries":
                return country.getNbRegimentOfCategory(1) >= NumbersUtils.toInt(value);
            case "num_of_merchants":
                return country.getMerchants().size() >= NumbersUtils.toInt(value);
            case "num_of_missionaries":
                return country.getMissionaries().size() >= NumbersUtils.toInt(value);
            case "num_of_owned_and_controlled_institutions":
                return country.getSave()
                              .getInstitutions()
                              .getOrigins()
                              .stream()
                              .filter(province -> country.equals(province.getOwner()) && country.equals(province.getController()))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_ports":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfPorts() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfPorts() >= other.getNumOfPorts();
                }
            case "num_of_ports_blockading": //Todo ??
                break;
            case "num_of_powerful_estates":
                return country.getEstates().stream().filter(estate -> estate.getInfluence() >= 70d).count() >= NumbersUtils.toDouble(value);
            case "num_of_protectorates": //Outdated
                break;
            case "num_of_provinces_in_states":
                return country.getNumOfProvincesInStates() >= NumbersUtils.toInt(value);
            case "num_of_provinces_in_territories":
                return country.getNumOfProvincesInTerritories() >= NumbersUtils.toInt(value);
            case "num_of_rebel_armies":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getSave()
                                  .getRebelFactions()
                                  .values()
                                  .stream()
                                  .filter(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive())
                                                          && country.equals(rebelFaction.getProvince().getOwner()))
                                  .count()
                           >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getSave()
                                  .getRebelFactions()
                                  .values()
                                  .stream()
                                  .filter(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive())
                                                          && country.equals(rebelFaction.getProvince().getOwner()))
                                  .count() >=
                           other.getSave()
                                .getRebelFactions()
                                .values()
                                .stream()
                                .filter(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive())
                                                        && other.equals(rebelFaction.getProvince().getOwner()))
                                .count();
                }
            case "num_of_rebel_controlled_provinces":
            case "num_of_revolts":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinces()
                                  .stream()
                                  .map(SaveProvince::getControllerTag)
                                  .map(ClausewitzUtils::removeQuotes)
                                  .filter("REB"::equals)
                                  .count() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getOwnedProvinces()
                                  .stream()
                                  .map(SaveProvince::getControllerTag)
                                  .map(ClausewitzUtils::removeQuotes)
                                  .filter("REB"::equals)
                                  .count() >=
                           other.getOwnedProvinces()
                                .stream()
                                .map(SaveProvince::getControllerTag)
                                .map(ClausewitzUtils::removeQuotes)
                                .filter("REB"::equals)
                                .count();
                }
            case "num_of_royal_marriages":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfRoyalMarriages() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfRoyalMarriages() >= other.getNumOfRoyalMarriages();
                }
            case "num_of_states":
                return country.getStates().size() >= NumbersUtils.toInt(value);
            case "num_of_streltsy":
                return country.getNbRegimentOfCategory(3) >= NumbersUtils.toInt(value);
            case "num_of_strong_trade_companies":
                return country.getNumOfStrongCompanies() >= NumbersUtils.toInt(value);
            case "num_of_subjects":
                return country.getNumOfSubjects() >= NumbersUtils.toInt(value);
            case "num_of_territories":
                return country.getSave()
                              .getAreas()
                              .values()
                              .stream()
                              .filter(area -> area.getProvinces().stream().anyMatch(province -> country.equals(province.getOwner()))
                                              && !area.getCountriesStates().containsKey(country))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_total_ports":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfTotalPorts() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfTotalPorts() >= other.getNumOfTotalPorts();
                }
            case "num_of_trade_embargos":
                return NumbersUtils.intOrDefault(country.getNumOfTradeEmbargos()) >= NumbersUtils.toInt(value);
            case "num_of_trading_bonuses":
                return country.getTradedBonus().size() >= NumbersUtils.toInt(value);
            case "num_of_transport":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNbTransports() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNbTransports() >= other.getNbTransports();
                }
            case "num_of_trusted_allies":
                return country.getAllies().stream().filter(ally -> ally.getActiveRelation(country).getTrustValue() >= 100).count() >= NumbersUtils.toInt(value);
            case "num_uncontested_cores":
                return NumbersUtils.intOrDefault(country.getNumUncontestedCores()) >= NumbersUtils.toInt(value);
            case "num_of_unions":
            case "personal_union":
                return country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION) >= NumbersUtils.toInt(value);
            case "num_of_unlocked_cults":
                return country.getUnlockedFetishistCults().size() >= NumbersUtils.toInt(value);
            case "num_of_vassals":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_VASSAL) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_VASSAL) >= other.getNumOfSubjectsOfType(Eu4Utils.SUBJECT_TYPE_VASSAL);
                }
            case "num_of_war_reparations":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfWarReparations() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfWarReparations() >= other.getNumOfWarReparations();
                }
            case "num_ships_privateering":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumShipsPrivateering() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumShipsPrivateering() >= other.getNumShipsPrivateering();
                }
            case "occupied_imperial":
                return country.getCapital() != null && country.getCapital().inHre()
                       && country.getOwnedProvinces().stream().filter(province -> province.inHre() && !province.getCores().contains(country)).count()
                          >= NumbersUtils.toInt(value);
            case "offensive_war_with":
                other = country.getSave().getCountry(value);
                return country.getActiveWars().stream().anyMatch(war -> war.getAttackers().containsKey(country) && war.getDefenders().containsKey(other));
            case "overextension_percentage":
                return NumbersUtils.doubleOrDefault(country.getOverextensionPercentage()) >= NumbersUtils.toDouble(value);
            case "overlord_of":
                other = country.getSave().getCountry(value);
                return other.getOverlord().equals(country);
            case "overseas_provinces_percentage":
                BigDecimal bigDecimal = new BigDecimal(value);
                BigDecimal nbProv = BigDecimal.valueOf(country.getOwnedProvinces().size());
                return nbProv.subtract(BigDecimal.valueOf(country.getNumOwnedHomeCores())).divide(nbProv, 3, RoundingMode.HALF_EVEN)
                             .compareTo(bigDecimal) >= 0;
            case "owns":
                return country.equals(country.getSave().getProvince(NumbersUtils.toInt(value)).getOwner());
            case "owns_core_province":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.equals(saveProvince.getOwner()) && saveProvince.getCores().contains(country);
            case "owns_or_non_sovereign_subject_of":
            case "country_or_non_sovereign_subject_holds":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinces().contains(saveProvince) ||
                       country.getSubjects()
                              .stream()
                              .filter(subject -> !subject.getSubjectType().isVoluntary())
                              .anyMatch(subject -> subject.getOwnedProvinces().contains(saveProvince));
            case "owns_or_subject_of":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinces().contains(saveProvince) ||
                       country.getSubjects().stream().anyMatch(subject -> subject.getOwnedProvinces().contains(saveProvince));
            case "papacy_active":
                return country.getSave().getReligions().getReligions().values().stream().anyMatch(rel -> rel.getPapacy() != null);
            case "papal_influence":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getPapalInfluence() != null && country.getPapalInfluence() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getPapalInfluence() == null || other.getPapalInfluence() == null || country.getPapalInfluence() >= other.getPapalInfluence();
                }
            case "patriarch_authority":
                return country.getPatriarchAuthority() >= NumbersUtils.toDouble(value);
            case "percentage_backing_issue":
                if (country.getParliament() == null || country.getParliament().getActiveParliamentIssue() == null) {
                    return true;
                }

                long seats = country.getOwnedProvinces().stream().filter(province -> province.getSeatInParliament() != null).count();
                return BigDecimal.valueOf(country.getOwnedProvinces()
                                                 .stream()
                                                 .filter(province -> province.getSeatInParliament() != null
                                                                     && BooleanUtils.toBoolean(
                                                         province.getSeatInParliament().getBack()))
                                                 .count())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(seats))) >= 0;
            case "personality":
            case "ruler_has_personality":
                return country.getMonarch() == null || country.getMonarch()
                                                              .getPersonalities()
                                                              .getPersonalities()
                                                              .stream()
                                                              .anyMatch(rulerPersonality -> value.equalsIgnoreCase(rulerPersonality.getName()));
            case "piety":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getPiety() == null || country.getPiety() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getPiety() == null || other.getPiety() == null || country.getArmyTradition() >= other.getArmyTradition();
                }
            case "policy": //Used for scope had_active_policy
                return true;
            case "power_projection":
                return NumbersUtils.doubleOrDefault(country.getCurrentPowerProjection()) >= NumbersUtils.toDouble(value);
            case "preferred_emperor":
                other = country.getSave().getCountry(value);
                return country.getPreferredEmperor() == null || other.equals(country.getPreferredEmperor());
            case "prestige":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getPrestige() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getPrestige()) >= NumbersUtils.doubleOrDefault(other.getPrestige());
                }
            case "primary_culture":
                return rawValueToCulture(rawValue, root, from).equalsIgnoreCase(country.getPrimaryCulture().getName());
            case "primitives":
                return "yes".equalsIgnoreCase(value) == (country.getTechnologyGroup() != null && country.getTechnologyGroup().isPrimitive());
            case "production_efficiency":
                return country.getProductionEfficiency() >= NumbersUtils.toDouble(value);
            case "production_income_percentage":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().containsKey(Income.PRODUCTION)
                       && BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.PRODUCTION))
                                    .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))) >= 0;
            case "provinces_on_capital_continent_of":
                other = country.getSave().getCountry(value);
                return country.getOwnedProvinces().stream().anyMatch(province -> Objects.equals(province.getContinent(), other.getCapital().getContinent()));
            case "real_day_of_year":
                return country.getSave().getDate().getDayOfYear() == NumbersUtils.toInt(value);
            case "reform_desire":
                return country.getSave()
                              .getReligions()
                              .getReligions()
                              .values()
                              .stream()
                              .anyMatch(rel -> rel.getPapacy() != null && rel.getPapacy().getReformDesire() >= NumbersUtils.toDouble(value));
            case "religion":
                return country.getReligionName() != null && country.getReligionName().equalsIgnoreCase(rawValueToReligion(rawValue, root, from));
            case "religion_group":
                return country.getReligion() != null
                       && country.getReligion().getReligionGroup().getName().equalsIgnoreCase(rawValueToReligionGroup(rawValue, root, from));
            case "religious_unity":
                return country.getReligiousUnity() >= NumbersUtils.toDouble(value);
            case "republican_tradition":
                return country.getRepublicanTradition() == null || country.getRepublicanTradition() >= NumbersUtils.toDouble(value);
            case "revanchism":
                return NumbersUtils.doubleOrDefault(country.getRecoveryMotivation()) >= NumbersUtils.toDouble(value);
            case "revolt_percentage":
                return BigDecimal.valueOf(country.getOwnedProvinces()
                                                 .stream()
                                                 .filter(province -> "REB".equals(ClausewitzUtils.removeQuotes(province.getControllerTag())))
                                                 .count())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOwnedProvinces().size()))) >= 0;
            case "revolution_target_exists":
                return "yes".equalsIgnoreCase(value) == (country.getSave().getRevolution().getRevolutionTarget() != null);
            case "root_out_corruption":
                return NumbersUtils.doubleOrDefault(country.getRootOutCorruptionSlider()) >= NumbersUtils.toDouble(value);
            case "ruler_age":
                return country.getMonarch() == null
                       || country.getMonarch().getBirthDate().plusYears(NumbersUtils.toInt(value)).isBefore(country.getSave().getDate());
            case "ruler_consort_marriage_length": //Todo
                break;
            case "ruler_culture":
                return country.getMonarch() == null || rawValueToCulture(rawValue, root, from).equalsIgnoreCase(country.getMonarch().getCultureName());
            case "ruler_is_foreigner":
                return "yes".equalsIgnoreCase(value) == (country.getMonarch() == null ||
                                                         (country.getMonarch().getCountry() != null && !country.equals(country.getMonarch().getCountry())));
            case "ruler_religion":
                return country.getMonarch() == null && rawValueToReligion(rawValue, root, from).equalsIgnoreCase(country.getMonarch().getReligion().getName());
            case "sailors":
                return NumbersUtils.doubleOrDefault(country.getSailors()) >= NumbersUtils.toDouble(value);
            case "sailors_percentage":
                return BigDecimal.valueOf(country.getSailors())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getMaxSailors()))) >= 0;
            case "max_sailors":
                return NumbersUtils.doubleOrDefault(country.getMaxSailors()) >= NumbersUtils.toDouble(value);
            case "secondary_religion":
                return country.getSecondaryReligion() == null || rawValueToReligion(rawValue, root, from).equalsIgnoreCase(
                        country.getSecondaryReligion().getName());
            case "senior_union_with":
                other = country.getSave().getCountry(value);
                return other.getOverlord() != null && other.getOverlord().equals(country)
                       && other.getSubjectType().getName().equalsIgnoreCase(Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION);
            case "splendor":
                return NumbersUtils.intOrDefault(country.getSplendor()) >= NumbersUtils.toInt(value);
            case "stability":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getStability() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.intOrDefault(country.getStability()) >= NumbersUtils.intOrDefault(other.getStability());
                }
            case "start_date":
                return country.getSave().getStartDate().equals(Eu4Utils.stringToDate(value));
            case "started_in":
                return country.getSave().getStartDate().equals(Eu4Utils.stringToDate(value))
                       || country.getSave().getStartDate().isAfter(Eu4Utils.stringToDate(value));
            case "statists_vs_orangists":
                return country.getStatistsVsMonarchists() == null
                       || NumbersUtils.doubleOrDefault(country.getStatistsVsMonarchists()) >= NumbersUtils.toDouble(value);
            case "subsidised_percent_amount":
                return BigDecimal.valueOf(country.getSave()
                                                 .getDiplomacy()
                                                 .getSubsidies()
                                                 .stream()
                                                 .filter(subsidies -> country.equals(subsidies.getSecond()))
                                                 .mapToDouble(QuantifyDatableRelation::getAmount)
                                                 .sum())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getEstimatedMonthlyIncome()))) >= 0;
            case "succession_claim":
                other = country.getSave().getCountry(value);
                return BooleanUtils.toBoolean(country.getActiveRelations().get(other.getTag()).hasSuccessionClaim());
            case "tag":
                return value.equalsIgnoreCase(country.getTag());
            case "target": //Used for scope has_casus_belli
                return true;
            case "tariff_value":
                return country.getTariff() != null && country.getTariff() >= NumbersUtils.toDouble(value);
            case "tax_income_percentage":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().containsKey(Income.TAXES)
                       && BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.TAXES))
                                    .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))) >= 0;
            case "technology_group":
                if ((other = country.getSave().getCountry(value)) != null) {
                    return (country.getTechnologyGroup() != null && other.getTechnologyGroup() != null
                            && country.getTechnologyGroup().equals(other.getTechnologyGroup()));
                } else {
                    return country.getTechnologyGroup() != null && value.equalsIgnoreCase(country.getTechnologyGroup().getName());
                }
            case "tech_difference":
                return root.getTech().getTotal() - NumbersUtils.toInt(value) >= from.getTech().getTotal();
            case "territory": //Used for scope estate_territory
                return true;
            case "tolerance_to_this":
                return country.getToleranceOwn() >= NumbersUtils.toDouble(value);
            case "total_base_tax":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getBaseTax() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getBaseTax() >= other.getBaseTax();
                }
            case "total_development":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getRawDevelopment()) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getRawDevelopment()) >= NumbersUtils.doubleOrDefault(other.getRawDevelopment());
                }
            case "total_number_of_cardinals":
                return country.getSave()
                              .getReligions()
                              .getReligions()
                              .values()
                              .stream()
                              .anyMatch(rel -> rel.getPapacy() != null && rel.getPapacy().getCardinals().size() >= NumbersUtils.toInt(value));
            case "trade_league_embargoed_by":
                other = country.getSave().getCountry(value);
                Optional<TradeLeague> tradeLeague = country.getSave()
                                                           .getTradeLeagues()
                                                           .stream()
                                                           .filter(league -> league.getMembers().contains(country))
                                                           .findFirst();

                return tradeLeague.isPresent() && tradeLeague.get().getMembers().stream().anyMatch(member -> member.getTradeEmbargoedBy().contains(other));
            case "trade_efficiency":
                return country.getTradeEfficiency() >= NumbersUtils.toDouble(value);
            case "trade_embargoing":
                other = country.getSave().getCountry(value);
                return other.getTradeEmbargoedBy().contains(country);
            case "trade_embargo_by":
                other = country.getSave().getCountry(value);
                return country.getTradeEmbargoedBy().contains(other);
            case "trade_income_percentage":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().containsKey(Income.TRADE)
                       && BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.TRADE))
                                    .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))) >= 0;
            case "transport_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize())).compareTo(BigDecimal.valueOf(country.getNbTransports())) >= 0;
            case "treasury":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getTreasury() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getTreasury()) >= NumbersUtils.doubleOrDefault(other.getTreasury());
                }
            case "tribal_allegiance":
                return country.getTribalAllegiance() == null || country.getTribalAllegiance() >= NumbersUtils.toDouble(value);
            case "type": //Used for scope has_casus_belli
                return true;
            case "unit_type":
                return value.equalsIgnoreCase(country.getUnitType());
            case "uses_authority":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().isUseAuthority()));
            case "uses_church_aspects":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usesChurchPower()));
            case "uses_blessings":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && country.getReligion().getGameReligion().getBlessings() != null);
            case "uses_cults":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().useFetishistCult()));
            case "uses_devotion":
                return "yes".equalsIgnoreCase(value) == (country.getGovernment() != null &&
                                                         country.getGovernment()
                                                                .getReforms()
                                                                .stream()
                                                                .anyMatch(reform -> reform.isHasDevotion().getKey()
                                                                                    && (reform.isHasDevotion().getValue() == null
                                                                                        || reform.isHasDevotion().getValue().apply(country, country))));
            case "uses_doom":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().useDoom()));
            case "uses_fervor":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().useFervor()));
            case "uses_harmony":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usesHarmony()));
            case "uses_isolationism":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usesIsolationism()));
            case "uses_karma":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usesKarma()));
            case "uses_papacy":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().getPapacy() != null));
            case "uses_patriarch_authority":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().hasPatriarchs()));
            case "uses_personal_deities":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usePersonalDeity()));
            case "uses_piety":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().usesPiety()));
            case "uses_religious_icons":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && country.getReligion().getGameReligion().getIcons() != null);
            case "uses_syncretic_faiths":
                return "yes".equalsIgnoreCase(value) == (country.getReligion() != null && BooleanUtils.toBoolean(
                        country.getReligion().getGameReligion().canHaveSecondaryReligion()));
            case "value": //Special case, used for scopes to count
                return true;
            case "vassal_of":
                other = country.getSave().getCountry(value);
                return other.equals(country.getOverlord()) && Eu4Utils.SUBJECT_TYPE_VASSAL.equals(country.getSubjectType().getName());
            case "war_exhaustion":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getWarExhaustion()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getWarExhaustion()) >= NumbersUtils.doubleOrDefault(other.getWarExhaustion());
                }
            case "war_score":
                return country.getActiveWars()
                              .stream()
                              .anyMatch(war -> (war.getDefenderScore() >= NumbersUtils.toDouble(value) && war.getDefenders().containsKey(country))
                                               || (war.getDefenderScore() <= -NumbersUtils.toDouble(value) && war.getAttackers().containsKey(country)));
            case "war_with":
                other = country.getSave().getCountry(value);
                return country.getActiveWars()
                              .stream()
                              .anyMatch(war -> war.getOtherSide(country).containsKey(other.getTag()));
            case "was_player":
                return "yes".equalsIgnoreCase(value) == country.wasPlayer();
            case "was_tag":
                return country.getPreviousCountryTags().contains(ClausewitzUtils.addQuotes(value));
            case "will_back_next_reform": //Todo
                break;
            case "yearly_corruption_increase":
                return country.getYearlyCorruption() >= NumbersUtils.toDouble(value);
            case "years_of_income":
                if (NumbersUtils.toDouble(value) != null) {
                    return BigDecimal.valueOf(country.getTreasury())
                                     .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getEstimatedMonthlyIncome()))
                                                                     .multiply(BigDecimal.valueOf(12))) >= 0;
                } else {
                    other = country.getSave().getCountry(value);
                    return BigDecimal.valueOf(country.getTreasury())
                                     .compareTo(BigDecimal.valueOf(other.getEstimatedMonthlyIncome()).multiply(BigDecimal.valueOf(12))) >= 0;
                }
        }

        LOGGER.debug("Don't know how to manage country condition: {} = {}", condition, value);
        return false;
    }

    public static boolean applyConditionToCountry(Country country, Country root, Country from, String condition, String rawValue) {
        Country other;
        Province province;
        Integer integer;
        String value;
        Religion religion;
        Government government;
        CountryHistoryItemI historyItem;
        CountryHistoryItemI otherHistoryItem;
        ConditionAnd scriptedTrigger;
        TradeGood tradeGood;

        if ("ROOT".equals(rawValue)) {
            value = root.getTag().toUpperCase();
        } else if ("FROM".equals(rawValue)) {
            value = from.getTag().toUpperCase();
        } else {
            value = rawValue;
        }

        if (country.getGame().getBuilding(condition) != null) {
            return country.getOwnedProvinceAt(country.getGame().getStartDate())
                          .filter(i -> i.getCumulatedBuildings().stream().anyMatch(b -> b.getName().equals(value)))
                          .count() >= NumbersUtils.toInt(value);
        }

        if ((tradeGood = country.getGame().getTradeGood(condition)) != null) {
            return country.getOwnedProvinceAt(country.getGame().getStartDate())
                          .filter(i -> tradeGood.equals(i.getTradeGoods()))
                          .count() >= NumbersUtils.toInt(value);
        }

        if ((scriptedTrigger = country.getGame().getScriptedTrigger(condition)) != null) {
            return scriptedTrigger.apply(country, from);
        }

        switch (condition.toLowerCase()) {
            case "always":
                return "yes".equalsIgnoreCase(value);
            case "can_create_vassals":
                return country.getOverlordAt(country.getGame().getStartDate()) == null;
            case "can_migrate":
                return (government = country.getHistoryItemAt(country.getGame().getStartDate()).getGovernment()) != null
                       && government.getBasicGovernmentReform().isAllowMigration().getKey();
            case "capital":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getCapital() != null
                       && historyItem.getCapital().getId() == NumbersUtils.toInt(value);
            case "consort_adm":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && historyItem.getQueen().getAdm() >= NumbersUtils.toInt(value);
            case "consort_age":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && historyItem.getQueen().getBirthDate().plusYears(NumbersUtils.toInt(value)).isBefore(country.getGame().getStartDate());
            case "consort_culture":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && rawValueToCulture(rawValue, root, from).equals(historyItem.getQueen().getCultureName());
            case "consort_dip":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && historyItem.getQueen().getDip() >= NumbersUtils.toInt(value);
            case "consort_has_personality":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && historyItem.getQueen().getPersonalities() != null
                       && historyItem.getQueen().getPersonalities().getPersonalities().stream().anyMatch(p -> value.equalsIgnoreCase(p.getName()));
            case "consort_mil":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && historyItem.getQueen().getMil() >= NumbersUtils.toInt(value);
            case "consort_religion":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                       && rawValueToReligion(rawValue, root, from).equals(historyItem.getQueen().getReligionName());
            case "controls":
                return country.getGame()
                              .getProvince(NumbersUtils.toInt(value))
                              .getHistoryItemAt(country.getGame().getStartDate())
                              .getController()
                              .equals(country);
            case "core_claim":
                break;//Todo
            case "core_percentage":
                break; //Todo
            case "culture_group":
                return rawValueToCultureGroup(rawValue, root, from)
                        .equals(Optional.ofNullable(country.getHistoryItemAt(country.getGame().getStartDate()).getPrimaryCulture())
                                        .map(Culture::getCultureGroup)
                                        .map(CultureGroup::getName)
                                        .orElse(null));
            case "culture_group_claim":
                break;//Todo
            case "current_age":
                return country.getGame()
                              .getAges()
                              .stream()
                              .filter(age -> age.getStart() <= country.getGame().getStartDate().getYear())
                              .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Age::getStart))))
                              .last()
                              .getName()
                              .equals(value);
            case "custom_nation_setup":
                return "yes".equalsIgnoreCase(value);
            case "days": //Used for scope had_active_policy
                return true;
            case "defensive_war_with":
                break; //Todo
            case "development_of_overlord_fraction":
                break;//Todo
            case "dip":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return historyItem.getMonarch() != null && historyItem.getMonarch().getDip() != null && historyItem.getMonarch().getDip() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                    return otherHistoryItem.getMonarch() == null || otherHistoryItem.getMonarch().getDip() == null
                           || (historyItem.getMonarch() != null
                               && historyItem.getMonarch().getDip() != null
                               && historyItem.getMonarch().getDip() >= otherHistoryItem.getMonarch().getDip());
                }
            case "disaster": //Used for scope has_disaster_progress
                return true;
            case "dominant_culture":
                break;//Todo
            case "dominant_religion":
                break;//Todo
            case "dynasty":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                if (Eu4Utils.isTag(value)) {
                    other = country.getGame().getCountry(value);
                    otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                    return otherHistoryItem.getMonarch() == null || otherHistoryItem.getMonarch().getDynasty() == null
                           || (historyItem.getMonarch() != null
                               && historyItem.getMonarch().getDynasty() != null
                               && historyItem.getMonarch().getDynasty().equals(otherHistoryItem.getMonarch().getDynasty()));
                } else {
                    return historyItem.getMonarch() != null && historyItem.getMonarch().getDynasty() != null &&
                           ClausewitzUtils.removeQuotes(value).equals(ClausewitzUtils.removeQuotes(historyItem.getMonarch().getDynasty()));
                }
            case "estate": //Used for scope estate_influence, estate_loyalty, estate_territory
                return true;
            case "exists":
                if ("yes".equalsIgnoreCase(value)) {
                    return true;
                } else {
                    return country.getGame().getCountry(value) != null;
                }
            case "faction": //Used for scope faction_influence
                return true;
            case "flag": //Used for scope had_consort_flag, had_country_flag, had_global_flag, had_heir_flag, had_province_flag, had_ruler_flag
                return true;
            case "government":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null
                       && ClausewitzUtils.removeQuotes(historyItem.getGovernment().getName()).equals(ClausewitzUtils.removeQuotes(value));
            case "government_rank":
                return country.getHistoryItemAt(country.getGame().getStartDate()).getGovernmentLevel() >= NumbersUtils.toInt(value);
            case "guaranteed_by":
                break; //Todo
            case "has_border_with_religious_enemy":
                break; //Todo
            case "has_consort":
                return country.getHistoryItemAt(country.getGame().getStartDate()).getQueen() != null;
            case "has_consort_regency":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                                                         && BooleanUtils.toBoolean(historyItem.getQueen().getRegent()));
            case "has_country_flag":
                return country.getHistoryItemAt(country.getGame().getStartDate()).getCumulatedAcceptedCultures().contains(value);
            case "has_female_consort":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                                                         && BooleanUtils.toBoolean(historyItem.getQueen().getFemale()));
            case "has_female_heir":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                                                         && BooleanUtils.toBoolean(historyItem.getHeir().getFemale()));
            case "has_first_revolution_started":
                return !"yes".equalsIgnoreCase(value);
            case "has_foreign_consort":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null
                                                         && !country.getTag().equalsIgnoreCase(historyItem.getQueen().getCountryOfOrigin()));
            case "has_game_started":
                return !"yes".equalsIgnoreCase(value);
            case "has_government_ability":
                break;//Todo
            case "has_given_consort_to":
                other = country.getGame().getCountry(value);
                otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                return "yes".equalsIgnoreCase(value) == (otherHistoryItem.getQueen() != null &&
                                                         country.getTag().equals(otherHistoryItem.getQueen().getCountryOfOrigin()));
            case "has_guaranteed":
                break; //Todo
            case "has_government_attribute":
                break;//Todo
            case "has_government_mechanic":
                break;//Todo
            case "has_heir":
                return "yes".equalsIgnoreCase(value) == (country.getHistoryItemAt(country.getGame().getStartDate()).getHeir() != null);
            case "has_matching_religion":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                if (Eu4Utils.isTag(value)) {
                    other = country.getGame().getCountry(value);
                    otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                    return otherHistoryItem.getReligion() != null && otherHistoryItem.getReligion().equals(historyItem.getReligion());
                } else {
                    return historyItem.getReligion() != null && historyItem.getReligion().getName().equals(value);
                }
            case "has_meritocracy":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                return "yes".equalsIgnoreCase(value) == (historyItem.getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isHasMeritocracy().getKey());
            case "has_militarised_society":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                return "yes".equalsIgnoreCase(value) == (historyItem.getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isMilitarisedSociety().getKey());
            case "has_parliament":
                return !"yes".equalsIgnoreCase(value); //Todo
            case "has_patriarchs":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                return "yes".equalsIgnoreCase(value) == (historyItem.getReligion() != null
                                                         && BooleanUtils.toBoolean(historyItem.getReligion().hasPatriarchs()));
            case "has_regency":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null
                       && BooleanUtils.toBoolean(historyItem.getMonarch().getRegent());
            case "has_reform":
                return country.getHistoryItemAt(country.getGame().getStartDate())
                              .getAddGovernmentReform()
                              .stream()
                              .anyMatch(reform -> value.equalsIgnoreCase(reform.getName()));
            case "has_ruler":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null
                       && historyItem.getMonarch().getName().equals(value);
            case "has_unified_culture_group":
                break; //Todo
            case "heir_adm":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null &&
                       NumbersUtils.intOrDefault(historyItem.getHeir().getAdm()) >= NumbersUtils.toInt(value);
            case "heir_age":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null &&
                       ChronoUnit.YEARS.between(historyItem.getHeir().getBirthDate(), country.getGame().getStartDate()) >= NumbersUtils.toInt(value);
            case "heir_dip":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && NumbersUtils.intOrDefault(historyItem.getHeir().getDip()) >= NumbersUtils.toInt(value);
            case "heir_claim":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && NumbersUtils.doubleOrDefault(historyItem.getHeir().getClaim()) >= NumbersUtils.toInt(value);
            case "heir_culture":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && rawValueToCulture(rawValue, root, from).equals(historyItem.getHeir().getCultureName());
            case "heir_has_consort_dynasty":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && historyItem.getQueen() != null && historyItem.getHeir().getDynasty().equals(historyItem.getQueen().getDynasty());
            case "heir_has_personality":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && historyItem.getHeir()
                                     .getPersonalities()
                                     .getPersonalities()
                                     .stream()
                                     .anyMatch(rulerPersonality -> value.equalsIgnoreCase(rulerPersonality.getName()));
            case "heir_has_ruler_dynasty":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && historyItem.getMonarch() != null && historyItem.getHeir().getDynasty().equals(historyItem.getMonarch().getDynasty());
            case "heir_mil":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && NumbersUtils.intOrDefault(historyItem.getHeir().getMil()) >= NumbersUtils.toInt(value);
            case "heir_religion":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                       && historyItem.getHeir().getReligion().getName().equals(rawValueToReligion(rawValue, root, from));
            case "historical_friend_with":
                other = country.getGame().getCountry(value);
                return country.getHistoryItemAt(country.getGame().getStartDate()).getHistoricalFriends().contains(other);
            case "historical_rival_with":
                other = country.getGame().getCountry(value);
                return country.getHistoryItemAt(country.getGame().getStartDate()).getHistoricalEnemies().contains(other);
            case "hre_size":
                break; //Todo
            case "incident": //Used for scope incident_variable_value
                return true;
            case "influence": //Used for scope faction_influence
                return true;
            case "ironman":
                return false;
            case "is_at_war":
                return "yes".equalsIgnoreCase(value) == country.getWarsAt(country.getGame().getStartDate()).findAny().isPresent();
            case "is_client_nation":
                return "yes".equalsIgnoreCase(value) == Country.CLIENT_STATE_PATTERN.matcher(country.getTag()).matches();
            case "is_colonial_nation":
                return "yes".equalsIgnoreCase(value) == Country.COLONY_PATTERN.matcher(country.getTag()).matches();
            case "is_core":
                return country.getGame()
                              .getProvince(NumbersUtils.toInt(value))
                              .getHistoryItemAt(country.getGame().getStartDate())
                              .getCumulatedCores()
                              .contains(country);
            case "is_elector":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(country.getHistoryItemAt(country.getGame().getStartDate()).getElector());
            case "is_emperor":
                return "yes".equalsIgnoreCase(value) == (country.getGame().getHreEmperorAt(country.getGame().getStartDate()) != null
                                                         && country.getTag().equalsIgnoreCase(country.getGame()
                                                                                                     .getHreEmperorAt(country.getGame().getStartDate())
                                                                                                     .getTag()));
            case "is_emperor_of_china":
                return "yes".equalsIgnoreCase(value) == (country.getGame().getCelestialEmperorAt(country.getGame().getStartDate()) != null
                                                         && country.getTag().equalsIgnoreCase(country.getGame()
                                                                                                     .getCelestialEmperorAt(country.getGame().getStartDate())
                                                                                                     .getTag()));
            case "is_female":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null
                                                         && BooleanUtils.toBoolean(historyItem.getMonarch().getFemale()));
            case "is_heir_leader":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getHeir() != null
                                                         && (historyItem.getHeir().getLeader() != null));
            case "is_institution_enabled":
                return Optional.ofNullable(country.getGame().getInstitution(value))
                               .map(Institution::getHistoricalStartDate)
                               .map(date -> date.isBefore(country.getGame().getStartDate()))
                               .orElse(false)
                       || Optional.ofNullable(country.getGame().getInstitution(value))
                                  .map(Institution::getHistoricalStartDate)
                                  .map(date -> date.equals(country.getGame().getStartDate()))
                                  .orElse(false);
            case "is_lesser_in_union":
                return "yes".equalsIgnoreCase(value) == (country.getOverlordAt(country.getGame().getStartDate()) != null &&
                                                         Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equalsIgnoreCase(
                                                                 country.getSubjectTypeAt(country.getGame().getStartDate())));
            case "is_monarch_leader":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null
                                                         && historyItem.getMonarch().getLeader() != null);
            case "is_march":
                return "yes".equalsIgnoreCase(value) == (country.getOverlordAt(country.getGame().getStartDate()) != null
                                                         && Eu4Utils.SUBJECT_TYPE_MARCH.equals(country.getSubjectTypeAt(country.getGame().getStartDate())));
            case "is_neighbor_of":
                break; //Todo
            case "is_nomad":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null
                                                         && historyItem.getGovernment().getBasicGovernmentReform().isNomad().getKey());
            case "is_origin_of_consort":
                other = country.getGame().getCountry(value);
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getQueen() != null &&
                       other.getTag().equals(historyItem.getQueen().getCountryOfOrigin());
            case "is_part_of_hre":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getCapital() != null
                                                         && BooleanUtils.toBoolean(historyItem.getCapital()
                                                                                              .getHistoryItemAt(country.getGame().getStartDate()).getHre()));
            case "is_playing_custom_nation":
                return "yes".equalsIgnoreCase(value) == Country.CUSTOM_COUNTRY_PATTERN.matcher(country.getTag()).matches();
            case "is_religion_enabled":
                religion = country.getGame().getReligion(rawValueToReligion(rawValue, root, from));
                return religion != null && (religion.getDate() == null || (country.getGame().getStartDate().isAfter(religion.getDate())));
            case "is_republic":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isRepublic().getKey());
            case "is_revolutionary":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isRevolutionary().getKey());
            case "is_subject":
                return "yes".equalsIgnoreCase(value) == (country.getOverlordAt(country.getGame().getStartDate()) != null);
            case "is_subject_of":
                other = country.getGame().getCountry(value);
                return country.getOverlordAt(country.getGame().getStartDate()) != null && country.getOverlordAt(country.getGame().getStartDate()).equals(other);
            case "is_subject_of_type":
                return country.getOverlordAt(country.getGame().getStartDate()) != null
                       && value.equalsIgnoreCase(country.getSubjectTypeAt(country.getGame().getStartDate()));
            case "is_subject_other_than_tributary_trigger":
                return "yes".equalsIgnoreCase(value) == (country.getSubjectTypeAt(country.getGame().getStartDate()) != null
                                                         && !country.getGame()
                                                                    .getSubjectType(country.getSubjectTypeAt(country.getGame().getStartDate()))
                                                                    .isVoluntary());
            case "is_tribal":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isTribal().getKey());
            case "is_tributary":
                return "yes".equalsIgnoreCase(value) == (country.getOverlordAt(country.getGame().getStartDate()) != null
                                                         && country.getGame()
                                                                   .getSubjectType(country.getSubjectTypeAt(country.getGame().getStartDate()))
                                                                   .isVoluntary());
            case "is_vassal":
                return "yes".equalsIgnoreCase(value) == (country.getOverlordAt(country.getGame().getStartDate()) != null
                                                         && Eu4Utils.SUBJECT_TYPE_VASSAL.equalsIgnoreCase(country.getSubjectTypeAt(country.getGame()
                                                                                                                                          .getStartDate())));
            case "is_year":
                return country.getGame().getStartDate().getYear() == NumbersUtils.toInt(value);
            case "junior_union_with":
                other = country.getGame().getCountry(value);
                return other.equals(country.getOverlordAt(country.getGame().getStartDate()))
                       && Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equals(country.getSubjectTypeAt(country.getGame().getStartDate()));
            case "marriage_with":
                other = country.getGame().getCountry(value);
                return country.getGame()
                              .getRoyalMarriage()
                              .stream()
                              .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) || r.getStartDate()
                                                                                                         .isBefore(country.getGame().getStartDate()))
                              .filter(r -> r.getEndDate().equals(country.getGame().getStartDate()) || r.getEndDate().isBefore(country.getGame().getStartDate()))
                              .anyMatch(marriage -> (marriage.getFirst().equals(country) && marriage.getSecond().equals(other)
                                                     || marriage.getFirst().equals(other) && marriage.getSecond().equals(country)));
            case "mil":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null
                           && historyItem.getMonarch().getMil() != null && historyItem.getMonarch().getMil() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                    historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                    return otherHistoryItem.getMonarch() == null || otherHistoryItem.getMonarch().getMil() == null
                           || (historyItem.getMonarch() != null
                               && historyItem.getMonarch().getMil() != null
                               && historyItem.getMonarch().getMil() >= otherHistoryItem.getMonarch().getMil());
                }
            case "modifier": //Used for scope has_estate_influence_modifier, has_estate_loyalty_modifier
                return true;
            case "national_focus":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getNationalFocus() != null
                       && historyItem.getNationalFocus().equals(Power.byName(value));
            case "num_of_cities":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).count() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).count() >=
                           other.getOwnedProvinceAt(other.getGame().getStartDate()).count();
                }
            case "num_of_continents":
                return country.getOwnedProvinceAt(country.getGame().getStartDate())
                              .map(ProvinceHistoryItemI::getProvince)
                              .map(Province::getContinent)
                              .distinct()
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_electors":
                return country.getGame().getHreEmperorAt(country.getGame().getStartDate()) != null &&
                       country.getGame()
                              .getCountries()
                              .stream()
                              .map(c -> c.getHistoryItemAt(country.getGame().getStartDate()))
                              .filter(i -> BooleanUtils.toBoolean(i.getElector()))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_foreign_hre_provinces":
                break; //Too complexe and very slow
            case "num_of_marches":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getGame()
                                  .getSubjectTypeRelations()
                                  .get(Eu4Utils.SUBJECT_TYPE_MARCH)
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate())
                                               || r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getGame()
                                  .getSubjectTypeRelations()
                                  .get(Eu4Utils.SUBJECT_TYPE_MARCH)
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate()) ||
                                               r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >=
                           other.getGame()
                                .getRoyalMarriage()
                                .stream()
                                .filter(r -> r.getStartDate().equals(other.getGame().getStartDate()) ||
                                             r.getStartDate().isBefore(other.getGame().getStartDate()))
                                .filter(r -> r.getEndDate().equals(other.getGame().getStartDate()) ||
                                             r.getEndDate().isBefore(other.getGame().getStartDate()))
                                .filter(marriage -> (marriage.getFirst().equals(other) || marriage.getSecond().equals(other)))
                                .count();
                }
            case "num_of_owned_and_controlled_institutions": //Fixme tech group seems to give institutions at start
                return country.getGame()
                              .getInstitutions()
                              .stream()
                              .filter(i -> i.getHistoricalStartProvince() != null)
                              .filter(i -> i.getHistoricalStartDate() == null || i.getHistoricalStartDate().isBefore(country.getGame().getStartDate()) ||
                                           country.getGame().getStartDate().equals(i.getHistoricalStartDate()))
                              .filter(i -> country.equals(country.getGame()
                                                                 .getProvince(i.getHistoricalStartProvince())
                                                                 .getHistoryItemAt(country.getGame().getStartDate())
                                                                 .getOwner()) &&
                                           country.equals(country.getGame()
                                                                 .getProvince(i.getHistoricalStartProvince())
                                                                 .getHistoryItemAt(country.getGame().getStartDate())
                                                                 .getController()))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_ports":
                //Fixme only connected to capital
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count()
                           >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count()
                           >=
                           other.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count();
                }
            case "num_of_rebel_controlled_provinces":
            case "num_of_revolts":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate())
                                  .map(ProvinceHistoryItemI::getController)
                                  .map(Country::getTag)
                                  .filter("REB"::equals)
                                  .count() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate())
                                  .map(ProvinceHistoryItemI::getController)
                                  .map(Country::getTag)
                                  .filter("REB"::equals)
                                  .count() >=
                           other.getOwnedProvinceAt(other.getGame().getStartDate())
                                .map(ProvinceHistoryItemI::getController)
                                .map(Country::getTag)
                                .filter("REB"::equals)
                                .count();
                }
            case "num_of_royal_marriages":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getGame()
                                  .getRoyalMarriage()
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate())
                                               || r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getGame()
                                  .getRoyalMarriage()
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate()) ||
                                               r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >=
                           other.getGame()
                                .getRoyalMarriage()
                                .stream()
                                .filter(r -> r.getStartDate().equals(other.getGame().getStartDate()) ||
                                             r.getStartDate().isBefore(other.getGame().getStartDate()))
                                .filter(r -> r.getEndDate().equals(other.getGame().getStartDate()) ||
                                             r.getEndDate().isBefore(other.getGame().getStartDate()))
                                .filter(marriage -> (marriage.getFirst().equals(other) || marriage.getSecond().equals(other)))
                                .count();
                }
            case "num_of_subjects":
                return country.getGame()
                              .getSubjectTypeRelations()
                              .values()
                              .stream()
                              .flatMap(Collection::stream)
                              .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                           r.getStartDate().isBefore(country.getGame().getStartDate()))
                              .filter(r -> r.getEndDate().equals(country.getGame().getStartDate())
                                           || r.getEndDate().isBefore(country.getGame().getStartDate()))
                              .filter(r -> (r.getFirst().equals(country) || r.getSecond().equals(country)))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_total_ports":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count()
                           >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count()
                           >=
                           other.getOwnedProvinceAt(country.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(Province::isPort).count();
                }
            case "num_uncontested_cores":
                return country.getCoresProvinceAt(country.getGame().getStartDate()).filter(p -> !country.equals(p.getController())).count() >=
                       NumbersUtils.toInt(value);
            case "num_of_unions":
            case "personal_union":
                return country.getGame()
                              .getSubjectTypeRelations()
                              .get(Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION)
                              .stream()
                              .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                           r.getStartDate().isBefore(country.getGame().getStartDate()))
                              .filter(r -> r.getEndDate().equals(country.getGame().getStartDate()) ||
                                           r.getEndDate().isBefore(country.getGame().getStartDate()))
                              .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_vassals":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getGame()
                                  .getSubjectTypeRelations()
                                  .get(Eu4Utils.SUBJECT_TYPE_VASSAL)
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate())
                                               || r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getGame()
                                  .getSubjectTypeRelations()
                                  .get(Eu4Utils.SUBJECT_TYPE_VASSAL)
                                  .stream()
                                  .filter(r -> r.getStartDate().equals(country.getGame().getStartDate()) ||
                                               r.getStartDate().isBefore(country.getGame().getStartDate()))
                                  .filter(r -> r.getEndDate().equals(country.getGame().getStartDate()) ||
                                               r.getEndDate().isBefore(country.getGame().getStartDate()))
                                  .filter(marriage -> (marriage.getFirst().equals(country) || marriage.getSecond().equals(country)))
                                  .count() >=
                           other.getGame()
                                .getRoyalMarriage()
                                .stream()
                                .filter(r -> r.getStartDate().equals(other.getGame().getStartDate()) ||
                                             r.getStartDate().isBefore(other.getGame().getStartDate()))
                                .filter(r -> r.getEndDate().equals(other.getGame().getStartDate()) ||
                                             r.getEndDate().isBefore(other.getGame().getStartDate()))
                                .filter(marriage -> (marriage.getFirst().equals(other) || marriage.getSecond().equals(other)))
                                .count();
                }
            case "offensive_war_with":
                other = country.getGame().getCountry(value);
                return country.getWarsAt(country.getGame().getStartDate())
                              .anyMatch(war -> war.getAttackersAt(country.getGame().getStartDate()).contains(country.getTag())
                                               && war.getDefendersAt(country.getGame().getStartDate()).contains(other.getTag()));
            case "overlord_of":
                other = country.getGame().getCountry(value);
                return other.getOverlordAt(country.getGame().getStartDate()).equals(country);
            case "overseas_provinces_percentage":
                break; //Todo
            case "owns":
                return country.equals(country.getGame().getProvince(NumbersUtils.toInt(value)).getHistoryItemAt(country.getGame().getStartDate()).getOwner());
            case "owns_core_province":
                province = country.getGame().getProvince(NumbersUtils.toInt(value));
                return country.equals(province.getHistoryItemAt(country.getGame().getStartDate()).getOwner())
                       && province.getHistoryItemAt(country.getGame().getStartDate()).getCumulatedCores().contains(country);
            case "owns_or_non_sovereign_subject_of":
            case "country_or_non_sovereign_subject_holds":
                province = country.getGame().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinceAt(country.getGame().getStartDate()).anyMatch(i -> i.getProvince().equals(province)) ||
                       country.getGame()
                              .getCountries()
                              .stream()
                              .filter(c -> country.equals(c.getOverlordAt(country.getGame().getStartDate())))
                              .filter(subject -> !country.getGame().getSubjectType(subject.getSubjectTypeAt(country.getGame().getStartDate())).isVoluntary())
                              .anyMatch(
                                      subject -> subject.getOwnedProvinceAt(country.getGame().getStartDate()).anyMatch(i -> province.equals(i.getProvince())));
            case "owns_or_subject_of":
                province = country.getGame().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinceAt(country.getGame().getStartDate()).anyMatch(i -> i.getProvince().equals(province)) ||
                       country.getGame()
                              .getCountries()
                              .stream()
                              .filter(c -> country.equals(c.getOverlordAt(country.getGame().getStartDate())))
                              .anyMatch(subject -> subject.equals(province.getHistoryItemAt(country.getGame().getStartDate()).getOwner()));
            case "personality":
            case "ruler_has_personality":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null &&
                       historyItem.getMonarch().getPersonalities().getPersonalities().stream().anyMatch(p -> value.equalsIgnoreCase(p.getName()));
            case "policy": //Used for scope had_active_policy
                return true;
            case "primary_culture":
                return rawValueToCulture(rawValue, root, from).equalsIgnoreCase(
                        Optional.ofNullable(country.getHistoryItemAt(country.getGame().getStartDate()).getPrimaryCulture()).map(Culture::getName).orElse(null));
            case "primitives":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getTechnologyGroup() != null
                                                         && historyItem.getTechnologyGroup().isPrimitive());
            case "provinces_on_capital_continent_of":
                other = country.getGame().getCountry(value);
                ProvinceList continent = other.getHistoryItemAt(other.getGame().getStartDate()).getCapital().getContinent();
                return country.getOwnedProvinceAt(country.getGame().getStartDate())
                              .map(ProvinceHistoryItemI::getProvince)
                              .anyMatch(p -> continent.equals(p.getContinent()));
            case "real_day_of_year":
                return country.getGame().getStartDate().getDayOfYear() == NumbersUtils.toInt(value);
            case "religion":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null
                       && historyItem.getReligion().getName().equalsIgnoreCase(rawValueToReligion(rawValue, root, from));
            case "religion_group":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null
                       && historyItem.getReligion().getReligionGroup().getName().equalsIgnoreCase(rawValueToReligionGroup(rawValue, root, from));
            case "revolt_percentage":
                return BigDecimal.valueOf(country.getOwnedProvinceAt(country.getGame().getStartDate())
                                                 .filter(i -> "REB".equals(i.getController().getTag()))
                                                 .count())
                                 .compareTo(new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOwnedProvinceAt(country.getGame().getStartDate())
                                                                                                     .count()))) >= 0;

            case "ruler_age":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null &&
                       historyItem.getMonarch().getBirthDate().plusYears(NumbersUtils.toInt(value)).isBefore(country.getGame().getStartDate());
            case "ruler_culture":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null &&
                       rawValueToCulture(rawValue, root, from).equalsIgnoreCase(historyItem.getMonarch().getCultureName());
            case "ruler_is_foreigner":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null &&
                                                         (historyItem.getMonarch().getCountry() != null &&
                                                          !country.equals(historyItem.getMonarch().getCountry())));
            case "ruler_religion":
                return (historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getMonarch() != null &&
                       historyItem.getMonarch().getReligion() != null &&
                       rawValueToReligion(rawValue, root, from).equalsIgnoreCase(historyItem.getMonarch().getReligion().getName());
            case "senior_union_with":
                other = country.getGame().getCountry(value);
                return country.equals(other.getOverlordAt(country.getGame().getStartDate())) &&
                       Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equalsIgnoreCase(other.getSubjectTypeAt(country.getGame().getStartDate()));
            case "start_date":
                return country.getGame().getStartDate().equals(Eu4Utils.stringToDate(value));
            case "started_in":
                return country.getGame().getStartDate().equals(Eu4Utils.stringToDate(value))
                       || country.getGame().getStartDate().isAfter(Eu4Utils.stringToDate(value));
            case "tag":
                return value.equalsIgnoreCase(country.getTag());
            case "target": //Used for scope has_casus_belli
                return true;
            case "technology_group":
                historyItem = country.getHistoryItemAt(country.getGame().getStartDate());
                if ((other = country.getGame().getCountry(value)) != null) {
                    otherHistoryItem = other.getHistoryItemAt(other.getGame().getStartDate());
                    return (historyItem.getTechnologyGroup() != null && otherHistoryItem.getTechnologyGroup() != null
                            && historyItem.getTechnologyGroup().equals(otherHistoryItem.getTechnologyGroup()));
                } else {
                    return historyItem.getTechnologyGroup() != null && value.equalsIgnoreCase(historyItem.getTechnologyGroup().getName());
                }
            case "territory": //Used for scope estate_territory
                return true;
            case "total_base_tax":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).mapToInt(ProvinceHistoryItemI::getBaseTax).sum() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate()).mapToInt(ProvinceHistoryItemI::getBaseTax).sum() >=
                           other.getOwnedProvinceAt(other.getGame().getStartDate()).mapToInt(ProvinceHistoryItemI::getBaseTax).sum();
                }
            case "total_development":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getOwnedProvinceAt(country.getGame().getStartDate())
                                  .mapToInt(i -> i.getBaseTax() + i.getBaseManpower() + i.getBaseProduction())
                                  .sum() >= integer;
                } else {
                    other = country.getGame().getCountry(value);
                    return country.getOwnedProvinceAt(country.getGame().getStartDate())
                                  .mapToInt(i -> i.getBaseTax() + i.getBaseManpower() + i.getBaseProduction())
                                  .sum() >=
                           other.getOwnedProvinceAt(country.getGame().getStartDate())
                                .mapToInt(i -> i.getBaseTax() + i.getBaseManpower() + i.getBaseProduction())
                                .sum();
                }
            case "type": //Used for scope has_casus_belli
                return true;
            case "uses_authority":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().isUseAuthority()));
            case "uses_church_aspects":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usesChurchPower()));
            case "uses_blessings":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().getBlessings() != null));
            case "uses_cults":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().useFetishistCult()));
            case "uses_devotion":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getGovernment() != null &&
                                                         historyItem.getGovernment().getBasicGovernmentReform().isHasDevotion().getKey());
            case "uses_doom":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().useDoom()));
            case "uses_fervor":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().useFervor()));
            case "uses_harmony":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usesHarmony()));
            case "uses_isolationism":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usesIsolationism()));
            case "uses_karma":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usesKarma()));
            case "uses_papacy":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().getPapacy() != null));
            case "uses_patriarch_authority":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().hasPatriarchs()));
            case "uses_personal_deities":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usePersonalDeity()));
            case "uses_piety":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().usesPiety()));
            case "uses_religious_icons":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().getIcons() != null));
            case "uses_syncretic_faiths":
                return "yes".equalsIgnoreCase(value) == ((historyItem = country.getHistoryItemAt(country.getGame().getStartDate())).getReligion() != null &&
                                                         BooleanUtils.toBoolean(historyItem.getReligion().canHaveSecondaryReligion()));
            case "value": //Special case, used for scopes to count
                return true;
            case "vassal_of":
                other = country.getGame().getCountry(value);
                return other.equals(country.getOverlordAt(country.getGame().getStartDate()))
                       && Eu4Utils.SUBJECT_TYPE_VASSAL.equals(country.getSubjectTypeAt(country.getGame().getStartDate()));
            case "war_with":
                other = country.getGame().getCountry(value);
                return country.getWarsAt(country.getGame().getStartDate()).anyMatch(war -> war.inOtherSideAt(country.getGame().getStartDate(), country, other));
            case "was_tag":
                return country.getHistoryItemAt(country.getGame().getStartDate())
                              .getChangedTagsFrom()
                              .stream()
                              .map(Country::getTag)
                              .anyMatch(value::equalsIgnoreCase);
        }

        LOGGER.debug("Don't know how to manage country condition: {} = {}", condition, value);
        return false;
    }

    public static boolean applyScopeToCountry(SaveCountry root, SaveCountry from, ConditionAbstract condition) {
        SaveCountry country;
        SaveProvince saveProvince;
        SubjectType subjectType;
        SaveEstate estate;
        SaveFaction faction;
        ActivePolicy activePolicy;
        Double aDouble;
        Integer integer;
        TradeGood tradeGood;
        SaveTradeNode tradeNode;
        SaveReligion religion;

        if ((country = root.getSave().getCountry(condition.getName())) != null) {
            return condition.apply(country, from);
        }

        if ((saveProvince = root.getSave().getProvince(NumbersUtils.toInt(condition.getName()))) != null) {
            return condition.apply(saveProvince);
        }

        switch (condition.getName().toLowerCase()) {
            case "or":
                return condition.or().apply(root, from);
            case "and", "if", "hidden_trigger":
                return condition.and().apply(root, from);
            case "not":
                return condition.not().apply(root, from);
            case "root":
                return condition.apply(root, from);
            case "from":
                return condition.apply(from, from);
            case "ai_attitude":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.getActiveRelation(country) != null
                       && condition.getCondition("attitude").equalsIgnoreCase(ClausewitzUtils.removeQuotes(root.getActiveRelation(country).getAttitude()));
            case "any_known_country":
                return root.getSave()
                           .getCountries()
                           .values()
                           .stream()
                           .filter(other -> other.isAlive() && other.getCapital() != null && other.getCapital().getDiscoveredBy().contains(root))
                           .anyMatch(other -> condition.apply(other, root));
            case "any_owned_province":
                return root.getOwnedProvinces().stream().anyMatch(condition::apply);
            case "army_strength":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return BigDecimal.valueOf(root.getArmySize())
                                 .multiply(new BigDecimal(condition.getCondition("value"))).compareTo(BigDecimal.valueOf(country.getArmySize())) >= 0;
            case "can_be_overlord":
                subjectType = root.getSave().getGame().getSubjectType(condition.getCondition("can_be_overlord"));
                return subjectType.isPotentialOverlord() == null || subjectType.isPotentialOverlord().apply(root, from);
            case "capital_scope":
                return condition.apply(root.getCapital());
            case "colonial_parent":
                return root.getColonialParent() != null && condition.apply(root.getColonialParent(), from);
            case "crusade_target":
                return root.getSave()
                           .getReligions()
                           .getReligions()
                           .values()
                           .stream()
                           .anyMatch(saveReligion -> saveReligion.getPapacy() != null && saveReligion.getPapacy().getCrusadeTarget() != null
                                                     && condition.apply(saveReligion.getPapacy().getCrusadeTarget(), from));
            case "development_in_provinces":
                return root.getOwnedProvinces()
                           .stream()
                           .filter(condition::apply)
                           .map(SaveProvince::getDevelopment)
                           .mapToDouble(NumbersUtils::doubleOrDefault)
                           .sum() >= NumbersUtils.toInt(condition.getCondition("value"));
            case "emperor":
                return !root.getSave().getHre().dismantled() && condition.apply(root.getSave().getHre().getEmperor(), from);
            case "employed_advisor":
                Power power = Power.byName(condition.getCondition("category"));
                String type = condition.getCondition("type");
                String cultures = condition.getCondition("culture");
                Culture culture = "root".equalsIgnoreCase(cultures) ? root.getPrimaryCulture() : root.getSave().getGame().getCulture(cultures);
                String religions = condition.getCondition("religion");

                return root.getActiveAdvisors().stream().anyMatch(advisor -> {
                    if (power != null && power != advisor.getGameAdvisor().getPower()) {
                        return false;
                    }

                    if (type != null && !type.equalsIgnoreCase(advisor.getType())) {
                        return false;
                    }

                    if (culture != null && !culture.equals(advisor.getCulture())) {
                        return false;
                    }

                    if (religions != null) {
                        if (("root".equalsIgnoreCase(religions) || "true_faith".equalsIgnoreCase(religions))
                            && !Objects.equals(root.getReligion(), advisor.getReligion())) {
                            return false;
                        } else if ("heretic".equalsIgnoreCase(religions)
                                   && (!root.getReligion().getReligionGroup().equals(advisor.getReligion().getReligionGroup())
                                       || root.getReligion().equals(advisor.getReligion()))) {
                            return false;
                        } else if ("heathen".equalsIgnoreCase(religions)
                                   && root.getReligion().getReligionGroup().equals(advisor.getReligion().getReligionGroup())) {
                            return false;
                        } else if (!advisor.getReligion().getName().equalsIgnoreCase(religions)) {
                            return false;
                        }
                    }

                    return true;
                });
            case "estate_influence":
                estate = root.getEstate(condition.getCondition("estate"));
                return estate != null && estate.getInfluence() >= NumbersUtils.toDouble(condition.getCondition("influence"));
            case "estate_loyalty":
                estate = root.getEstate(condition.getCondition("estate"));
                return estate != null && NumbersUtils.doubleOrDefault(estate.getLoyalty()) >= NumbersUtils.toDouble(condition.getCondition("loyalty"));
            case "estate_territory":
                estate = root.getEstate(condition.getCondition("estate"));
                return estate != null && NumbersUtils.doubleOrDefault(estate.getTerritory()) >= NumbersUtils.toDouble(condition.getCondition("territory"));
            case "faction_influence":
                faction = root.getFaction(condition.getCondition("faction"));
                return faction != null && NumbersUtils.doubleOrDefault(faction.getInfluence()) >= NumbersUtils.toDouble(condition.getCondition("influence"));
            case "had_active_policy":
                activePolicy = root.getActivePolicy(condition.getCondition("policy"));
                return activePolicy != null
                       && activePolicy.getDate().plusDays(NumbersUtils.toInt(condition.getCondition("days"))).isBefore(root.getSave().getDate());
            case "had_consort_flag":
                return root.getConsort() != null && root.getConsort().getRulerFlags().contains(condition.getCondition("flag"))
                       && root.getConsort().getRulerFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                              .isBefore(root.getSave().getDate());
            case "had_country_flag":
                return root.getFlags() != null && root.getFlags().contains(condition.getCondition("flag"))
                       && root.getFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                              .isBefore(root.getSave().getDate());
            case "had_global_flag":
                return root.getSave().getFlags().contains(condition.getCondition("flag"))
                       && root.getSave().getFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                              .isBefore(root.getSave().getDate());
            case "had_heir_flag":
                return root.getHeir() != null && root.getHeir().getRulerFlags().contains(condition.getCondition("flag"))
                       && root.getHeir().getRulerFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                              .isBefore(root.getSave().getDate());
            case "had_ruler_flag":
                return root.getMonarch() != null && root.getMonarch().getRulerFlags().contains(condition.getCondition("flag"))
                       && root.getMonarch().getRulerFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                              .isBefore(root.getSave().getDate());
            case "has_casus_belli":
                country = root.getSave().getCountry(condition.getCondition("target"));
                SaveCountry finalCountry = country;
                return root.getSave()
                           .getDiplomacy()
                           .getCasusBellis()
                           .stream()
                           .anyMatch(casusBelli -> root.equals(casusBelli.getFirst())
                                                   && finalCountry.equals(casusBelli.getSecond())
                                                   && condition.getCondition("type").equalsIgnoreCase(casusBelli.getType().getName()));
            case "has_disaster_progress":
                aDouble = root.getVariables().get(condition.getCondition("disaster") + "_progress");
                return aDouble != null && aDouble >= NumbersUtils.toDouble(condition.getCondition("value"));
            case "has_estate_influence_modifier":
                estate = root.getEstate(condition.getCondition("estate"));

                if (estate == null) {
                    return false;
                }

                Optional<SaveEstateModifier> m = estate.getInfluenceModifiers()
                                                       .stream()
                                                       .filter(modifier -> condition.getCondition("modifier").equalsIgnoreCase(modifier.getDesc()))
                                                       .findFirst();

                if (m.isEmpty() || condition.getCondition("value") == null) {
                    return false;
                }

                return NumbersUtils.doubleOrDefault(m.get().getValue()) >= NumbersUtils.toDouble(condition.getCondition("value")) ;
            case "has_estate_loyalty_modifier":
                estate = root.getEstate(condition.getCondition("estate"));
                return estate != null && estate.getLoyaltyModifiers()
                                               .stream()
                                               .anyMatch(modifier -> condition.getCondition("modifier").equalsIgnoreCase(modifier.getDesc()));
            case "has_global_modifier_value": //Todo object
                break;
            case "has_leader_with":
                Integer fire = NumbersUtils.toInt(condition.getCondition("fire"));
                Integer shock = NumbersUtils.toInt(condition.getCondition("shock"));
                Integer manuever = NumbersUtils.toInt(condition.getCondition("manuever"));
                Integer siege = NumbersUtils.toInt(condition.getCondition("siege"));
                Integer totalPips = NumbersUtils.toInt(condition.getCondition("total_pips"));
                boolean isMonarchLeader = BooleanUtils.toBoolean(condition.getCondition("is_monarch_leader"));
                LeaderType leaderType = condition.getConditions() == null ? null : condition.getConditions()
                                                                                            .keySet()
                                                                                            .stream()
                                                                                            .map(LeaderType::value)
                                                                                            .filter(Objects::nonNull)
                                                                                            .findFirst()
                                                                                            .orElse(null);

                List<Leader> leaders;

                if (isMonarchLeader) {
                    leaders = (root.getMonarch() == null || root.getMonarch().getLeader() == null)
                              ? null : Collections.singletonList(root.getMonarch().getLeader());
                } else if (leaderType != null) {
                    leaders = root.getLeadersOfType(leaderType);
                } else {
                    leaders = new ArrayList<>(root.getLeaders().values());
                }

                return leaders != null && leaders.stream().anyMatch(leader -> {
                    if (fire != null && leader.getFire() < fire) {
                        return false;
                    }

                    if (shock != null && leader.getShock() < shock) {
                        return false;
                    }

                    if (manuever != null && leader.getManuever() < manuever) {
                        return false;
                    }

                    if (siege != null && leader.getSiege() < siege) {
                        return false;
                    }

                    if (totalPips != null && leader.getTotalPips() < totalPips) {
                        return false;
                    }

                    return true;
                });
            case "has_opinion_modifier":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.getActiveRelation(country) != null
                       && root.getActiveRelation(country)
                              .getOpinions()
                              .stream()
                              .anyMatch(opinion -> condition.getCondition("modifier").equalsIgnoreCase(opinion.getModifier()));
            case "has_spy_network_from":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return NumbersUtils.intOrDefault(country.getActiveRelation(root).getSpyNetwork())
                       >= NumbersUtils.toIntOrDefault(condition.getCondition("value"));
            case "has_spy_network_in":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return NumbersUtils.intOrDefault(root.getActiveRelation(country).getSpyNetwork())
                       >= NumbersUtils.toIntOrDefault(condition.getCondition("value"));
            case "incident_variable_value":
                return root.getIncidentVariables().getOrDefault(condition.getCondition("incident"), 0d)
                       >= NumbersUtils.toDouble(condition.getCondition("value"));
            case "institution_difference":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.getNbEmbracedInstitutions() + NumbersUtils.toIntOrDefault(condition.getCondition("value")) >= country.getNbEmbracedInstitutions();
            case "is_in_war":
                List<String> attackers = condition.getConditions() == null ? null : condition.getConditions().get("attackers");
                List<String> defenders = condition.getConditions() == null ? null : condition.getConditions().get("defenders");
                String attackerLeader = condition.getCondition("attacker_leader");
                String defenderLeader = condition.getCondition("defender_leader");
                String casusBelli = condition.getCondition("casus_belli");
                Integer warGoalProvince = NumbersUtils.toInt(condition.getCondition("war_goal_province"));
                LocalDate startDate = Eu4Utils.stringToDate(condition.getCondition("start_date"));
                Integer duration = NumbersUtils.toInt(condition.getCondition("duration"));

                return root.getActiveWars().stream().anyMatch(activeWar -> {
                    if (CollectionUtils.isNotEmpty(attackers) && activeWar.getAttackers().keySet().containsAll(attackers)) {
                        return false;
                    }

                    if (CollectionUtils.isNotEmpty(defenders) && activeWar.getDefenders().keySet().containsAll(defenders)) {
                        return false;
                    }

                    if (attackerLeader != null && !activeWar.getAttackers().keySet().iterator().next().equals(attackerLeader)) {
                        return false;
                    }

                    if (defenderLeader != null && !activeWar.getDefenders().keySet().iterator().next().equals(defenderLeader)) {
                        return false;
                    }

                    if (StringUtils.isNotBlank(casusBelli) && !casusBelli.equalsIgnoreCase(activeWar.getWarGoal().getCasusBelli(root.getSave().getGame()).getName())) {
                        return false;
                    }

                    if (warGoalProvince != null && activeWar.getWarGoal().getProvince() != null
                        && !Objects.equals(activeWar.getWarGoal().getProvince(), warGoalProvince)) {
                        return false;
                    }

                    if (startDate != null && activeWar.getStartDate().isBefore(startDate)) {
                        return false;
                    }

                    if (duration != null && activeWar.getStartDate().plusDays(duration).isAfter(root.getSave().getDate())) {
                        return false;
                    }

                    return true;
                });
            case "military_strength":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return BigDecimal.valueOf((long) root.getArmySize() + root.getNavySize())
                                 .multiply(new BigDecimal(condition.getCondition("value")))
                                 .compareTo(BigDecimal.valueOf((long) country.getArmySize() + country.getNavySize())) >= 0;
            case "naval_strength":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return BigDecimal.valueOf(root.getNavySize())
                                 .multiply(new BigDecimal(condition.getCondition("value"))).compareTo(BigDecimal.valueOf(country.getNavySize())) >= 0;
            case "num_of_owned_provinces_with":
                return root.getOwnedProvinces().stream().filter(condition::apply).count() >= NumbersUtils.toInt(condition.getCondition("value"));
            case "num_of_provinces_owned_or_owned_by_non_sovereign_subjects_with":
                return root.getOwnedProvinces().stream().filter(condition::apply).count()
                       + root.getSubjects()
                             .stream()
                             .filter(c -> !c.getSubjectType().isVoluntary())
                             .map(SaveCountry::getOwnedProvinces)
                             .flatMap(Collection::stream)
                             .filter(condition::apply)
                             .count()
                       >= NumbersUtils.toInt(condition.getCondition("value"));
            case "num_of_religion":
                religion = null;
                if (condition.getCondition("secondary") != null) {
                    religion = root.getSecondaryReligion();
                } else if (condition.getCondition("religion") != null) {
                    religion = root.getSave().getReligions().getReligion(condition.getCondition("religion"));
                }

                if ((integer = NumbersUtils.toInt(condition.getCondition("value"))) != null) {
                    if (religion != null) {
                        SaveReligion finalReligion = religion;
                        return root.getOwnedProvinces().stream().filter(province -> finalReligion.equals(province.getReligion())).count() >= integer;
                    } else if (condition.getCondition("heretic") != null) {
                        return root.getOwnedProvinces().stream().filter(province -> !Objects.equals(root.getReligion(), province.getReligion())).count()
                               >= integer;
                    } else {
                        return false;
                    }
                } else if ((aDouble = NumbersUtils.toDouble(condition.getCondition("value"))) != null) {
                    long nbProv;
                    if (religion != null) {
                        SaveReligion finalReligion = religion;
                        nbProv = root.getOwnedProvinces().stream().filter(province -> finalReligion.equals(province.getReligion())).count();
                    } else if (condition.getCondition("heretic") != null) {
                        nbProv = root.getOwnedProvinces().stream().filter(province -> !Objects.equals(root.getReligion(), province.getReligion())).count();
                    } else {
                        return false;
                    }

                    return BigDecimal.valueOf(aDouble).multiply(BigDecimal.valueOf(root.getOwnedProvinces().size())).compareTo(BigDecimal.valueOf(nbProv)) >= 0;
                }
                break;
            case "overlord":
                return root.getOverlord() != null && condition.apply(root.getOverlord(), from);
            case "owns_all_provinces":
                List<SaveCountry> countries = new ArrayList<>();
                countries.add(root);
                countries.addAll(root.getSubjects().stream().filter(c -> !c.getSubjectType().isVoluntary()).toList());
                return root.getSave().getProvinces().values().stream().filter(condition::apply).allMatch(province -> countries.contains(province.getOwner()));
            case "production_leader":
                tradeGood = root.getSave().getGame().getTradeGood(condition.getCondition("trade_goods"));
                return root.equals(root.getSave().getProductionLeaders().get(tradeGood));
            case "religion_years":
                religion = root.getSave().getReligions().getReligion(condition.getConditions().entrySet().iterator().next().getValue().get(0));
                if (religion.getGameReligion().getDate() == null) {
                    return root.getSave().getStartDate().plusYears(NumbersUtils.toInt(condition.getCondition(religion.getName())))
                               .isBefore(root.getSave().getDate());
                } else {
                    return religion.getEnable() != null && religion.getEnable().plusYears(NumbersUtils.toInt(condition.getCondition(religion.getName())))
                                                                   .isBefore(root.getSave().getDate());
                }
            case "religious_school":
                return root.getReligion() != null && condition.getCondition("group").equalsIgnoreCase(root.getReligion().getReligionGroup().getName())
                       && condition.getCondition("school").equalsIgnoreCase(ClausewitzUtils.removeQuotes(root.getReligiousSchool()));
            case "reverse_has_opinion_modifier":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return country.getActiveRelation(root) != null
                       && country.getActiveRelation(root)
                                 .getOpinions()
                                 .stream()
                                 .anyMatch(opinion -> condition.getCondition("modifier").equalsIgnoreCase(opinion.getModifier()));
            case "revolution_target":
                return root.getSave().getRevolution() != null && root.getSave().getRevolution().getRevolutionTarget() != null &&
                       condition.apply(root.getSave().getRevolution().getRevolutionTarget(), from);
            case "same_continent": //Todo object
                break;
            case "trading_bonus":
                tradeGood = root.getSave().getGame().getTradeGood(condition.getCondition("trade_goods"));
                return "yes".equalsIgnoreCase(condition.getCondition("value")) == root.getTradedBonus().contains(tradeGood);
            case "trading_part":
                tradeGood = root.getSave().getGame().getTradeGood(condition.getCondition("trade_goods"));
                return root.getTraded().get(tradeGood) >= NumbersUtils.toDouble(condition.getCondition("value"));
            case "trading_policy_in_node":
                tradeNode = root.getSave().getTradeNode(condition.getCondition("node"));

                if ("any".equalsIgnoreCase(condition.getCondition("policy"))) {
                    return tradeNode.getCountry(root).getTradePolicy() != null;
                } else {
                    return condition.getCondition("policy").equalsIgnoreCase(tradeNode.getCountry(root).getTradePolicy().getName());
                }
            case "trust":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.getActiveRelation(country) != null
                       && root.getActiveRelation(country).getTrustValue() >= NumbersUtils.toInt(condition.getCondition("value"));
            case "war_score_against":
                country = root.getSave().getCountry(condition.getCondition("who"));
                SaveCountry finalCountry1 = country;
                return !CollectionUtils.isNotEmpty(root.getActiveWars())
                       && root.getActiveWars()
                              .stream()
                              .anyMatch(activeWar -> activeWar.getOtherSide(root).containsKey(finalCountry1.getTag())
                                                     && activeWar.getScore(root) >= NumbersUtils.toDouble(condition.getCondition("value")));
            case "years_in_union_under":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.equals(country.getOverlord()) && Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equals(country.getSubjectType().getName())
                       && country.getSubjectStartDate().plusYears(NumbersUtils.toInt(condition.getCondition("value"))).isBefore(root.getSave().getDate());
            case "years_in_vassalage_under":
                country = root.getSave().getCountry(condition.getCondition("who"));
                return root.equals(country.getOverlord()) && Eu4Utils.SUBJECT_TYPE_VASSAL.equals(country.getSubjectType().getName())
                       && country.getSubjectStartDate().plusYears(NumbersUtils.toInt(condition.getCondition("value"))).isBefore(root.getSave().getDate());
        }

        LOGGER.debug("Don't know how to manage country scope: {} !", condition);
        return false;
    }

    public static boolean applyScopeToCountry(Country root, Country from, ConditionAbstract condition) {
        Country country;
        Province province;
        SubjectType subjectType;
        Double aDouble;
        Integer integer;
        Religion religion;

        if ((country = root.getGame().getCountry(condition.getName())) != null) {
            return condition.apply(country, from);
        }

        if ((province = Optional.ofNullable(NumbersUtils.toInt(condition.getName())).map(i -> root.getGame().getProvince(i)).orElse(null)) != null) {
            return condition.apply(province);
        }

        switch (condition.getName().toLowerCase()) {
            case "or":
                return condition.or().apply(root, from);
            case "and", "if", "hidden_trigger":
                return condition.and().apply(root, from);
            case "not":
                return condition.not().apply(root, from);
            case "root":
                return condition.apply(root, from);
            case "from":
                return condition.apply(from, root);
            case "any_owned_province":
                return root.getOwnedProvinceAt(root.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).anyMatch(condition::apply);
            case "can_be_overlord":
                subjectType = root.getGame().getSubjectType(condition.getCondition("can_be_overlord"));
                return subjectType.isPotentialOverlord() == null || subjectType.isPotentialOverlord().apply(root, from);
            case "capital_scope":
                return (province = root.getHistoryItemAt(root.getGame().getStartDate()).getCapital()) != null && condition.apply(province);
            case "development_in_provinces":
                return root.getOwnedProvinceAt(root.getGame().getStartDate())
                           .filter(i -> condition.apply(i.getProvince()))
                           .mapToInt(p -> p.getBaseTax() + p.getBaseProduction() + p.getBaseManpower())
                           .sum() >= NumbersUtils.toInt(condition.getCondition("value"));
            case "emperor":
                return root.getGame().getHreEmperorAt(root.getGame().getStartDate()) != null &&
                       condition.apply(root.getGame().getCountry(root.getGame().getHreEmperorAt(root.getGame().getStartDate()).getTag()), from);
            case "is_in_war":
                List<String> attackers = condition.getConditions() == null ? null : condition.getConditions().get("attackers");
                List<String> defenders = condition.getConditions() == null ? null : condition.getConditions().get("defenders");
                String attackerLeader = condition.getCondition("attacker_leader");
                String defenderLeader = condition.getCondition("defender_leader");
                String casusBelli = condition.getCondition("casus_belli");
                Integer warGoalProvince = NumbersUtils.toInt(condition.getCondition("war_goal_province"));
                LocalDate startDate = Eu4Utils.stringToDate(condition.getCondition("start_date"));
                Integer duration = NumbersUtils.toInt(condition.getCondition("duration"));

                return root.getWarsAt(root.getGame().getStartDate()).anyMatch(activeWar -> {
                    if (CollectionUtils.isNotEmpty(attackers)
                        && new HashSet<>(activeWar.getAttackersAt(root.getGame().getStartDate())).containsAll(attackers)) {
                        return false;
                    }

                    if (CollectionUtils.isNotEmpty(defenders)
                        && new HashSet<>(activeWar.getDefendersAt(root.getGame().getStartDate())).containsAll(defenders)) {
                        return false;
                    }

                    if (attackerLeader != null && !Objects.equals(activeWar.getAttackersAt(root.getGame().getStartDate()).get(0),
                                                                  root.getGame().getCountry(attackerLeader).getTag())) {
                        return false;
                    }

                    if (defenderLeader != null && !Objects.equals(activeWar.getDefendersAt(root.getGame().getStartDate()).get(0),
                                                                  root.getGame().getCountry(defenderLeader).getTag())) {
                        return false;
                    }

                    if (StringUtils.isNotBlank(casusBelli) && !casusBelli.equalsIgnoreCase(activeWar.getWarGoal().getCasusBelli(root.getGame()).getName())) {
                        return false;
                    }

                    if (warGoalProvince != null && activeWar.getWarGoal().getProvince() != null
                        && !Objects.equals(activeWar.getWarGoal().getProvince(), warGoalProvince)) {
                        return false;
                    }

                    if (startDate != null && activeWar.getStart().isBefore(startDate)) {
                        return false;
                    }

                    if (duration != null && activeWar.getStart().plusDays(duration).isAfter(root.getGame().getStartDate())) {
                        return false;
                    }

                    return true;
                });
            case "num_of_owned_provinces_with":
                return root.getOwnedProvinceAt(root.getGame().getStartDate()).map(ProvinceHistoryItemI::getProvince).filter(condition::apply).count()
                       >= NumbersUtils.toInt(condition.getCondition("value"));
            case "num_of_provinces_owned_or_owned_by_non_sovereign_subjects_with":
                break; //To complexe to be worth computing
            case "num_of_religion":
                religion = null;
                if (condition.getCondition("secondary") != null) {
                    return false;
                } else if (condition.getCondition("religion") != null) {
                    religion = root.getGame().getReligion(condition.getCondition("religion"));
                }

                Religion finalReligion = religion;
                if ((integer = NumbersUtils.toInt(condition.getCondition("value"))) != null) {
                    if (religion != null) {
                        return root.getOwnedProvinceAt(root.getGame().getStartDate()).filter(i -> finalReligion.equals(i.getReligion())).count() >= integer;
                    } else if (condition.getCondition("heretic") != null) {
                        return root.getOwnedProvinceAt(root.getGame().getStartDate())
                                   .filter(i -> !Objects.equals(root.getHistoryItemAt(root.getGame().getStartDate()).getReligion(), i.getReligion())).count()
                               >= integer;
                    } else {
                        return false;
                    }
                } else if ((aDouble = NumbersUtils.toDouble(condition.getCondition("value"))) != null) {
                    long nbProv;
                    if (religion != null) {
                        nbProv = root.getOwnedProvinceAt(root.getGame().getStartDate()).filter(i -> finalReligion.equals(i.getReligion())).count();
                    } else if (condition.getCondition("heretic") != null) {
                        nbProv = root.getOwnedProvinceAt(root.getGame().getStartDate())
                                     .filter(i -> !Objects.equals(root.getHistoryItemAt(root.getGame().getStartDate()).getReligion(), i.getReligion()))
                                     .count();
                    } else {
                        return false;
                    }

                    return BigDecimal.valueOf(aDouble)
                                     .multiply(BigDecimal.valueOf(root.getOwnedProvinceAt(root.getGame().getStartDate()).count()))
                                     .compareTo(BigDecimal.valueOf(nbProv)) >= 0;
                }
                break;
            case "overlord":
                return root.getOverlordAt(root.getGame().getStartDate()) != null && condition.apply(root.getOverlordAt(root.getGame().getStartDate()), from);
            case "owns_all_provinces":
                List<Country> countries = new ArrayList<>();
                countries.add(root);
                countries.addAll(root.getSubjectsAt(root.getGame().getStartDate())
                                     .filter(c -> !c.getGame().getSubjectType(c.getSubjectTypeAt(root.getGame().getStartDate())).isVoluntary())
                                     .toList());
                return root.getGame()
                           .getProvinces()
                           .values()
                           .stream()
                           .filter(condition::apply)
                           .allMatch(p -> countries.contains(p.getHistoryItemAt(root.getGame().getStartDate()).getOwner()));
            case "religion_years":
                religion = root.getGame().getReligion(condition.getConditions().entrySet().iterator().next().getValue().get(0));
                if (religion.getDate() == null) {
                    return NumbersUtils.toInt(condition.getCondition(religion.getName())) <= 0;
                } else {
                    return religion.getDate() != null && religion.getDate().plusYears(NumbersUtils.toInt(condition.getCondition(religion.getName())))
                                                                 .isBefore(root.getGame().getStartDate());
                }
            case "same_continent": //Todo object
                break;
            case "years_in_union_under":
                country = root.getGame().getCountry(condition.getCondition("who"));
                return root.equals(country.getOverlordAt(root.getGame().getStartDate())) &&
                       Eu4Utils.SUBJECT_TYPE_PERSONAL_UNION.equals(country.getSubjectTypeAt(root.getGame().getStartDate())) &&
                       country.getSubjectSinceAt(root.getGame().getStartDate())
                              .plusYears(NumbersUtils.toInt(condition.getCondition("value")))
                              .isBefore(root.getGame().getStartDate());
            case "years_in_vassalage_under":
                country = root.getGame().getCountry(condition.getCondition("who"));
                return root.equals(country.getOverlordAt(root.getGame().getStartDate())) &&
                       Eu4Utils.SUBJECT_TYPE_VASSAL.equals(country.getSubjectTypeAt(root.getGame().getStartDate())) &&
                       country.getSubjectSinceAt(root.getGame().getStartDate())
                              .plusYears(NumbersUtils.toInt(condition.getCondition("value")))
                              .isBefore(root.getGame().getStartDate());
        }

        LOGGER.debug("Don't know how to manage country scope: {} !", condition);
        return false;
    }

    public static boolean applyConditionToProvince(SaveProvince province, String condition, String rawValue) {
        String value;
        Institution institution;
        Integer integer;
        SaveCountry other;
        Building building;
        ConditionAnd scriptedTrigger;

        if ("ROOT".equals(rawValue)) {
            value = province.getOwnerTag();
        } else if ("FROM".equals(rawValue)) {
            value = province.getOwnerTag();
        } else {
            value = rawValue;
        }

        if ((institution = province.getSave().getGame().getInstitution(condition)) != null) {
            return province.getInstitutionsProgress().get(institution.getIndex()) >= NumbersUtils.toDouble(value);
        }

        if ((scriptedTrigger = province.getSave().getGame().getScriptedTrigger(condition)) != null) {
            return scriptedTrigger.apply(province);
        }

        switch (condition.toLowerCase()) {
            case "allows_female_emperor":
                return province.getSave().getHre() != null && BooleanUtils.toBoolean(province.getSave().getHre().getAllowsFemaleEmperor());
            case "always":
                return "yes".equalsIgnoreCase(value);
            case "area":
                return province.getSave().getGame().getArea(value).getProvinces().contains(province.getId());
            case "artillery_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getArtillery().size() >= integer;
                } else {
                    return value != null &&
                           province.getArtillery().stream().anyMatch(regiment -> regiment.getArmy().getCountry().equals(province.getSave().getCountry(value)));
                }
            case "base_manpower":
                return province.getBaseManpower() >= NumbersUtils.toInt(value);
            case "base_production":
                return province.getBaseProduction() >= NumbersUtils.toInt(value);
            case "base_tax":
                return province.getBaseTax() >= NumbersUtils.toInt(value);
            case "can_build":
                building = province.getSave().getGame().getBuilding(value);
                return building != null && (building.getTrigger() == null || building.getTrigger().apply(province));
            case "capital":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getOwner().getCapital().equals(province));
            case "cavalry_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getCavalry().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return value != null && province.getCavalry().stream().anyMatch(regiment -> regiment.getArmy().getCountry().equals(other));
                }
            case "colonial_region":
                return province.getSave().getGame().getColonialRegion(value).getProvinces().contains(province.getId());
            case "colonysize":
                return NumbersUtils.doubleOrDefault(province.getColonySize()) >= NumbersUtils.toDouble(value);
            case "construction_progress":
                return province.getBuildingConstruction() == null || province.getBuildingConstruction().getProgress() >= NumbersUtils.toDouble(value);
            case "continent":
                return value.equalsIgnoreCase(province.getContinent().getName());
            case "controlled_by":
                return value.equalsIgnoreCase(ClausewitzUtils.removeQuotes(province.getControllerTag()));
            case "country_or_subject_holds":
            case "country_or_vassal_holds":
                other = province.getSave().getCountry(value);
                return province.getOwner() != null
                       && (province.getOwner().equals(other) || (province.getOwner().getOverlord() != null && province.getOwner().getOverlord().equals(other)));
            case "culture":
                return province.getCulture() != null && rawValueToCulture(rawValue, province).equalsIgnoreCase(province.getCultureName());
            case "culture_group":
                return province.getCulture() != null && province.getCulture().getCultureGroup().getName().equals(rawValueToCultureGroup(rawValue, province));
            case "current_age":
                return province.getSave().getCurrentAge().getName().equals(value);
            case "current_institution_growth": //Todo (global + local)
                break;
            case "custom_nation_setup":
                return "yes".equalsIgnoreCase(value) == NationSetup.CUSTOM.equals(province.getSave().getGameplayOptions().getNationSetup());
            case "devastation":
                return NumbersUtils.doubleOrDefault(province.getDevastation()) >= NumbersUtils.toDouble(value);
            case "development":
                return NumbersUtils.doubleOrDefault(province.getDevelopment()) >= NumbersUtils.toDouble(value);
            case "difficulty":
                return value.equalsIgnoreCase(province.getSave().getGameplayOptions().getDifficulty().name());
            case "empire_of_china_reform_level":
                return province.getSave().getCelestialEmpire() != null
                       && province.getSave().getCelestialEmpire().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "exists":
                if ("yes".equalsIgnoreCase(value)) {
                    return true;
                } else {
                    return province.getSave().getCountry(value) != null;
                }
            case "fort_level":
                return province.getBuildings()
                               .stream()
                               .anyMatch(build -> build.getBuilding().getModifiers().hasModifier("fort_level")
                                                  && build.getBuilding().getModifiers().getModifier("fort_level") >= NumbersUtils.toInt(value));
            case "galleys_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getGalleys().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getGalleys().stream().anyMatch(ship -> ship.getNavy().getCountry().equals(other));
                }
            case "garrison":
                return NumbersUtils.doubleOrDefault(province.getGarrison()) >= NumbersUtils.toDouble(value);
            case "has_active_triggered_province_modifier":
                return province.getTriggeredModifiers().contains(ClausewitzUtils.addQuotes(value));
            case "has_building":
                return province.getBuildings().stream().anyMatch(provinceBuilding -> value.equalsIgnoreCase(provinceBuilding.getName()));
            case "has_cardinal":
                return "yes".equalsIgnoreCase(value) == province.getSave()
                                                                .getReligions()
                                                                .getReligions()
                                                                .values()
                                                                .stream()
                                                                .filter(religion -> religion.getPapacy() != null)
                                                                .filter(religion -> religion.getPapacy().getCardinals() != null)
                                                                .flatMap(religion -> religion.getPapacy().getCardinals().stream())
                                                                .anyMatch(cardinal -> province.equals(cardinal.getLocation()));
            case "has_climate":
                return value.equalsIgnoreCase(province.getClimate());
            case "has_colonist":
                return "yes".equalsIgnoreCase(value) == province.getColonyConstruction().getEnvoy() >= 0;
            case "has_devastation":
                return province.getDevastation() != null;
            case "has_discovered":
                other = province.getSave().getCountry(value);
                return province.getDiscoveredBy().contains(other);
            case "has_dlc":
                return province.getSave().getDlcEnabled().contains(value);
            case "has_first_revolution_started":
                return province.getSave().getRevolution() != null && BooleanUtils.toBoolean(province.getSave().getRevolution().hasFirstRevolutionStarted());
            case "has_game_started":
                return !province.getSave().getDate().equals(province.getSave().getStartDate());
            case "has_global_flag":
                return province.getSave().getFlags().contains(value);
            case "has_great_project":
                return province.getGreatProjects().stream().anyMatch(greatProject -> greatProject.getName().equalsIgnoreCase(value));
            case "has_heir_leader_from":
                other = province.getSave().getCountry(value);
                return province.getArmies()
                               .stream()
                               .anyMatch(army -> army.getCountry().equals(other) && army.getLeader() != null && army.getCountry().getHeir() != null
                                                 && army.getLeader().equals(army.getCountry().getHeir().getLeaderId()));
            case "has_influencing_fort":
                return "yes".equalsIgnoreCase(value) == (province.getFortInfluencing() != null);
            case "has_latent_trade_goods":
                return value.equalsIgnoreCase(province.getLatentTradeGoods());
            case "has_merchant":
            case "has_trader":
                other = province.getSave().getCountry(value);
                return province.getSave().getTradeNodes().get(province.getTrade()).getCountries().get(other).hasTrader();
            case "has_missionary":
                return province.getMissionaryConstruction() != null;
            case "has_most_province_trade_power":
                other = province.getSave().getCountry(value);
                return other.equals(province.getSave().getTradeNodes().get(province.getTrade()).getTopProvinces().entrySet().iterator().next().getKey());
            case "has_owner_accepted_culture":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getCulture() != null
                                                         && province.getOwner().getAcceptedCultures().contains(province.getCulture()));
            case "has_owner_culture":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getCulture() != null
                                                         && province.getOwner().getPrimaryCulture().equals(province.getCulture()));
            case "has_owner_culture_group":

                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getCulture() != null
                                                         && province.getOwner()
                                                                    .getPrimaryCulture()
                                                                    .getCultureGroup()
                                                                    .equals(province.getCulture().getCultureGroup()));
            case "has_owner_religion":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getOwner().getReligion() != null &&
                                                         province.getOwner().getReligion().equals(province.getReligion()));
            case "has_port":
                return "yes".equalsIgnoreCase(value) == (province.isPort());
            case "has_province_flag":
                return province.getFlags().contains(value);
            case "has_province_modifier":
                return province.getModifiers().containsKey(value)
                       || province.getTriggeredModifiers().contains(ClausewitzUtils.addQuotes(value))
                       || province.getAppliedTriggeredModifiers().contains(ClausewitzUtils.addQuotes(value));
            case "has_rebel_faction":
                return "REB".equalsIgnoreCase(value) ? province.getControllerTag().equals("REB")
                                                     : value.equalsIgnoreCase(ClausewitzUtils.removeQuotes(province.getSave().getRebelFactions()
                                                                                                                   .get(province.getRebelFaction()).getType()));
            case "has_ruler_leader_from":
                other = province.getSave().getCountry(value);
                return province.getArmies()
                               .stream()
                               .anyMatch(army -> army.getCountry().equals(other) && army.getLeader() != null && army.getCountry().getMonarch() != null
                                                 && army.getLeader().equals(army.getCountry().getMonarch().getLeaderId()));
            case "has_seat_in_parliament":
                return "yes".equalsIgnoreCase(value) == (province.getSeatInParliament() != null);
            case "has_siege":
                return "yes".equalsIgnoreCase(value) == (province.getSiege() != null);
            case "has_state_patriach":
                return "yes".equalsIgnoreCase(value) ==
                       (province.getSaveArea() != null && province.getSaveArea().getCountriesStates() != null
                        && province.getSaveArea().getCountryState(province.getOwner()) != null
                        && BooleanUtils.toBoolean(province.getSaveArea().getCountryState(province.getOwner()).hasStatePatriarch()));
            case "has_state_pasha":
                return "yes".equalsIgnoreCase(value) ==
                       (province.getSaveArea() != null && province.getSaveArea().getCountriesStates() != null
                        && province.getSaveArea().getCountryState(province.getOwner()) != null
                        && BooleanUtils.toBoolean(province.getSaveArea().getCountryState(province.getOwner()).hasStatePasha()));
            case "has_terrain": //Don't do
                break;
            case "has_supply_depot":
                return "yes".equalsIgnoreCase(value) ==
                       (province.getOwner() != null && province.getSaveArea() != null && province.getSaveArea().getSupplyDepots() != null
                        && province.getSaveArea()
                                   .getSupplyDepots()
                                   .stream()
                                   .anyMatch(supplyDepot -> province.getOwner().equals(supplyDepot.getBuilder())
                                                            || province.getOwner().getSubjects().contains(supplyDepot.getBuilder())
                                                            || province.getOwner()
                                                                       .getActiveWars()
                                                                       .stream()
                                                                       .map(activeWar -> activeWar.getSide(province.getOwner()).keySet())
                                                                       .flatMap(Collection::stream)
                                                                       .map(s -> province.getSave().getCountry(s))
                                                                       .anyMatch(country -> country.equals(supplyDepot.getBuilder()))));
            case "has_winter":
                return value.equalsIgnoreCase(province.getWinter());
            case "has_monsoon":
                return value.equalsIgnoreCase(province.getClimate());
            case "heavy_ships_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getHeavyShips().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getHeavyShips().stream().anyMatch(ship -> ship.getNavy().getCountry().equals(other));
                }
            case "highest_value_trade_node":
                return province.getSave()
                               .getTradeNodes()
                               .values()
                               .stream()
                               .max(Comparator.comparingDouble(SaveTradeNode::getCurrent))
                               .get()
                               .equals(province.getSave().getTradeNodes().get(province.getTrade()));
            case "hre_heretic_religion":
                return !province.getSave().getHre().dismantled()
                       && BooleanUtils.toBoolean(province.getSave().getReligions().getReligion(rawValueToReligion(rawValue, province)).isHreHereticReligion());
            case "hre_leagues_enabled":
                return !province.getSave().getHre().dismantled() && BooleanUtils.toBoolean(province.getSave().getHreLeaguesActive());
            case "hre_reform_level":
                return !province.getSave().getHre().dismantled() && province.getSave().getHre().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "hre_reform_passed":
                return !province.getSave().getHre().dismantled()
                       && province.getSave().getHre().getPassedReforms().stream().anyMatch(reform -> ClausewitzUtils.addQuotes(value).equals(reform.getName()));
            case "hre_religion":
                return !province.getSave().getHre().dismantled() &&
                       BooleanUtils.toBoolean(province.getSave().getReligions().getReligion(rawValueToReligion(rawValue, province)).isHreReligion());
            case "hre_religion_locked":
                return "yes".equalsIgnoreCase(value) == (!province.getSave().getHre().dismantled()
                                                         && !HreReligionStatus.PEACE.equals(province.getSave().getHreReligionStatus()));
            case "hre_religion_treaty":
                return "yes".equalsIgnoreCase(value) == (!province.getSave().getHre().dismantled()
                                                         && HreReligionStatus.PEACE.equals(province.getSave().getHreReligionStatus()));
            case "hre_size":
                return !province.getSave().getHre().dismantled()
                       && province.getSave().getCountries().values().stream().filter(c -> c.getCapital().inHre()).count() >= NumbersUtils.toInt(value);
            case "imperial_influence":
                return !province.getSave().getHre().dismantled()
                       && NumbersUtils.doubleOrDefault(province.getSave().getHre().getImperialInfluence()) >= NumbersUtils.toDouble(value);
            case "imperial_mandate":
                return !province.getSave().getCelestialEmpire().dismantled()
                       && NumbersUtils.doubleOrDefault(province.getSave().getCelestialEmpire().getImperialInfluence()) >= NumbersUtils.toDouble(value);
            case "in_capital_state":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getArea().equals(province.getOwner().getCapital().getArea()));
            case "infantry_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getInfantry().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getInfantry().stream().anyMatch(regiment -> regiment.getArmy().getCountry().equals(other));
                }
            case "ironman":
                return false;
            case "is_backing_current_issue":
                return province.getSeatInParliament() != null && BooleanUtils.toBoolean(province.getSeatInParliament().getBack());
            case "is_blockaded":
                return province.blockade();
            case "is_capital":
                return "yes".equalsIgnoreCase(value) == (province.getSave()
                                                                 .getCountries()
                                                                 .values()
                                                                 .stream()
                                                                 .anyMatch(country -> province.equals(country.getCapital())));
            case "is_capital_of":
                other = province.getSave().getCountry(value);
                return province.equals(other.getCapital());
            case "is_city":
                return "yes".equalsIgnoreCase(value) == province.isCity();
            case "is_claim":
                return "yes".equalsIgnoreCase(value) == province.getClaimsTags().contains(value.toUpperCase());
            case "is_colony":
                return "yes".equalsIgnoreCase(value) == province.isColony();
            case "is_core":
                return province.getCoresTags().contains(value.toUpperCase());
            case "is_empty":
                return "yes".equalsIgnoreCase(value) == province.isOccupied();
            case "is_imperial_ban_allowed":
                return "yes".equalsIgnoreCase(value) == (!province.getSave().getHre().dismantled()
                                                         && BooleanUtils.toBoolean(province.getSave().getHre().getImperialBanAllowed()));
            case "is_institution_enabled":
                return "yes".equalsIgnoreCase(value) == province.getSave().getInstitutions().isAvailable(province.getSave().getGame().getInstitution(value));
            case "is_institution_origin":
                return "yes".equalsIgnoreCase(value) == province.equals(province.getSave()
                                                                                .getInstitutions()
                                                                                .getOrigin(province.getSave().getGame().getInstitution(value)));
            case "is_lake":
                return "yes".equalsIgnoreCase(value) == province.isLake();
            case "is_cored":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getCores().contains(province.getOwner()));
            case "is_looted":
                return "yes".equalsIgnoreCase(value) == (province.getLootRemaining() != null);
            case "is_month":
            case "real_month_of_year":
                return province.getSave().getDate().getMonthValue() == NumbersUtils.toInt(value);
            case "is_node_in_trade_company_region":
                return "yes".equalsIgnoreCase(value) == province.getSave()
                                                                .getGame()
                                                                .getTradeCompanies()
                                                                .stream()
                                                                .anyMatch(tradeCompany -> tradeCompany.getProvinces().contains(province.getId()));
            case "is_occupied":
                return "yes".equalsIgnoreCase(value) == (province.getController() != null && province.getOwner() != null
                                                         && !province.getOwner().equals(province.getController()));
            case "is_ocean":
                return "yes".equalsIgnoreCase(value) == province.isOcean();
            case "is_owned_by_trade_company":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(province.activeTradeCompany());
            case "is_part_of_hre":
                return "yes".equalsIgnoreCase(value) == province.inHre();
            case "is_permanent_claim":
                other = province.getSave().getCountry(value);
                return province.getClaimsTags().contains(value.toUpperCase())
                       && province.getHistory().getClaims().getOrDefault(Eu4Utils.DEFAULT_DATE, new ArrayList<>()).contains(other);
            case "is_prosperous":
                return "yes".equalsIgnoreCase(value) ==
                       (province.getSaveArea() != null && province.getSaveArea().getCountriesStates() != null
                        && province.getSaveArea().getCountryState(province.getController()) != null
                        && NumbersUtils.doubleOrDefault(province.getSaveArea().getCountryState(province.getController()).getProsperity()) >= 100d);
            case "is_random_new_world":
                return "yes".equalsIgnoreCase(value) == province.getSave().isRandomNewWorld();
            case "is_reformation_center":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(province.centerOfReligion());
            case "is_religion_enabled":
                SaveReligion saveReligion = province.getSave().getReligions().getReligion(rawValueToReligion(rawValue, province));
                return saveReligion != null && (saveReligion.getGameReligion().getDate() == null
                                                || (saveReligion.getEnable() != null && province.getSave().getDate().isAfter(saveReligion.getEnable())));
            case "is_religion_grant_colonial_claim":
                if (province.getSave().getCountry(value) != null) {
                    return !province.getSave()
                                    .getReligions()
                                    .getReligions()
                                    .values()
                                    .stream()
                                    .anyMatch(religion -> {
                                        if (religion.getPapacy() == null) {
                                            return false;
                                        }

                                        Integer colonialRegionIndex = null;

                                        for (int i = 0; i < province.getSave().getGame().getColonialRegions().size(); i++) {
                                            if (province.getSave().getGame().getColonialRegions().get(i).getProvinces().contains(province.getId())) {
                                                colonialRegionIndex = i;
                                            }
                                        }

                                        if (colonialRegionIndex == null) {
                                            return false;
                                        }

                                        return value.equalsIgnoreCase(religion.getPapacy().getColonyClaim(colonialRegionIndex));
                                    });
                } else {
                    return !province.getSave()
                                    .getReligions()
                                    .getReligions()
                                    .values()
                                    .stream()
                                    .anyMatch(religion -> {
                                        if (religion.getPapacy() == null) {
                                            return false;
                                        }

                                        Integer colonialRegionIndex = null;

                                        for (int i = 0; i < province.getSave().getGame().getColonialRegions().size(); i++) {
                                            if (province.getSave().getGame().getColonialRegions().get(i).getProvinces().contains(province.getId())) {
                                                colonialRegionIndex = i;
                                            }
                                        }

                                        if (colonialRegionIndex == null) {
                                            return false;
                                        }

                                        return "yes".equalsIgnoreCase(value)
                                               != Eu4Utils.DEFAULT_TAG.equalsIgnoreCase(religion.getPapacy().getColonyClaim(colonialRegionIndex));
                                    });
                }
            case "is_sea":
                return "yes".equalsIgnoreCase(value) == province.isOcean();
            case "is_state":
                return "yes".equalsIgnoreCase(value) == (province.getSaveArea() != null && province.getSaveArea().getCountriesStates() != null
                                                         && province.getSaveArea().getCountryState(province.getOwner()) != null);
            case "is_state_core":
                other = province.getSave().getCountry(value);
                return province.getCores().contains(other) && province.getSaveArea() != null && province.getSaveArea().getCountriesStates() != null
                       && province.getSaveArea().getCountryState(other) != null;
            case "is_strongest_trade_power":
                other = province.getSave().getCountry(value);
                return other.equals(province.getSave().getTradeNodes().get(province.getTrade()).getTopPower().entrySet().iterator().next().getKey());
            case "is_territory":
                return "yes".equalsIgnoreCase(value) == (province.getSaveArea() == null || province.getSaveArea().getCountriesStates() == null
                                                         || province.getSaveArea().getCountryState(province.getOwner()) == null);
            case "is_territorial_core":
                other = province.getSave().getCountry(value);
                return (province.getSaveArea() == null || province.getSaveArea().getCountriesStates() == null
                        || province.getSaveArea().getCountryState(other) == null)
                       && province.getCores().contains(other);
            case "is_wasteland":
                return "yes".equalsIgnoreCase(value) == (province.isImpassable());
            case "is_year":
                return province.getSave().getDate().getYear() == NumbersUtils.toInt(value);
            case "knowledge_sharing":
                return "yes".equalsIgnoreCase(value) ==
                       (province.getOwner() != null && province.getArea().equals(province.getOwner().getCapital().getArea())
                        && province.getSave()
                                   .getDiplomacy()
                                   .getKnowledgeSharing()
                                   .stream()
                                   .anyMatch(knowledgeSharing -> knowledgeSharing.getSecond().equals(province.getOwner())));
            case "light_ships_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getLightShips().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getLightShips().stream().anyMatch(ship -> ship.getNavy().getCountry().equals(other));
                }
            case "likely_rebels":
                return value.equalsIgnoreCase(ClausewitzUtils.removeQuotes(province.getRebels()));
            case "local_autonomy":
                return NumbersUtils.doubleOrDefault(province.getLocalAutonomy()) >= NumbersUtils.toDouble(value);
            case "months_since_defection": //Todo
                break;
            case "nationalism":
                return NumbersUtils.intOrDefault(province.getNationalism()) >= NumbersUtils.toInt(value);
            case "native_ferocity":
                return NumbersUtils.intOrDefault(province.getNativeFerocity()) >= NumbersUtils.toInt(value);
            case "native_hostileness":
                return NumbersUtils.intOrDefault(province.getNativeHostileness()) >= NumbersUtils.toInt(value);
            case "national_defense":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getCores().contains(province.getOwner())
                                                         && province.getCulture() != null
                                                         && province.getCulture()
                                                                    .getCultureGroup()
                                                                    .equals(province.getOwner().getPrimaryCulture().getCultureGroup())
                                                         && province.getOwner()
                                                                    .getActiveWars()
                                                                    .stream()
                                                                    .anyMatch(activeWar -> activeWar.getDefender(province.getOwner()) != null));
            case "native_size":
                return NumbersUtils.intOrDefault(province.getNativeSize()) >= NumbersUtils.toInt(value);
            case "num_of_times_improved":
                return province.getTotalImproveCount() >= NumbersUtils.toInt(value);
            case "non_accepted_culture_republic":
                return province.getOwner() != null
                       && province.getCulture() != null
                       && province.getOwner()
                                  .getGovernment()
                                  .getReforms()
                                  .stream()
                                  .anyMatch(reform -> reform.isRepublic().getKey()
                                                      && (reform.isRepublic().getValue() == null
                                                          || reform.isRepublic().getValue().apply(province.getOwner(), province.getOwner())))
                       && !province.getCulture().getCultureGroup().equals(province.getOwner().getPrimaryCulture().getCultureGroup())
                       && !province.getOwner().getAcceptedCultures().contains(province.getCulture());
            case "normal_or_historical_nations":
                return "yes".equalsIgnoreCase(value) == (NationSetup.NORMAL.equals(province.getSave().getGameplayOptions().getNationSetup())
                                                         || NationSetup.HISTORICAL.equals(province.getSave().getGameplayOptions().getNationSetup()));
            case "normal_province_values":
                return "yes".equalsIgnoreCase(value) == (ProvinceTaxManpower.HISTORICAL.equals(
                        province.getSave().getGameplayOptions().getProvinceTaxManpower()));
            case "num_of_electors":
                return !province.getSave().getHre().dismantled() && province.getSave().getHre().getElectors().size() >= NumbersUtils.toInt(value);
            case "num_of_foreign_hre_provinces":
                return !province.getSave().getHre().dismantled()
                       && province.getSave()
                                  .getProvinces()
                                  .values()
                                  .stream()
                                  .filter(prov -> prov.inHre()
                                                  && ((prov.getOwner().getOverlord() == null && !prov.getOwner().getCapital().inHre())
                                                      || (prov.getOwner().getOverlord() != null && !prov.getOwner().getOverlord().getCapital().inHre())))
                                  .count() >= NumbersUtils.toInt(value);
            case "owned_by":
                return value.equalsIgnoreCase(province.getOwnerTag());
            case "owner_has_patriarchs":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getOwner().getReligion() != null &&
                                                         BooleanUtils.toBoolean(province.getOwner().getReligion().getGameReligion().hasPatriarchs()));
            case "papacy_active":
                return province.getSave().getReligions().getReligions().values().stream().anyMatch(religion -> religion.getPapacy() != null);
            case "previous_owner":
                return province.getHistory().getOwners().size() >= 2
                       && value.equalsIgnoreCase(new ArrayList<>(province.getHistory().getOwners().values()).get(province.getHistory().getOwners().size() - 2)
                                                                                                            .getTag());
            case "production_leader":
                return "yes".equalsIgnoreCase(value) == (province.getOwner() != null && province.getTradeGood() != null
                                                         && province.getSave().getProductionLeaders().get(province.getTradeGood()).equals(province.getOwner()));
            case "province_has_center_of_trade_of_level":
                return NumbersUtils.intOrDefault(province.getCenterOfTradeLevel()) >= NumbersUtils.toInt(value);
            case "province_id":
                return province.getId() == NumbersUtils.toInt(value);
            case "province_trade_power":
                return NumbersUtils.doubleOrDefault(province.getTradePower()) >= NumbersUtils.toDouble(value);
            case "pure_unrest": // Fixme ??
                break;
            case "real_day_of_year":
                return province.getSave().getDate().getDayOfYear() == NumbersUtils.toInt(value);
            case "reform_desire":
                return province.getSave()
                               .getReligions()
                               .getReligions()
                               .values()
                               .stream()
                               .anyMatch(religion -> religion.getPapacy() != null && religion.getPapacy().getReformDesire() >= NumbersUtils.toDouble(value));
            case "region":
                return province.getArea() != null && value.equalsIgnoreCase(province.getArea().getRegion().getName());
            case "religion":
                return province.getReligion() != null && rawValueToReligion(rawValue, province).equalsIgnoreCase(province.getReligionName());
            case "religion_group":
                return province.getReligion() != null && rawValueToReligionGroup(rawValue, province).equalsIgnoreCase(
                        province.getReligion().getReligionGroup().getName());
            case "revolution_target_exists":
                return "yes".equalsIgnoreCase(value) == (province.getSave().getRevolution().getRevolutionTarget() != null);
            case "sieged_by":
                other = province.getSave().getCountry(value);
                return province.getOwner().isAtWar() && province.getSave()
                                                                .getCombats()
                                                                .getSiegeCombats()
                                                                .stream()
                                                                .anyMatch(siegeCombat -> province.equals(siegeCombat.getLocation())
                                                                                         && siegeCombat.getAttacker()
                                                                                                       .getParticipatingCountries()
                                                                                                       .contains(other));
            case "start_date":
                return province.getSave().getStartDate().equals(Eu4Utils.stringToDate(value));
            case "started_in":
                return province.getSave().getStartDate().equals(Eu4Utils.stringToDate(value))
                       || province.getSave().getStartDate().isAfter(Eu4Utils.stringToDate(value));
            case "superregion":
                return province.getArea() != null && value.equalsIgnoreCase(province.getArea().getRegion().getSuperRegion().getName());
            case "total_number_of_cardinals":
                return province.getSave()
                               .getReligions()
                               .getReligions()
                               .values()
                               .stream()
                               .anyMatch(religion -> religion.getPapacy() != null && religion.getPapacy().getCardinals().size() >= NumbersUtils.toInt(value));
            case "transports_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getTransports().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getTransports().stream().anyMatch(ship -> ship.getNavy().getCountry().equals(other));
                }
            case "trade_company_region":
                return province.getSave().getGame().getTradeCompany(value).getProvinces().contains(province.getId());
            case "trade_company_size":
                return province.getOwner()
                               .getTradeCompanies()
                               .stream()
                               .anyMatch(tradeCompany -> tradeCompany.getProvinces().contains(province)
                                                         && tradeCompany.getProvinces().size() >= NumbersUtils.toInt(value));
            case "trade_goods":
                return value.equalsIgnoreCase(province.getTradeGoods());
            case "unit_has_leader":
                return province.getArmies().stream().anyMatch(army -> army.getLeader() != null);
            case "unit_in_battle":
                return "yes".equalsIgnoreCase(value) == province.getSave()
                                                                .getCombats()
                                                                .getLandCombats()
                                                                .stream()
                                                                .anyMatch(landCombat -> province.equals(landCombat.getLocation()));
            case "unit_in_siege":
                return "yes".equalsIgnoreCase(value) == province.getSave()
                                                                .getCombats()
                                                                .getSiegeCombats()
                                                                .stream()
                                                                .anyMatch(siegeCombat -> province.equals(siegeCombat.getLocation()));
            case "units_in_province":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return province.getUnits().size() >= integer;
                } else {
                    other = province.getSave().getCountry(value);
                    return province.getUnits().stream().anyMatch(abstractRegiment -> abstractRegiment.getArmy().getCountry().equals(other));
                }
            case "unrest":
                return NumbersUtils.doubleOrDefault(province.getUnrest()) >= NumbersUtils.toDouble(value);
        }

        LOGGER.debug("Don't know how to manage province condition: {} !", condition);
        return false;
    }

    public static boolean applyConditionToProvince(Province province, String condition, String rawValue) {
        String value;
        Country other;
        Building building;
        ProvinceHistoryItemI historyItem;
        ConditionAnd scriptedTrigger;

        if ("ROOT".equals(rawValue) || "FROM".equals(rawValue)) {
            value = Optional.ofNullable(province.getHistoryItemAt(province.getGame().getStartDate()).getOwner()).map(Country::getTag).orElse(null);
        } else {
            value = rawValue;
        }

        if ((scriptedTrigger = province.getGame().getScriptedTrigger(condition)) != null) {
            return scriptedTrigger.apply(province);
        }

        switch (condition.toLowerCase()) {
            case "always":
                return "yes".equalsIgnoreCase(value);
            case "area":
                return province.getGame().getArea(value).getProvinces().contains(province.getId());
            case "base_manpower":
                return value != null
                       && NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseManpower()) >=
                          NumbersUtils.toInt(value);
            case "base_production":
                return value != null
                       && NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseProduction()) >=
                          NumbersUtils.toInt(value);
            case "base_tax":
                return value != null
                       && NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseTax()) >= NumbersUtils.toInt(value);
            case "can_build":
                building = province.getGame().getBuilding(value);
                return building != null && (building.getTrigger() == null || building.getTrigger().apply(province));
            case "capital":
                return "yes".equalsIgnoreCase(value) == (province.getHistoryItemAt(province.getGame().getStartDate()).getOwner() != null &&
                                                         province.getHistoryItemAt(province.getGame().getStartDate())
                                                                 .getOwner()
                                                                 .getHistoryItemAt(province.getGame().getStartDate())
                                                                 .getCapital()
                                                                 .equals(province));
            case "colonial_region":
                return province.getGame().getColonialRegion(value).getProvinces().contains(province.getId());
            case "continent":
                return province.getContinent() != null && value != null && value.equalsIgnoreCase(province.getContinent().getName());
            case "controlled_by":
                return value != null && value.equalsIgnoreCase(ClausewitzUtils.removeQuotes(
                        Optional.ofNullable(province.getHistoryItemAt(province.getGame().getStartDate()).getController()).map(Country::getTag).orElse(null)));
            case "country_or_subject_holds":
            case "country_or_vassal_holds":
                other = province.getGame().getCountry(value);
                return value != null && province.getHistoryItemAt(province.getGame().getStartDate()).getOwner() != null
                       && (province.getHistoryItemAt(province.getGame().getStartDate()).getOwner().equals(other) ||
                           other.equals(
                                   province.getHistoryItemAt(province.getGame().getStartDate()).getOwner().getOverlordAt(province.getGame().getStartDate())));
            case "culture":
                return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture() != null &&
                       rawValueToCulture(rawValue, province).equalsIgnoreCase(
                               province.getHistoryItemAt(province.getGame().getStartDate()).getCulture().getName());
            case "culture_group":
                return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture() != null
                       && province.getHistoryItemAt(province.getGame().getStartDate())
                                  .getCulture()
                                  .getCultureGroup()
                                  .getName()
                                  .equals(rawValueToCultureGroup(rawValue, province));
            case "current_age":
                return province.getGame()
                               .getAges()
                               .stream()
                               .filter(age -> age.getStart() <= province.getGame().getStartDate().getYear())
                               .collect(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparingInt(Age::getStart))))
                               .last()
                               .getName()
                               .equals(value);
            case "development":
                return value != null && NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseManpower()) +
                                        NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseProduction()) +
                                        NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getBaseTax())
                                        >= NumbersUtils.toDouble(value);
            case "exists":
                if ("yes".equalsIgnoreCase(value)) {
                    return true;
                } else {
                    return province.getGame().getCountry(value) != null;
                }
            case "fort_level":
                return value != null && province.getHistoryItemAt(province.getGame().getStartDate())
                                                .getCumulatedBuildings()
                                                .stream()
                                                .anyMatch(build -> build.getModifiers().hasModifier("fort_level")
                                                                   && build.getModifiers().getModifier("fort_level") >= NumbersUtils.toInt(value));
            case "has_building":
                return value != null && province.getHistoryItemAt(province.getGame().getStartDate())
                                                .getCumulatedBuildings()
                                                .stream()
                                                .anyMatch(provinceBuilding -> value.equalsIgnoreCase(provinceBuilding.getName()));
            case "has_climate":
                return value != null && value.equalsIgnoreCase(province.getClimate());
            case "has_owner_accepted_culture":
                return "yes".equalsIgnoreCase(value) == ((historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                                                         historyItem.getCulture() != null &&
                                                         historyItem.getOwner()
                                                                    .getHistoryItemAt(province.getGame().getStartDate())
                                                                    .getCumulatedAcceptedCultures()
                                                                    .contains(historyItem.getCulture().getName()));
            case "has_owner_culture":
                return "yes".equalsIgnoreCase(value) == ((historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                                                         historyItem.getCulture() != null &&
                                                         historyItem.getOwner()
                                                                    .getHistoryItemAt(province.getGame().getStartDate())
                                                                    .getPrimaryCulture()
                                                                    .equals(historyItem.getCulture()));
            case "has_owner_culture_group":
                return "yes".equalsIgnoreCase(value) == ((historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                                                         historyItem.getCulture() != null &&
                                                         historyItem.getOwner()
                                                                    .getHistoryItemAt(province.getGame().getStartDate())
                                                                    .getPrimaryCulture()
                                                                    .getCultureGroup()
                                                                    .equals(historyItem.getCulture().getCultureGroup()));
            case "has_owner_religion":
                return "yes".equalsIgnoreCase(value) == ((historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                                                         historyItem.getReligion() != null &&
                                                         historyItem.getOwner()
                                                                    .getHistoryItemAt(province.getGame().getStartDate())
                                                                    .getReligion()
                                                                    .equals(historyItem.getReligion()));
            case "has_port":
                return "yes".equalsIgnoreCase(value) == (province.isPort());
            case "has_terrain": //Don't do
                break;
            case "has_winter":
                return value != null && value.equalsIgnoreCase(province.getWinter());
            case "has_monsoon":
                return value != null && value.equalsIgnoreCase(province.getClimate());
            case "hre_size":
                break; //Todo
            case "in_capital_state":
                return province.getArea() != null &&
                       "yes".equalsIgnoreCase(value) == ((historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                                                         province.getArea()
                                                                 .equals(historyItem.getOwner()
                                                                                    .getHistoryItemAt(province.getGame().getStartDate())
                                                                                    .getCapital()
                                                                                    .getArea()));
            case "is_capital":
                return "yes".equalsIgnoreCase(value) == (province.getGame()
                                                                 .getCountries()
                                                                 .stream()
                                                                 .anyMatch(country -> province.equals(
                                                                         country.getHistoryItemAt(province.getGame().getStartDate()).getCapital())));
            case "is_capital_of":
                return value != null && province.equals(province.getGame().getCountry(value).getHistoryItemAt(province.getGame().getStartDate()).getCapital());
            case "is_city":
                return "yes".equalsIgnoreCase(value);
            case "is_core":
                return province.getHistoryItemAt(province.getGame().getStartDate())
                               .getCumulatedCores()
                               .stream()
                               .anyMatch(country -> country.getTag().equalsIgnoreCase(value));
            case "is_empty":
                return "yes".equalsIgnoreCase(value) == (province.getHistoryItemAt(province.getGame().getStartDate()).getController() != null);
            case "is_institution_origin":
                return "yes".equalsIgnoreCase(value) == Objects.equals(province.getId(), province.getGame().getInstitution(value).getHistoricalStartProvince());
            case "is_lake":
                return "yes".equalsIgnoreCase(value) == province.isLake();
            case "is_cored":
                return "yes".equalsIgnoreCase(value) == (province.getHistoryItemAt(province.getGame().getStartDate()).getOwner() != null &&
                                                         province.getHistoryItemAt(province.getGame().getStartDate())
                                                                 .getCumulatedCores()
                                                                 .contains(province.getHistoryItemAt(province.getGame().getStartDate()).getOwner()));
            case "is_month":
            case "real_month_of_year":
                return Objects.equals(province.getGame().getStartDate().getMonthValue(), NumbersUtils.toInt(value));
            case "is_node_in_trade_company_region":
                return "yes".equalsIgnoreCase(value) == province.getGame()
                                                                .getTradeCompanies()
                                                                .stream()
                                                                .anyMatch(tradeCompany -> tradeCompany.getProvinces().contains(province.getId()));
            case "is_occupied":
                return "yes".equalsIgnoreCase(value) == (province.getHistoryItemAt(province.getGame().getStartDate()).getController() != null);
            case "is_ocean":
                return "yes".equalsIgnoreCase(value) == province.isOcean();
            case "is_part_of_hre":
                return "yes".equalsIgnoreCase(value) == BooleanUtils.toBoolean(province.getHistoryItemAt(province.getGame().getStartDate()).getHre());
            case "is_religion_enabled":
                Religion religion = province.getGame().getReligion(rawValueToReligion(rawValue, province));
                return religion != null && (religion.getDate() == null || (religion.getDate().isBefore(province.getGame().getStartDate())));
            case "is_sea":
                return "yes".equalsIgnoreCase(value) == province.isOcean();
            case "is_wasteland":
                return "yes".equalsIgnoreCase(value) == (province.isImpassable());
            case "is_year":
                return Objects.equals(province.getGame().getStartDate().getYear(), NumbersUtils.toInt(value));
            case "months_since_defection": //Todo
                break;
            case "num_of_electors":
                return value != null && province.getGame().getHreEmperorAt(province.getGame().getStartDate()) != null &&
                       province.getGame()
                               .getCountries()
                               .stream()
                               .map(c -> c.getHistoryItemAt(province.getGame().getStartDate()))
                               .filter(i -> BooleanUtils.toBoolean(i.getElector()))
                               .count() >= NumbersUtils.toInt(value);
            case "num_of_foreign_hre_provinces":
                break; //Too complexe and very slow
            case "owned_by":
                return value != null && value.equalsIgnoreCase(
                        Optional.ofNullable(province.getHistoryItemAt(province.getGame().getStartDate()).getOwner()).map(Country::getTag).orElse(null));
            case "owner_has_patriarchs":
                return "yes".equalsIgnoreCase(value) == (province.getHistoryItemAt(province.getGame().getStartDate()).getOwner() != null &&
                                                         province.getHistoryItemAt(province.getGame().getStartDate())
                                                                 .getOwner()
                                                                 .getHistoryItemAt(province.getGame().getStartDate())
                                                                 .getReligion()
                                                                 .hasPatriarchs());
            case "papacy_active":
                return province.getGame()
                               .getReligions()
                               .stream()
                               .anyMatch(r -> r.getPapacy() != null && (r.getDate() == null || province.getGame().getStartDate().isAfter(r.getDate())));
            case "province_has_center_of_trade_of_level":
                return value != null &&
                       NumbersUtils.intOrDefault(province.getHistoryItemAt(province.getGame().getStartDate()).getCenterOfTrade()) >= NumbersUtils.toInt(value);
            case "province_id":
                return value != null && province.getId() == NumbersUtils.toInt(value);
            case "real_day_of_year":
                return value != null && province.getGame().getStartDate().getDayOfYear() == NumbersUtils.toInt(value);
            case "region":
                return value != null && province.getArea() != null && value.equalsIgnoreCase(province.getArea().getRegion().getName());
            case "religion":
                return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion() != null &&
                       rawValueToReligion(rawValue, province).equalsIgnoreCase(
                               province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getName());
            case "religion_group":
                return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion() != null &&
                       rawValueToReligionGroup(rawValue, province).equalsIgnoreCase(
                               province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getReligionGroup().getName());
            case "start_date":
                return province.getGame().getStartDate().equals(Eu4Utils.stringToDate(value));
            case "started_in":
                return province.getGame().getStartDate().equals(Eu4Utils.stringToDate(value))
                       || province.getGame().getStartDate().isAfter(Eu4Utils.stringToDate(value));
            case "superregion":
                return province.getArea() != null && value != null && value.equalsIgnoreCase(province.getArea().getRegion().getSuperRegion().getName());
            case "trade_company_region":
                return province.getGame().getTradeCompany(value).getProvinces().contains(province.getId());
            case "trade_goods":
                return value != null && value.equalsIgnoreCase(province.getHistoryItemAt(province.getGame().getStartDate()).getTradeGoods().getName());
        }

        LOGGER.debug("Don't know how to manage province condition: {} !", condition);
        return false;
    }

    public static boolean applyScopeToProvince(SaveProvince province, ConditionAbstract condition) {
        SaveCountry country;
        SaveTradeNode tradeNode;

        if ((country = province.getSave().getCountry(condition.getName())) != null) {
            return condition.apply(country, country);
        }

        if (province.getSave().getProvince(NumbersUtils.toInt(condition.getName())) != null) {
            return condition.apply(province.getSave().getProvince(NumbersUtils.toInt(condition.getName())));
        }

        switch (condition.getName().toLowerCase()) {
            case "or":
                return condition.or().apply(province);
            case "and", "if", "hidden_trigger":
                return condition.and().apply(province);
            case "not":
                return condition.not().apply(province);
            case "controller":
                return condition.apply(province.getController(), province.getController());
            case "crusade_target":
                return province.getSave()
                               .getReligions()
                               .getReligions()
                               .values()
                               .stream()
                               .anyMatch(religion -> religion.getPapacy() != null && religion.getPapacy().getCrusadeTarget() != null
                                                     && condition.apply(religion.getPapacy().getCrusadeTarget(), religion.getPapacy().getCrusadeTarget()));
            case "emperor":
                return !province.getSave().getHre().dismantled() && condition.apply(province.getSave().getHre().getEmperor(),
                                                                                    province.getSave().getHre().getEmperor());
            case "had_global_flag":
                return province.getSave().getFlags().contains(condition.getCondition("flag"))
                       && province.getSave().getFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                                  .isBefore(province.getSave().getDate());
            case "had_province_flag":
                return province.getFlags().contains(condition.getCondition("flag"))
                       && province.getFlags().get(condition.getCondition("flag")).plusDays(NumbersUtils.toInt(condition.getCondition("days")))
                                  .isBefore(province.getSave().getDate());
            case "has_local_modifier_value":
                return province.getModifier(ModifiersUtils.getModifier(condition.getCondition("which")))
                       > NumbersUtils.toDouble(condition.getCondition("value"));
            case "has_trade_modifier":
                country = province.getSave().getCountry(condition.getCondition("who"));
                return province.getSave()
                               .getTradeNodes()
                               .get(province.getTrade())
                               .getCountry(country)
                               .getModifiers()
                               .containsKey(condition.getCondition("key"));
            case "most_province_trade_power":
                country = province.getSave().getTradeNodes().get(province.getTrade()).getTopProvinces().entrySet().iterator().next().getKey();
                return condition.apply(country, country);
            case "num_investments_in_trade_company_region":
                String investment = condition.getCondition("investment");
                return province.getOwner() != null && BooleanUtils.toBoolean(province.activeTradeCompany())
                       && province.getSaveArea() != null &&
                       province.getSaveArea()
                               .getInvestment(province.getOwner())
                               .getInvestments()
                               .stream()
                               .filter(invest -> invest.getName().equalsIgnoreCase(investment))
                               .count()
                       >= NumbersUtils.toInt(condition.getCondition("value"));
            case "num_of_units_in_province":
                UnitType type = UnitType.value(condition.getCondition("type"));
                SaveCountry finalCountry = province.getSave().getCountry(condition.getCondition("who"));

                return province.getUnits().stream().filter(regiment -> {
                    if (type != null && !type.equals(regiment.getUnitType())) {
                        return false;
                    }

                    if (finalCountry != null && !finalCountry.equals(regiment.getArmy().getCountry())) {
                        return false;
                    }

                    return true;
                }).count() >= NumbersUtils.toInt(condition.getCondition("amount"));
            case "owner":
                return condition.apply(province.getOwner(), province.getOwner());
            case "privateer_power":
                country = province.getSave().getCountry(condition.getCondition("country"));
                tradeNode = province.getSave().getTradeNodes().get(province.getTrade());
                return BigDecimal.valueOf(NumbersUtils.doubleOrDefault(tradeNode.getCountry(country).getPrivateerMission()))
                                 .compareTo(new BigDecimal(condition.getCondition("share")).multiply(BigDecimal.valueOf(tradeNode.getTotal()))) >= 0;
            case "revolution_target":
                return province.getSave().getRevolution() != null && province.getSave().getRevolution().getRevolutionTarget() != null &&
                       condition.apply(province.getSave().getRevolution().getRevolutionTarget(), province.getSave().getRevolution().getRevolutionTarget());
            case "sea_zone": //Todo Refers to the sea province on which the current land province scope has a port.
                break;
            case "strongest_trade_power":
                country = province.getSave().getTradeNodes().get(province.getTrade()).getTopPower().entrySet().iterator().next().getKey();
                return condition.apply(country, country);
        }

        LOGGER.debug("Don't know how to manage province scope: {} !", condition);
        return false;
    }

    public static boolean applyScopeToProvince(Province province, ConditionAbstract condition) {
        Country country;
        ProvinceHistoryItemI historyItem;

        if ((country = province.getGame().getCountry(condition.getName())) != null) {
            return condition.apply(country, country);
        }

        if (Optional.ofNullable(NumbersUtils.toInt(condition.getName())).map(i -> province.getGame().getProvince(i)).isPresent()) {
            return condition.apply(province.getGame().getProvince(NumbersUtils.toInt(condition.getName())));
        }

        switch (condition.getName().toLowerCase()) {
            case "or":
                return condition.or().apply(province);
            case "and", "if", "hidden_trigger":
                return condition.and().apply(province);
            case "not":
                return condition.not().apply(province);
            case "controller":
                return (historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getController() != null &&
                       condition.apply(historyItem.getController(), historyItem.getController());
            case "emperor":
                return province.getGame().getHreEmperorAt(province.getGame().getStartDate()) != null &&
                       condition.apply(province.getGame().getCountry(province.getGame().getHreEmperorAt(province.getGame().getStartDate()).getTag()),
                                       province.getGame().getCountry(province.getGame().getHreEmperorAt(province.getGame().getStartDate()).getTag()));
            case "owner":
                return (historyItem = province.getHistoryItemAt(province.getGame().getStartDate())).getOwner() != null &&
                       condition.apply(historyItem.getOwner(), historyItem.getOwner());
            case "sea_zone": //Todo Refers to the sea province on which the current land province scope has a port.
                break;
        }

        LOGGER.debug("Don't know how to manage province scope: {} !", condition);
        return false;
    }

    public static String rawValueToReligion(String rawValue, SaveCountry root, SaveCountry from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return Optional.ofNullable(root).map(SaveCountry::getReligion).map(SaveReligion::getName).orElse(null);
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return Optional.ofNullable(from).map(SaveCountry::getReligion).map(SaveReligion::getName).orElse(null);
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligion(String rawValue, Country root, Country from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return Optional.ofNullable(root)
                           .map(c -> c.getHistoryItemAt(c.getGame().getStartDate()))
                           .map(CountryHistoryItemI::getReligion)
                           .map(Religion::getName)
                           .orElse(null);
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return Optional.ofNullable(from)
                           .map(c -> c.getHistoryItemAt(c.getGame().getStartDate()))
                           .map(CountryHistoryItemI::getReligion)
                           .map(Religion::getName)
                           .orElse(null);
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligionGroup(String rawValue, SaveCountry root, SaveCountry from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getReligion().getGameReligion().getReligionGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getReligion().getGameReligion().getReligionGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligionGroup(String rawValue, Country root, Country from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getHistoryItemAt(root.getGame().getStartDate()).getReligion().getReligionGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getHistoryItemAt(root.getGame().getStartDate()).getReligion().getReligionGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCulture(String rawValue, SaveCountry root, SaveCountry from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getPrimaryCulture().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getPrimaryCulture().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCulture(String rawValue, Country root, Country from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getHistoryItemAt(root.getGame().getStartDate()).getPrimaryCulture().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getHistoryItemAt(from.getGame().getStartDate()).getPrimaryCulture().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCultureGroup(String rawValue, SaveCountry root, SaveCountry from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getPrimaryCulture().getCultureGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getPrimaryCulture().getCultureGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCultureGroup(String rawValue, Country root, Country from) {
        if ("ROOT".equalsIgnoreCase(rawValue)) {
            return root.getHistoryItemAt(root.getGame().getStartDate()).getPrimaryCulture().getCultureGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return from.getHistoryItemAt(from.getGame().getStartDate()).getPrimaryCulture().getCultureGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligion(String rawValue, SaveProvince province) {
        if (province.getReligion() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getReligion().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getReligion().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligion(String rawValue, Province province) {
        if (province.getHistoryItemAt(province.getGame().getStartDate()).getReligion() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligionGroup(String rawValue, SaveProvince province) {
        if (province.getReligion() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getReligion().getGameReligion().getReligionGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getReligion().getGameReligion().getReligionGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToReligionGroup(String rawValue, Province province) {
        if (province.getHistoryItemAt(province.getGame().getStartDate()).getReligion() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getReligionGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getReligion().getReligionGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCulture(String rawValue, SaveProvince province) {
        if (province.getCulture() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getCulture().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getCulture().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCulture(String rawValue, Province province) {
        if (province.getHistoryItemAt(province.getGame().getStartDate()).getCulture() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCultureGroup(String rawValue, SaveProvince province) {
        if (province.getCulture() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getCulture().getCultureGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getCulture().getCultureGroup().getName();
        } else {
            return rawValue;
        }
    }

    public static String rawValueToCultureGroup(String rawValue, Province province) {
        if (province.getHistoryItemAt(province.getGame().getStartDate()).getCulture() == null) {
            return rawValue;
        } else if ("ROOT".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture().getCultureGroup().getName();
        } else if ("FROM".equalsIgnoreCase(rawValue)) {
            return province.getHistoryItemAt(province.getGame().getStartDate()).getCulture().getCultureGroup().getName();
        } else {
            return rawValue;
        }
    }
}
