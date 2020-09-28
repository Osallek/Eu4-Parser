package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.Condition;
import com.osallek.eu4parser.model.game.Culture;
import com.osallek.eu4parser.model.game.TradeGood;
import com.osallek.eu4parser.model.save.Power;
import com.osallek.eu4parser.model.save.TradeLeague;
import com.osallek.eu4parser.model.save.country.ActivePolicy;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.country.Estate;
import com.osallek.eu4parser.model.save.country.Faction;
import com.osallek.eu4parser.model.save.country.Income;
import com.osallek.eu4parser.model.save.country.Leader;
import com.osallek.eu4parser.model.save.country.LeaderType;
import com.osallek.eu4parser.model.save.country.Modifier;
import com.osallek.eu4parser.model.save.country.PowerProjection;
import com.osallek.eu4parser.model.save.country.Queen;
import com.osallek.eu4parser.model.save.country.TradeCompany;
import com.osallek.eu4parser.model.save.diplomacy.QuantifyDatableRelation;
import com.osallek.eu4parser.model.save.diplomacy.SubjectType;
import com.osallek.eu4parser.model.save.empire.HreReligionStatus;
import com.osallek.eu4parser.model.save.gameplayoptions.NationSetup;
import com.osallek.eu4parser.model.save.gameplayoptions.ProvinceTaxManpower;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ConditionsUtils {

    private static final Logger LOGGER = Logger.getLogger(ConditionsUtils.class.getName());

    private ConditionsUtils() {}

    public static boolean applyConditionToCountry(Country country, Country root, Country from, String condition, String rawValue) {
        TradeGood tradeGood;
        Country other;
        SaveProvince saveProvince;
        Integer integer;
        Double aDouble;
        Calendar calendar = Calendar.getInstance();
        Calendar calendar2 = Calendar.getInstance();
        String value;

        if ("ROOT".equals(rawValue)) {
            value = root.getTag().toUpperCase();
        } else if ("FROM".equals(rawValue)) {
            value = from.getTag().toUpperCase();
        } else {
            value = rawValue;
        }

        if (country.getSave().getGame().getAdvisor(condition) != null) {
            return country.getAdvisors()
                          .values()
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
            return country.getIdeaGroups().getIdeaGroupsNames().containsKey(condition)
                   && country.getIdeaGroups().getIdeaGroupsNames().get(condition) >= NumbersUtils.toInt(value);
        }

        if (country.getSave().getGame().getReligion(condition) != null) {
            //Todo tolerance
        }

        if ((tradeGood = country.getSave().getGame().getTradeGood(condition)) != null) {
            return country.getOwnedProvinces()
                          .stream()
                          .filter(province -> tradeGood.equals(province.getTradeGood()))
                          .count() >= NumbersUtils.toInt(value);
        }

        switch (condition.toLowerCase()) {
            case "absolutism":
                return country.getAbsolutism() >= NumbersUtils.toInt(value);
            case "accepted_culture":
                return country.getAcceptedCultures().stream().map(Culture::getName).anyMatch(value::equals);
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
                return country.getAdvisors().values().stream().anyMatch(advisor -> advisor.getType().equals(value));
            case "advisor_exists":
                return country.getSave().getAdvisors().containsKey(Integer.parseInt(value));
            case "ai":
                return !country.isHuman();
            case "ai_attitude": //Todo Object
                break;
            case "alliance_with":
                return country.getAllies().stream().map(Country::getTag).anyMatch(tag -> tag.equalsIgnoreCase(value));
            case "allows_female_emperor":
                return country.getSave().getHre() != null && BooleanUtils.toBoolean(country.getSave().getHre().getAllowsFemaleEmperor());
            case "always":
                return "yes".equals(value);
            case "army_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getArmySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getArmySize() >= other.getArmySize();
                }
            case "army_size_percentage": //Todo fl
                break;
            case "army_strength": // Todo object
                break;
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
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize())).compareTo(BigDecimal.valueOf(country.getNbArtillery())) >= 0;
            case "at_war_with_religious_enemy":
                return country.getCurrentAtWarWith().stream().anyMatch(c -> !c.getReligion().equals(country.getReligion()));
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
            case "border_distance": //Todo object
                break;
            case "calc_true_if": //Todo object
                break;
            case "can_be_overlord": //Todo object
                break;
            case "can_create_vassals":
                return country.getOverlord() == null;
            case "can_heir_be_child_of_consort": //Fixme ???
                break;
            case "can_justify_trade_conflict": //Fixme ??
                break;
            case "can_migrate": //Fixme cool down reductions
                return ("gov_native_council".equals(country.getGovernmentName().getName())
                        || "siberian_native_council".equals(country.getGovernmentName().getName()))
                       && country.getOwnedProvinces().size() == 1 && !country.isAtWar() &&
                       (country.getLastMigration() == null || country.getLastMigration().before(DateUtils.addYears(country.getSave().getDate(), -5)));
            case "capital":
                return country.getCapitalId() != null && country.getCapitalId().equals(NumbersUtils.toInt(value));
            case "capital_distance": //Fixme distance
                break;
            case "capital_trade_node":
                return Objects.equals(country.getTradePort().getId(), NumbersUtils.toInt(value));
            case "cavalry_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize())).compareTo(BigDecimal.valueOf(country.getNbCavalry())) >= 0;
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
                return country.getSubjects().stream().filter(Country::isColony).count() >= NumbersUtils.toInt(value);
            case "colony_claim":
                return country.getClaimProvinces().stream().anyMatch(province -> province.isColony() && province.getOwner().getTag().equals(value));
            case "consort_adm":
                return country.getConsort() != null && country.getConsort().getAdm() >= NumbersUtils.toInt(value);
            case "consort_age":
                return country.getConsort() != null
                       && DateUtils.addYears(country.getConsort().getBirthDate(), NumbersUtils.toInt(value)).before(country.getSave().getDate());
            case "consort_culture":
                return country.getConsort() != null && country.getConsort().getCulture().getName().equals(value);
            case "consort_dip":
                return country.getConsort() != null && country.getConsort().getDip() >= NumbersUtils.toInt(value);
            case "consort_has_personality":
                return country.getConsort() != null && country.getConsort().getPersonalities() != null
                       && country.getConsort().getPersonalities().getPersonalities().contains(value);
            case "consort_mil":
                return country.getConsort() != null && country.getConsort().getMil() >= NumbersUtils.toInt(value);
            case "consort_religion":
                return country.getConsort() != null && country.getConsort().getReligion().getName().equals(value);
            case "controls":
                return country.getSave().getProvince(NumbersUtils.toInt(value)).getController().equals(country);
            case "core_claim":
                return country.getCoreProvinces().stream().anyMatch(province -> value.equals(province.getOwnerTag()));
            case "core_percentage":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOwnedProvinces().size()))
                                            .compareTo(BigDecimal.valueOf(country.getCoreProvinces().size())) >= 0;
            case "corruption":
                return NumbersUtils.doubleOrDefault(country.getCorruption()) >= NumbersUtils.toDouble(value);
            case "culture_group":
                return country.getPrimaryCulture().getCultureGroup().getName().equals(value);
            case "culture_group_claim":
                other = country.getSave().getCountry(value);
                return other.getOwnedProvinces()
                            .stream()
                            .anyMatch(province -> province.getCulture().getCultureGroup().equals(country.getPrimaryCulture().getCultureGroup()));
            case "current_age":
                return country.getSave().getCurrentAge().equals(value);
            case "current_debate":
                return country.getParliament() != null && country.getParliament().getActiveParliamentIssue() != null
                       && value.equals(ClausewitzUtils.removeQuotes(country.getParliament().getActiveParliamentIssue().getWhich()));
            case "current_icon":
                return country.getCurrentIcon() != null
                       && value.equals(country.getReligion().getGameReligion().getIcons().get(country.getCurrentIcon()).getName());
            case "current_income_balance":
                return country.getLedger() != null && country.getLedger().getLastMonthIncome() != null && country.getLedger().getLastMonthExpense() != null
                       && new BigDecimal(value).add(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))
                                               .subtract(BigDecimal.valueOf(country.getLedger().getLastMonthExpense())).compareTo(BigDecimal.ZERO) >= 0;
            case "current_institution_growth": //Fixme ???
                break;
            case "current_size_of_parliament":
                return country.getParliament() != null
                       && country.getOwnedProvinces().stream().map(SaveProvince::getSeatInParliament).filter(Objects::nonNull).count() >=
                          NumbersUtils.toInt(value);
            case "defensive_war_with":
                other = country.getSave().getCountry(value);
                return country.getActiveWars().stream().anyMatch(activeWar -> activeWar.getDefender(country) != null && activeWar.getDefender(other) != null);
            case "development_in_provinces": //Todo object
                break;
            case "development_of_overlord_fraction":
                return country.getOverlord() != null && new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOverlord().getDevelopment()))
                                                                             .compareTo(BigDecimal.valueOf(country.getDevelopment())) >= 0;
            case "devotion":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getDevotion()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getChurch() == null || other.getChurch() == null
                           || NumbersUtils.doubleOrDefault(country.getDevotion()) >= NumbersUtils.doubleOrDefault(other.getDevotion());
                }
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
            case "diplomatic_reputation": //Todo diplomatic_reputation
                break;
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
            case "dominant_culture":
                return country.getDominantCulture().getName().equals(value);
            case "dominant_religion":
                return country.getReligion().getName().equals(value);
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
            case "employed_advisor": //Todo object
                break;
            case "estate_influence": //Todo object
                break;
            case "estate_loyalty": //Todo object
                break;
            case "estate_territory": //Todo object
                break;
            case "exists":
                if ("yes".equals(value)) {
                    return true;
                } else {
                    return country.getSave().getCountry(value) != null;
                }
            case "faction_in_power":
                return CollectionUtils.isNotEmpty(country.getFactions())
                       && value.equals(country.getFactions().stream().max(Comparator.comparing(Faction::getInfluence)).get().getType());
            case "faction_influence": //Todo faction
                break;
            case "federation_size":
                return country.getFederationFriends().size() >= NumbersUtils.toInt(value);
            case "fervor":
                return country.getFervor() != null && NumbersUtils.doubleOrDefault(country.getFervor().getValue()) >= NumbersUtils.toDouble(value);
            case "full_idea_group":
                return country.getIdeaGroups().getIdeaGroupsNames().getOrDefault(value, -1) == 7;
            case "galley_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize())).compareTo(BigDecimal.valueOf(country.getNbGalleys())) >= 0;
            case "gold_income":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().get(Income.GOLD) >= NumbersUtils.toDouble(value);
            case "gold_income_percentage":
                return country.getLedger() != null
                       && new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))
                                               .compareTo(BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.GOLD))) >= 0;
            case "government":
                return country.getGovernment().getType().equals(ClausewitzUtils.addQuotes(value));
            case "government_rank":
                return NumbersUtils.intOrDefault(country.getGovernmentRank()) >= NumbersUtils.toInt(value);
            case "grown_by_development":
                return country.getHistoryStatsCache() != null &&
                       BigDecimal.valueOf(NumbersUtils.doubleOrDefault(country.getHistoryStatsCache().getStartingDevelopment()))
                                 .subtract(BigDecimal.valueOf(NumbersUtils.doubleOrDefault(country.getDevelopment()))).compareTo(BigDecimal.ZERO) >= 0;
            case "great_power_rank":
                return NumbersUtils.intOrDefault(country.getGreatPowerRank(), country.getSave().getGame().getNbGreatPowers() + 1) <= NumbersUtils.toInt(value);
            case "guaranteed_by":
                other = country.getSave().getCountry(value);
                return country.getGuarantees().contains(other);
            case "had_active_policy": //Todo object
                break;
            case "had_consort_flag": //Todo object
                break;
            case "had_country_flag": //Todo object
                break;
            case "had_global_flag": //Todo object
                break;
            case "had_heir_flag": //Todo object
                break;
            case "had_recent_war":
                return DateUtils.addYears(country.getLastWarEnded(), NumbersUtils.toInt(value)).after(country.getSave().getDate());
            case "had_ruler_flag": //Todo object
                break;
            case "harmonization_progress":
                return NumbersUtils.doubleOrDefault(country.getHarmonyProgress()) >= NumbersUtils.toDouble(value);
            case "harmony":
                return NumbersUtils.doubleOrDefault(country.getHarmony()) >= NumbersUtils.toDouble(value);
            case "has_active_debate":
                return country.getParliament() != null && country.getParliament().getActiveParliamentIssue() != null;
            case "has_active_fervor":
                return country.getFervor() != null && "yes".equals(value) != country.getFervor().getActives().isEmpty();
            case "has_active_policy":
                return country.getActivePolicies().stream().map(ActivePolicy::getPolicy).anyMatch(value::equals);
            case "has_adopted_cult":
                return country.getFetishistCults().contains(ClausewitzUtils.addQuotes(value));
            case "has_advisor":
                return !country.getAdvisors().isEmpty();
            case "has_age_ability":
                return country.getActiveAgeAbility().contains(ClausewitzUtils.addQuotes(value));
            case "has_any_disaster":
                return country.getActiveDisaster() != null;
            case "has_border_with_religious_enemy":
                return country.getBorderProvinces()
                              .stream()
                              .map(SaveProvince::getOwner)
                              .filter(Objects::nonNull)
                              .anyMatch(c -> !c.getReligion().equals(country.getReligion()));
            case "has_casus_belli": //Todo object
                break;
            case "has_casus_belli_against":
                return country.getSave()
                              .getDiplomacy()
                              .getCasusBellis()
                              .stream()
                              .anyMatch(casusBelli -> casusBelli.getFirst().equals(country) && casusBelli.getSecond().getTag().equals(value));
            case "has_changed_nation": //Todo
                break;
            case "has_church_aspect":
                return country.getChurch() != null && country.getChurch().getAspects().contains(value);
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
                return country.getFlags().contains(value);
            case "has_country_modifier":
                return country.getModifiers().stream().anyMatch(modifier -> modifier.getModifier().equals(value));
            case "has_custom_ideas":
                return CollectionUtils.isNotEmpty(country.getCustomNationalIdeas());
            case "has_disaster":
                return value.equals(country.getActiveDisaster());
            case "has_disaster_progress": //Todo object
                break;
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
                return country.getEstates().stream().map(Estate::getType).map(ClausewitzUtils::removeQuotes).anyMatch(value::equals);
            case "has_estate_influence_modifier": //Todo object
                break;
            case "has_estate_loyalty_modifier": //Todo object
                break;
            case "has_faction":
                return CollectionUtils.isNotEmpty(country.getFactions()) && country.getFactions().stream().map(Faction::getType).anyMatch(value::equals);
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
                              .filter(province -> province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> BooleanUtils.toBoolean(province.centerOfReligion()));
            case "has_game_started":
                return !country.getSave().getDate().equals(country.getSave().getStartDate());
            case "has_given_consort_to":
                other = country.getSave().getCountry(value);
                return other.getConsort() != null && Queen.class.equals(other.getConsort().getClass())
                       && ((Queen) other.getConsort()).getCountryOfOrigin().equals(country);
            case "has_guaranteed":
                other = country.getSave().getCountry(value);
                return other.getGuarantees().contains(country);
            case "has_global_flag":
                return country.getSave().getFlags().contains(value);
            case "has_global_modifier_value": //Todo object
                break;
            case "has_government_attribute": //Todo parse reforms custom_attributes = { is_merchant_republic = yes }
                break;
            case "has_government_mechanic":
                return country.getGovernment().hasMechanic(value);
            case "has_harmonized_with":
                return country.getHarmonizedReligionGroups()
                              .stream()
                              .map(index -> country.getSave().getGame().getReligions().get(index))
                              .anyMatch(religion -> value.equals(religion.getName()));
            case "has_heir":
                return "yes".equals(value) == (country.getHeir() != null);
            case "has_heir_flag":
                return country.getHeir() != null && country.getHeir().getRulerFlags().contains(value);
            case "has_hostile_reformation_center":
                return country.getOwnedProvinces()
                              .stream()
                              .filter(province -> !province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> BooleanUtils.toBoolean(province.centerOfReligion()));
            case "has_idea":
                return country.getIdeaGroups().hasIdea(value);
            case "has_idea_group":
                return country.getIdeaGroups().getIdeaGroupsNames().containsKey(value);
            case "has_institution":
                return country.getEmbracedInstitutionsIds()
                              .stream()
                              .map(index -> country.getSave().getGame().getInstitution(index))
                              .anyMatch(institution -> value.equals(institution.getName()));
            case "has_leader":
                return country.getLeaders().values().stream().map(Leader::getName).anyMatch(value::equals);
            case "has_leader_with": //Todo object
                break;
            case "has_matching_religion":
                if (Eu4Utils.isTag(value)) {
                    other = country.getSave().getCountry(value);
                    return other.getReligion().equals(country.getReligion())
                           || (country.getSecondaryReligion() != null && other.getSecondaryReligion() != null
                               && country.getSecondaryReligion().equals(other.getSecondaryReligion()));
                } else {
                    return country.getReligion().getName().equals(value)
                           || (country.getSecondaryReligion() != null && country.getSecondaryReligion().getName().equals(value));
                }
            case "has_mission": //Todo parse missions tree
                break;
            case "has_new_dynasty":
                return country.getPreviousMonarchs().size() >= 2 && country.getHistory().getMonarchs().size() >= 2
                       && !country.getHistory()
                                  .getMonarch(country.getPreviousMonarchs().get(country.getPreviousMonarchs().size() - 2).getId())
                                  .getDynasty()
                                  .equals(country.getMonarch().getDynasty());
            case "has_opinion": //Todo object
                break;
            case "has_opinion_diff": //Todo object
                break;
            case "has_opinion_modifier": //Todo object
                break;
            case "has_parliament":
                return country.getParliament() != null;
            case "has_personal_deity":
                return value.equals(ClausewitzUtils.removeQuotes(country.getPersonalDeity()));
            case "has_privateers":
                return "yes".equals(value) == NumbersUtils.intOrDefault(country.getNumShipsPrivateering()) > 0;
            case "has_promote_investments":
                return CollectionUtils.isNotEmpty(country.getTradeCompanies())
                       && country.getTradeCompanies()
                                 .stream()
                                 .anyMatch(tradeCompany -> value.equals(tradeCompany.getName()) && tradeCompany.promoteInvestments());
            case "has_regency":
                return BooleanUtils.toBoolean(country.getMonarch().getRegent());
            case "has_reform":
                return country.getGovernment().getReforms().contains(ClausewitzUtils.addQuotes(value));
            case "have_had_reform":
                return country.getGovernment().getHistory().contains(ClausewitzUtils.addQuotes(value));
            case "government_reform_progress":
                return NumbersUtils.doubleOrDefault(country.getGovernmentReformProgress()) >= NumbersUtils.toDouble(value);
            case "has_removed_fow": //Todo object
                break;
            case "has_ruler":
                return country.getMonarch() != null && country.getMonarch().getName().equals(value);
            case "has_ruler_flag":
                return country.getMonarch() == null || (country.getMonarch().getRulerFlags() != null && country.getMonarch().getRulerFlags().contains(value));
            case "has_ruler_modifier":
                return country.getModifiers()
                              .stream()
                              .filter(modifier -> BooleanUtils.toBoolean(modifier.rulerModifier()))
                              .map(Modifier::getModifier)
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
                              .stream()
                              .anyMatch(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getType().equals(ClausewitzUtils.removeQuotes(value)));
            case "has_spawned_supported_rebels":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getRebelFactions()
                              .stream()
                              .anyMatch(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getSupportiveCountry() != null && rebelFaction.getSupportiveCountry().equals(other));
            case "has_spy_network_from": //Todo object
                break;
            case "has_spy_network_in": //Todo object
                break;
            case "has_subsidize_armies":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(3));
            case "has_support_loyalists":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && BooleanUtils.toBoolean(country.getSubjectInteractions().get(2));
            case "has_switched_nation":
                return BooleanUtils.toBoolean(country.hasSwitchedNation());
            case "has_truce":
            case "truce_with":
                other = country.getSave().getCountry(value);
                return country.getActiveRelation(other) != null && BooleanUtils.toBoolean(country.getActiveRelation(other).truce());
            case "has_unembraced_institution":
                return country.getNotEmbracedInstitutionsIds()
                              .stream()
                              .map(index -> country.getSave().getGame().getInstitution(index))
                              .anyMatch(institution -> value.equals(institution.getName()));
            case "has_unified_culture_group":
                return country.getSave()
                              .getProvinces()
                              .values()
                              .stream()
                              .filter(province -> province.getCulture().getCultureGroup().equals(country.getPrimaryCulture().getCultureGroup()))
                              .allMatch(province -> province.getOwner().equals(country));
            case "has_unit_type":
                return country.getUnitType().equals(value);
            case "has_unlocked_cult": //Todo parse cults
                break;
            case "has_wartaxes":
                return BooleanUtils.toBoolean(country.warTaxes());
            case "heavy_ship_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize())).compareTo(BigDecimal.valueOf(country.getNbHeavyShips())) >= 0;
            case "heir_adm":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getAdm()) >= NumbersUtils.toInt(value);
            case "heir_age":
                return country.getHeir() != null
                       && ChronoUnit.YEARS.between(country.getHeir().getBirthDate().toInstant(), country.getSave().getDate().toInstant())
                          >= NumbersUtils.toInt(value);
            case "heir_dip":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getDip()) >= NumbersUtils.toInt(value);
            case "heir_claim":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getClaim()) >= NumbersUtils.toInt(value);
            case "heir_culture":
                return country.getHeir() != null && country.getHeir().getCulture().getName().equals(value);
            case "heir_has_consort_dynasty":
                return country.getHeir() != null && country.getConsort() != null && country.getHeir().getDynasty().equals(country.getConsort().getDynasty());
            case "heir_has_personality":
                return country.getHeir() != null && country.getHeir().getPersonalities().getPersonalities().contains(value);
            case "heir_has_ruler_dynasty":
                return country.getHeir() != null && country.getHeir().getDynasty().equals(country.getMonarch().getDynasty());
            case "heir_mil":
                return country.getHeir() != null && NumbersUtils.intOrDefault(country.getHeir().getMil()) >= NumbersUtils.toInt(value);
            case "heir_nationality":
                other = country.getSave().getCountry(value);
                return country.getHeir() != null && country.getHeir().getWho().equals(other);
            case "heir_religion":
                return country.getHeir() != null && country.getHeir().getReligion().getName().equals(value);
            case "hidden_trigger":// Todo object
                break;
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
                    return !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(other.getReligion().isHreHereticReligion());
                } else {
                    return !country.getSave().getHre().dismantled()
                           && BooleanUtils.toBoolean(country.getSave().getReligions().getReligion(value).isHreHereticReligion());
                }
            case "hre_leagues_enabled":
                return !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(country.getSave().getHreLeaguesActive());
            case "hre_reform_level":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "hre_reform_passed":
                return !country.getSave().getHre().dismantled()
                       && country.getSave().getHre().getPassedReforms().stream().anyMatch(reform -> ClausewitzUtils.addQuotes(value).equals(reform.getName()));
            case "hre_religion":
                return !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(country.getSave().getReligions().getReligion(value).isHreReligion());
            case "hre_religion_locked":
                return "yes".equals(value) == (!country.getSave().getHre().dismantled()
                                               && !HreReligionStatus.PEACE.equals(country.getSave().getHreReligionStatus()));
            case "hre_religion_treaty":
                return "yes".equals(value) == (!country.getSave().getHre().dismantled()
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
                       && ChronoUnit.YEARS.between(country.getGoldenEraDate().toInstant(), country.getSave().getDate().toInstant())
                          < country.getSave().getGame().getGoldenEraDuration();
            case "infantry_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize())).compareTo(BigDecimal.valueOf(country.getNbInfantry())) >= 0;
            case "inflation":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getInflation()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getInflation()) >= NumbersUtils.doubleOrDefault(other.getInflation());
                }
            case "incident_variable_value": //Todo object
                break;
            case "innovativeness":
                return NumbersUtils.doubleOrDefault(country.getInnovativeness()) >= NumbersUtils.toDouble(value);
            case "institution_difference": //Todo object
                break;
            case "invasion_nation":
                return country.getModifiers().stream().anyMatch(modifier -> "\"invasion_nation\"".equals(modifier.getModifier()));
            case "invested_papal_influence":
                return country.getPapalInfluence() != null && country.getPapalInfluence() >= NumbersUtils.toDouble(value);
            case "in_league":
                return country.getSave().getReligions().getReligion(value).getInLeague().contains(country);
            case "ironman": //Todo
                break;
            case "is_advisor_employed":
                return country.getSave().getCountries().values().stream().anyMatch(c -> c.getActiveAdvisors().containsKey(NumbersUtils.toInt(value)));
            case "is_at_war":
                return country.isAtWar();
            case "is_bankrupt":
                return country.lastBankrupt() != null &&
                       ChronoUnit.YEARS.between(country.lastBankrupt().toInstant(), country.getSave().getDate().toInstant())
                       < country.getSave().getGame().getBankruptcyDuration();
            case "is_claim":
                return country.getClaimProvinces().stream().map(SaveProvince::getId).anyMatch(NumbersUtils.toInt(value)::equals);
            case "is_client_nation":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country)
                                                      && ClausewitzUtils.removeQuotes(dependency.getSubjectType())
                                                                        .equalsIgnoreCase(SubjectType.CLIENT_VASSAL.name()));
            case "is_client_nation_of":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country)
                                                      && dependency.getFirst().equals(other)
                                                      && ClausewitzUtils.removeQuotes(dependency.getSubjectType())
                                                                        .equalsIgnoreCase(SubjectType.CLIENT_VASSAL.name()));
            case "is_colonial_nation":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country)
                                                      && ClausewitzUtils.removeQuotes(dependency.getSubjectType())
                                                                        .equalsIgnoreCase(SubjectType.COLONY.name()));
            case "is_colonial_nation_of":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country)
                                                      && dependency.getFirst().equals(other)
                                                      && ClausewitzUtils.removeQuotes(dependency.getSubjectType())
                                                                        .equalsIgnoreCase(SubjectType.COLONY.name()));
            case "is_core":
                return country.getCoreProvinces().stream().map(SaveProvince::getId).anyMatch(NumbersUtils.toInt(value)::equals);
            case "is_crusade_target":
                return country.getSave()
                              .getReligions()
                              .getReligions()
                              .values()
                              .stream()
                              .anyMatch(saveReligion -> saveReligion.hasPapacy() && saveReligion.getPapacy().getCrusadeTarget() != null
                                                        && saveReligion.getPapacy().getCrusadeTarget().equals(country));
            case "is_defender_of_faith":
                return country.getSave().getReligions().getReligion(country.getReligionName()).hasDefenderOfFaith()
                       && country.equals(country.getSave().getReligions().getReligion(country.getReligionName()).getDefender());
            case "is_dynamic_tag":
                return country.isClientState();
            case "is_elector":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getElectors().contains(country);
            case "is_emperor":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getEmperor().equals(country);
            case "is_emperor_of_china":
                return !country.getSave().getCelestialEmpire().dismantled() && country.getSave().getCelestialEmpire().getEmperor().equals(country);
            case "is_enemy":
                other = country.getSave().getCountry(value);
                return country.getEnemies().contains(other);
            case "is_excommunicated":
                return BooleanUtils.toBoolean(country.isExcommunicated());
            case "is_federation_leader":
                return country.equals(country.getFederationLeader());
            case "is_female":
                return BooleanUtils.toBoolean(country.getMonarch().getFemale());
            case "is_force_converted":
                return country.getForceConvert() != null;
            case "is_former_colonial_nation":
                return "yes".equals(value) == (country.getOverlord() == null && country.getColonialParent() != null);
            case "is_free_or_tributary_trigger":
                return "yes".equals(value) == (country.getOverlord() == null
                                               || country.getSave()
                                                         .getDiplomacy()
                                                         .getDependencies()
                                                         .stream()
                                                         .anyMatch(dependency -> country.equals(dependency.getSecond())
                                                                                 && "tributary_state".equals(dependency.getSubjectType())));
            case "is_great_power":
                return country.isGreatPower();
            case "is_harmonizing_with":
                return value.equals(ClausewitzUtils.removeQuotes(country.getHarmonyWithReligion()));
            case "is_heir_leader":
                return country.getHeir() != null && country.getHeir().getLeader() != null;
            case "is_hegemon":
                return "yes".equals(value) == (country.getPowerProjections().stream().map(PowerProjection::getModifier).anyMatch("is_hegemon"::equals));
            case "is_imperial_ban_allowed":
                return !country.getSave().getHre().dismantled() && BooleanUtils.toBoolean(country.getSave().getHre().getImperialBanAllowed());
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
            case "is_incident_possible": //Todo ???
            case "is_incident_potential":
                return country.getPotentialIncidents().contains(ClausewitzUtils.addQuotes(value));
            case "is_institution_enabled":
                return country.getSave().getInstitutions().isAvailable(IntStream.range(0, country.getSave().getGame().getInstitutions().size())
                                                                                .filter(index -> value.equals(country.getSave()
                                                                                                                     .getGame()
                                                                                                                     .getInstitution(index)
                                                                                                                     .getName()))
                                                                                .findFirst().orElse(0));
            case "is_in_coalition":
                return country.getCoalitionTarget() != null;
            case "is_in_coalition_war":
                return country.getActiveWars().stream().anyMatch(war -> war.isCoalition());
            case "is_in_deficit":
                return country.getLedger().getLastMonthIncome() < country.getLedger().getLastMonthExpense();
            case "is_in_league_war":
                return country.getActiveWars().stream().anyMatch(war -> war.getWarGoal() != null && war.getWarGoal().getCasusBelli().isLeague());
            case "is_in_trade_league":
                return country.getSave().getTradeLeagues().stream().anyMatch(tradeLeague -> tradeLeague.hasMember(country));
            case "is_in_trade_league_with":
                other = country.getSave().getCountry(value);
                return country.getSave().getTradeLeagues().stream().anyMatch(tradeLeague -> tradeLeague.hasMember(country) && tradeLeague.hasMember(other));
            case "is_in_war": //Todo object
                break;
            case "is_league_enemy":
                other = country.getSave().getCountry(value);
                return !country.getReligion().equals(other.getReligion()) && country.getReligion().getInLeague().contains(country)
                       && country.getReligion().getInLeague().contains(other);
            case "is_lacking_institutions":
                return country.getSave().getInstitutions().getNbInstitutions() > country.getNbEmbracedInstitutions();
            case "is_league_friend":
                other = country.getSave().getCountry(value);
                return country.getReligion().equals(other.getReligion()) && country.getReligion().getInLeague().contains(country)
                       && country.getReligion().getInLeague().contains(other);
            case "is_league_leader":
                return BooleanUtils.toBoolean(country.getSave().getHreLeaguesActive())
                       && (country.getSave().getHre().getEmperor().equals(country)
                           || CollectionUtils.isNotEmpty(country.getReligion().getInLeague()) && country.getReligion().getInLeague().get(0).equals(country));
            case "is_lesser_in_union":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country) && "personal_union".equals(dependency.getSubjectType()));
            case "is_monarch_leader":
                return country.getMonarch().getLeader() != null;
            case "is_month":
            case "real_month_of_year":
                calendar.setTime(country.getSave().getDate());
                return calendar.get(Calendar.MONTH) == NumbersUtils.toInt(value);
            case "is_march":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country) && "march".equals(dependency.getSubjectType()));
            case "is_neighbor_of":
                other = country.getSave().getCountry(value);
                return country.getNeighbours().contains(other);
            case "is_nomad": //Todo parse governments reforms, nomad = yes
                //                return "yes".equals(value) ==
                break;
            case "is_orangists_in_power":
                return country.getStatistsVsMonarchists() != null && country.getStatistsVsMonarchists() > 0;
            case "is_origin_of_consort":
                other = country.getSave().getCountry(value);
                return country.getConsort() != null && country.getConsort().getWho().equals(other);
            case "is_overseas_subject":
                if (country.getOverlord() == null) {
                    return false;
                }

                Set<Integer> continents = country.getOwnedProvinces().stream().map(SaveProvince::getContinent).collect(Collectors.toSet());
                continents.retainAll(country.getOverlord().getOwnedProvinces().stream().map(SaveProvince::getContinent).collect(Collectors.toSet()));
                return continents.isEmpty();
            case "is_papal_controller":
                return country.getReligion().getPapacy() != null && country.equals(country.getReligion().getPapacy().getController());
            case "is_part_of_hre":
                return country.getCapital().inHre();
            case "is_playing_custom_nation":
                return country.isCustom();
            case "is_possible_march":
                other = country.getSave().getCountry(value);
                return country.equals(other.getOverlord())
                       && country.getSave()
                                 .getDiplomacy()
                                 .getDependencies()
                                 .stream()
                                 .anyMatch(dependency -> country.equals(dependency.getFirst()) && other.equals(dependency.getSecond())
                                                         && "vassal".equals(dependency.getSubjectType()));
            case "is_possible_vassal": //Todo object
                break;
            case "is_previous_papal_controller":
                return country.getReligion().getPapacy() != null && country.equals(country.getReligion().getPapacy().getPreviousController());
            case "is_protectorate":
                return false;
            case "is_random_new_world":
                return country.getSave().isRandomNewWorld();
            case "is_religion_enabled":
                return country.getSave().getReligions().getReligion(value) != null
                       && country.getSave().getReligions().getReligion(value).getEnable().before(country.getSave().getDate());
            case "is_religion_reformed":
                return "yes".equals(value) == (BooleanUtils.toBoolean(country.hasReformedReligion()));
            case "is_revolutionary": //Todo parse reforms revolutionary = yes
                break;
            case "is_revolution_target":
                return country.getSave().getRevolution() != null && country.equals(country.getSave().getRevolution().getRevolutionTarget());
            case "is_revolutionary_republic_trigger":
                return "yes".equals(value) == country.getGovernment().getReforms().contains("\"revolutionary_republic_reform\"");
            case "is_rival":
                other = country.getSave().getCountry(value);
                return country.getRivals().containsKey(ClausewitzUtils.addQuotes(other.getTag()));
            case "is_state_core":
                integer = NumbersUtils.toInt(value);
                saveProvince = country.getSave().getProvince(integer);
                return country.getCoreProvinces().contains(saveProvince)
                       && country.getStates().keySet().stream().anyMatch(area -> area.getProvinces().contains(saveProvince));
            case "is_statists_in_power": //Fixme check if country uses statists
                return NumbersUtils.doubleOrDefault(country.getStatistsVsMonarchists()) <= 0;
            case "is_monarchists_in_power": //Fixme check if country uses statists
                return NumbersUtils.doubleOrDefault(country.getStatistsVsMonarchists()) > 0;
            case "is_subject":
                return "yes".equals(value) == (country.getOverlord() != null);
            case "is_subject_of":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other);
            case "is_subject_of_type":
                return country.getOverlord() != null
                       && country.getSave().getDiplomacy().getDependencies().stream().anyMatch(dependency -> country.equals(dependency.getSecond())
                                                                                                             && value.equals(dependency.getSubjectType()));
            case "is_subject_other_than_tributary_trigger":
                return "yes".equals(value) == (country.getOverlord() != null
                                               && country.getSave()
                                                         .getDiplomacy()
                                                         .getDependencies()
                                                         .stream()
                                                         .noneMatch(dependency -> country.equals(dependency.getSecond())
                                                                                  && "tributary_state".equals(dependency.getSubjectType())));
            case "is_territorial_core":
                integer = NumbersUtils.toInt(value);
                saveProvince = country.getSave().getProvince(integer);
                return country.getCoreProvinces().contains(saveProvince)
                       && country.getStates().keySet().stream().noneMatch(area -> area.getProvinces().contains(saveProvince));
            case "is_threat":
                other = country.getSave().getCountry(value);
                return "attitude_threatened".equals(other.getActiveRelation(country).getAttitude());
            case "is_trade_league_leader":
                return country.getSave()
                              .getTradeLeagues()
                              .stream()
                              .filter(tradeLeague -> CollectionUtils.isNotEmpty(tradeLeague.getMembers()))
                              .anyMatch(tradeLeague -> tradeLeague.getMembers().get(0).equals(country));
            case "is_tribal": //Todo parse governments reforms, tribal = yes
                //                return "yes".equals(value) ==
                break;
            case "is_vassal":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .anyMatch(dependency -> dependency.getSecond().equals(country)
                                                      && ClausewitzUtils.removeQuotes(dependency.getSubjectType()).equalsIgnoreCase(SubjectType.VASSAL.name()));
            case "is_year":
                calendar.setTime(country.getSave().getDate());
                return calendar.get(Calendar.YEAR) == NumbersUtils.toInt(value);
            case "isolationism":
                return country.getIsolationism() != null && country.getIsolationism() >= NumbersUtils.toInt(value);
            case "janissary_percentage":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize()))
                                            .compareTo(BigDecimal.valueOf(country.getNbRegimentOfCategory(5))) >= 0;
            case "junior_union_with":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other)
                       && country.getSave()
                                 .getDiplomacy()
                                 .getDependencies()
                                 .stream()
                                 .anyMatch(dependency -> "personal_union".equals(dependency.getSubjectType()) && country.equals(dependency.getSecond()));
            case "karma":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return NumbersUtils.intOrDefault(country.getKarma()) >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.intOrDefault(country.getKarma()) >= NumbersUtils.intOrDefault(other.getKarma());
                }
            case "knows_country":
                other = country.getSave().getCountry(value);
                return other.getCapital().getDiscoveredBy().contains(country);
            case "land_forcelimit": //Todo fl
                break;
            case "land_maintenance":
                return NumbersUtils.doubleOrDefault(country.getLandMaintenance()) >= NumbersUtils.toDouble(value);
            case "land_morale": //Todo morale
                break;
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
                return country.getOverlord() != null || country.getLibertyDesire() >= NumbersUtils.toDouble(value);
            case "light_ship_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize())).compareTo(BigDecimal.valueOf(country.getNbLightShips())) >= 0;
            case "luck":
                return !country.wasPlayer() && !country.isHuman() && BooleanUtils.toBoolean(country.isLucky());
            case "march_of":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other)
                       && country.getSave()
                                 .getDiplomacy()
                                 .getDependencies()
                                 .stream()
                                 .anyMatch(dependency -> "march".equals(dependency.getSubjectType()) && country.equals(dependency.getSecond()));
            case "manpower":
                return country.getManpower() >= NumbersUtils.toInt(value) * 1000;
            case "manpower_percentage":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getMaxManpower())).compareTo(BigDecimal.valueOf(country.getManpower())) >= 0;
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
                return country.getCompletedMissions().contains(ClausewitzUtils.addQuotes(value));
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

                calendar.setTime(country.getSave().getDate());
                calendar2.setTime(country.getHistory().getMonarch(country.getMonarch().getId().getId()).getMonarchDate());
                calendar2.add(Calendar.MONTH, NumbersUtils.toInt(value));

                return calendar2.before(calendar);
            case "military_strength": //Todo object
                break;
            case "national_focus":
                return country.getNationalFocus().equals(Power.valueOf(value.toUpperCase()));
            case "nation_designer_points":
                return country.getCustomNationPoints() != null && country.getCustomNationPoints() >= NumbersUtils.toDouble(value);
            case "naval_forcelimit": //Todo fl
                break;
            case "naval_maintenance":
                return NumbersUtils.doubleOrDefault(country.getNavalMaintenance()) >= NumbersUtils.toDouble(value);
            case "naval_morale": //Todo morale
                break;
            case "navy_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNavySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNavySize() >= other.getNavySize();
                }
            case "navy_size_percentage": //Todo fl
                break;
            case "naval_strength": //Todo object
                break;
            case "navy_tradition":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return NumbersUtils.doubleOrDefault(country.getNavyTradition()) >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getNavyTradition()) >= NumbersUtils.doubleOrDefault(other.getNavyTradition());
                }
            case "normal_or_historical_nations":
                return "yes".equals(value) == (NationSetup.NORMAL.equals(country.getSave().getGameplayOptions().getNationSetup())
                                               || NationSetup.HISTORICAL.equals(country.getSave().getGameplayOptions().getNationSetup()));
            case "normal_province_values":
                return "yes".equals(value) == (ProvinceTaxManpower.HISTORICAL.equals(country.getSave().getGameplayOptions().getProvinceTaxManpower()));
            case "num_accepted_cultures":
                return country.getAcceptedCultures().size() >= NumbersUtils.toInt(value);
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
                return country.getLeadersOfType(LeaderType.ADMIRAL).stream().filter(leader -> StringUtils.isNotBlank(leader.getPersonality())).count()
                       >= NumbersUtils.toInt(value);
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
                return country.getSave().getCountries().values().stream().filter(Country::isCustom).count() >= NumbersUtils.toInt(value);
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
            case "num_of_free_diplomatic_relations": //Todo num of relations
                break;
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
                return country.getLeadersOfType(LeaderType.GENERAL).stream().filter(leader -> StringUtils.isNotBlank(leader.getPersonality())).count()
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
                    return country.getSave()
                                  .getDiplomacy()
                                  .getDependencies()
                                  .stream()
                                  .filter(dependency -> dependency.getFirst().equals(country) && "march".equals(dependency.getSubjectType())).count()
                           >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getSave()
                                  .getDiplomacy()
                                  .getDependencies()
                                  .stream()
                                  .filter(dependency -> dependency.getFirst().equals(country) && "march".equals(dependency.getSubjectType())).count() >=
                           other.getSave()
                                .getDiplomacy()
                                .getDependencies()
                                .stream()
                                .filter(dependency -> dependency.getFirst().equals(other) && "march".equals(dependency.getSubjectType()))
                                .count();
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
            case "num_of_owned_provinces_with": //Todo object
                break;
            case "num_of_ports":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getNumOfPorts() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getNumOfPorts() >= other.getNumOfPorts();
                }
            case "num_of_ports_blockading": //Todo ??
                break;
            case "num_of_powerful_estates": //Todo influence
                //                return country.getEstates().stream().filter(estate -> estate.getInfluence() >= 70d).count() >= NumbersUtils.toInt(value);
                break;
            case "num_of_protectorates": //Outdated
                break;
            case "num_of_provinces_in_states":
                return country.getNumOfProvincesInStates() >= NumbersUtils.toInt(value);
            case "num_of_provinces_in_territories":
                return country.getNumOfProvincesInTerritories() >= NumbersUtils.toInt(value);
            case "num_of_provinces_owned_or_owned_by_non_sovereign_subjects_with": //Todo
                break;
            case "num_of_rebel_armies":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getSave()
                                  .getRebelFactions()
                                  .stream()
                                  .filter(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive())
                                                          && country.equals(rebelFaction.getProvince().getOwner()))
                                  .count()
                           >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getSave()
                                  .getRebelFactions()
                                  .stream()
                                  .filter(rebelFaction -> BooleanUtils.toBoolean(rebelFaction.isActive())
                                                          && country.equals(rebelFaction.getProvince().getOwner()))
                                  .count() >=
                           other.getSave()
                                .getRebelFactions()
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
            case "num_of_religion": //Todo object
                break;
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
                return country.getTradeCompanies().stream().filter(TradeCompany::strongCompany).count() >= NumbersUtils.toInt(value);
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
                return country.getNumOfTradeEmbargos() >= NumbersUtils.toInt(value);
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
            case "num_of_unions":
            case "personal_union":
                return country.getSave()
                              .getDiplomacy()
                              .getDependencies()
                              .stream()
                              .filter(dependency -> country.equals(dependency.getFirst()) && "personal_union".equals(dependency.getSubjectType()))
                              .count() >= NumbersUtils.toInt(value);
            case "num_of_unlocked_cults":
                return country.getFetishistCults().size() >= NumbersUtils.toInt(value);
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
                return nbProv.subtract(BigDecimal.valueOf(country.getNumOwnedHomeCores()))
                             .divide(nbProv, 3, RoundingMode.HALF_EVEN).compareTo(bigDecimal) >= 0;
            case "owns":
                return country.equals(country.getSave().getProvince(NumbersUtils.toInt(value)).getOwner());
            case "owns_all_provinces": //Todo object
                break;
            case "owns_core_province":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.equals(saveProvince.getOwner()) && saveProvince.getCores().contains(country);
            case "owns_or_non_sovereign_subject_of":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinces().contains(saveProvince) ||
                       country.getSubjects().stream().anyMatch(subject -> subject.getOwnedProvinces().contains(saveProvince)); //Fixme not tributaries
            //Todo parse subject types and check is_voluntary = no
            case "owns_or_subject_of":
                saveProvince = country.getSave().getProvince(NumbersUtils.toInt(value));
                return country.getOwnedProvinces().contains(saveProvince) ||
                       country.getSubjects().stream().anyMatch(subject -> subject.getOwnedProvinces().contains(saveProvince));
            case "papacy_active":
                return country.getSave().getReligions().getReligions().values().stream().anyMatch(religion -> religion.getPapacy() != null);
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
                return new BigDecimal(value)
                               .multiply(BigDecimal.valueOf(seats))
                               .compareTo(BigDecimal.valueOf(country.getOwnedProvinces()
                                                                    .stream()
                                                                    .filter(province -> province.getSeatInParliament() != null
                                                                                        && BooleanUtils.toBoolean(province.getSeatInParliament().getBack()))
                                                                    .count())) >= 0;
            case "personality":
            case "ruler_has_personality":
                return country.getMonarch() == null || country.getMonarch().getPersonalities().getPersonalities().contains(value);
            case "piety":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getPiety() == null || country.getPiety() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getPiety() == null || other.getPiety() == null || country.getArmyTradition() >= other.getArmyTradition();
                }
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
                return value.equals(country.getPrimaryCulture().getName());
            case "primitives": //Todo
                break;
            case "production_efficiency": //Todo
                break;
            case "production_income_percentage": //Can't do
                break;
            case "production_leader": //Todo object
                break;
            case "provinces_on_capital_continent_of":
                other = country.getSave().getCountry(value);
                return country.getOwnedProvinces().stream().anyMatch(province -> province.getContinent() == other.getCapital().getContinent());
            case "real_day_of_year":
                calendar.setTime(country.getSave().getDate());
                return calendar.get(Calendar.DAY_OF_YEAR) == NumbersUtils.toInt(value);
            case "reform_desire":
                return country.getSave()
                              .getReligions()
                              .getReligions()
                              .values()
                              .stream()
                              .anyMatch(religion -> religion.getPapacy() != null && religion.getPapacy().getReformDesire() >= NumbersUtils.toDouble(value));
            case "religion":
                return value.equals(country.getReligionName());
            case "religion_group":
                return value.equals(country.getReligion().getReligionGroup().getName());
            case "religion_group_claim": // ???
                break;
            case "religion_years": //Todo object
                break;
            case "religious_school": //Todo object
                break;
            case "religious_unity":
                return country.getReligiousUnity() >= NumbersUtils.toDouble(value);
            case "republican_tradition":
                return country.getRepublicanTradition() == null || country.getRepublicanTradition() >= NumbersUtils.toDouble(value);
            case "revanchism":
                return NumbersUtils.doubleOrDefault(country.getRepublicanTradition()) >= NumbersUtils.toDouble(value);
            case "reverse_has_opinion": //Todo object
                break;
            case "reverse_has_opinion_modifier": //Todo object
                break;
            case "revolt_percentage":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getOwnedProvinces().size()))
                                            .compareTo(BigDecimal.valueOf(country.getOwnedProvinces()
                                                                                 .stream()
                                                                                 .filter(province -> "REB".equals(ClausewitzUtils.removeQuotes(province.getControllerTag())))
                                                                                 .count())) >= 0;
            case "revolution_target_exists":
                return country.getSave().getRevolution().getRevolutionTarget() != null;
            case "ruler_age":
                return country.getMonarch() == null
                       || DateUtils.addYears(country.getMonarch().getBirthDate(), NumbersUtils.toInt(value)).before(country.getSave().getDate());
            case "ruler_consort_marriage_length": //Todo
                break;
            case "ruler_culture":
                return country.getMonarch() == null || value.equals(country.getMonarch().getCulture().getName());
            case "ruler_is_foreigner":
                return "yes".equals(value) == (country.getMonarch() == null ||
                                               (country.getMonarch().getCountry() != null && !country.equals(country.getMonarch().getCountry())));
            case "ruler_religion":
                return country.getMonarch() == null || value.equals(country.getMonarch().getReligion().getName());
            case "sailors":
                return NumbersUtils.doubleOrDefault(country.getSailors()) >= NumbersUtils.toDouble(value);
            case "sailors_percentage":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getMaxSailors()))
                                            .compareTo(BigDecimal.valueOf(country.getSailors())) >= 0;
            case "max_sailors":
                return NumbersUtils.doubleOrDefault(country.getMaxSailors()) >= NumbersUtils.toDouble(value);
            case "same_continent": //Todo object
                break;
            case "secondary_religion":
                return country.getSecondaryReligion() == null || value.equals(country.getSecondaryReligion().getName());
            case "senior_union_with":
                other = country.getSave().getCountry(value);
                return other.getOverlord() != null && other.getOverlord().equals(country)
                       && country.getSave()
                                 .getDiplomacy()
                                 .getDependencies()
                                 .stream()
                                 .anyMatch(dependency -> "personal_union".equals(dependency.getSubjectType()) && other.equals(dependency.getSecond()));
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
                       || country.getSave().getStartDate().after(Eu4Utils.stringToDate(value));
            case "statists_vs_orangists":
                return country.getStatistsVsMonarchists() == null
                       || NumbersUtils.doubleOrDefault(country.getStatistsVsMonarchists()) >= NumbersUtils.toDouble(value);
            case "subsidised_percent_amount":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getEstimatedMonthlyIncome()))
                                            .compareTo(BigDecimal.valueOf(country.getSave()
                                                                                 .getDiplomacy()
                                                                                 .getSubsidies()
                                                                                 .stream()
                                                                                 .filter(subsidies -> country.equals(subsidies.getSecond()))
                                                                                 .mapToDouble(QuantifyDatableRelation::getAmount)
                                                                                 .sum())) >= 0;
            case "succession_claim":
                other = country.getSave().getCountry(value);
                return BooleanUtils.toBoolean(country.getActiveRelations().get(other.getTag()).hasSuccessionClaim());
            case "tag":
                return value.equals(country.getTag());
            case "tariff_value":
                return country.getTariff() != null && country.getTariff() >= NumbersUtils.toDouble(value);
            case "tax_income_percentage": //Can't do
                break;
            case "tech_difference": //Todo object
                break;
            case "technology_group":
                if ((other = country.getSave().getCountry(value)) != null) {
                    return country.getTechnologyGroup().equals(other.getTechnologyGroup());
                } else {
                    return value.equals(country.getTechnologyGroup());
                }
            case "tolerance_to_this": //Todo
                break;
            case "total_base_tax":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getBaseTax() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getBaseTax() >= other.getBaseTax();
                }
            case "total_development":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getRawDevelopment() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getRawDevelopment() >= other.getRawDevelopment();
                }
            case "total_number_of_cardinals":
                return country.getSave()
                              .getReligions()
                              .getReligions()
                              .values()
                              .stream()
                              .anyMatch(religion -> religion.getPapacy() != null && religion.getPapacy().getCardinals().size() >= NumbersUtils.toInt(value));
            case "trade_league_embargoed_by":
                other = country.getSave().getCountry(value);
                Optional<TradeLeague> tradeLeague = country.getSave()
                                                           .getTradeLeagues()
                                                           .stream()
                                                           .filter(league -> league.getMembers().contains(country))
                                                           .findFirst();

                return tradeLeague.isPresent() && tradeLeague.get().getMembers().stream().anyMatch(member -> member.getTradeEmbargoedBy().contains(other));
            case "trade_efficiency": //Todo
                break;
            case "trade_embargoing":
                other = country.getSave().getCountry(value);
                return other.getTradeEmbargoedBy().contains(country);
            case "trade_embargo_by":
                other = country.getSave().getCountry(value);
                return country.getTradeEmbargoedBy().contains(other);
            case "trade_income_percentage": //Can't do
                break;
            case "trading_bonus": //Todo object
                break;
            case "trading_part": //Todo object
                break;
            case "trading_policy_in_node": //Todo object
                break;
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
            case "trust": //Todo object
                break;
            case "unit_type":
                return value.equals(country.getUnitType());
            case "uses_authority":
                return country.getReligion().getGameReligion().useAuthority();
            case "uses_church_aspects":
                return country.getReligion().getGameReligion().usesChurchPower();
            case "uses_blessings":
                return country.getReligion().getGameReligion().getBlessings() != null;
            case "uses_cults":
                return country.getReligion().getGameReligion().useFetishistCult();
            case "uses_devotion": //Todo
                break;
            case "uses_doom": //Todo
                break;
            case "uses_fervor":
                return country.getReligion().getGameReligion().useFervor();
            case "uses_isolationism":
                return country.getReligion().getGameReligion().usesIsolationism();
            case "uses_karma":
                return country.getReligion().getGameReligion().usesKarma();
            case "uses_papacy":
                return country.getReligion().getGameReligion().getPapacy() != null;
            case "uses_patriarch_authority":
                return country.getReligion().getGameReligion().hasPatriarchs();
            case "uses_personal_deities":
                return country.getReligion().getGameReligion().usePersonalDeity();
            case "uses_piety":
                return country.getReligion().getGameReligion().usesPiety();
            case "uses_religious_icons":
                return country.getReligion().getGameReligion().getIcons() != null;
            case "uses_syncretic_faiths":
                return country.getReligion().getGameReligion().canHaveSecondaryReligion();
            case "variable_arithmetic_trigger": //Todo object
                break;
            case "vassal_of":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other)
                       && country.getSave()
                                 .getDiplomacy()
                                 .getDependencies()
                                 .stream()
                                 .anyMatch(dependency -> "vassal".equals(dependency.getSubjectType()) && country.equals(dependency.getSecond()));
            case "war_exhaustion":
                if ((aDouble = NumbersUtils.toDouble(value)) != null) {
                    return country.getWarExhaustion() >= aDouble;
                } else {
                    other = country.getSave().getCountry(value);
                    return NumbersUtils.doubleOrDefault(country.getWarExhaustion()) >= NumbersUtils.doubleOrDefault(other.getWarExhaustion());
                }
            case "war_score":
                return country.getActiveWars()
                              .stream()
                              .anyMatch(war -> (war.getDefenderScore() >= NumbersUtils.toDouble(value) && war.getDefenders().containsKey(country))
                                               || (war.getDefenderScore() <= -NumbersUtils.toDouble(value) && war.getAttackers().containsKey(country)));
            case "war_score_against": //Todo object
                break;
            case "war_with":
                other = country.getSave().getCountry(value);
                return country.getActiveWars()
                              .stream()
                              .anyMatch(war -> war.getOtherSide(country).containsKey(other));
            case "was_player":
                return "yes".equals(value) == country.wasPlayer();
            case "was_tag":
                return country.getPreviousCountryTags().contains(ClausewitzUtils.addQuotes(value));
            case "will_back_next_reform": //Todo
                break;
            case "yearly_corruption_increase": //Todo
                break;
            case "years_in_union_under": //Todo object
                break;
            case "years_in_vassalage_under": //Todo
                break;
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
            default:
                LOGGER.info("Don't know how to manage country condition: " + condition + "=" + value);
        }

        return true;
    }

    public static boolean applyScopeToCountry(Country root, Country from, Condition condition) {
        switch (condition.getName().toLowerCase()) {
            case "or":
                return condition.getConditions().entrySet()
                                .stream()
                                .anyMatch(entry -> entry.getValue()
                                                        .stream()
                                                        .anyMatch(value -> applyConditionToCountry(root, root, from, entry.getKey(), value)))
                       || condition.getScopes().stream().anyMatch(scope -> applyScopeToCountry(root, from, scope));
            case "and":
                return condition.getConditions().entrySet()
                                .stream()
                                .allMatch(entry -> entry.getValue()
                                                        .stream()
                                                        .anyMatch(value -> applyConditionToCountry(root, root, from, entry.getKey(), value)))
                       && condition.getScopes().stream().allMatch(scope -> applyScopeToCountry(root, from, scope));
            case "not":
                return condition.getConditions().entrySet()
                                .stream()
                                .noneMatch(entry -> entry.getValue()
                                                         .stream()
                                                         .anyMatch(value -> applyConditionToCountry(root, root, from, entry.getKey(), value)))
                       && condition.getScopes().stream().noneMatch(scope -> applyScopeToCountry(root, from, scope));
            case "colonial_parent":
                return root.getColonialParent() != null && condition.apply(root.getColonialParent(), from);
            case "overlord":
                return root.getOverlord() != null && condition.apply(root.getOverlord(), from);
            case "capital_scope": //Todo province conditions
                break;
            case "root":
                return condition.apply(root, from);
            case "from":
                return condition.apply(from, root);
            default:
                LOGGER.info("Don't know how to manage country scope: " + condition);
        }

        return true;
    }
}
