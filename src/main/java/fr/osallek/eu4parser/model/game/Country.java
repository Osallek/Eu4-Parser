package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Country {

    public static final Pattern COUNTRY_PATTERN = Pattern.compile("[A-Z]{3}");

    public static final Pattern CUSTOM_COUNTRY_PATTERN = Pattern.compile("D[0-9]{2}");

    public static final Pattern COLONY_PATTERN = Pattern.compile("C[0-9]{2}");

    public static final Pattern TRADING_CITY_PATTERN = Pattern.compile("T[0-9]{2}");

    public static final Pattern CLIENT_STATE_PATTERN = Pattern.compile("K[0-9]{2}");

    private final Game game;

    private final String tag;

    private final FileNode commonFileNode;

    private final ClausewitzItem commonItem;

    private FileNode historyFileNode;

    private CountryHistoryItem defaultHistoryItem;

    private SortedMap<LocalDate, CountryHistoryItem> historyItems;

    public Country(String tag, FileNode commonFileNode, ClausewitzItem commonItem, Game game) {
        this.tag = tag;
        this.game = game;
        this.commonFileNode = commonFileNode;
        this.commonItem = commonItem;
    }

    public void setHistory(ClausewitzItem item, FileNode historyMod) {
        this.historyFileNode = historyMod;
        this.defaultHistoryItem = new CountryHistoryItem(item, this.game);
        this.historyItems = item.getChildren()
                                .stream()
                                .filter(child -> Eu4Utils.DATE_PATTERN.matcher(child.getName()).matches())
                                .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()), child -> new CountryHistoryItem(child, this.game),
                                                          (o1, o2) -> o1, TreeMap::new));
    }

    public String getTag() {
        return tag;
    }

    public String getFlagPath(String extension) {
        return Path.of(Eu4Utils.GFX_FOLDER_PATH, "flags", getTag() + "." + extension).toString();
    }

    public String getGraphicalCulture() {
        return this.commonItem.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        this.commonItem.setVariable("graphical_culture", graphicalCulture);
    }

    public String getHistoricalCouncil() {
        return this.commonItem.getVarAsString("historical_council");
    }

    public void setHistoricalCouncil(String historicalCouncil) {
        this.commonItem.setVariable("historical_council", historicalCouncil);
    }

    public Integer getHistoricalScore() {
        return this.commonItem.getVarAsInt("historical_score");
    }

    public void setHistoricalScore(Integer historicalScore) {
        if (historicalScore == null) {
            this.commonItem.removeVariable("historical_score");
        } else {
            this.commonItem.setVariable("historical_score", historicalScore);
        }
    }

    public List<IdeaGroup> getHistoricalIdeaGroups() {
        return this.commonItem.hasList("historical_idea_groups") ? this.commonItem.getList("historical_idea_groups")
                                                                                  .getValues()
                                                                                  .stream()
                                                                                  .map(this.game::getIdeaGroup)
                                                                                  .collect(Collectors.toList()) : null;
    }

    public void setHistoricalIdeaGroups(Collection<IdeaGroup> historicalIdeaGroups) {
        setHistoricalIdeaGroups(historicalIdeaGroups.stream().map(IdeaGroup::getName).collect(Collectors.toList()));
    }

    public void setHistoricalIdeaGroups(List<String> historicalIdeaGroups) {
        ClausewitzList list = this.commonItem.getList("historical_idea_groups");

        if (list == null) {
            this.commonItem.addList("historical_idea_groups", historicalIdeaGroups);
        } else {
            list.setAll(historicalIdeaGroups);
        }
    }

    public List<Pair<String, Integer>> getMonarchNames() {
        return this.commonItem.hasChild("monarch_names") ? this.commonItem.getChild("monarch_names")
                                                                          .getVariables()
                                                                          .stream()
                                                                          .map(variable -> Pair.of(variable.getName(), variable.getAsInt()))
                                                                          .collect(Collectors.toList())
                                                         : null;
    }

    public void setMonarchNames(List<Pair<String, Integer>> monarchNames) {
        ClausewitzItem item = this.commonItem.getChild("monarch_names");

        if (item == null) {
            item = this.commonItem.addChild("monarch_names");
        }

        item.removeAllChildren();
        ClausewitzItem finalItem = item;
        monarchNames.forEach(pair -> finalItem.addVariable(pair.getKey(), pair.getValue()));
    }

    public List<Unit> getHistoricalUnits() {
        return this.commonItem.hasList("historical_units") ? this.commonItem.getList("historical_units")
                                                                            .getValues()
                                                                            .stream()
                                                                            .map(this.game::getUnit)
                                                                            .collect(Collectors.toList()) : null;
    }

    public void setHistoricalUnits(Collection<Unit> historicalUnits) {
        setHistoricalUnits(historicalUnits.stream().map(Unit::getName).collect(Collectors.toList()));
    }

    public void setHistoricalUnits(List<String> historicalUnits) {
        ClausewitzList list = this.commonItem.getList("historical_units");

        if (list == null) {
            this.commonItem.addList("historical_units", historicalUnits);
        } else {
            list.setAll(historicalUnits);
        }
    }

    public List<String> getLeaderNames() {
        return this.commonItem.hasList("leader_names") ? this.commonItem.getList("leader_names")
                                                                        .getValues()
                                                                        .stream()
                                                                        .map(ClausewitzUtils::removeQuotes)
                                                                        .collect(Collectors.toList()) : null;
    }

    public void setLeaderNames(List<String> leaderNames) {
        ClausewitzList list = this.commonItem.getList("leader_names");

        leaderNames = leaderNames.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());

        if (list == null) {
            this.commonItem.addList("leader_names", leaderNames);
        } else {
            list.setAll(leaderNames);
        }
    }

    public List<String> getShipNames() {
        return this.commonItem.hasList("ship_names") ? this.commonItem.getList("ship_names")
                                                                      .getValues()
                                                                      .stream()
                                                                      .map(ClausewitzUtils::removeQuotes)
                                                                      .collect(Collectors.toList()) : null;
    }

    public void setShipNames(List<String> shipNames) {
        ClausewitzList list = this.commonItem.getList("ship_names");

        shipNames = shipNames.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());

        if (list == null) {
            this.commonItem.addList("ship_names", shipNames);
        } else {
            list.setAll(shipNames);
        }
    }

    public List<String> getArmyNames() {
        return this.commonItem.hasList("army_names") ? this.commonItem.getList("army_names")
                                                                      .getValues()
                                                                      .stream()
                                                                      .map(ClausewitzUtils::removeQuotes)
                                                                      .collect(Collectors.toList()) : null;
    }

    public void setArmyNames(List<String> armyNames) {
        ClausewitzList list = this.commonItem.getList("army_names");

        armyNames = armyNames.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());

        if (list == null) {
            this.commonItem.addList("army_names", armyNames);
        } else {
            list.setAll(armyNames);
        }
    }

    public List<String> getFleetNames() {
        return this.commonItem.hasList("fleet_names") ? this.commonItem.getList("fleet_names")
                                                                       .getValues()
                                                                       .stream()
                                                                       .map(ClausewitzUtils::removeQuotes)
                                                                       .collect(Collectors.toList()) : null;
    }

    public void setFleetNames(List<String> fleetNames) {
        ClausewitzList list = this.commonItem.getList("fleet_names");

        fleetNames = fleetNames.stream().map(ClausewitzUtils::addQuotes).collect(Collectors.toList());

        if (list == null) {
            this.commonItem.addList("fleet_names", fleetNames);
        } else {
            list.setAll(fleetNames);
        }
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

    public Color getRevolutionaryColor() {
        ClausewitzList colorList = this.commonItem.getList("revolutionary_colors");

        if (colorList == null) {
            return null;
        }

        return new Color(colorList);
    }

    public void setRevolutionaryColor(java.awt.Color color) {
        ClausewitzList colorList = this.commonItem.getList("revolutionary_colors");

        if (colorList == null) {
            Color.addToItem(this.commonItem, "revolutionary_colors", color.getRed(), color.getGreen(), color.getBlue());
        } else {
            Color c = new Color(colorList);
            c.setRed(color.getRed());
            c.setGreen(color.getGreen());
            c.setBlue(color.getBlue());
        }
    }

    public FileNode getCommonFileNode() {
        return commonFileNode;
    }

    public FileNode getHistoryFileNode() {
        return historyFileNode;
    }

    public CountryHistoryItem getDefaultHistoryItem() {
        return defaultHistoryItem;
    }

    public SortedMap<LocalDate, CountryHistoryItem> getHistoryItems() {
        return historyItems;
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
