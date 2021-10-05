package fr.osallek.eu4parser.model.save.empire;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.todo.ImperialReform;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Empire {

    protected final ClausewitzItem item;

    protected final Save save;

    protected List<OldEmperor> oldEmperors;

    protected Map<String, ImperialReform> imperialReforms;

    public Empire(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        this.imperialReforms = this.save.getGame()
                                        .getImperialReforms()
                                        .stream()
                                        .filter(imperialReform -> getId().equals(imperialReform.getEmpire()))
                                        .filter(imperialReform -> this.save.getDlcEnabled().containsAll(imperialReform.dlcRequired()))
                                        .filter(imperialReform -> Collections.disjoint(this.save.getDlcEnabled(), imperialReform.dlcRequiredNot()))
                                        .collect(Collectors.toMap(ImperialReform::getName, Function.identity()));
        refreshAttributes();
    }

    protected abstract String getId();

    public boolean dismantled() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("hre_dismantled"));
    }

    public void dismantle() {
        this.item.addVariable("hre_dismantled", true);
        this.item.removeVariable("emperor");
        this.item.removeVariable("imperial_influence");
        this.item.removeVariables("passed_reform");
    }

    public SaveCountry getEmperor() {
        if (dismantled()) {
            return null;
        }

        String emperor = this.item.getVarAsString("emperor");

        return this.save.getCountry(ClausewitzUtils.removeQuotes(emperor));
    }

    public void setEmperor(SaveCountry country) {
        if (dismantled()) {
            this.item.removeVariable("hre_dismantled");
        }

        if (country == null) {
            dismantle();
            return;
        }

        if (!ClausewitzUtils.removeQuotes(this.item.getVarAsString("emperor")).equals(country.getTag())) {
            this.item.setVariable("emperor", ClausewitzUtils.addQuotes(country.getTag()));

            if (getImperialInfluence() == null) {
                this.item.addVariable("imperial_influence", 0d);
            }

            addOldEmperor(country);
        }
    }

    public Double getImperialInfluence() {
        if (dismantled()) {
            return null;
        }

        return this.item.getVarAsDouble("imperial_influence");
    }

    public void setImperialInfluence(double imperialInfluence) {
        if (dismantled()) {
            return;
        }

        this.item.setVariable("imperial_influence", imperialInfluence);
    }

    public List<ImperialReform> getMainLineReforms() {
        return this.imperialReforms.values().stream().filter(ImperialReform::isMainLine).sorted().collect(Collectors.toList());
    }

    public List<ImperialReform> getLeftBranchReforms() {
        return this.imperialReforms.values().stream().filter(ImperialReform::isLeftBranch).sorted().collect(Collectors.toList());
    }

    public List<ImperialReform> getRightBranchReforms() {
        return this.imperialReforms.values().stream().filter(ImperialReform::isRightBranch).sorted().collect(Collectors.toList());
    }

    public List<ImperialReform> getPassedReforms() {
        if (dismantled()) {
            return new ArrayList<>();
        }

        return this.item.getVarsAsStrings("passed_reform")
                        .stream()
                        .map(ClausewitzUtils::removeQuotes)
                        .map(this.imperialReforms::get)
                        .sorted()
                        .collect(Collectors.toList());
    }

    public List<ImperialReform> getMainLinePassedReforms() {
        return getPassedReforms().stream().filter(ImperialReform::isMainLine).sorted().collect(Collectors.toList());
    }


    public List<ImperialReform> getMainLineNotPassedReforms() {
        return getMainLineReforms().stream().filter(reform -> !getPassedReforms().contains(reform)).sorted().collect(Collectors.toList());
    }

    public List<ImperialReform> getLeftBranchPassedReforms() {
        return getPassedReforms().stream().filter(ImperialReform::isLeftBranch).sorted().collect(Collectors.toList());
    }


    public List<ImperialReform> getLeftBranchNotPassedReforms() {
        return getLeftBranchReforms().stream().filter(reform -> !getPassedReforms().contains(reform)).sorted().collect(Collectors.toList());
    }

    public List<ImperialReform> getRightBranchPassedReforms() {
        return getPassedReforms().stream().filter(ImperialReform::isRightBranch).sorted().collect(Collectors.toList());
    }


    public List<ImperialReform> getRightBranchNotPassedReforms() {
        return getRightBranchReforms().stream().filter(reform -> !getPassedReforms().contains(reform)).sorted().collect(Collectors.toList());
    }

    public void addPassedReform(ImperialReform reform) {
        if (dismantled()) {
            return;
        }

        List<String> passedReform = this.item.getVarsAsStrings("passed_reform");

        if (reform.isMainLine()) {
            getPassedReforms().stream().filter(imperialReform -> !imperialReform.isMainLine()).forEach(this::removePassedReform);
        } else if (reform.isLeftBranch()) {
            getPassedReforms().stream().filter(imperialReform -> !imperialReform.isLeftBranch()).forEach(this::removePassedReform);
        } else if (reform.isRightBranch()) {
            getPassedReforms().stream().filter(imperialReform -> !imperialReform.isRightBranch()).forEach(this::removePassedReform);
        }

        do {

            if (!passedReform.contains(ClausewitzUtils.addQuotes(reform.getName()))) {
                this.item.addVariable("passed_reform", ClausewitzUtils.addQuotes(reform.getName()));
            }

            reform = reform.getRequiredReform();
        } while (reform != null);
    }

    public void removePassedReform(ImperialReform reform) {
        if (dismantled()) {
            return;
        }

        this.item.removeVariable("passed_reform", ClausewitzUtils.addQuotes(reform.getName()));
    }

    public void setPassedReforms(List<ImperialReform> passedReforms) {
        getPassedReforms().forEach(this::removePassedReform);
        passedReforms.forEach(this::addPassedReform);
    }

    public List<OldEmperor> getOldEmperors() {
        return oldEmperors;
    }

    public void addOldEmperor(SaveCountry country) {
        String id = Integer.toString(getOldEmperors().stream().map(OldEmperor::getId).max(Integer::compareTo).orElse(new Random().nextInt(9000)));
        OldEmperor.addToItem(this.item, id, country.getTag(), this.save.getDate());
        refreshAttributes();
    }

    public void removeOldEmperor(int id) {
        this.item.removeChild("old_emperor", id);
        refreshAttributes();
    }

    protected void refreshAttributes() {
        this.oldEmperors = this.item.getChildren("old_emperor").stream().map(OldEmperor::new).collect(Collectors.toList());
    }
}
