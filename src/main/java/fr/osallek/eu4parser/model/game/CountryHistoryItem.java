package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.model.Power;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class CountryHistoryItem implements CountryHistoryItemI {

    private final Game game;

    private final ClausewitzItem item;

    private final Country country;

    public CountryHistoryItem(ClausewitzItem item, Game game, Country country) {
        this.game = game;
        this.item = item;
        this.country = country;
    }

    @Override
    public Optional<TechGroup> getTechnologyGroup() {
        return this.item.getVarAsString("technology_group").map(this.game::getTechGroup);
    }

    public void setTechnologyGroup(String technologyGroup) {
        this.item.setVariable("technology_group", technologyGroup);
    }

    @Override
    public Optional<String> getUnitType() {
        return this.item.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        this.item.setVariable("unit_type", unitType);
    }

    @Override
    public Optional<Integer> getMercantilism() {
        return this.item.getVarAsInt("mercantilism");
    }

    public void setMercantilism(Integer mercantilism) {
        this.item.setVariable("mercantilism", mercantilism);
    }

    @Override
    public Optional<Province> getCapital() {
        return this.item.getVarAsInt("capital").map(this.game::getProvince);
    }

    public void setCapital(Province capital) {
        setCapital(capital.getId());
    }

    public void setCapital(Integer capital) {
        this.item.setVariable("capital", capital);
    }

    @Override
    public Optional<Country> getChangedTagFrom() {
        return this.item.getVarAsString("changed_tag_from").map(this.game::getCountry);
    }

    public void setChangedTagFrom(Country country) {
        setChangedTagFrom(country.getTag());
    }

    public void setChangedTagFrom(String changedTagFrom) {
        this.item.setVariable("changed_tag_from", changedTagFrom);
    }

    @Override
    public Optional<Province> getFixedCapital() {
        return this.item.getVarAsInt("fixed_capital").map(this.game::getProvince);
    }

    public void setFixedCapital(Province capital) {
        setCapital(capital.getId());
    }

    public void setFixedCapital(Integer capital) {
        this.item.setVariable("fixed_capital", capital);
    }

    @Override
    public Optional<Government> getGovernment() {
        return this.item.getVarAsString("government").map(this.game::getGovernment);
    }

    public void setGovernment(Government government) {
        setGovernment(government.getName());
    }

    public void setGovernment(String government) {
        this.item.setVariable("government", government);
    }

    @Override
    public Optional<String> getReligiousSchool() {
        return this.item.getVarAsString("religious_school");
    }

    public void setReligiousSchool(String religiousSchool) {
        this.item.setVariable("religious_school", religiousSchool);
    }

    @Override
    public Optional<Power> getNationalFocus() {
        return this.item.getVarAsString("national_focus").map(Power::byName);
    }

    public void setNationalFocus(Power nationalFocus) {
        if (nationalFocus == null) {
            this.item.removeVariable("national_focus");
        } else {
            this.item.setVariable("national_focus", nationalFocus.name());
        }
    }

    @Override
    public Optional<Integer> getGovernmentLevel() {
        return this.item.getVarAsInt("government_rank");
    }

    @Override
    public Optional<GovernmentRank> getGovernmentRank() {
        return getGovernmentLevel().map(this.game::getGovernmentRank);
    }

    public void setGovernmentRank(Integer governmentRank) {
        if (governmentRank == null) {
            this.item.removeVariable("government_rank");
        } else {
            if (governmentRank <= 0) {
                governmentRank = 1;
            }

            this.item.setVariable("government_rank", governmentRank);
        }
    }

    @Override
    public Optional<Culture> getPrimaryCulture() {
        return this.item.getVarAsString("primary_culture").map(this.game::getCulture);
    }

    public void setPrimaryCulture(Culture primaryCulture) {
        setPrimaryCulture(primaryCulture.getName());
    }

    public void setPrimaryCulture(String primaryCulture) {
        this.item.setVariable("primary_culture", primaryCulture);
    }

    @Override
    public Optional<Religion> getReligion() {
        return this.item.getVarAsString("religion").map(this.game::getReligion);
    }

    public void setReligion(Religion religion) {
        setReligion(religion.getName());
    }

    public void setReligion(String religion) {
        this.item.setVariable("religion", religion);
    }

    @Override
    public Optional<Religion> getJoinLeague() {
        return this.item.getVarAsString("join_league").map(this.game::getReligion);
    }

    public void setJoinLeague(Religion religion) {
        setReligion(religion == null ? null : religion.getName());
    }

    public void setJoinLeague(String religion) {
        if (StringUtils.isBlank(religion)) {
            this.item.removeVariable("join_league");
        } else {
            this.item.setVariable("join_league", religion);
        }
    }

    @Override
    public Optional<Double> getAddArmyProfessionalism() {
        return this.item.getVarAsDouble("add_army_professionalism");
    }

    public void setAddArmyProfessionalism(Double addArmyProfessionalism) {
        if (addArmyProfessionalism == null) {
            this.item.removeVariable("add_army_professionalism");
        } else {
            this.item.setVariable("add_army_professionalism", addArmyProfessionalism);
        }
    }

    @Override
    public List<String> getAddAcceptedCultures() {
        return this.item.getVarsAsStrings("add_accepted_culture");
    }

    public void setAddAcceptedCultures(List<Culture> cultures) {
        getAddAcceptedCultures().forEach(acceptedCulture -> cultures.stream()
                                                                    .filter(culture -> culture.getName().equals(acceptedCulture))
                                                                    .findFirst()
                                                                    .ifPresentOrElse(cultures::remove,
                                                                                     () -> this.item.removeVariable("add_accepted_culture",
                                                                                                                    acceptedCulture)));

        cultures.forEach(this::addAddAcceptedCulture);
    }

    public void addAddAcceptedCulture(Culture culture) {
        addAddAcceptedCulture(culture.getName());
    }

    public void addAddAcceptedCulture(String culture) {
        List<String> acceptedCultures = this.item.getVarsAsStrings("add_accepted_culture");

        if (!acceptedCultures.contains(culture)) {
            this.item.addVariable("add_accepted_culture", culture, this.item.getVar("primary_culture").map(ClausewitzVariable::getOrder).orElse(0) + 1);
        }
    }

    @Override
    public List<String> getRemoveAcceptedCultures() {
        return this.item.getVarsAsStrings("remove_accepted_culture");
    }

    public void setRemoveAcceptedCultures(List<Culture> cultures) {
        getAddAcceptedCultures().forEach(acceptedCulture -> cultures.stream()
                                                                    .filter(culture -> culture.getName().equals(acceptedCulture))
                                                                    .findFirst()
                                                                    .ifPresentOrElse(cultures::remove,
                                                                                     () -> this.item.removeVariable("remove_accepted_culture",
                                                                                                                    acceptedCulture)));

        cultures.forEach(this::removeAcceptedCulture);
    }

    public void removeAcceptedCulture(Culture culture) {
        removeAcceptedCulture(culture.getName());
    }

    public void removeAcceptedCulture(String culture) {
        List<String> removeAcceptedCulture = this.item.getVarsAsStrings("remove_accepted_culture");

        if (!removeAcceptedCulture.contains(culture)) {
            this.item.addVariable("remove_accepted_culture", culture, this.item.getVar("primary_culture").map(ClausewitzVariable::getOrder).orElse(0) + 1);
        }
    }

    @Override
    public List<String> getCumulatedAcceptedCultures() {
        List<String> removeAcceptedCultures = getRemoveAcceptedCultures();
        return getAddAcceptedCultures().stream().filter(Predicate.not(removeAcceptedCultures::contains)).toList();
    }

    @Override
    public List<Country> getHistoricalFriends() {
        return this.item.getVarsAsStrings("historical_friend").stream().map(this.game::getCountry).toList();
    }

    public void setHistoricalFriends(List<Country> friends) {
        this.item.removeVariables("historical_friend");
        friends.forEach(this::addHistoricalFriend);
    }

    public void addHistoricalFriend(Country friend) {
        addHistoricalFriend(friend.getTag());
    }

    public void addHistoricalFriend(String friend) {
        List<String> historicalFriends = this.item.getVarsAsStrings("historical_friend");

        if (!historicalFriends.contains(friend)) {
            this.item.addVariable("historical_friend", friend, this.item.getVar("capital").map(ClausewitzVariable::getOrder).orElse(0) + 1);
        }
    }

    @Override
    public List<Country> getHistoricalEnemies() {
        return this.item.getVarsAsStrings("historical_rival").stream().map(this.game::getCountry).toList();
    }

    public void setHistoricalEnemies(Collection<Country> enemies) {
        setHistoricalEnemies(enemies.stream().map(Country::getTag).toList());
    }

    public void setHistoricalEnemies(List<String> enemies) {
        this.item.removeVariables("historical_rival");
        enemies.forEach(this::addHistoricalEnemy);
    }

    public void addHistoricalEnemy(Country enemy) {
        addHistoricalEnemy(enemy.getTag());
    }

    public void addHistoricalEnemy(String enemy) {
        List<String> historicalEnemies = this.item.getVarsAsStrings("historical_rival");

        if (!historicalEnemies.contains(enemy)) {
            this.item.addVariable("historical_rival", enemy, this.item.getVar("capital").map(ClausewitzVariable::getOrder).orElse(0) + 1);
        }
    }

    @Override
    public Optional<Boolean> getElector() {
        return this.item.getVarAsBool("elector");
    }

    public void setElector(Boolean elector) {
        this.item.setVariable("elector", elector);
    }

    @Override
    public Optional<Boolean> getRevolutionTarget() {
        return this.item.getVarAsBool("revolution_target");
    }

    public void setRevolutionTarget(Boolean revolutionTarget) {
        this.item.setVariable("revolution_target", revolutionTarget);
    }

    @Override
    public Optional<Boolean> getClearScriptedPersonalities() {
        return this.item.getVarAsBool("clear_scripted_personalities");
    }

    public void setClearScriptedPersonalities(Boolean clearScriptedPersonalities) {
        this.item.setVariable("clear_scripted_personalities", clearScriptedPersonalities);
    }

    @Override
    public List<ChangeEstateLandShare> getChangeEstateLandShares() {
        List<ClausewitzItem> list = this.item.getChildren("change_estate_land_share");
        return CollectionUtils.isEmpty(list) ? null : list.stream().map(child -> new ChangeEstateLandShare(child, this.game)).toList();
    }

    public void setChangeEstateLandShares(Map<String, Double> shares) {
        if (MapUtils.isEmpty(shares)) {
            this.item.removeChildren("change_estate_land_share");
            return;
        }

        shares.forEach((estate, share) -> ChangeEstateLandShare.addToItem(this.item, estate, share));
    }

    @Override
    public List<RulerPersonality> getAddHeirPersonalities() {
        return this.item.getVarsAsStrings("add_heir_personality").stream().map(this.game::getRulerPersonality).toList();
    }

    public void setAddHeirPersonalities(List<String> addHeirPersonalities) {
        this.item.removeVariables("add_heir_personality");

        if (CollectionUtils.isNotEmpty(addHeirPersonalities)) {
            addHeirPersonalities.forEach(s -> this.item.addVariable("add_heir_personality", s));
        }
    }

    public void addAddHeirPersonality(String addHeirPersonality) {
        if (!this.item.hasVar("add_heir_personality", addHeirPersonality)) {
            this.item.addVariable("add_heir_personality", addHeirPersonality);
        }
    }

    public void removeAddHeirPersonality(String addHeirPersonality) {
        this.item.removeVariable("add_heir_personality", addHeirPersonality);
    }

    @Override
    public List<RulerPersonality> getAddRulerPersonalities() {
        return this.item.getVarsAsStrings("add_ruler_personality").stream().map(this.game::getRulerPersonality).toList();
    }

    public void setAddRulerPersonalities(List<String> addRulerPersonalities) {
        this.item.removeVariables("add_ruler_personality");

        if (CollectionUtils.isNotEmpty(addRulerPersonalities)) {
            addRulerPersonalities.forEach(s -> this.item.addVariable("add_ruler_personality", s));
        }
    }

    public void addAddRulerPersonality(String addRulerPersonality) {
        if (!this.item.hasVar("add_ruler_personality", addRulerPersonality)) {
            this.item.addVariable("add_ruler_personality", addRulerPersonality);
        }
    }

    public void removeAddRulerPersonality(String addRulerPersonality) {
        this.item.removeVariable("add_ruler_personality", addRulerPersonality);
    }

    @Override
    public List<RulerPersonality> getAddQueenPersonalities() {
        return this.item.getVarsAsStrings("add_queen_personality").stream().map(this.game::getRulerPersonality).toList();
    }

    public void setAddQueenPersonalities(List<String> addQueenPersonalities) {
        this.item.removeVariables("add_queen_personality");

        if (CollectionUtils.isNotEmpty(addQueenPersonalities)) {
            addQueenPersonalities.forEach(s -> this.item.addVariable("add_queen_personality", s));
        }
    }

    public void addAddQueenPersonality(String addQueenPersonality) {
        if (!this.item.hasVar("add_queen_personality", addQueenPersonality)) {
            this.item.addVariable("add_queen_personality", addQueenPersonality);
        }
    }

    public void removeAddQueenPersonality(String addQueenPersonality) {
        this.item.removeVariable("add_queen_personality", addQueenPersonality);
    }

    @Override
    public List<EstatePrivilege> getSetEstatePrivilege() {
        return this.item.getVarsAsStrings("set_estate_privilege").stream().map(this.game::getEstatePrivilege).toList();
    }

    public void setSetEstatePrivilege(List<String> setEstatePrivilege) {
        this.item.removeVariables("set_estate_privilege");

        if (CollectionUtils.isNotEmpty(setEstatePrivilege)) {
            setEstatePrivilege.forEach(s -> this.item.addVariable("set_estate_privilege", s));
        }
    }

    public void addSetEstatePrivilege(String setEstatePrivilege) {
        if (!this.item.hasVar("set_estate_privilege", setEstatePrivilege)) {
            this.item.addVariable("set_estate_privilege", setEstatePrivilege);
        }
    }

    public void removeSetEstatePrivilege(String setEstatePrivilege) {
        this.item.removeVariable("set_estate_privilege", setEstatePrivilege);
    }

    @Override
    public List<GovernmentReform> getAddGovernmentReform() {
        return this.item.getVarsAsStrings("add_government_reform").stream().map(this.game::getGovernmentReform).toList();
    }

    public void addAddGovernmentReform(List<String> addGovernmentReform) {
        this.item.removeVariables("add_government_reform");

        if (CollectionUtils.isNotEmpty(addGovernmentReform)) {
            addGovernmentReform.forEach(s -> this.item.addVariable("add_government_reform", s));
        }
    }

    public void addAddGovernmentReform(String addGovernmentReform) {
        if (!this.item.hasVar("add_government_reform", addGovernmentReform)) {
            this.item.addVariable("add_government_reform", addGovernmentReform);
        }
    }

    public void removeAddGovernmentReform(String addGovernmentReform) {
        this.item.removeVariable("add_government_reform", addGovernmentReform);
    }

    @Override
    public List<String> getSetCountryFlag() {
        return this.item.getVarsAsStrings("set_country_flag");
    }

    public void setSetCountryFlag(List<String> setCountryFlag) {
        this.item.removeVariables("set_country_flag");

        if (CollectionUtils.isNotEmpty(setCountryFlag)) {
            setCountryFlag.forEach(s -> this.item.addVariable("set_country_flag", s));
        }
    }

    public void addSetCountryFlag(String setCountryFlag) {
        if (!this.item.hasVar("set_country_flag", setCountryFlag)) {
            this.item.addVariable("set_country_flag", setCountryFlag);
        }
    }

    public void removeSetCountryFlag(String setCountryFlag) {
        this.item.removeVariable("set_country_flag", setCountryFlag);
    }

    @Override
    public List<String> getClearCountryFlag() {
        return this.item.getVarsAsStrings("clr_country_flag");
    }

    public void clrClearCountryFlag(List<String> clrCountryFlag) {
        this.item.removeVariables("clr_country_flag");

        if (CollectionUtils.isNotEmpty(clrCountryFlag)) {
            clrCountryFlag.forEach(s -> this.item.addVariable("clr_country_flag", s));
        }
    }

    public void addClearCountryFlag(String clrCountryFlag) {
        if (!this.item.hasVar("clr_country_flag", clrCountryFlag)) {
            this.item.addVariable("clr_country_flag", clrCountryFlag);
        }
    }

    public void removeClearCountryFlag(String clrCountryFlag) {
        this.item.removeVariable("clr_country_flag", clrCountryFlag);
    }

    @Override
    public List<String> getCumulatedCountryFlags() {
        List<String> clearCountryFlag = getClearCountryFlag();
        return getSetCountryFlag().stream().filter(Predicate.not(clearCountryFlag::contains)).toList();
    }

    @Override
    public Optional<Heir> getHeir() {
        return this.item.getChild("heir").map(c -> new Heir(c, this.country));
    }

    @Override
    public Optional<Monarch> getMonarch() {
        return this.item.getChild("monarch").map(c -> new Monarch(c, this.country));
    }

    @Override
    public Optional<Queen> getQueen() {
        return this.item.getChild("queen").map(c -> new Queen(c, this.country));
    }

    @Override
    public List<Leader> getLeaders() {
        return this.item.getChildren("leader").stream().map(c -> new Leader(c, this.country)).toList();
    }
}
