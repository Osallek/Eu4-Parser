package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.util.List;
import java.util.stream.Collectors;

public class CultureGroup extends AbstractCulture {

    private final List<Culture> cultures;

    private String localizedName;

    private String graphicalCulture;

    public CultureGroup(ClausewitzItem item) {
        super(item);
        this.graphicalCulture = item.getVarAsString("graphical_culture");
        this.cultures = item.getChildren()
                            .stream()
                            .filter(child -> !"male_names".equals(child.getName())
                                             && !"female_names".equals(child.getName())
                                             && !"dynasty_names".equals(child.getName()))
                            .map(child -> new Culture(child, this))
                            .collect(Collectors.toList());
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getGraphicalCulture() {
        return this.graphicalCulture;
    }

    public void setGraphicalCulture(String graphicalCulture) {
        this.graphicalCulture = graphicalCulture;
    }

    public List<Culture> getCultures() {
        return cultures;
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
