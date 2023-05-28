package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class ImperialReform implements Comparable<ImperialReform> {

    private final Game game;

    private final ClausewitzItem item;

    public ImperialReform(ClausewitzItem item, Game game) {
        this.game = game;
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public Optional<ConditionAnd> getPotential() {
        return this.item.getChild("potential").map(ConditionAnd::new);
    }

    public List<String> dlcRequired() {
        return getPotential().map(potential -> potential.getConditions().get("has_dlc")).orElse(new ArrayList<>());
    }

    public List<String> dlcRequiredNot() {
        return getPotential().map(potential -> potential.getScopes()
                                                        .stream()
                                                        .filter(condition -> "NOT".equals(condition.getName()))
                                                        .map(condition -> condition.getConditions().get("has_dlc"))
                                                        .flatMap(Collection::stream)
                                                        .toList()).orElse(new ArrayList<>());
    }

    public Optional<String> getEmpire() {
        return this.item.getVarAsString("empire");
    }

    public void setEmpire(String empire) {
        if (StringUtils.isBlank(empire)) {
            this.item.removeVariable("empire");
        } else {
            this.item.setVariable("empire", empire);
        }
    }

    public Optional<Modifiers> getProvinceModifiers() {
        return this.item.getChild("province").map(Modifiers::new);
    }

    public Optional<Modifiers> getEmperorModifiers() {
        return this.item.getChild("emperor").map(Modifiers::new);
    }

    public Optional<Modifiers> getAllModifiers() {
        return this.item.getChild("all").map(Modifiers::new);
    }

    public Optional<Modifiers> getMemberModifiers() {
        return this.item.getChild("member").map(Modifiers::new);
    }

    public Optional<Modifiers> getEmperorPerPrinceModifiers() {
        return this.item.getChild("emperor_per_prince").map(Modifiers::new);
    }

    public Optional<Modifiers> getElectorPerPrinceModifiers() {
        return this.item.getChild("elector_per_prince").map(Modifiers::new);
    }

    public Optional<String> getDisabledByName() {
        return this.item.getVarAsString("disabled_by");
    }

    public Optional<ImperialReform> getDisabledBy() {
        return getDisabledByName().map(this.game::getImperialReform);
    }

    public void setDisabledBy(String reform) {
        if (StringUtils.isBlank(reform)) {
            this.item.removeVariable("disabled_by");
        } else {
            this.item.setVariable("disabled_by", reform);
        }
    }

    public Optional<String> getRequiredReformName() {
        return this.item.getVarAsString("required_reform");
    }

    public Optional<ImperialReform> getRequiredReform() {
        return getRequiredReformName().map(this.game::getImperialReform);
    }

    public void setRequiredReform(String reform) {
        if (StringUtils.isBlank(reform)) {
            this.item.removeVariable("required_reform");
        } else {
            this.item.setVariable("required_reform", reform);
        }
    }

    public Optional<String> getGuiContainer() {
        return this.item.getVarAsString("gui_container");
    }

    public void setGuiContainer(String guiContainer) {
        if (StringUtils.isBlank(guiContainer)) {
            this.item.removeVariable("gui_container");
        } else {
            this.item.setVariable("gui_container", guiContainer);
        }
    }

    public Optional<ImperialReformEffect> getOnEffect() {
        return this.item.getChild("on_effect").map(ImperialReformEffect::new);
    }

    public Optional<ImperialReformEffect> getOffEffect() {
        return this.item.getChild("off_effect").map(ImperialReformEffect::new);
    }

    public boolean isMainLine() {
        Optional<String> container = getGuiContainer();
        return container.isEmpty() || "mainline".equals(container.get()) || "nodlc".equals(container.get());
    }

    public boolean isLeftBranch() {
        return getGuiContainer().filter("left_branch"::equals).isPresent();
    }

    public boolean isRightBranch() {
        return getGuiContainer().filter("right_branch"::equals).isPresent();
    }

    private Integer getDepth() {
        int i = 0;
        Optional<ImperialReform> reform = Optional.of(this);

        while (reform.isPresent() && reform.get().getRequiredReform().isPresent()) {
            i++;
            reform = reform.get().getRequiredReform();
        }

        return i;
    }

    @Override
    public int compareTo(ImperialReform o) {
        return this.getDepth().compareTo(o.getDepth());
    }
}
