package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.model.ClausewitzObject;
import fr.osallek.eu4parser.model.Color;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class Government {

    private final ClausewitzItem item;

    private final Game game;

    public Government(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<String> getBasicReform() {
        return this.item.getVarAsString("basic_reform");
    }

    public Optional<GovernmentReform> getBasicGovernmentReform() {
        return getBasicReform().map(this.game::getGovernmentReform);
    }

    public void setBasicReform(String basicReform) {
        if (StringUtils.isBlank(basicReform)) {
            this.item.removeVariable("basic_reform");
        } else {
            this.item.setVariable("basic_reform", basicReform);
        }
    }

    public Optional<Color> getColor() {
        return this.item.getList("color").map(Color::new);
    }

    public void setColor(Color color) {
        if (color == null) {
            this.item.removeList("color");
            return;
        }

        this.item.getList("color").ifPresentOrElse(list -> {
            Color actualColor = new Color(list);
            actualColor.setRed(color.getRed());
            actualColor.setGreen(color.getGreen());
            actualColor.setBlue(color.getBlue());
        }, () -> Color.addToItem(this.item, "color", color));
    }

    public List<String> getLegacyGovernment() {
        return this.item.getList("legacy_government").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setLegacyGovernment(List<String> legacyGovernment) {
        if (CollectionUtils.isEmpty(legacyGovernment)) {
            this.item.removeList("legacy_government");
            return;
        }

        this.item.getList("legacy_government")
                 .ifPresentOrElse(list -> list.setAll(legacyGovernment.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("legacy_government", legacyGovernment.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public List<String> getExclusiveReforms() {
        return this.item.getList("exclusive_reforms").map(ClausewitzList::getValues).orElse(new ArrayList<>());
    }

    public void setExclusiveReforms(List<String> exclusiveReforms) {
        if (CollectionUtils.isEmpty(exclusiveReforms)) {
            this.item.removeList("exclusive_reforms");
            return;
        }

        this.item.getList("exclusive_reforms")
                 .ifPresentOrElse(list -> list.setAll(exclusiveReforms.stream().filter(Objects::nonNull).toArray(String[]::new)),
                                  () -> this.item.addList("exclusive_reforms", exclusiveReforms.stream().filter(Objects::nonNull).toArray(String[]::new)));
    }

    public Map<String, List<GovernmentReform>> getReformLevels() {
        return this.item.getChild("reform_levels")
                        .map(ClausewitzItem::getChildren)
                        .filter(CollectionUtils::isNotEmpty)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(ClausewitzObject::getName,
                                                  clausewitzItem -> clausewitzItem.getList("reforms")
                                                                                  .map(list -> list.getValues()
                                                                                                   .stream()
                                                                                                   .map(this.game::getGovernmentReform)
                                                                                                   .toList())
                                                                                  .orElse(new ArrayList<>()),
                                                  (reforms, reforms1) -> reforms,
                                                  LinkedHashMap::new));
    }
}
