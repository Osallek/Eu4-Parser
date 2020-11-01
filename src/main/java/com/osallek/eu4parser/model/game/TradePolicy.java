package com.osallek.eu4parser.model.game;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Objects;

public class TradePolicy {

    private final String name;

    private String localizedName;

    private final String buttonGfx;

    private final boolean unique;

    private final boolean showAlert;

    private final boolean centerOfReformation;

    private final Condition canSelect;

    private final Condition canMaintain;

    private final Modifiers tradePower;

    private final Modifiers countriesWithMerchantModifier;

    private final Modifiers nodeProvinceModifier;


    public TradePolicy(ClausewitzItem item) {
        this.name = item.getName();
        this.tradePower = new Modifiers(item.getChild("trade_power"));
        this.countriesWithMerchantModifier = new Modifiers(item.getChild("countries_with_merchant_modifier"));
        this.nodeProvinceModifier = new Modifiers(item.getChild("node_province_modifier"));
        this.buttonGfx = item.getVarAsString("button_gfx");
        this.unique = BooleanUtils.toBoolean(item.getVarAsBool("unique"));
        this.showAlert = BooleanUtils.toBoolean(item.getVarAsBool("show_alert"));
        this.centerOfReformation = BooleanUtils.toBoolean(item.getVarAsBool("center_of_reformation"));

        ClausewitzItem child = item.getChild("can_select");
        this.canSelect = child == null ? null : new Condition(child);

        child = item.getChild("can_maintain");
        this.canMaintain = child == null ? null : new Condition(child);
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

    public Modifiers getTradePower() {
        return tradePower;
    }

    public Modifiers getCountriesWithMerchantModifier() {
        return countriesWithMerchantModifier;
    }

    public Modifiers getNodeProvinceModifier() {
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
