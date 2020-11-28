package com.osallek.eu4parser.model.save.religion;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.game.Game;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.SaveReligion;
import com.osallek.eu4parser.model.save.province.SaveProvince;

public class ReformationCenter {

    private final Save save;

    private final ClausewitzItem item;

    public ReformationCenter(Save save, ClausewitzItem item) {
        this.save = save;
        this.item = item;
    }

    public SaveProvince getProvince() {
        return this.save.getProvince(this.item.getVarAsInt("province_id"));
    }

    public void setProvinceId(SaveProvince province) {
        this.item.setVariable("province_id", province.getId());
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

    public SaveReligion getReligion() {
        return this.save.getReligions().getReligion(this.item.getVarAsString("religion"));
    }

    public Double getMaxSpeed() {
        return this.item.getVarAsDouble("max_speed");
    }

    public String getType() {
        return this.item.getVarAsString("type");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, Integer provinceId, SaveReligion religion) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "reformation_center", parent.getOrder() + 1);
        toItem.addVariable("province_id", provinceId);
        toItem.addVariable("religion", religion.getName());
        toItem.addVariable("type", ClausewitzUtils.addQuotes(religion.getName() + "_center_of_reformation"));

        parent.addChild(toItem);

        return toItem;
    }
}
