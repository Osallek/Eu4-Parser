package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ImperialReform implements Comparable<ImperialReform> {

    private final Game game;

    private String name;

    private String localizedName;

    private final List<String> dlcRequired;

    private final List<String> dlcRequiredNot;

    private String empire;

    private Modifiers provinceModifiers;

    private Modifiers emperorModifiers;

    private Modifiers allModifiers;

    private Modifiers memberModifiers;

    private Modifiers emperorPerPrinceModifiers;

    private Modifiers electorPerPrinceModifiers;

    private String disabledBy;

    private String requiredReform;

    private String guiContainer;

    private boolean enableImperialBanAllowed;

    private boolean disableImperialBanAllowed;

    private boolean enableInternalHreCb;

    private boolean disableInternalHreCb;

    private boolean enableEnableImperialRealmWar;

    private boolean disableEnableImperialRealmWar;

    private boolean enableHreInheritable;

    private boolean disableHreInheritable;

    public ImperialReform(ClausewitzItem item, Game game) {
        this.game = game;
        this.name = item.getName();
        ClausewitzItem child = item.getChild("potential");
        this.dlcRequired = child == null ? null : child.getVarsAsStrings("has_dlc");

        if (child != null) {
            List<ClausewitzItem> children = child.getChildren("NOT");
            this.dlcRequiredNot = children.isEmpty() ? null : children.stream()
                                                                      .map(notItem -> notItem.getVarsAsStrings("has_dlc"))
                                                                      .flatMap(Collection::stream)
                                                                      .collect(Collectors.toList());
        } else {
            this.dlcRequiredNot = new ArrayList<>();
        }
        this.empire = item.getVarAsString("empire");
        this.provinceModifiers = new Modifiers(item.getChild("province"));
        this.emperorModifiers = new Modifiers(item.getChild("emperor"));
        this.allModifiers = new Modifiers(item.getChild("all"));
        this.memberModifiers = new Modifiers(item.getChild("member"));
        this.emperorPerPrinceModifiers = new Modifiers(item.getChild("emperor_per_prince"));
        this.electorPerPrinceModifiers = new Modifiers(item.getChild("elector_per_prince"));
        this.disabledBy = item.getVarAsString("disabled_by");
        this.requiredReform = item.getVarAsString("required_reform");
        this.guiContainer = item.getVarAsString("gui_container");

        child = item.getChild("on_effect");

        if (child != null) {
            this.enableImperialBanAllowed = BooleanUtils.toBoolean(child.getVarAsBool("imperial_ban_allowed"));
            this.disableImperialBanAllowed = BooleanUtils.isFalse(child.getVarAsBool("imperial_ban_allowed"));
            this.enableInternalHreCb = BooleanUtils.toBoolean(child.getVarAsBool("internal_hre_cb"));
            this.disableInternalHreCb = BooleanUtils.isFalse(child.getVarAsBool("internal_hre_cb"));
            this.enableEnableImperialRealmWar = BooleanUtils.toBoolean(child.getVarAsBool("enable_imperial_realm_war"));
            this.disableEnableImperialRealmWar = BooleanUtils.isFalse(child.getVarAsBool("enable_imperial_realm_war"));
            this.enableHreInheritable = BooleanUtils.toBoolean(child.getVarAsBool("hre_inheritable"));
            this.disableHreInheritable = BooleanUtils.isFalse(child.getVarAsBool("hre_inheritable"));
        }
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<String> dlcRequired() {
        return this.dlcRequired == null ? new ArrayList<>() : this.dlcRequired;
    }

    public List<String> dlcRequiredNot() {
        return this.dlcRequiredNot == null ? new ArrayList<>() : this.dlcRequiredNot;
    }

    public String getEmpire() {
        return this.empire;
    }

    public void setEmpire(String empire) {
        this.empire = empire;
    }

    public Modifiers getProvinceModifiers() {
        return this.provinceModifiers;
    }

    public void addProvinceModifier(String modifier, String quantity) {
        if (this.provinceModifiers == null) {
            this.provinceModifiers = new Modifiers();
        }

        this.provinceModifiers.add(modifier, quantity);
    }

    public void removeProvinceModifier(String modifier) {
        if (this.provinceModifiers != null) {
            this.provinceModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getEmperorModifiers() {
        return this.emperorModifiers;
    }

    public void addEmperorModifier(String modifier, String quantity) {
        if (this.emperorModifiers == null) {
            this.emperorModifiers = new Modifiers();
        }

        this.emperorModifiers.add(modifier, quantity);
    }

    public void removeEmperorModifier(String modifier) {
        if (this.emperorModifiers != null) {
            this.emperorModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getAllModifiers() {
        return this.allModifiers;
    }

    public void addAllModifier(String modifier, String quantity) {
        if (this.allModifiers == null) {
            this.allModifiers = new Modifiers();
        }

        this.allModifiers.add(modifier, quantity);
    }

    public void removeAllModifier(String modifier) {
        if (this.allModifiers != null) {
            this.allModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getMemberModifiers() {
        return this.memberModifiers;
    }

    public void addMemberModifier(String modifier, String quantity) {
        if (this.memberModifiers == null) {
            this.memberModifiers = new Modifiers();
        }

        this.memberModifiers.add(modifier, quantity);
    }

    public void removeMemberModifier(String modifier) {
        if (this.memberModifiers != null) {
            this.memberModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getEmperorPerPrinceModifiers() {
        return this.emperorPerPrinceModifiers;
    }

    public void addEmperorPerPrinceModifier(String modifier, String quantity) {
        if (this.emperorPerPrinceModifiers == null) {
            this.emperorPerPrinceModifiers = new Modifiers();
        }

        this.emperorPerPrinceModifiers.add(modifier, quantity);
    }

    public void removeEmperorPerPrinceModifier(String modifier) {
        if (this.emperorPerPrinceModifiers != null) {
            this.emperorPerPrinceModifiers.removeModifier(modifier);
        }
    }

    public Modifiers getElectorPerPrinceModifiers() {
        return this.electorPerPrinceModifiers;
    }

    public void addElectorPerPrinceModifier(String modifier, String quantity) {
        if (this.electorPerPrinceModifiers == null) {
            this.electorPerPrinceModifiers = new Modifiers();
        }

        this.electorPerPrinceModifiers.add(modifier, quantity);
    }

    public void removeElectorPerPrinceModifier(String modifier) {
        if (this.electorPerPrinceModifiers != null) {
            this.electorPerPrinceModifiers.removeModifier(modifier);
        }
    }

    public ImperialReform getDisabledBy() {
        return this.game.getImperialReform(this.disabledBy);
    }

    public void setDisabledBy(ImperialReform imperialReform) {
        this.disabledBy = imperialReform.getName();
    }

    public ImperialReform getRequiredReform() {
        return this.game.getImperialReform(this.requiredReform);
    }

    public void setRequiredReform(ImperialReform imperialReform) {
        this.requiredReform = imperialReform.getName();
    }

    public String getGuiContainer() {
        return this.guiContainer;
    }

    public void setGuiContainer(String guiContainer) {
        this.guiContainer = guiContainer;
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

    public boolean enableImperialBanAllowed() {
        return this.enableImperialBanAllowed;
    }

    public boolean disableImperialBanAllowed() {
        return this.disableImperialBanAllowed;
    }

    public boolean enableInternalHreCb() {
        return this.enableInternalHreCb;
    }

    public boolean disableInternalHreCb() {
        return this.disableInternalHreCb;
    }

    public boolean enableEnableImperialRealmWar() {
        return this.enableEnableImperialRealmWar;
    }

    public boolean disableEnableImperialRealmWar() {
        return this.disableEnableImperialRealmWar;
    }

    public boolean enableHreInheritable() {
        return this.enableHreInheritable;
    }

    public boolean disableHreInheritable() {
        return this.disableHreInheritable;
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
