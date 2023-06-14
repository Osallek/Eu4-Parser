package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Government;
import fr.osallek.eu4parser.model.game.GovernmentReform;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
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

    public Optional<String> getTypeName() {
        return this.item.getVarAsString("government").map(ClausewitzUtils::removeQuotes);
    }

    public Optional<Government> getType() {
        return getTypeName().map(this.game::getGovernment);
    }

    public void setType(Government type) {
        this.item.setVariable("government", ClausewitzUtils.addQuotes(type.getName()));
    }

    public List<GovernmentReform> getReforms() {
        return this.item.getChild("reform_stack")
                        .flatMap(reformStack -> reformStack.getList("reforms"))
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(this.game::getGovernmentReform)
                        .filter(Objects::nonNull)
                        .toList();
    }

    public List<String> getReformsNames() {
        return this.item.getChild("reform_stack")
                        .flatMap(reformStack -> reformStack.getList("reforms"))
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(ClausewitzUtils::removeQuotes)
                        .toList();
    }

    public void setReforms(List<GovernmentReform> reforms) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack").orElse(this.item.addChild("reform_stack"));
        reformStack.getList("reforms").ifPresentOrElse(list -> {
            list.clear();
            list.addAll(reforms.stream().map(reform -> ClausewitzUtils.addQuotes(reform.getName())).toArray(String[]::new));
        }, () -> reformStack.addList("reforms", reforms.stream().map(reform -> ClausewitzUtils.addQuotes(reform.getName())).toArray(String[]::new)));
    }

    public Map<String, List<GovernmentReform>> getAvailableReforms() {
        return getType().map(Government::getReformLevels)
                        .map(Map::entrySet)
                        .stream()
                        .flatMap(Collection::stream)
                        .collect(Collectors.toMap(Map.Entry::getKey,
                                                  entry -> entry.getValue()
                                                                .stream()
                                                                .filter(governmentReform -> governmentReform.getPotential()
                                                                                                            .filter(c -> c.apply(this.country, this.country))
                                                                                                            .isPresent())
                                                                .toList(),
                                                  (governmentReforms, governmentReforms2) -> governmentReforms,
                                                  LinkedHashMap::new));
    }

    public List<String> getHistory() {
        return this.item.getChild("reform_stack")
                        .flatMap(reformStack -> reformStack.getList("history"))
                        .map(ClausewitzList::getValues)
                        .stream()
                        .flatMap(Collection::stream)
                        .map(ClausewitzUtils::removeQuotes)
                        .toList();
    }

    public void addReform(GovernmentReform reform) {
        ClausewitzItem reformStack = this.item.getChild("reform_stack").orElse(this.item.addChild("reform_stack"));
        reformStack.getList("reforms").ifPresentOrElse(reforms -> reforms.add(ClausewitzUtils.addQuotes(reform.getName())),
                                                       () -> reformStack.addList("reforms", ClausewitzUtils.addQuotes(reform.getName())));

        reformStack.getList("history").ifPresentOrElse(history -> history.add(ClausewitzUtils.addQuotes(reform.getName())),
                                                       () -> reformStack.addList("history", ClausewitzUtils.addQuotes(reform.getName())));
    }

    public void changeReform(GovernmentReform previous, GovernmentReform newOne) {
        this.item.getChild("reform_stack").ifPresent(reformStack -> {
            reformStack.getList("reforms")
                       .ifPresent(reforms -> reforms.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName())));
            reformStack.getList("history")
                       .ifPresent(history -> history.change(ClausewitzUtils.addQuotes(previous.getName()), ClausewitzUtils.addQuotes(newOne.getName())));
        });
    }

    public void removeReform(GovernmentReform reform) {
        this.item.getChild("reform_stack").ifPresent(reformStack -> {
            reformStack.getList("reforms").ifPresent(reforms -> reforms.removeLast(ClausewitzUtils.addQuotes(reform.getName())));
            reformStack.getList("history").ifPresent(history -> history.removeLast(ClausewitzUtils.addQuotes(reform.getName())));
        });
    }

    public boolean hasMechanic(String mechanic) {
        return this.item.hasChild(mechanic);
    }
}
