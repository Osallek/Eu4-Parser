package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CultureGroup extends AbstractCulture {

    //Small fix because it seems that creating a group with the same name add culture to the existing group
    private final List<ClausewitzItem> items = new ArrayList<>();

    public CultureGroup(Game game, ClausewitzItem item) {
        super(game, item);
        this.items.add(item);
    }

    public String getGraphicalCulture() {
        return this.item.getVarAsString("graphical_culture");
    }

    public void setGraphicalCulture(String graphicalCulture) {
        if (StringUtils.isBlank(graphicalCulture)) {
            this.item.removeVariable("graphical_culture");
        } else {
            this.item.setVariable("graphical_culture", graphicalCulture);
        }
    }

    public List<Culture> getCultures() {
        return new ArrayList<>(this.items.stream()
                                         .map(i -> i.getChildrenNot("male_names", "female_names", "dynasty_names"))
                                         .flatMap(Collection::stream)
                                         .map(child -> new Culture(this.game, child, this))
                                         .collect(Collectors.toMap(Culture::getName, Function.identity(), (a, b) -> b))
                                         .values());
    }

    public void addItem(ClausewitzItem item) {
        this.items.add(item);
    }

    @Override
    public List<String> getPossibleMaleNames() {
        return getMaleNames();
    }

    @Override
    public List<String> getPossibleFemaleNames() {
        return getFemaleNames();
    }

    @Override
    public List<String> getPossibleDynastyNames() {
        return getDynastyNames();
    }
}
