package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImperialReform implements Comparable<ImperialReform> {

    private final Game game;

    private String name;

    private String localizedName;

    private final List<String> dlcRequired;

    private final List<String> dlcRequiredNot;

    private String empire;

    private Map<String, Double> provinceModifiers;

    private Map<String, Double> emperorModifiers;

    private Map<String, Double> allModifiers;

    private Map<String, Double> memberModifiers;

    private Map<String, Double> emperorPerPrinceModifiers;

    private Map<String, Double> electorPerPrinceModifiers;

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
        child = item.getChild("province");
        this.provinceModifiers = child == null ? null : child.getVariables()
                                                             .stream()
                                                             .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                       ClausewitzVariable::getAsDouble,
                                                                                       (a, b) -> b,
                                                                                       LinkedHashMap::new));
        child = item.getChild("emperor");
        this.emperorModifiers = child == null ? null : child.getVariables()
                                                            .stream()
                                                            .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                      ClausewitzVariable::getAsDouble,
                                                                                      (a, b) -> b,
                                                                                      LinkedHashMap::new));
        child = item.getChild("all");
        this.allModifiers = child == null ? null : child.getVariables()
                                                        .stream()
                                                        .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                  ClausewitzVariable::getAsDouble,
                                                                                  (a, b) -> b,
                                                                                  LinkedHashMap::new));
        child = item.getChild("member");
        this.memberModifiers = child == null ? null : child.getVariables()
                                                           .stream()
                                                           .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                     ClausewitzVariable::getAsDouble,
                                                                                     (a, b) -> b,
                                                                                     LinkedHashMap::new));
        child = item.getChild("emperor_per_prince");
        this.emperorPerPrinceModifiers = child == null ? null : child.getVariables()
                                                                     .stream()
                                                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                               ClausewitzVariable::getAsDouble,
                                                                                               (a, b) -> b,
                                                                                               LinkedHashMap::new));
        child = item.getChild("elector_per_prince");
        this.electorPerPrinceModifiers = child == null ? null : child.getVariables()
                                                                     .stream()
                                                                     .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                                               ClausewitzVariable::getAsDouble,
                                                                                               (a, b) -> b,
                                                                                               LinkedHashMap::new));
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

    public Map<String, Double> getProvinceModifiers() {
        return this.provinceModifiers == null ? new LinkedHashMap<>() : this.provinceModifiers;
    }

    public void addProvinceModifier(String modifier, Double quantity) {
        if (this.provinceModifiers == null) {
            this.provinceModifiers = new LinkedHashMap<>();
        }

        this.provinceModifiers.put(modifier, quantity);
    }

    public void removeProvinceModifier(String modifier) {
        if (this.provinceModifiers != null) {
            this.provinceModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getEmperorModifiers() {
        return this.emperorModifiers == null ? new LinkedHashMap<>() : this.emperorModifiers;
    }

    public void addEmperorModifier(String modifier, Double quantity) {
        if (this.emperorModifiers == null) {
            this.emperorModifiers = new LinkedHashMap<>();
        }

        this.emperorModifiers.put(modifier, quantity);
    }

    public void removeEmperorModifier(String modifier) {
        if (this.emperorModifiers != null) {
            this.emperorModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getAllModifiers() {
        return this.allModifiers == null ? new LinkedHashMap<>() : this.allModifiers;
    }

    public void addAllModifier(String modifier, Double quantity) {
        if (this.allModifiers == null) {
            this.allModifiers = new LinkedHashMap<>();
        }

        this.allModifiers.put(modifier, quantity);
    }

    public void removeAllModifier(String modifier) {
        if (this.allModifiers != null) {
            this.allModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getMemberModifiers() {
        return this.memberModifiers == null ? new LinkedHashMap<>() : this.memberModifiers;
    }

    public void addMemberModifier(String modifier, Double quantity) {
        if (this.memberModifiers == null) {
            this.memberModifiers = new LinkedHashMap<>();
        }

        this.memberModifiers.put(modifier, quantity);
    }

    public void removeMemberModifier(String modifier) {
        if (this.memberModifiers != null) {
            this.memberModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getEmperorPerPrinceModifiers() {
        return this.emperorPerPrinceModifiers == null ? new LinkedHashMap<>() : this.emperorPerPrinceModifiers;
    }

    public void addEmperorPerPrinceModifier(String modifier, Double quantity) {
        if (this.emperorPerPrinceModifiers == null) {
            this.emperorPerPrinceModifiers = new LinkedHashMap<>();
        }

        this.emperorPerPrinceModifiers.put(modifier, quantity);
    }

    public void removeEmperorPerPrinceModifier(String modifier) {
        if (this.emperorPerPrinceModifiers != null) {
            this.emperorPerPrinceModifiers.remove(modifier);
        }
    }

    public Map<String, Double> getElectorPerPrinceModifiers() {
        return this.electorPerPrinceModifiers == null ? new LinkedHashMap<>() : this.electorPerPrinceModifiers;
    }

    public void addElectorPerPrinceModifier(String modifier, Double quantity) {
        if (this.electorPerPrinceModifiers == null) {
            this.electorPerPrinceModifiers = new LinkedHashMap<>();
        }

        this.electorPerPrinceModifiers.put(modifier, quantity);
    }

    public void removeElectorPerPrinceModifier(String modifier) {
        if (this.electorPerPrinceModifiers != null) {
            this.electorPerPrinceModifiers.remove(modifier);
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
