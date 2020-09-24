package com.osallek.eu4parser.common;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.eu4parser.model.game.Culture;
import com.osallek.eu4parser.model.save.Power;
import com.osallek.eu4parser.model.save.country.ActivePolicy;
import com.osallek.eu4parser.model.save.country.Country;
import com.osallek.eu4parser.model.save.country.Estate;
import com.osallek.eu4parser.model.save.country.Faction;
import com.osallek.eu4parser.model.save.country.Income;
import com.osallek.eu4parser.model.save.country.Leader;
import com.osallek.eu4parser.model.save.country.Modifier;
import com.osallek.eu4parser.model.save.country.PowerProjection;
import com.osallek.eu4parser.model.save.country.Queen;
import com.osallek.eu4parser.model.save.diplomacy.SubjectType;
import com.osallek.eu4parser.model.save.empire.HreReligionStatus;
import com.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConditionsUtils {

    private ConditionsUtils() {}

    public static boolean applyConditionToCountry(Country country, String condition, String value) {
        Country other;
        SaveProvince saveProvince;
        Integer integer;
        Double aDouble;
        Calendar calendar = Calendar.getInstance();

        switch (condition) {
            case "absolutism":
                return country.getAbsolutism() >= Integer.parseInt(value);
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
                return country.getSave().getHre() != null && Boolean.TRUE.equals(country.getSave().getHre().getAllowsFemaleEmperor());
            case "always":
                return "yes".equals(value);
            case "army_size":
                if ((integer = NumbersUtils.toInt(value)) != null) {
                    return country.getArmySize() >= integer;
                } else {
                    other = country.getSave().getCountry(value);
                    return country.getArmySize() >= other.getArmySize();
                }
            case "army_size_percentage": //Fixme do land limit
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
            case "current_icon": //Todo read icons from religion.txt
                break;
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
                    return country != null;
                } else {
                    return country.getSave().getCountry(value) != null;
                }
            case "faction_in_power": //Todo faction
                break;
            case "faction_influence": //Todo faction
                break;
            case "federation_size":
                return country.getFederationFriends().size() >= NumbersUtils.toInt(value);
            case "fervor":
                return country.getFervor() != null && NumbersUtils.doubleOrDefault(country.getFervor().getValue()) >= NumbersUtils.toDouble(value);
            case "full_idea_group":
                return country.getIdeaGroups().getIdeaGroups().getOrDefault(value, -1) == 7;
            case "galley_fraction":
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getNavySize())).compareTo(BigDecimal.valueOf(country.getNbGalleys())) >= 0;
            case "gold_income":
                return country.getLedger() != null && country.getLedger().getLastMonthIncomeTable().get(Income.GOLD) >= NumbersUtils.toDouble(value);
            case "gold_income_percentage":
                return country.getLedger() != null
                       && new BigDecimal(value).multiply(BigDecimal.valueOf(country.getLedger().getLastMonthIncome()))
                                               .compareTo(BigDecimal.valueOf(country.getLedger().getLastMonthIncomeTable().get(Income.GOLD))) >= 0;
            case "government":
                return country.getGovernment().getType().equals(value);
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
                return country.getConsort() != null && Boolean.TRUE.equals(country.getConsort().getRegent());
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
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(6));
            case "has_embargo_rivals":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(1));
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
                return country.getConsort() != null && Boolean.TRUE.equals(country.getConsort().getFemale());
            case "has_female_heir":
                return country.getHeir() != null && Boolean.TRUE.equals(country.getHeir().getFemale());
            case "has_first_revolution_started":
                return country.getSave().getRevolution() != null && Boolean.TRUE.equals(country.getSave().getRevolution().hasFirstRevolutionStarted());
            case "has_foreign_consort":
                return country.getConsort() != null && country.getConsort().getWho() != null;
            case "has_foreign_heir":
                return country.getHeir() != null && country.getHeir().getWho() != null;
            case "has_friendly_reformation_center":
                return country.getOwnedProvinces()
                              .stream()
                              .filter(province -> province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> Boolean.TRUE.equals(province.centerOfReligion()));
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
            case "has_government_mechanic":
                return country.getGovernment().hasMechanic(value);
            case "has_harmonized_with": //Todo religions id
                //                return country.getHarmonizedReligionGroups().contains();
                break;
            case "has_heir":
                return "yes".equals(value) == (country.getHeir() != null);
            case "has_heir_flag":
                return country.getHeir() != null && country.getHeir().getRulerFlags().contains(value);
            case "has_hostile_reformation_center":
                return country.getOwnedProvinces()
                              .stream()
                              .filter(province -> !province.getReligion().equals(country.getReligion()))
                              .anyMatch(province -> Boolean.TRUE.equals(province.centerOfReligion()));
            case "has_idea": //Todo parse idea groups
                break;
            case "has_idea_group":
                return country.getIdeaGroups().getIdeaGroups().containsKey(value);
            case "has_institution": //Todo get institution id
                //                return country.getEmbracedInstitution();
                break;
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
                return NumbersUtils.intOrDefault(country.getNumShipsPrivateering()) >= NumbersUtils.toInt(value);
            case "has_promote_investments": //Todo parse trade companies
                return CollectionUtils.isNotEmpty(country.getTradeCompanies());
            case "has_regency":
                return Boolean.TRUE.equals(country.getMonarch().getRegent());
            case "has_reform":
                return country.getGovernment().getReforms().contains(value);
            case "government_reform_progress":
                return NumbersUtils.doubleOrDefault(country.getGovernmentReformProgress()) >= NumbersUtils.toDouble(value);
            case "has_removed_fow": //Todo object
                break;
            case "has_ruler":
                return country.getMonarch().getName().equals(value);
            case "has_ruler_flag":
                return country.getMonarch().getRulerFlags().contains(value);
            case "has_ruler_modifier":
                return country.getModifiers()
                              .stream()
                              .filter(modifier -> Boolean.TRUE.equals(modifier.rulerModifier()))
                              .map(Modifier::getModifier)
                              .anyMatch(value::equals);
            case "has_saved_event_target": //Todo ???
                break;
            case "has_scutage":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(4));
            case "has_secondary_religion":
                return country.getSecondaryReligion() != null;
            case "has_send_officers":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(5));
            case "has_spawned_rebels":
                return country.getSave()
                              .getRebelFactions()
                              .stream()
                              .anyMatch(rebelFaction -> Boolean.TRUE.equals(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getType().equals(ClausewitzUtils.removeQuotes(value)));
            case "has_spawned_supported_rebels":
                other = country.getSave().getCountry(value);
                return country.getSave()
                              .getRebelFactions()
                              .stream()
                              .anyMatch(rebelFaction -> Boolean.TRUE.equals(rebelFaction.isActive()) && rebelFaction.getCountry().equals(country)
                                                        && rebelFaction.getSupportiveCountry() != null && rebelFaction.getSupportiveCountry().equals(other));
            case "has_spy_network_from": //Todo object
                break;
            case "has_spy_network_in": //Todo object
                break;
            case "has_subsidize_armies":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(3));
            case "has_support_loyalists":
                return CollectionUtils.isNotEmpty(country.getSubjectInteractions()) && Boolean.TRUE.equals(country.getSubjectInteractions().get(2));
            case "has_switched_nation":
                return Boolean.TRUE.equals(country.hasSwitchedNation());
            case "has_truce":
                other = country.getSave().getCountry(value);
                return country.getActiveRelation(other) != null && Boolean.TRUE.equals(country.getActiveRelation(other).truce());
            case "has_unembraced_institution": //Todo institutions
                //                return country.getEmbracedInstitution();
                break;
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
                return Boolean.TRUE.equals(country.warTaxes());
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
                return !country.getSave().getHre().dismantled() && Boolean.TRUE.equals(
                        country.getSave().getReligions().getReligion(value).isHreHereticReligion());
            case "hre_leagues_enabled":
                return !country.getSave().getHre().dismantled() && Boolean.TRUE.equals(country.getSave().getHreLeaguesActive());
            case "hre_reform_level":
                return !country.getSave().getHre().dismantled() && country.getSave().getHre().getPassedReforms().size() >= NumbersUtils.toInt(value);
            case "hre_religion":
                return !country.getSave().getHre().dismantled() && Boolean.TRUE.equals(country.getSave().getReligions().getReligion(value).isHreReligion());
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
                return Boolean.TRUE.equals(country.isExcommunicated());
            case "is_federation_leader":
                return country.equals(country.getFederationLeader());
            case "is_female":
                return Boolean.TRUE.equals(country.getMonarch().getFemale());
            case "is_force_converted":
                return country.getForceConvert() != null;
            case "is_former_colonial_nation":
                return "yes".equals(value) == (country.getOverlord() == null && country.getColonialParent() != null);
            case "is_great_power":
                return country.isGreatPower();
            case "is_harmonizing_with":
                return value.equals(ClausewitzUtils.removeQuotes(country.getHarmonyWithReligion()));
            case "is_heir_leader":
                return country.getHeir() != null && country.getHeir().getLeader() != null;
            case "is_hegemon":
                return "yes".equals(value) == (country.getPowerProjections().stream().map(PowerProjection::getModifier).anyMatch("is_hegemon"::equals));
            case "is_imperial_ban_allowed":
                return !country.getSave().getHre().dismantled() && Boolean.TRUE.equals(country.getSave().getHre().getImperialBanAllowed());
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
            case "is_institution_enabled": //Todo institutions
                break;
            case "is_in_coalition":
                return country.getCoalitionTarget() != null;
            case "is_in_coalition_war": //Todo parse cb to know if it's a coalition cb and check actives wars
                break;
            case "is_in_deficit":
                return country.getLedger().getLastMonthIncome() < country.getLedger().getLastMonthExpense();
            case "is_in_league_war": //Todo parse cb to know if it's a league cb and check actives wars
                break;
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
                return Boolean.TRUE.equals(country.getSave().getHreLeaguesActive())
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
                return "yes".equals(value) == (Boolean.TRUE.equals(country.hasReformedReligion()));
            case "is_revolution_target":
                return country.getSave().getRevolution() != null && country.equals(country.getSave().getRevolution().getRevolutionTarget());
            case "is_rival":
                other = country.getSave().getCountry(value);
                return country.getRivals().containsKey(ClausewitzUtils.addQuotes(other.getTag()));
            case "is_state_core":
                integer = NumbersUtils.toInt(value);
                saveProvince = country.getSave().getProvince(integer);
                return country.getCoreProvinces().contains(saveProvince)
                       && country.getStates().keySet().stream().anyMatch(area -> area.getProvinces().contains(saveProvince));
            case "is_subject":
                return "yes".equals(value) == (country.getOverlord() != null);
            case "is_subject_of":
                other = country.getSave().getCountry(value);
                return country.getOverlord() != null && country.getOverlord().equals(other);
            case "is_subject_of_type":
                return country.getOverlord() != null
                       && country.getSave().getDiplomacy().getDependencies().stream().anyMatch(dependency -> country.equals(dependency.getSecond())
                                                                                                             && value.equals(dependency.getSubjectType()));
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
                return new BigDecimal(value).multiply(BigDecimal.valueOf(country.getArmySize())).compareTo(BigDecimal.valueOf(country.getNbRegimentOf("ottoman_janissary"))) >= 0;
        }

        return true;
    }
}
