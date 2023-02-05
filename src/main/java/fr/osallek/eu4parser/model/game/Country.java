package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzVariable;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.io.FileUtils;

import javax.imageio.ImageIO;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Country {

    public static final Pattern COUNTRY_PATTERN = Pattern.compile("[A-Z]{3}");

    public static final Pattern CUSTOM_COUNTRY_PATTERN = Pattern.compile("D\\d{2}");

    public static final Pattern COLONY_PATTERN = Pattern.compile("C\\d{2}");

    public static final Pattern TRADING_CITY_PATTERN = Pattern.compile("T\\d{2}");

    public static final Pattern CLIENT_STATE_PATTERN = Pattern.compile("K\\d{2}");

    public static final Pattern COSSACK_REVOLT_PATTERN = Pattern.compile("E\\d{2}");

    public static final Pattern OBSERVER_PATTERN = Pattern.compile("O\\d{2}");

    public static final Pattern UNKNOWN_PATTERN = Pattern.compile("F\\d{2}");

    private final Game game;

    private final String tag;

    private final FileNode commonFileNode;

    private final ClausewitzItem commonItem;

    private FileNode historyFileNode;

    private CountryHistoryItem defaultHistoryItem;

    private SortedMap<LocalDate, CountryHistoryItem> historyItems;

    private Path writenTo;

    public Country(String tag, FileNode commonFileNode, ClausewitzItem commonItem, Game game) {
        this.tag = tag;
        this.game = game;
        this.commonFileNode = commonFileNode;
        this.commonItem = commonItem;
    }

    public void setHistory(ClausewitzItem item, FileNode historyMod) {
        this.historyFileNode = historyMod;
        this.defaultHistoryItem = new CountryHistoryItem(item, this.game, this);
        this.historyItems = item.getChildren()
                                .stream()
                                .filter(child -> Eu4Utils.DATE_PATTERN.matcher(child.getName()).matches())
                                .collect(Collectors.toMap(child -> Eu4Utils.stringToDate(child.getName()),
                                                          child -> new CountryHistoryItem(child, this.game, this),
                                                          (o1, o2) -> o1, TreeMap::new));
    }

    public Game getGame() {
        return game;
    }

    public String getTag() {
        return tag;
    }

    public String getFlagPath(String extension) {
        return Path.of(Eu4Utils.GFX_FOLDER_PATH, "flags", getTag() + "." + extension).toString();
    }

    public File getFlagFile() {
        return this.game.getCountryFlagImage(this);
    }

    public void writeImageTo(Path dest) throws IOException {
        FileUtils.forceMkdirParent(dest.toFile());
        ImageIO.write(ImageIO.read(getFlagFile()), "png", dest.toFile());
        Eu4Utils.optimizePng(dest, dest);
        this.writenTo = dest;
    }

    public Path getWritenTo() {
        return writenTo;
    }

    public void setWritenTo(Path writenTo) {
        this.writenTo = writenTo;
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
                                                                                  .toList() : null;
    }

    public void setHistoricalIdeaGroups(Collection<IdeaGroup> historicalIdeaGroups) {
        setHistoricalIdeaGroups(historicalIdeaGroups.stream().map(IdeaGroup::getName).toList());
    }

    public void setHistoricalIdeaGroups(List<String> historicalIdeaGroups) {
        ClausewitzList list = this.commonItem.getList("historical_idea_groups");

        if (list == null) {
            this.commonItem.addList("historical_idea_groups", historicalIdeaGroups);
        } else {
            list.setAll(historicalIdeaGroups);
        }
    }

    public Map<String, Integer> getMonarchNames() {
        return this.commonItem.hasChild("monarch_names") ? this.commonItem.getChild("monarch_names")
                                                                          .getVariables()
                                                                          .stream()
                                                                          .collect(Collectors.toMap(ClausewitzVariable::getName, ClausewitzVariable::getAsInt,
                                                                                                    (a, b) -> a))
                                                         : null;
    }

    public void setMonarchNames(Map<String, Integer> monarchNames) {
        ClausewitzItem item = this.commonItem.getChild("monarch_names");

        if (item == null) {
            item = this.commonItem.addChild("monarch_names");
        }

        item.removeAllVariables();
        ClausewitzItem finalItem = item;
        monarchNames.forEach((name, weight) -> finalItem.addVariable(ClausewitzUtils.addQuotes(name), weight));
    }

    public List<Unit> getHistoricalUnits() {
        return this.commonItem.hasList("historical_units") ? this.commonItem.getList("historical_units")
                                                                            .getValues()
                                                                            .stream()
                                                                            .map(this.game::getUnit)
                                                                            .toList() : null;
    }

    public void setHistoricalUnits(Collection<Unit> historicalUnits) {
        setHistoricalUnits(historicalUnits.stream().map(Unit::getName).toList());
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
                                                                        .toList() : null;
    }

    public void setLeaderNames(List<String> leaderNames) {
        ClausewitzList list = this.commonItem.getList("leader_names");

        leaderNames = leaderNames.stream().map(ClausewitzUtils::addQuotes).toList();

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
                                                                      .toList() : null;
    }

    public void setShipNames(List<String> shipNames) {
        ClausewitzList list = this.commonItem.getList("ship_names");

        shipNames = shipNames.stream().map(ClausewitzUtils::addQuotes).toList();

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
                                                                      .toList() : null;
    }

    public void setArmyNames(List<String> armyNames) {
        ClausewitzList list = this.commonItem.getList("army_names");

        armyNames = armyNames.stream().map(ClausewitzUtils::addQuotes).toList();

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
                                                                       .toList() : null;
    }

    public void setFleetNames(List<String> fleetNames) {
        ClausewitzList list = this.commonItem.getList("fleet_names");

        fleetNames = fleetNames.stream().map(ClausewitzUtils::addQuotes).toList();

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

    public List<java.awt.Color> getRevolutionaryColors() {
        ClausewitzList colorList = this.commonItem.getList("revolutionary_colors");

        if (colorList == null) {
            return null;
        }

        return colorList.getValuesAsInt().stream().map(Eu4Utils.REVOLUTIONARY_COLORS::get).toList();
    }

    public void setRevolutionaryColor(List<Integer> colors) {
        ClausewitzList colorList = this.commonItem.getList("revolutionary_colors");

        if (colorList == null) {
            this.commonItem.addList("revolutionary_colors", colors.stream().filter(Objects::nonNull).toArray(Integer[]::new));
        } else {
            colorList.set(0, colors.get(0));
            colorList.set(1, colors.get(1));
            colorList.set(2, colors.get(2));
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

    public CountryHistoryItemI getHistoryItemAt(LocalDate date) {
        List<CountryHistoryItemI> items = Stream.concat(Stream.of(this.defaultHistoryItem),
                                                        this.historyItems.entrySet()
                                                                         .stream()
                                                                         .filter(e -> date.isBefore(e.getKey()) || date.equals(e.getKey()))
                                                                         .map(Map.Entry::getValue))
                                                .collect(Collectors.toList());
        Collections.reverse(items);
        return new CountryHistoryItems(items);
    }

    public Country getOverlordAt(LocalDate date) {
        return this.game.getSubjectTypeRelations()
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(r -> this.equals(r.getSecond()))
                        .filter(r -> r.getStartDate().equals(date) || r.getStartDate().isBefore(date))
                        .filter(r -> r.getEndDate().equals(date) || r.getStartDate().isAfter(date))
                        .findFirst()
                        .map(DiplomacyRelation::getFirst)
                        .orElse(null);
    }

    public String getSubjectTypeAt(LocalDate date) {
        return this.game.getSubjectTypeRelations()
                        .entrySet()
                        .stream()
                        .filter(e -> e.getValue().stream().anyMatch(r -> this.equals(r.getSecond())))
                        .filter(e -> e.getValue().stream().anyMatch(r -> r.getStartDate().equals(date) || r.getStartDate().isBefore(date)))
                        .filter(e -> e.getValue().stream().anyMatch(r -> r.getEndDate().equals(date) || r.getEndDate().isBefore(date)))
                        .findFirst()
                        .map(Map.Entry::getKey)
                        .orElse(null);
    }

    public LocalDate getSubjectSinceAt(LocalDate date) {
        return this.game.getSubjectTypeRelations()
                        .values()
                        .stream()
                        .flatMap(Collection::stream)
                        .filter(r -> this.equals(r.getSecond()))
                        .filter(r -> r.getStartDate().equals(date) || r.getStartDate().isBefore(date))
                        .filter(r -> r.getEndDate().equals(date) || r.getStartDate().isAfter(date))
                        .findFirst()
                        .map(DiplomacyRelation::getStartDate)
                        .orElse(null);
    }

    public Stream<Country> getSubjectsAt(LocalDate date) {
        return this.game.getCountries()
                        .stream()
                        .filter(c -> this.equals(c.getOverlordAt(date)));
    }

    public Stream<ProvinceHistoryItemI> getOwnedProvinceAt(LocalDate date) {
        return this.game.getProvinces().values().stream().map(p -> p.getHistoryItemAt(date)).filter(p -> this.equals(p.getOwner()));
    }

    public Stream<ProvinceHistoryItemI> getCoresProvinceAt(LocalDate date) {
        return this.game.getProvinces().values().stream().map(p -> p.getHistoryItemAt(date)).filter(p -> p.getCumulatedCores().contains(this));
    }

    public Stream<War> getWarsAt(LocalDate date) {
        return this.game.getWars().stream()
                        .filter(war -> war.getStart().equals(date) || war.getStart().isBefore(date))
                        .filter(war -> war.getEnd() == null || war.getEnd().equals(date) || war.getEnd().isBefore(date))
                        .filter(war -> war.getAttackersAt(date).contains(this.tag) || war.getDefendersAt(date).contains(this.tag));
    }

    public void writeCommon(BufferedWriter writer) throws IOException {
        this.commonItem.write(writer, true, 0, new HashMap<>());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Country province)) {
            return false;
        }

        return Objects.equals(tag, province.tag);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(tag);
    }
}
