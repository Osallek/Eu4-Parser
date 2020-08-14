package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;

public class ReformationCenter {

    private final ClausewitzItem item;

    public ReformationCenter(ClausewitzItem item) {
        this.item = item;
    }

    public Integer getProvinceId() {
        return this.item.getVarAsInt("province_id");
    }

    public void setProvinceId(Integer provinceId) {
        this.item.setVariable("province_id", provinceId);
    }

    public Integer getCurrentlyConverting() {
        return this.item.getVarAsInt("any_target_province");
    }

    public void setCurrentlyConverting(Integer provinceId) {
        this.item.setVariable("any_target_province", provinceId);
    }

    public Double getConversionProgress() {
        return this.item.getVarAsDouble("missionary_progress");
    }

    public void setConversionProgress(Double conversionProgress) {
        this.item.setVariable("missionary_progress", conversionProgress);
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
        toItem.addVariable("type", ClausewitzUtils.hasQuotes(type) ? type : ClausewitzUtils.addQuotes(type));

        parent.addChild(toItem);

        return toItem;
    }
}
