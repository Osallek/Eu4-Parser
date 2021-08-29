package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.Color;

import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Country {

    public static final Pattern COUNTRY_PATTERN = Pattern.compile("[A-Z]{3}");

    public static final Pattern CUSTOM_COUNTRY_PATTERN = Pattern.compile("D[0-9]{2}");

    public static final Pattern COLONY_PATTERN = Pattern.compile("C[0-9]{2}");

    public static final Pattern TRADING_CITY_PATTERN = Pattern.compile("T[0-9]{2}");

    public static final Pattern CLIENT_STATE_PATTERN = Pattern.compile("K[0-9]{2}");

    private final Game game;

    private final ClausewitzItem historyItem;

    private final ClausewitzItem commonItem;

    private final String tag;

    public Country(String tag, ClausewitzItem historyItem, ClausewitzItem commonItem, Game game) {
        this.tag = tag;
        this.game = game;
        this.historyItem = historyItem;
        this.commonItem = commonItem;
    }

    public String getTag() {
        return tag;
    }

    public String getName() {
        return this.game.getLocalisation(this.tag);
    }

    public TechGroup getTechnologyGroup() {
        return this.game.getTechGroup(ClausewitzUtils.removeQuotes(this.historyItem.getVarAsString("technology_group")));
    }

    public void setTechnologyGroup(String technologyGroup) {
        this.historyItem.setVariable("technology_group", technologyGroup);
    }

    public String getUnitType() {
        return this.historyItem.getVarAsString("unit_type");
    }

    public void setUnitType(String unitType) {
        this.historyItem.setVariable("unit_type", unitType);
    }

    public Government getGovernment() {
        return this.game.getGovernment(ClausewitzUtils.removeQuotes(this.historyItem.getVarAsString("government")));
    }

    public void setGovernment(Government government) {
        setGovernment(government.getName());
    }

    public void setGovernment(String government) {
        this.historyItem.setVariable("government", ClausewitzUtils.addQuotes(government));
    }

    public Integer getGovernmentLevel() {
        return this.historyItem.getVarAsInt("government_rank");
    }

    public GovernmentRank getGovernmentRank() {
        return getGovernmentLevel() == null ? null : this.game.getGovernmentRank(getGovernmentLevel());
    }

    public void setGovernmentRank(Integer governmentRank) {
        if (governmentRank == null) {
            this.historyItem.removeVariable("government_rank");
        } else {
            if (governmentRank <= 0) {
                governmentRank = 1;
            }

            this.historyItem.setVariable("government_rank", governmentRank);
        }
    }

    public Culture getPrimaryCulture() {
        return this.game.getCulture(this.historyItem.getVarAsString("primary_culture"));
    }

    public void setPrimaryCulture(Culture primaryCulture) {
        setPrimaryCulture(primaryCulture.getName());
    }

    public void setPrimaryCulture(String primaryCulture) {
        this.historyItem.setVariable("primary_culture", primaryCulture);
    }

    public List<Culture> getAcceptedCultures() {
        return this.historyItem.getVarsAsStrings("add_accepted_culture").stream().map(this.game::getCulture).collect(Collectors.toList());
    }

    public void setAcceptedCultures(List<Culture> cultures) {
        getAcceptedCultures().forEach(acceptedCulture -> cultures.stream()
                                                                 .filter(culture -> culture.equals(acceptedCulture))
                                                                 .findFirst()
                                                                 .ifPresentOrElse(cultures::remove,
                                                                                  () -> this.historyItem.removeVariable("add_accepted_culture",
                                                                                                                        acceptedCulture.getName())));

        cultures.forEach(this::addAcceptedCulture);
    }

    public void addAcceptedCulture(Culture culture) {
        addAcceptedCulture(culture.getName());
    }

    public void addAcceptedCulture(String culture) {
        List<String> acceptedCultures = this.historyItem.getVarsAsStrings("add_accepted_culture");

        if (!acceptedCultures.contains(culture)) {
            this.historyItem.addVariable("add_accepted_culture", culture, this.historyItem.getVar("primary_culture").getOrder() + 1);
        }
    }

    public List<Culture> getRemoveAcceptedCultures() {
        return this.historyItem.getVarsAsStrings("remove_accepted_culture").stream().map(this.game::getCulture).collect(Collectors.toList());
    }

    public void setRemoveAcceptedCultures(List<Culture> cultures) {
        getAcceptedCultures().forEach(acceptedCulture -> cultures.stream()
                                                                 .filter(culture -> culture.equals(acceptedCulture))
                                                                 .findFirst()
                                                                 .ifPresentOrElse(cultures::remove,
                                                                                  () -> this.historyItem.removeVariable("remove_accepted_culture",
                                                                                                                        acceptedCulture.getName())));

        cultures.forEach(this::removeAcceptedCulture);
    }

    public void removeAcceptedCulture(Culture culture) {
        removeAcceptedCulture(culture.getName());
    }

    public void removeAcceptedCulture(String culture) {
        List<String> removeAcceptedCulture = this.historyItem.getVarsAsStrings("remove_accepted_culture");

        if (!removeAcceptedCulture.contains(culture)) {
            this.historyItem.addVariable("remove_accepted_culture", culture, this.historyItem.getVar("primary_culture").getOrder() + 1);
        }
    }

    public List<Country> getHistoricalFriends() {
        return this.historyItem.getVarsAsStrings("historical_friend").stream().map(this.game::getCountry).collect(Collectors.toList());
    }

    public void setHistoricalFriends(List<Country> friends) {
        getHistoricalFriends().forEach(historicalFriend -> friends.stream()
                                                                  .filter(friend -> friend.equals(historicalFriend))
                                                                  .findFirst()
                                                                  .ifPresentOrElse(friends::remove, () -> this.historyItem.removeVariable("historical_friend",
                                                                                                                                          historicalFriend.getTag())));

        friends.forEach(this::addHistoricalFriend);
    }

    public void addHistoricalFriend(Country friend) {
        addHistoricalFriend(friend.getTag());
    }

    public void addHistoricalFriend(String friend) {
        List<String> historicalFriends = this.historyItem.getVarsAsStrings("historical_friend");

        if (!historicalFriends.contains(friend)) {
            this.historyItem.addVariable("historical_friend", friend, this.historyItem.getVar("capital").getOrder() + 1);
        }
    }

    public List<Country> getHistoricalEnemies() {
        return this.historyItem.getVarsAsStrings("historical_rival").stream().map(this.game::getCountry).collect(Collectors.toList());
    }

    public void setHistoricalEnemies(List<Country> enemies) {
        getHistoricalEnemies().forEach(historicalEnemy -> enemies.stream()
                                                                 .filter(enemy -> enemy.equals(historicalEnemy))
                                                                 .findFirst()
                                                                 .ifPresentOrElse(enemies::remove, () -> this.historyItem.removeVariable("historical_rival",
                                                                                                                                         historicalEnemy.getTag())));

        enemies.forEach(this::addHistoricalEnemy);
    }

    public void addHistoricalEnemy(Country enemy) {
        addHistoricalEnemy(enemy.getTag());
    }

    public void addHistoricalEnemy(String enemy) {
        List<String> historicalEnemies = this.historyItem.getVarsAsStrings("historical_rival");

        if (!historicalEnemies.contains(enemy)) {
            this.historyItem.addVariable("historical_rival", enemy, this.historyItem.getVar("capital").getOrder() + 1);
        }
    }

    public String getGraphicalCulture() {
        return this.commonItem.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        this.commonItem.setVariable("graphical_culture", graphicalCulture);
    }

    public Color getColor() {
        ClausewitzList colorList = this.commonItem.getList("color");

        if (colorList == null) {
            return null;
        }

        return new Color(colorList);
    }

    public void setColor(java.awt.Color color) {
        ClausewitzList colorList = this.commonItem.getList("color");

        if (colorList == null) {
            Color.addToItem(this.commonItem, "color", color.getRed(), color.getGreen(), color.getBlue());
        } else {
            Color c = new Color(colorList);
            c.setRed(color.getRed());
            c.setGreen(color.getGreen());
            c.setBlue(color.getBlue());
        }
    }

    public Boolean getElector() {
        return this.historyItem.getVarAsBool("elector");
    } //Move to history

    public void setElector(Boolean elector) {
        this.historyItem.setVariable("elector", elector);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country)) {
            return false;
        }

        Country province = (Country) o;

        return Objects.equals(tag, province.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tag);
    }
}
