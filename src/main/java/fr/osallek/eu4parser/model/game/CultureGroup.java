package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CultureGroup extends AbstractCulture {

    public CultureGroup(ClausewitzItem item) {
        super(item);
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
        return this.item.getChildren()
                        .stream()
                        .filter(child -> !"male_names".equals(child.getName())
                                         && !"female_names".equals(child.getName())
                                         && !"dynasty_names".equals(child.getName()))
                        .map(child -> new Culture(child, this))
                        .toList();
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
