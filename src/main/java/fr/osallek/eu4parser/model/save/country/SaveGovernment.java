package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Government;
import fr.osallek.eu4parser.model.game.GovernmentReform;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class SaveGovernment {

    private final Game game;

    private final SaveCountry country;

    private final ClausewitzItem item;

    public SaveGovernment(ClausewitzItem item, Game game, SaveCountry country) {
        this.game = game;
        this.item = item;
        this.country = country;
    }

    public String getTypeName() {
        return ClausewitzUtils.removeQuotes(this.item.getVarAsString("government"));
    }

    public Government getType() {
        return this.game.getGovernment(ClausewitzUtils.removeQuotes(this.item.getVarAsString("government")));
    }

    public void setType(Government type) {
        this.item.setVariable("government", ClausewitzUtils.addQuotes(type.getName()));
    }

    public List<GovernmentReform> getReforms() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("reforms");

            if (list != null) {
                return list.getValues()
                           .stream()
                           .map(ClausewitzUtils::removeQuotes)
                           .map(this.game::getGovernmentReform)
                           .filter(Objects::nonNull)
                           .toList();
            }
        }

        return new ArrayList<>();
    }

    public List<String> getReformsNames() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("reforms");

            if (list != null) {
                return list.getValues()
                           .stream()
                           .map(ClausewitzUtils::removeQuotes)
                           .toList();
            }
        }

        return new ArrayList<>();
    }

    public void setReforms(List<GovernmentReform> reforms) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack == null) {
            reformStack = this.item.addChild("reform_stack");
        }

        ClausewitzList list = reformStack.getList("reforms");

        if (list == null) {
            reformStack.addList("reforms", reforms.stream().map(reform -> ClausewitzUtils.addQuotes(reform.getName())).toArray(String[]::new));
        } else {
            list.clear();
            list.addAll(reforms.stream().map(reform -> ClausewitzUtils.addQuotes(reform.getName())).toArray(String[]::new));
        }
    }

    public Map<String, List<GovernmentReform>> getAvailableReforms() {
        return getType().getReformLevels()
                        .entrySet()
                        .stream()
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                  entry -> entry.getValue()
                                                                .stream()
                                                                .filter(governmentReform -> governmentReform.getPotential() == null
                                                                                            || governmentReform.getPotential().apply(country, country))
                                                                .toList(),
                                                  (governmentReforms, governmentReforms2) -> governmentReforms,
                                                  LinkedHashMap::new));
    }

    public List<String> getHistory() {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList list = reformStack.getList("history");

            if (list != null) {
                return list.getValues().stream().map(ClausewitzUtils::removeQuotes).toList();
            }
        }

        return new ArrayList<>();
    }

    public void addReform(GovernmentReform reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack == null) {
            reformStack = this.item.addChild("reform_stack");
        }

        ClausewitzList reforms = reformStack.getList("reforms");

        if (reforms != null) {
            reforms.add(ClausewitzUtils.addQuotes(reform.getName()));
        } else {
            reformStack.addList("reforms", ClausewitzUtils.addQuotes(reform.getName()));
        }

        ClausewitzList history = reformStack.getList("history");

        if (history != null) {
            history.add(ClausewitzUtils.addQuotes(reform.getName()));
        } else {
            reformStack.addList("history", ClausewitzUtils.addQuotes(reform.getName()));
        }
    }

    public void changeReform(GovernmentReform previous, GovernmentReform newOne) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName()));
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName()));
            }
        }
    }

    public void removeReform(GovernmentReform reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack");

        if (reformStack != null) {
            ClausewitzList reforms = reformStack.getList("reforms");

            if (reforms != null) {
                reforms.removeLast(ClausewitzUtils.addQuotes(reform.getName()));
            }

            ClausewitzList history = reformStack.getList("history");

            if (history != null) {
                history.removeLast(ClausewitzUtils.addQuotes(reform.getName()));
            }
        }
    }

    public boolean hasMechanic(String mechanic) {
        return this.item.hasChild(mechanic);
    }
}
