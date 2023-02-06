package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.List;

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

    public ConditionAnd getPotential() {
        ClausewitzItem child = this.item.getChild("potential");
        return child == null ? null : new ConditionAnd(child);
    }

    public List<String> dlcRequired() {
        ConditionAnd potential = getPotential();
        return potential == null ? null : potential.getConditions().get("has_dlc");
    }

    public List<String> dlcRequiredNot() {
        ConditionAnd potential = getPotential();

        if (potential == null) {
            return null;
        }

        return potential.getScopes()
                        .stream()
                        .filter(condition -> "NOT".equals(condition.getName()))
                        .map(condition -> condition.getConditions().get("has_dlc"))
                        .flatMap(Collection::stream)
                        .toList();
    }

    public String getEmpire() {
        return this.item.getVarAsString("empire");
    }

    public void setEmpire(String empire) {
        if (StringUtils.isBlank(empire)) {
            this.item.removeVariable("empire");
        } else {
            this.item.setVariable("empire", empire);
        }
    }

    public Modifiers getProvinceModifiers() {
        return new Modifiers(this.item.getChild("province"));
    }

    public Modifiers getEmperorModifiers() {
        return new Modifiers(this.item.getChild("emperor"));
    }

    public Modifiers getAllModifiers() {
        return new Modifiers(this.item.getChild("all"));
    }

    public Modifiers getMemberModifiers() {
        return new Modifiers(this.item.getChild("member"));
    }

    public Modifiers getEmperorPerPrinceModifiers() {
        return new Modifiers(this.item.getChild("emperor_per_prince"));
    }

    public Modifiers getElectorPerPrinceModifiers() {
        return new Modifiers(this.item.getChild("elector_per_prince"));
    }

    public String getDisabledByName() {
        return this.item.getVarAsString("disabled_by");
    }

    public ImperialReform getDisabledBy() {
        return StringUtils.isBlank(getDisabledByName()) ? null : this.game.getImperialReform(getDisabledByName());
    }

    public void setDisabledBy(String reform) {
        if (StringUtils.isBlank(reform)) {
            this.item.removeVariable("disabled_by");
        } else {
            this.item.setVariable("disabled_by", reform);
        }
    }

    public String getRequiredReformName() {
        return this.item.getVarAsString("required_reform");
    }

    public ImperialReform getRequiredReform() {
        return StringUtils.isBlank(getRequiredReformName()) ? null : this.game.getImperialReform(getRequiredReformName());
    }

    public void setRequiredReform(String reform) {
        if (StringUtils.isBlank(reform)) {
            this.item.removeVariable("required_reform");
        } else {
            this.item.setVariable("required_reform", reform);
        }
    }

    public String getGuiContainer() {
        return this.item.getVarAsString("gui_container");
    }

    public void setGuiContainer(String guiContainer) {
        if (StringUtils.isBlank(guiContainer)) {
            this.item.removeVariable("gui_container");
        } else {
            this.item.setVariable("gui_container", guiContainer);
        }
    }

    public ImperialReformEffect getOnEffect() {
        ClausewitzItem child = this.item.getChild("on_effect");
        return child == null ? null : new ImperialReformEffect(child);
    }

    public ImperialReformEffect getOffEffect() {
        ClausewitzItem child = this.item.getChild("off_effect");
        return child == null ? null : new ImperialReformEffect(child);
    }

    public boolean isMainLine() {
        return getGuiContainer() == null || "mainline".equals(getGuiContainer()) || "nodlc".equals(getGuiContainer());
    }

    public boolean isLeftBranch() {
        return "left_branch".equals(getGuiContainer());
    }

    public boolean isRightBranch() {
        return "right_branch".equals(getGuiContainer());
    }

    private Integer getDepth() {
        int i = 0;
        ImperialReform reform = this;

        while (reform.getRequiredReform() != null) {
            i++;
            reform = reform.getRequiredReform();
        }

        return i;
    }

    @Override
    public int compareTo(ImperialReform o) {
        return this.getDepth().compareTo(o.getDepth());
    }
}
