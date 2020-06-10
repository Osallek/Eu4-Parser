package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ImperialReform {

    private final ClausewitzItem item;

    private final Game game;

    private String localizedName;

    public ImperialReform(ClausewitzItem item, Game game) {
        this.item = item;
        this.game = game;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public String getLocalizedName() {
        return localizedName;
    }

    void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public List<String> dlcRequired() {
        ClausewitzItem potentialItem = this.item.getChild("potential");

        if (potentialItem != null) {
            return potentialItem.getVarsAsStrings("has_dlc");
        }

        return new ArrayList<>();
    }

    public List<String> dlcRequiredNot() {
        ClausewitzItem potentialItem = this.item.getChild("potential");

        if (potentialItem != null) {
            List<ClausewitzItem> notItems = this.item.getChildren("NOT");

            if (!notItems.isEmpty()) {
                return notItems.stream()
                               .map(notItem -> notItem.getVarsAsStrings("has_dlc"))
                               .flatMap(Collection::stream)
                               .collect(Collectors.toList());
            }
        }

        return new ArrayList<>();
    }

    public String getEmpire() {
        return this.item.getVarAsString("empire");
    }

    public void setEmpire(String empire) {
        this.item.setVariable("empire", empire);
    }

    public Map<String, Double> getProvinceModifiers() {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            return provincesItem.getVariables()
                                .stream()
                                .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                          ClausewitzVariable::getAsDouble,
                                                          (a, b) -> b,
                                                          LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addProvinceModifier(String modifier, Double quantity) {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            provincesItem.setVariable(modifier, quantity);
        }
    }

    public void removeProvinceModifier(String modifier) {
        ClausewitzItem provincesItem = this.item.getChild("province");

        if (provincesItem != null) {
            provincesItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getEmperorModifiers() {
        ClausewitzItem emperorsItem = this.item.getChild("emperor");

        if (emperorsItem != null) {
            return emperorsItem.getVariables()
                               .stream()
                               .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                         ClausewitzVariable::getAsDouble,
                                                         (a, b) -> b,
                                                         LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addEmperorModifier(String modifier, Double quantity) {
        ClausewitzItem emperorsItem = this.item.getChild("emperor");

        if (emperorsItem != null) {
            emperorsItem.setVariable(modifier, quantity);
        }
    }

    public void removeEmperorModifier(String modifier) {
        ClausewitzItem emperorsItem = this.item.getChild("emperor");

        if (emperorsItem != null) {
            emperorsItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getAllModifiers() {
        ClausewitzItem allsItem = this.item.getChild("all");

        if (allsItem != null) {
            return allsItem.getVariables()
                           .stream()
                           .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                     ClausewitzVariable::getAsDouble,
                                                     (a, b) -> b,
                                                     LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addAllModifier(String modifier, Double quantity) {
        ClausewitzItem allsItem = this.item.getChild("all");

        if (allsItem != null) {
            allsItem.setVariable(modifier, quantity);
        }
    }

    public void removeAllModifier(String modifier) {
        ClausewitzItem allsItem = this.item.getChild("all");

        if (allsItem != null) {
            allsItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getMemberModifiers() {
        ClausewitzItem membersItem = this.item.getChild("member");

        if (membersItem != null) {
            return membersItem.getVariables()
                              .stream()
                              .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                        ClausewitzVariable::getAsDouble,
                                                        (a, b) -> b,
                                                        LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addMemberModifier(String modifier, Double quantity) {
        ClausewitzItem membersItem = this.item.getChild("member");

        if (membersItem != null) {
            membersItem.setVariable(modifier, quantity);
        }
    }

    public void removeMemberModifier(String modifier) {
        ClausewitzItem membersItem = this.item.getChild("member");

        if (membersItem != null) {
            membersItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getEmperorPerPrinceModifiers() {
        ClausewitzItem emperorPerPrincesItem = this.item.getChild("emperor_per_prince");

        if (emperorPerPrincesItem != null) {
            return emperorPerPrincesItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                  ClausewitzVariable::getAsDouble,
                                                                  (a, b) -> b,
                                                                  LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addEmperorPerPrinceModifier(String modifier, Double quantity) {
        ClausewitzItem emperorPerPrincesItem = this.item.getChild("emperor_per_prince");

        if (emperorPerPrincesItem != null) {
            emperorPerPrincesItem.setVariable(modifier, quantity);
        }
    }

    public void removeEmperorPerPrinceModifier(String modifier) {
        ClausewitzItem emperorPerPrincesItem = this.item.getChild("emperor_per_prince");

        if (emperorPerPrincesItem != null) {
            emperorPerPrincesItem.removeVariable(modifier);
        }
    }

    public Map<String, Double> getElectorPerPrinceModifiers() {
        ClausewitzItem electorPerPrincesItem = this.item.getChild("elector_per_prince");

        if (electorPerPrincesItem != null) {
            return electorPerPrincesItem.getVariables()
                                        .stream()
                                        .collect(Collectors.toMap(ClausewitzVariable::getName,
                                                                  ClausewitzVariable::getAsDouble,
                                                                  (a, b) -> b,
                                                                  LinkedHashMap::new));
        }

        return new LinkedHashMap<>();
    }

    public void addElectorPerPrinceModifier(String modifier, Double quantity) {
        ClausewitzItem electorPerPrincesItem = this.item.getChild("elector_per_prince");

        if (electorPerPrincesItem != null) {
            electorPerPrincesItem.setVariable(modifier, quantity);
        }
    }

    public void removeElectorPerPrinceModifier(String modifier) {
        ClausewitzItem electorPerPrincesItem = this.item.getChild("elector_per_prince");

        if (electorPerPrincesItem != null) {
            electorPerPrincesItem.removeVariable(modifier);
        }
    }

    public ImperialReform getDisabledBy() {
        ClausewitzVariable disabledByVar = this.item.getVar("disabled_by");

        if (disabledByVar != null) {
            return this.game.getImperialReform(disabledByVar.getValue());
        }

        return null;
    }

    public void setDisabledBy(ImperialReform imperialReform) {
        this.item.setVariable("disabled_by", imperialReform.getName());
    }

    public ImperialReform getRequiredReform() {
        ClausewitzVariable requiredReformVar = this.item.getVar("required_reform");

        if (requiredReformVar != null) {
            return this.game.getImperialReform(requiredReformVar.getValue());
        }

        return null;
    }

    public void setRequiredReform(ImperialReform imperialReform) {
        this.item.setVariable("required_reform", imperialReform.getName());
    }

    public String getGuiContainer() {
        return this.item.getVarAsString("gui_container");
    }

    public void setGuiContainer(String guiContainer) {
        this.item.setVariable("gui_container", guiContainer);
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
        ClausewitzItem onEffectItem = this.item.getChild("on_effect");

        if (onEffectItem != null) {
            return Boolean.TRUE.equals(onEffectItem.getVarAsBool("imperial_ban_allowed"));
        }

        return false;
    }

    public boolean disableImperialBanAllowed() {
        ClausewitzItem onEffectItem = this.item.getChild("off_effect");

        if (onEffectItem != null) {
            return Boolean.FALSE.equals(onEffectItem.getVarAsBool("imperial_ban_allowed"));
        }

        return false;
    }

    public boolean enableInternalHreCb() {
        ClausewitzItem onEffectItem = this.item.getChild("on_effect");

        if (onEffectItem != null) {
            return Boolean.TRUE.equals(onEffectItem.getVarAsBool("internal_hre_cb"));
        }

        return false;
    }

    public boolean disableInternalHreCb() {
        ClausewitzItem onEffectItem = this.item.getChild("off_effect");

        if (onEffectItem != null) {
            return Boolean.FALSE.equals(onEffectItem.getVarAsBool("internal_hre_cb"));
        }

        return false;
    }

    public boolean enableEnableImperialRealmWar() {
        ClausewitzItem onEffectItem = this.item.getChild("on_effect");

        if (onEffectItem != null) {
            return Boolean.TRUE.equals(onEffectItem.getVarAsBool("enable_imperial_realm_war"));
        }

        return false;
    }

    public boolean disableEnableImperialRealmWar() {
        ClausewitzItem onEffectItem = this.item.getChild("off_effect");

        if (onEffectItem != null) {
            return Boolean.FALSE.equals(onEffectItem.getVarAsBool("enable_imperial_realm_war"));
        }

        return false;
    }

    public boolean enableHreInheritable() {
        ClausewitzItem onEffectItem = this.item.getChild("on_effect");

        if (onEffectItem != null) {
            return Boolean.TRUE.equals(onEffectItem.getVarAsBool("hre_inheritable"));
        }

        return false;
    }

    public boolean disableHreInheritable() {
        ClausewitzItem onEffectItem = this.item.getChild("off_effect");

        if (onEffectItem != null) {
            return Boolean.FALSE.equals(onEffectItem.getVarAsBool("hre_inheritable"));
        }

        return false;
    }
}
