package com.osallek.eu4parser.model.country;

import com.osallek.clausewitzparser.common.Utils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.Power;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Country {

    //Todo cooldowns, history, flags, hidden_flags, variables, colors, convert, fervor, parliament

    //Todo combined countries:
    //trade_embargoed_by, trade_embargoes, diplomacy(transfer_trade_power)
    //subjects, overlord, diplomacy(dependency)

    private final ClausewitzItem item;

    private Technology tech;

    private List<Estate> estates;

    private Map<String, Rival> rivals;

    private List<VictoryCard> victoryCards;

    private List<ActivePolicy> activePolicies;

    private List<PowerProjection> powerProjections;

    private Ledger ledger;

    private Map<Integer, Province> provinces;

    public Country(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public String getTag() {
        return this.item.getName();
    }

    public Boolean isHuman() {
        return this.item.getVarAsBool("human");
    }

    public Boolean wasPlayer() {
        return this.item.getVarAsBool("was_player");
    }

    public void setWasPlayer(boolean wasPlayer) {
        ClausewitzVariable var = this.item.getVar("was_player");

        if (var != null) {
            var.setValue(wasPlayer);
        } else {
            this.item.addVariable("was_player", wasPlayer);
        }
    }

    public Boolean hasSetGovernmentName() {
        return this.item.getVarAsBool("has_set_government_name");
    }

    public GovernmentRank getGovernmentRank() {
        ClausewitzVariable var = this.item.getVar("government_rank");

        if (var != null) {
            return GovernmentRank.values()[var.getAsInt()];
        } else {
            return null;
        }
    }

    public void setGovernmentRank(GovernmentRank governmentRank) {
        ClausewitzVariable var = this.item.getVar("government_rank");

        if (var != null) {
            var.setValue(governmentRank.ordinal());
        } else {
            this.item.addVariable("government_rank", governmentRank.ordinal());
        }
    }

    public String getGovernmentName() {
        return this.item.getVarAsString("government_name");
    }

    public void setGovernmentName(String governmentName) {
        governmentName = Utils.addQuotes(governmentName);

        ClausewitzVariable var = this.item.getVar("government_name");

        if (var != null) {
            var.setValue(governmentName);
        } else {
            this.item.addVariable("government_name", governmentName);
        }
    }

    public Integer getSubjectFocus() {
        return this.item.getVarAsInt("subject_focus");
    }

    public Double getTradeMission() {
        return this.item.getVarAsDouble("trade_mission");
    }

    public Double getBlockadeMission() {
        return this.item.getVarAsDouble("blockade_mission");
    }

    public List<Continent> getContinents() {
        ClausewitzList list = this.item.getList("continent");
        List<Continent> continents = new ArrayList<>();

        if (list != null) {
            for (int i = 0; i < Continent.values().length; i++) {
                if (1 == list.getAsInt(i)) {
                    continents.add(Continent.values()[i]);
                }
            }
        }

        return continents;
    }

    public Power getNationalFocus() {
        ClausewitzVariable var = this.item.getVar("national_focus");

        if (var != null) {
            return Power.valueOf(var.getValue().toUpperCase());
        }

        return null;
    }

    public void setNationalFocus(Power power, Date date) {
        ClausewitzVariable var = this.item.getVar("national_focus");

        if (var != null) {
            var.setValue(power.name());
        } else {
            this.item.addVariable("national_focus", power.name());
        }

        //TODO Add to history: 1596.3.23={national_focus=MIL}
    }

    public List<Institution> getEmbracedInstitutions() {
        ClausewitzList list = this.item.getList("institutions");
        List<Institution> institutions = new ArrayList<>();

        if (list != null) {
            for (int i = 0; i < Institution.values().length; i++) {
                if (1 == list.getAsInt(i)) {
                    institutions.add(Institution.values()[i]);
                }
            }
        }

        return institutions;
    }

    public void embracedInstitution(Institution institution, boolean embraced) {
        ClausewitzList list = this.item.getList("institutions");

        if (list != null) {
            list.set(institution.ordinal(), embraced ? 1 : 0);
        }
    }

    public Integer getNumOfAgeObjectives() {
        return this.item.getVarAsInt("num_of_age_objectives");
    }

    public List<AgeAbility> getActiveAgeAbility() {
        List<AgeAbility> list = new ArrayList<>();

        for (String ability : this.item.getVarsAsStrings("active_age_ability")) {
            list.add(AgeAbility.valueOf(Utils.removeQuotes(ability).toUpperCase()));
        }

        return list;
    }

    public void addAgeAbility(AgeAbility ageAbility) {
        List<String> abilities = this.item.getVarsAsStrings("active_age_ability");

        if (!abilities.contains(ageAbility.name().toLowerCase())) {
            this.item.addVariable("active_age_ability", Utils.addQuotes(ageAbility.name().toLowerCase()));
        }
    }

    public void removeAgeAbility(int index) {
        this.item.removeVariable("active_age_ability", index);
    }

    public void removeAgeAbility(AgeAbility ageAbility) {
        this.item.removeVariable("active_age_ability", ageAbility.name());
    }

    public Date getLastSoldProvince() {
        return this.item.getVarAsDate("last_sold_province");
    }

    public void setLastSoldProvince(Date lastSoldProvince) {
        ClausewitzVariable var = this.item.getVar("last_sold_province");

        if (var != null) {
            var.setValue(lastSoldProvince);
        } else {
            this.item.addVariable("last_sold_province", lastSoldProvince);
        }
    }

    public Date getGoldenEraDate() {
        return this.item.getVarAsDate("golden_era_date");
    }

    public void setGoldenEraDate(Date goldenEraDate) {
        ClausewitzVariable var = this.item.getVar("golden_era_date");

        if (var != null) {
            var.setValue(goldenEraDate);
        } else {
            this.item.addVariable("golden_era_date", goldenEraDate);
        }
    }

    public Date getLastFocusMove() {
        return this.item.getVarAsDate("last_focus_move");
    }

    public void setLastFocusMove(Date lastFocusMove) {
        ClausewitzVariable var = this.item.getVar("last_focus_move");

        if (var != null) {
            var.setValue(lastFocusMove);
        } else {
            this.item.addVariable("last_focus_move", lastFocusMove);
        }
    }

    public Date getLastSentAllianceOffer() {
        return this.item.getVarAsDate("last_sent_alliance_offer");
    }

    public void setLastSentAllianceOffer(Date lastSentAllianceOffer) {
        ClausewitzVariable var = this.item.getVar("last_sent_alliance_offer");

        if (var != null) {
            var.setValue(lastSentAllianceOffer);
        } else {
            this.item.addVariable("last_sent_alliance_offer", lastSentAllianceOffer);
        }
    }

    public List<String> getIgnoreDecision() {
        return this.item.getVarsAsStrings("ignore_decision");
    }

    public void addIgnoreDecision(String ignoreDecision) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("ignore_decision");

        if (!ignoreDecisions.contains(ignoreDecision)) {
            this.item.addVariable("ignore_decision", Utils.addQuotes(ignoreDecision));
        }
    }

    public void removeIgnoreDecision(int index) {
        this.item.removeVariable("ignore_decision", index);
    }

    public Integer getCapital() {
        return this.item.getVarAsInt("capital");
    }

    public void setCapital(Integer provinceId) {
        if (provinces.containsKey(provinceId)) {
            ClausewitzVariable var = this.item.getVar("capital");

            if (var != null) {
                var.setValue(provinceId);
            } else {
                this.item.addVariable("capital", provinceId);
            }
        }
    }

    public Integer getOriginalCapital() {
        return this.item.getVarAsInt("original_capital");
    }

    public void setOriginalCapital(Integer provinceId) {
        ClausewitzVariable var = this.item.getVar("original_capital");

        if (var != null) {
            var.setValue(provinceId);
        } else {
            this.item.addVariable("capital", provinceId);
        }
    }

    public Integer getTradePort() {
        return this.item.getVarAsInt("trade_port");
    }

    public void setTradePort(Integer provinceId) {
        if (provinces.containsKey(provinceId)) {
            ClausewitzVariable var = this.item.getVar("trade_port");

            if (var != null) {
                var.setValue(provinceId);
            } else {
                this.item.addVariable("trade_port", provinceId);
            }
        }
    }

    public Double getBaseTax() {
        return this.item.getVarAsDouble("base_tax");
    }

    public Double getDevelopment() {
        return this.item.getVarAsDouble("development");
    }

    public Double getRawDevelopment() {
        return this.item.getVarAsDouble("raw_development");
    }

    public Double getCappedDevelopment() {
        return this.item.getVarAsDouble("capped_development");
    }

    public Double getRealmDevelopment() {
        return this.item.getVarAsDouble("realm_development");
    }

    public Integer getIsolationism() {
        return this.item.getVarAsInt("isolationism");
    }

    public void setIsolationism(Integer isolationism) {
        ClausewitzVariable var = this.item.getVar("isolationism");

        if (var != null) {
            var.setValue(isolationism);
        } else {
            this.item.addVariable("isolationism", isolationism);
        }
    }

    public boolean hasCircumnavigatedWorld() {
        return this.item.getVarAsBool("has_circumnavigated_world");
    }

    public void setHasCircumnavigatedWorld(boolean hasCircumnavigatedWorld) {
        ClausewitzVariable var = this.item.getVar("has_circumnavigated_world");

        if (var != null) {
            var.setValue(hasCircumnavigatedWorld);
        } else {
            this.item.addVariable("has_circumnavigated_world", hasCircumnavigatedWorld);
        }
    }

    public boolean initializedRivals() {
        return this.item.getVarAsBool("initialized_rivals");
    }

    public boolean recalculateStrategy() {
        return this.item.getVarAsBool("recalculate_strategy");
    }

    public boolean dirtyColony() {
        return this.item.getVarAsBool("dirty_colony");
    }

    public String getPrimaryCulture() {
        return this.item.getVarAsString("primary_culture");
    }

    public void setPrimaryCulture(String primaryCulture) {
        ClausewitzVariable var = this.item.getVar("primary_culture");

        if (var != null) {
            var.setValue(primaryCulture);
        } else {
            this.item.addVariable("primary_culture", primaryCulture);
        }
    }

    public String getDominantCulture() {
        return this.item.getVarAsString("dominant_culture");
    }

    public List<String> getAcceptedCultures() {
        return this.item.getVarsAsStrings("accepted_culture");
    }

    public void addAcceptedCulture(String culture) {
        List<String> ignoreDecisions = this.item.getVarsAsStrings("accepted_culture");

        if (!ignoreDecisions.contains(culture)) {
            this.item.addVariable("accepted_culture", culture);
        }
    }

    public void removeAcceptedCulture(int index) {
        this.item.removeVariable("accepted_culture", index);
    }

    public void removeAcceptedCulture(String culture) {
        this.item.removeVariable("accepted_culture", culture);
    }

    public String getReligion() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(String religion) {
        ClausewitzVariable var = this.item.getVar("religion");

        if (var != null) {
            var.setValue(religion);
        } else {
            this.item.addVariable("religion", religion);
        }
    }

    public String getDominantReligion() {
        return this.item.getVarAsString("dominant_religion");
    }

    public String getTechnologyGroup() {
        return this.item.getVarAsString("technology_group");
    }

    public void setTechnologyGroup(String technologyGroup) {
        ClausewitzVariable var = this.item.getVar("technology_group");

        if (var != null) {
            var.setValue(technologyGroup);
        } else {
            this.item.addVariable("technology_group", technologyGroup);
        }
    }

    public String getUnitType() {
        return this.item.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        ClausewitzVariable var = this.item.getVar("unit_type");

        if (var != null) {
            var.setValue(unitType);
        } else {
            this.item.addVariable("unit_type", unitType);
        }
    }

    public Technology getTech() {
        return tech;
    }

    public List<Estate> getEstates() {
        return estates;
    }

    public Map<String, Rival> getRivals() {
        return rivals;
    }

    public void addRival(String country, Date date) {
        if (!this.rivals.containsKey(country)) {
            Rival.addToItem(this.item, country, date);
            refreshAttributes();
        }
    }

    public void removeRival(int index) {
        this.item.removeVariable("rival", index);
        refreshAttributes();
    }

    public void removeRival(String rival) {
        Integer index = null;
        List<Rival> rivalList = new ArrayList<>(this.rivals.values());

        for (int i = 0; i < rivalList.size(); i++) {
            if (rivalList.get(i).getRival().equalsIgnoreCase(rival)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("rival", index);
            refreshAttributes();
        }
    }

    public Integer getHighestPossibleFort() {
        return this.item.getVarAsInt("highest_possible_fort");
    }

    public String getHighestPossibleFortBuilding() {
        return this.item.getVarAsString("highest_possible_fort_building");
    }

    public Double getTransferHomeBonus() {
        return this.item.getVarAsDouble("transfer_home_bonus");
    }

    public String getOverlord() {
        return this.item.getVarAsString("overlord");
    }

    public List<String> getEnemies() {
        return this.item.getVarsAsStrings("enemy");
    }

    public Integer getGoldType() {
        return this.item.getVarAsInt("goldtype");
    }

    public List<String> getOurSpyNetwork() {
        return this.item.getVarsAsStrings("our_spy_network");
    }

    public List<String> getTheirSpyNetwork() {
        return this.item.getVarsAsStrings("their_spy_network");
    }

    public List<VictoryCard> getVictoryCards() {
        return victoryCards;
    }

    public void addVictoryCard(String area) {
        if (getVictoryCards().stream().noneMatch(victoryCard -> victoryCard.getArea().equalsIgnoreCase(area))) {
            int level = getVictoryCards().stream().mapToInt(VictoryCard::getLevel).max().orElse(0);
            VictoryCard.addToItem(this.item, area, level + 1, 0d, false);
            refreshAttributes();
        }
    }

    public void removeVictoryCard(int index) {
        this.item.removeVariable("victory_card", index);
        refreshAttributes();
    }

    public void removeVictoryCard(String area) {
        Integer index = null;
        List<VictoryCard> victoryCardList = getVictoryCards();

        for (int i = 0; i < victoryCardList.size(); i++) {
            if (victoryCardList.get(i).getArea().equalsIgnoreCase(area)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("victory_card", index);
            refreshAttributes();
        }
    }

    public boolean newMonarch() {
        return this.item.getVarAsBool("new_monarch");
    }

    public boolean isAtWar() {
        return this.item.getVarAsBool("is_at_war");
    }

    public Date lastElection() {
        return this.item.getVarAsDate("last_election");
    }

    public void setLastElection(Date lastElection) {
        ClausewitzVariable var = this.item.getVar("last_election");

        if (var != null) {
            var.setValue(lastElection);
        } else {
            this.item.addVariable("last_election", lastElection);
        }
    }

    public Double getDelayedTreasure() {
        return this.item.getVarAsDouble("delayed_treasure");
    }

    public List<ActivePolicy> getActivePolicies() {
        return activePolicies;
    }

    public void addActivePolicy(String policy, Date date) {
        if (getActivePolicies().stream().noneMatch(activePolicy -> activePolicy.getPolicy().equalsIgnoreCase(policy))) {
            ActivePolicy.addToItem(this.item, policy, date);
            refreshAttributes();
        }
    }

    public void removeActivePolicy(int index) {
        this.item.removeVariable("active_policy", index);
        refreshAttributes();
    }

    public void removeActivePolicy(String policy) {
        Integer index = null;

        for (int i = 0; i < getActivePolicies().size(); i++) {
            if (getActivePolicies().get(i).getPolicy().equalsIgnoreCase(policy)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("active_policy", index);
            refreshAttributes();
        }
    }

    public Double getCurrentPowerProjection() {
        return this.item.getVarAsDouble("current_power_projection");
    }

    private void setCurrentPowerProjection(Double currentPowerProjection) {
        ClausewitzVariable var = this.item.getVar("current_power_projection");

        if (var != null) {
            var.setValue(currentPowerProjection);
        } else {
            this.item.addVariable("current_power_projection", currentPowerProjection);
        }
    }

    public Double getGreatPowerScore() {
        return this.item.getVarAsDouble("great_power_score");
    }

    public List<PowerProjection> getPowerProjections() {
        return powerProjections;
    }

    public void removePowerProjections(int index) {
        this.item.removeVariable("power_projection", index);
        refreshAttributes();
    }

    public void removePowerProjections(String modifier) {
        Integer index = null;

        for (int i = 0; i < getActivePolicies().size(); i++) {
            if (getPowerProjections().get(i).getModifier().equalsIgnoreCase(modifier)) {
                index = i;
                break;
            }
        }

        if (index != null) {
            this.item.removeVariable("power_projection", index);
            refreshAttributes();
        }
    }

    public Integer getNumOfTradeEmbargos() {
        return this.item.getVarAsInt("num_of_trade_embargos");
    }

    public Integer getNumOfNonRivalTradeEmbargos() {
        return this.item.getVarAsInt("num_of_non_rival_trade_embargos");
    }

    public Double getNavyStrength() {
        return this.item.getVarAsDouble("navy_strength");
    }

    public Integer getTotalWarWorth() {
        return this.item.getVarAsInt("total_war_worth");
    }

    public Integer getNumOwnedHomeCores() {
        return this.item.getVarAsInt("num_owned_home_cores");
    }

    public Double getNonOverseasDevelopment() {
        return this.item.getVarAsDouble("non_overseas_development");
    }

    public Integer getNumOfControlledCities() {
        return this.item.getVarAsInt("num_of_controlled_cities");
    }

    public Integer getNumOfPorts() {
        return this.item.getVarAsInt("num_of_ports");
    }

    public Integer getNumOfCorePorts() {
        return this.item.getVarAsInt("num_of_core_ports");
    }

    public Integer getNumOfTotalPorts() {
        return this.item.getVarAsInt("num_of_total_ports");
    }

    public Integer getNumOfCardinals() {
        return this.item.getVarAsInt("num_of_cardinals");
    }

    public Integer getNumOfMercenaries() {
        return this.item.getVarAsInt("num_of_mercenaries");
    }

    public Integer getNumOfRegulars() {
        return this.item.getVarAsInt("num_of_regulars");
    }

    public Integer getNumOfCities() {
        return this.item.getVarAsInt("num_of_cities");
    }

    public Integer getNumOfProvincesInStates() {
        return this.item.getVarAsInt("num_of_provinces_in_states");
    }

    public Integer getNumOfProvincesInTerritories() {
        return this.item.getVarAsInt("num_of_provinces_in_territories");
    }

    public Integer getNumOfForts() {
        return this.item.getVarAsInt("forts");
    }

    public Integer getNumOfAllies() {
        return this.item.getVarAsInt("num_of_allies");
    }

    public Integer getNumOfRoyalMarriages() {
        return this.item.getVarAsInt("num_of_royal_marriages");
    }

    public Integer getNumOfSubjects() {
        return this.item.getVarAsInt("num_of_subjects");
    }

    public Integer getNumOfHeathenProvs() {
        return this.item.getVarAsInt("num_of_heathen_provs");
    }

    public Double getInlandSeaRatio() {
        return this.item.getVarAsDouble("inland_sea_ratio");
    }

    public Boolean hasFriendlyReformationCenter() {
        return this.item.getVarAsBool("has_friendly_reformation_center=yes");
    }

    public Double getAverageUnrest() {
        return this.item.getVarAsDouble("average_unrest");
    }

    public Double getAverageEffectiveUnrest() {
        return this.item.getVarAsDouble("average_effective_unrest");
    }

    public Double getAverageAutonomy() {
        return this.item.getVarAsDouble("average_autonomy");
    }

    public Double getAverageAutonomyAboveMin() {
        return this.item.getVarAsDouble("average_autonomy_above_min");
    }

    public Double getAverageHomeAutonomy() {
        return this.item.getVarAsDouble("average_home_autonomy");
    }

    public List<String> getFriends() {
        ClausewitzList list = this.item.getList("friend_tags");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public Map<Integer, Integer> getNumOfBuildingsIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_buildings_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Integer> getNumOfBuildingsUnderConstructionIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_buildings_under_construction_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public List<Double> getProducedGoodsValue() {
        ClausewitzList list = this.item.getList("produced_goods_value");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public List<Integer> getNumOfGoodsProduced() {
        ClausewitzList list = this.item.getList("num_of_goods_produced");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Double> getTraded() {
        ClausewitzList list = this.item.getList("traded");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public Map<Integer, Integer> getNumOfReligionsIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_religions_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getNumOfReligionsDev() {
        ClausewitzItem child = this.item.getChild("num_of_religions_dev");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public List<Integer> getNumOfLeaders() {
        ClausewitzList list = this.item.getList("num_of_leaders");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getNumOfLeadersWithTraits() {
        ClausewitzList list = this.item.getList("num_of_leaders_with_traits");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getNumOfFreeLeaders() {
        ClausewitzList list = this.item.getList("num_of_free_leaders");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public Map<Integer, Integer> getNumOfSubjectCountIndexed() {
        ClausewitzItem child = this.item.getChild("num_of_subject_count_indexed");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsInt));
    }

    public Map<Integer, Double> getBorderPct() {
        ClausewitzItem child = this.item.getChild("border_pct");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public Map<Integer, Double> getBorderSit() {
        ClausewitzItem child = this.item.getChild("border_sit");

        if (child == null) {
            return new HashMap<>();
        }

        return child.getVariables()
                    .stream()
                    .collect(Collectors.toMap(var -> Integer.parseInt(var.getName()), ClausewitzVariable::getAsDouble));
    }

    public List<Integer> getBorderProvinces() {
        ClausewitzList list = this.item.getList("border_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<String> getNeighbours() {
        ClausewitzList list = this.item.getList("neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getHomeNeighbours() {
        ClausewitzList list = this.item.getList("home_neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getCoreNeighbours() {
        ClausewitzList list = this.item.getList("core_neighbours");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getCurrentAtWarWith() {
        ClausewitzList list = this.item.getList("current_at_war_with");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getCurrentWarAllies() {
        ClausewitzList list = this.item.getList("current_war_allies");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getCallToArmsFriends() {
        ClausewitzList list = this.item.getList("call_to_arms_friends");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getSubjects() {
        ClausewitzList list = this.item.getList("subjects");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getCondottieriClient() {
        ClausewitzList list = this.item.getList("condottieri_client");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getTradeEmbargoedBy() {
        ClausewitzList list = this.item.getList("trade_embargoed_by");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getTradeEmbargoes() {
        ClausewitzList list = this.item.getList("trade_embargoes");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getTransferTradePowerFrom() {
        ClausewitzList list = this.item.getList("transfer_trade_power_from");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public List<String> getTransferTradePowerTo() {
        ClausewitzList list = this.item.getList("ransfer_trade_power_to");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValues();
    }

    public Double getCardScore() {
        return this.item.getVarAsDouble("card_score");
    }

    public Map<Power, Double> getScoreRating() {
        ClausewitzList list = this.item.getList("score_rating");
        Map<Power, Double> scoreRatings = new EnumMap<>(Power.class);

        if (list == null) {
            return scoreRatings;
        }

        for (Power power : Power.values()) {
            scoreRatings.put(power, list.getAsDouble(power.ordinal()));
        }

        return scoreRatings;
    }

    public Map<Power, Integer> getScoreRank() {
        ClausewitzList list = this.item.getList("score_rank");
        Map<Power, Integer> scoreRanks = new EnumMap<>(Power.class);

        if (list == null) {
            return scoreRanks;
        }

        for (Power power : Power.values()) {
            scoreRanks.put(power, list.getAsInt(power.ordinal()));
        }

        return scoreRanks;
    }

    public List<Double> getAgeScore() {
        ClausewitzList list = this.item.getList("age_score");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public void setAgeScore(int age, double score) {
        ClausewitzList list = this.item.getList("age_score");

        if (list == null) {
            Double[] scores = new Double[age + 1];

            for (int i = 0; i < age; i++) {
                scores[i] = 0d;
            }

            scores[age] = score;

            this.item.addList("age_score", scores);
        } else {
            if (list.getValues().size() > age) {
                list.set(age, score);
            }
        }
    }

    public List<Double> getVictoryCardAgeScore() {
        ClausewitzList list = this.item.getList("vc_age_score");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public void setVictoryCardAgeScore(int age, double score) {
        ClausewitzList list = this.item.getList("vc_age_score");

        if (list == null) {
            Double[] scores = new Double[age + 1];

            for (int i = 0; i < age; i++) {
                scores[i] = 0d;
            }

            scores[age] = score;

            this.item.addList("vc_age_score", scores);
        } else {
            if (list.getValues().size() > age) {
                list.set(age, score);
            }
        }
    }

    public Integer getScorePlace() {
        return this.item.getVarAsInt("score_place");
    }

    public Double getPrestige() {
        return this.item.getVarAsDouble("prestige");
    }

    public void setPrestige(Double prestige) {
        ClausewitzVariable var = this.item.getVar("prestige");

        if (prestige < -100d) {
            prestige = -100d;
        } else if (prestige > 100d) {
            prestige = 100d;
        }

        if (var != null) {
            var.setValue(prestige);
        } else {
            this.item.addVariable("prestige", prestige);
        }
    }

    public Integer getStability() {
        Double stability = this.item.getVarAsDouble("stability");

        if (stability == null) {
            return null;
        } else {
            return stability.intValue();
        }
    }

    public void setStability(Integer stability) {
        ClausewitzVariable var = this.item.getVar("stability");

        if (stability < -3) {
            stability = -3;
        } else if (stability > 3) {
            stability = 3;
        }

        if (var != null) {
            var.setValue((double) stability);
        } else {
            this.item.addVariable("stability", (double) stability);
        }
    }

    public Double getTreasury() {
        return this.item.getVarAsDouble("treasury");
    }

    public void setTreasury(Double treasury) {
        ClausewitzVariable var = this.item.getVar("treasury");

        if (treasury > 1000000d) {
            treasury = 1000000d;
        }

        if (var != null) {
            var.setValue(treasury);
        } else {
            this.item.addVariable("treasury", treasury);
        }
    }

    public Double getEstimatedMonthlyIncome() {
        return this.item.getVarAsDouble("estimated_monthly_income");
    }

    public Double getInflation() {
        return this.item.getVarAsDouble("inflation");
    }

    public void setInflation(Double inflation) {
        ClausewitzVariable var = this.item.getVar("inflation");

        if (inflation < 0d) {
            inflation = 0d;
        } else if (inflation.intValue() >= Integer.MAX_VALUE) {
            inflation = (double) Integer.MAX_VALUE;
        }

        if (var != null) {
            var.setValue(inflation);
        } else {
            this.item.addVariable("inflation", inflation);
        }
    }

    public List<Double> getInflationHistory() {
        ClausewitzList list = this.item.getList("inflation_history");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsDouble();
    }

    public List<Integer> getOpinionCache() {
        ClausewitzList list = this.item.getList("opinion_cache");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    /**
     * Units under construction: infantry, cavalry, artillery, heavy, light, gallay, transport
     */
    public List<Integer> getUnderConstruction() {
        ClausewitzList list = this.item.getList("under_construction");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getUnderConstructionQueued() {
        ClausewitzList list = this.item.getList("under_construction_queued");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getTotalCount() {
        ClausewitzList list = this.item.getList("total_count");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public List<Integer> getOwnedProvinces() {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addOwnedProvince(int provinceId) {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (!list.contains(provinceId)) {
            list.add(provinceId);
        }
    }

    public void removeOwnedProvince(int provinceId) {
        ClausewitzList list = this.item.getList("owned_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), provinceId)) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<Integer> getControlledProvinces() {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addControlledProvince(int provinceId) {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (!list.contains(provinceId)) {
            list.add(provinceId);
        }
    }

    public void removeControlledProvince(int provinceId) {
        ClausewitzList list = this.item.getList("controlled_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), provinceId)) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<Integer> getCoreProvinces() {
        ClausewitzList list = this.item.getList("core_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addCoreProvince(int provinceId) {
        ClausewitzList list = this.item.getList("core_provinces");

        if (!list.contains(provinceId)) {
            list.add(provinceId);
        }
    }

    public void removeCoreProvince(int provinceId) {
        ClausewitzList list = this.item.getList("core_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), provinceId)) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<Integer> getClaimProvinces() {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public void addClaimProvince(int provinceId) {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (!list.contains(provinceId)) {
            list.add(provinceId);
        }
    }

    public void removeClaimProvince(int provinceId) {
        ClausewitzList list = this.item.getList("claim_provinces");

        if (list != null) {
            Integer index = null;
            for (int i = 0; i < list.size(); i++) {
                if (Objects.equals(list.getAsInt(i), provinceId)) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                list.remove(index);
            }
        }
    }

    public List<Integer> getIdeaMayCache() {
        ClausewitzList list = this.item.getList("idea_may_cache");

        if (list == null) {
            return new ArrayList<>();
        }

        return list.getValuesAsInt();
    }

    public Boolean updateOpinionCache() {
        return this.item.getVarAsBool("update_opinion_cache");
    }

    public Boolean needsRefresh() {
        return this.item.getVarAsBool("needs_refresh");
    }

    public Boolean casusBellisRefresh() {
        return this.item.getVarAsBool("casus_bellis_refresh");
    }

    public Boolean needsRebelUnitRefresh() {
        return this.item.getVarAsBool("needs_rebel_unit_refresh");
    }

    public Boolean canTakeWartaxes() {
        return this.item.getVarAsBool("can_take_wartaxes");
    }

    public Double getLandMaintenance() {
        return this.item.getVarAsDouble("land_maintenance");
    }

    public void setLandMaintenance(Double landMaintenance) {
        ClausewitzVariable var = this.item.getVar("land_maintenance");

        if (landMaintenance < 0d) {
            landMaintenance = 0d;
        } else if (landMaintenance > 1d) {
            landMaintenance = 1d;
        }

        if (var != null) {
            var.setValue(landMaintenance);
        } else {
            this.item.addVariable("land_maintenance", landMaintenance);
        }
    }

    public Double getNavalMaintenance() {
        return this.item.getVarAsDouble("naval_maintenance");
    }

    public void setNavalMaintenance(Double navalMaintenance) {
        ClausewitzVariable var = this.item.getVar("naval_maintenance");

        if (navalMaintenance < 0d) {
            navalMaintenance = 0d;
        } else if (navalMaintenance > 1d) {
            navalMaintenance = 1d;
        }

        if (var != null) {
            var.setValue(navalMaintenance);
        } else {
            this.item.addVariable("naval_maintenance", navalMaintenance);
        }
    }

    public Double getColonialMaintenance() {
        return this.item.getVarAsDouble("colonial_maintenance");
    }

    public void setColonialMaintenance(Double colonialMaintenance) {
        ClausewitzVariable var = this.item.getVar("colonial_maintenance");

        if (colonialMaintenance < 0d) {
            colonialMaintenance = 0d;
        } else if (colonialMaintenance > 1d) {
            colonialMaintenance = 1d;
        }

        if (var != null) {
            var.setValue(colonialMaintenance);
        } else {
            this.item.addVariable("colonial_maintenance", colonialMaintenance);
        }
    }

    public Double getMissionaryMaintenance() {
        return this.item.getVarAsDouble("missionary_maintenance");
    }

    public void setMissionaryMaintenance(Double missionaryMaintenance) {
        ClausewitzVariable var = this.item.getVar("missionary_maintenance");

        if (missionaryMaintenance < 0d) {
            missionaryMaintenance = 0d;
        } else if (missionaryMaintenance > 1d) {
            missionaryMaintenance = 1d;
        }

        if (var != null) {
            var.setValue(missionaryMaintenance);
        } else {
            this.item.addVariable("missionary_maintenance", missionaryMaintenance);
        }
    }

    public Double getArmyTradition() {
        return this.item.getVarAsDouble("army_tradition");
    }

    public void setArmyTradition(Double armyTradition) {
        ClausewitzVariable var = this.item.getVar("army_tradition");

        if (armyTradition < 0d) {
            armyTradition = 0d;
        } else if (armyTradition > 100d) {
            armyTradition = 100d;
        }

        if (var != null) {
            var.setValue(armyTradition);
        } else {
            this.item.addVariable("army_tradition", armyTradition);
        }
    }

    public Double getNavyTradition() {
        return this.item.getVarAsDouble("navy_tradition");
    }

    public void setNavyTradition(Double navyTradition) {
        ClausewitzVariable var = this.item.getVar("navy_tradition");

        if (navyTradition < 0d) {
            navyTradition = 0d;
        } else if (navyTradition > 100d) {
            navyTradition = 100d;
        }

        if (var != null) {
            var.setValue(navyTradition);
        } else {
            this.item.addVariable("navy_tradition", navyTradition);
        }
    }

    public Date getLastWarEnded() {
        return this.item.getVarAsDate("last_war_ended");
    }

    public Integer getNumUncontestedCores() {
        return this.item.getVarAsInt("num_uncontested_cores");
    }

    public Ledger getLedger() {
        return ledger;
    }

    public Integer getCancelledLoans() {
        return this.item.getVarAsInt("cancelled_loans");
    }

    public Integer getLoanSize() {
        return this.item.getVarAsInt("loan_size");
    }

    /**
     * Estimation of interests for a loan (ie: 100 = get 300 ducas, need to give back 400)
     */
    public Double getEstimatedLoan() {
        return this.item.getVarAsDouble("estimated_loan");
    }

    public Double getReligiousUnity() {
        return this.item.getVarAsDouble("religious_unity");
    }

    public Double getMeritocracy() {
        return this.item.getVarAsDouble("meritocracy");
    }

    public void setMeritocracy(Double meritocracy) {
        ClausewitzVariable var = this.item.getVar("meritocracy");

        if (meritocracy < 0d) {
            meritocracy = 0d;
        } else if (meritocracy > 100d) {
            meritocracy = 100d;
        }

        if (var != null) {
            var.setValue(meritocracy);
        } else {
            this.item.addVariable("meritocracy", meritocracy);
        }
    }

    public Double getPapalInfluence() {
        return this.item.getVarAsDouble("papal_influence");
    }

    public void setPapalInfluence(Double papalInfluence) {
        ClausewitzVariable var = this.item.getVar("papal_influence");

        if (papalInfluence < 0d) {
            papalInfluence = 0d;
        } else if (papalInfluence > 100d) {
            papalInfluence = 100d;
        }

        if (var != null) {
            var.setValue(papalInfluence);
        } else {
            this.item.addVariable("papal_influence", papalInfluence);
        }
    }

    public Double getCorruption() {
        return this.item.getVarAsDouble("corruption");
    }

    public void setCorruption(Double corruption) {
        ClausewitzVariable var = this.item.getVar("corruption");

        if (corruption < 0d) {
            corruption = 0d;
        } else if (corruption > 100d) {
            corruption = 100d;
        }

        if (var != null) {
            var.setValue(corruption);
        } else {
            this.item.addVariable("corruption", corruption);
        }
    }

    public Double getLegitimacy() {
        return this.item.getVarAsDouble("legitimacy");
    }

    public void setLegitimacy(Double legitimacy) {
        ClausewitzVariable var = this.item.getVar("legitimacy");

        if (legitimacy < 0d) {
            legitimacy = 0d;
        } else if (legitimacy > 100d) {
            legitimacy = 100d;
        }

        if (var != null) {
            var.setValue(legitimacy);
        } else {
            this.item.addVariable("legitimacy", legitimacy);
        }
    }

    public Integer getMercantilism() {
        Double aDouble = this.item.getVarAsDouble("mercantilism");

        if (aDouble == null) {
            return null;
        } else {
            return aDouble.intValue();
        }
    }

    public void setMercantilism(Integer mercantilism) {
        ClausewitzVariable var = this.item.getVar("mercantilism");

        if (mercantilism < 0) {
            mercantilism = 0;
        } else if (mercantilism > 100) {
            mercantilism = 100;
        }

        if (var != null) {
            var.setValue((double) mercantilism);
        } else {
            this.item.addVariable("mercantilism", (double) mercantilism);
        }
    }

    public Integer getSplendor() {
        Double absolutism = this.item.getVarAsDouble("splendor");

        if (absolutism == null) {
            return null;
        } else {
            return absolutism.intValue();
        }
    }

    public void setSplendor(Integer splendor) {
        ClausewitzVariable var = this.item.getVar("splendor");

        if (splendor < 0) {
            splendor = 0;
        }

        if (var != null) {
            var.setValue((double) splendor);
        } else {
            this.item.addVariable("splendor", (double) splendor);
        }
    }

    public Integer getAbsolutism() {
        Double absolutism = this.item.getVarAsDouble("absolutism");

        if (absolutism == null) {
            return null;
        } else {
            return absolutism.intValue();
        }
    }

    public void setAbsolutism(Integer absolutism) {
        ClausewitzVariable var = this.item.getVar("absolutism");

        if (absolutism < 0) {
            absolutism = 0;
        } else if (absolutism > 100) {
            absolutism = 100;
        }

        if (var != null) {
            var.setValue((double) absolutism);
        } else {
            this.item.addVariable("absolutism", (double) absolutism);
        }
    }

    public Double getArmyProfessionalism() {
        return this.item.getVarAsDouble("army_professionalism");
    }

    public void setArmyProfessionalism(Double armyProfessionalism) {
        ClausewitzVariable var = this.item.getVar("army_professionalism");

        if (armyProfessionalism < 0d) {
            armyProfessionalism = 0d;
        } else if (armyProfessionalism > 100d) {
            armyProfessionalism = 100d;
        }

        if (var != null) {
            var.setValue(armyProfessionalism);
        } else {
            this.item.addVariable("army_professionalism", armyProfessionalism);
        }
    }

    public Double getMaxHistoryArmyProfessionalism() {
        return this.item.getVarAsDouble("max_historic_army_professionalism");
    }

    public void setMaxHistoryArmyProfessionalism(Double armyProfessionalism) {
        ClausewitzVariable var = this.item.getVar("max_historic_army_professionalism");

        if (armyProfessionalism < 0d) {
            armyProfessionalism = 0d;
        } else if (armyProfessionalism > 100d) {
            armyProfessionalism = 100d;
        }

        if (var != null) {
            var.setValue(armyProfessionalism);
        } else {
            this.item.addVariable("max_historic_army_professionalism", armyProfessionalism);
        }
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    private void refreshAttributes() {
        ClausewitzItem techItem = this.item.getChild("technology");

        if (techItem != null) {
            this.tech = new Technology(techItem);
        }

        List<ClausewitzItem> estateItems = this.item.getChildren("estate");
        this.estates = estateItems.stream()
                                  .map(clausewitzItem -> new Estate(clausewitzItem, this))
                                  .collect(Collectors.toList());

        List<ClausewitzItem> rivalItems = this.item.getChildren("rival");
        this.rivals = rivalItems.stream()
                                .map(Rival::new)
                                .collect(Collectors.toMap(Rival::getRival, Function.identity()));

        List<ClausewitzItem> victoryCardItems = this.item.getChildren("victory_card");
        this.victoryCards = victoryCardItems.stream()
                                            .map(VictoryCard::new)
                                            .collect(Collectors.toList());

        List<ClausewitzItem> activePolicyItems = this.item.getChildren("active_policy");
        this.activePolicies = activePolicyItems.stream()
                                               .map(ActivePolicy::new)
                                               .collect(Collectors.toList());

        List<ClausewitzItem> powerProjectionItems = this.item.getChildren("power_projection");
        this.powerProjections = powerProjectionItems.stream()
                                                    .map(PowerProjection::new)
                                                    .collect(Collectors.toList());

        Double powerProjection = null;

        for (PowerProjection projection : this.powerProjections) {
            if (projection.getCurrent() != null && !projection.getCurrent().equals(0d)) {
                double value = BigDecimal.valueOf(projection.getCurrent())
                                         .setScale(0, RoundingMode.HALF_UP)
                                         .doubleValue();
                if (powerProjection == null) {
                    powerProjection = value;
                } else {
                    powerProjection += value;
                }
            }
        }

        if (powerProjection != null) {
            if (powerProjection <= 0d) {
                powerProjection = null;
            } else if (powerProjection >= 100d) {
                powerProjection = 100d;
            }
        }

        if (!Objects.equals(powerProjection, getCurrentPowerProjection())) {
            setCurrentPowerProjection(powerProjection);
        }

        ClausewitzItem ledgerItem = this.item.getChild("ledger");

        if (ledgerItem != null) {
            this.ledger = new Ledger(ledgerItem);
        }
    }
}
