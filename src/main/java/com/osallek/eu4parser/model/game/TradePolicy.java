package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzObject;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import org.apache.commons.lang3.BooleanUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class TradePolicy {

    private final String name;

    private String localizedName;

    private final String buttonGfx;

    private final boolean unique;

    private final boolean showAlert;

    private final boolean centerOfReformation;

    private final Condition canSelect;

    private final Condition canMaintain;

    private final Map<String, List<String>> tradePower;

    private final Map<String, List<String>> countriesWithMerchantModifier;

    private final Map<String, List<String>> nodeProvinceModifier;


    public TradePolicy(ClausewitzItem item) {
        this.name = item.getName();

        ClausewitzItem child = item.getChild("can_select");
        this.canSelect = child == null ? null : new Condition(child);

        child = item.getChild("can_maintain");
        this.canMaintain = child == null ? null : new Condition(child);

        child = item.getChild("trade_power");
        this.tradePower = child == null ? null : child.getVariables()
                                                      .stream()
                                                      .collect(Collectors.groupingBy(ClausewitzObject::getName,
                                                                                     Collectors.mapping(ClausewitzVariable::getValue, Collectors.toList())));

        child = item.getChild("countries_with_merchant_modifier");
        this.countriesWithMerchantModifier = child == null ? null : child.getVariables()
                                                                         .stream()
                                                                         .collect(Collectors.groupingBy(ClausewitzObject::getName,
                                                                                                        Collectors.mapping(ClausewitzVariable::getValue,
                                                                                                                           Collectors.toList())));

        child = item.getChild("node_province_modifier");
        this.nodeProvinceModifier = child == null ? null : child.getVariables()
                                                                .stream()
                                                                .collect(Collectors.groupingBy(ClausewitzObject::getName,
                                                                                               Collectors.mapping(ClausewitzVariable::getValue,
                                                                                                                  Collectors.toList())));

        this.buttonGfx = item.getVarAsString("button_gfx");
        this.unique = BooleanUtils.toBoolean(item.getVarAsBool("unique"));
        this.showAlert = BooleanUtils.toBoolean(item.getVarAsBool("show_alert"));
        this.centerOfReformation = BooleanUtils.toBoolean(item.getVarAsBool("center_of_reformation"));
    }

    public String getName() {
        return name;
    }

    public String getLocalizedName() {
        return localizedName;
    }

    public void setLocalizedName(String localizedName) {
        this.localizedName = localizedName;
    }

    public String getButtonGfx() {
        return buttonGfx;
    }

    public boolean isUnique() {
        return unique;
    }

    public boolean isShowAlert() {
        return showAlert;
    }

    public boolean isCenterOfReformation() {
        return centerOfReformation;
    }

    public Condition getCanSelect() {
        return canSelect;
    }

    public Condition getCanMaintain() {
        return canMaintain;
    }

    public Map<String, List<String>> getTradePower() {
        return tradePower;
    }

    public Map<String, List<String>> getCountriesWithMerchantModifier() {
        return countriesWithMerchantModifier;
    }

    public Map<String, List<String>> getNodeProvinceModifier() {
        return nodeProvinceModifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradePolicy)) {
            return false;
        }

        TradePolicy ageAbility = (TradePolicy) o;

        return Objects.equals(name, ageAbility.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public String toString() {
        return name;
    }
}
