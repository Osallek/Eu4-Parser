package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.condition.ConditionAnd;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

public class TradePolicy {

    private final ClausewitzItem item;

    public TradePolicy(ClausewitzItem item) {
        this.item = item;
    }

    public String getName() {
        return this.item.getName();
    }

    public void setName(String name) {
        this.item.setName(name);
    }

    public String getButtonGfx() {
        return this.item.getVarAsString("button_gfx");
    }

    public void setButtonGfx(String buttonGfx) {
        if (StringUtils.isBlank(buttonGfx)) {
            this.item.removeVariable("button_gfx");
        } else {
            this.item.setVariable("button_gfx", buttonGfx);
        }
    }

    public Boolean isUnique() {
        return this.item.getVarAsBool("unique");
    }

    public void setUnique(Boolean unique) {
        if (unique == null) {
            this.item.removeVariable("unique");
        } else {
            this.item.setVariable("unique", unique);
        }
    }

    public Boolean showAlert() {
        return this.item.getVarAsBool("show_alert");
    }

    public void setShowAlert(Boolean showAlert) {
        if (showAlert == null) {
            this.item.removeVariable("show_alert");
        } else {
            this.item.setVariable("show_alert", showAlert);
        }
    }

    public boolean isCenterOfReformation() {
        return BooleanUtils.toBoolean(this.item.getVarAsBool("center_of_reformation"));
    }

    public void setCenterOfReformation(boolean centerOfReformation) {
        this.item.setVariable("center_of_reformation", centerOfReformation);
    }

    public ConditionAnd getCanSelect() {
        ClausewitzItem child = item.getChild("can_select");
        return child == null ? null : new ConditionAnd(child);
    }

    public ConditionAnd getCanMaintain() {
        ClausewitzItem child = item.getChild("can_maintain");
        return child == null ? null : new ConditionAnd(child);
    }

    public Modifiers getTradePower() {
        return this.item.getChild("trade_power").map(Modifiers::new);
    }

    public Modifiers getCountriesWithMerchantModifier() {
        return this.item.getChild("countries_with_merchant_modifier").map(Modifiers::new);
    }

    public Modifiers getNodeProvinceModifier() {
        return this.item.getChild("node_province_modifier").map(Modifiers::new);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (!(o instanceof TradePolicy tradePolicy)) {
            return false;
        }

        return Objects.equals(getName(), tradePolicy.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public String toString() {
        return getName();
    }
}
