package com.osallek.eu4parser.model.religion;

import com.osallek.clausewitzparser.common.Utils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;

public class ReformationCenter {

    private final ClausewitzItem item;

    public ReformationCenter(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getProvinceId() {
        return this.item.getVarAsInt("province_id");
    }

    public void setProvinceId(Integer provinceId) {
        ClausewitzVariable provinceVar = this.item.getVar("province_id");

        if (provinceVar != null) {
            provinceVar.setValue(provinceId);
        }
    }

    public Integer getCurrentlyConverting() {
        return this.item.getVarAsInt("any_target_province");
    }

    public void setCurrentlyConverting(Integer provinceId) {
        ClausewitzVariable provinceVar = this.item.getVar("any_target_province");

        if (provinceVar != null) {
            provinceVar.setValue(provinceId);
        } else {
            this.item.addVariable("any_target_province", provinceId);
        }
    }

    public Double getConversionProgress() {
        return this.item.getVarAsDouble("missionary_progress");
    }

    public void setConversionProgress(Double conversionProgress) {
        ClausewitzVariable provinceVar = this.item.getVar("missionary_progress");

        if (provinceVar != null) {
            provinceVar.setValue(conversionProgress);
        } else {
            this.item.addVariable("missionary_progress", conversionProgress);
        }
    }

    public String getReligion() {
        return this.item.getVarAsString("protestant");
    }

    public Double getMaxSpeed() {
        return this.item.getVarAsDouble("max_speed");
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Integer provinceId, String religion, String type) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "reformation_center", parent.getOrder() + 1);
        toItem.addVariable("province_id", provinceId);
        toItem.addVariable("religion", religion);
        toItem.addVariable("type", Utils.hasQuotes(type) ? type : Utils.addQuotes(type));

        parent.addChild(toItem);

        return toItem;
    }
}
