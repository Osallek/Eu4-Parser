package fr.osallek.eu4parser.model.save.religion;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.Optional;

public record ReformationCenter(Save save, ClausewitzItem item) {

    public Optional<SaveProvince> getProvince() {
        return this.item.getVarAsInt("province_id").map(this.save::getProvince);
    }

    public void setProvinceId(SaveProvince province) {
        this.item.setVariable("province_id", province.getId());
    }

    public Optional<Integer> getCurrentlyConverting() {
        return this.item.getVarAsInt("any_target_province");
    }

    public void setCurrentlyConverting(Integer provinceId) {
        this.item.setVariable("any_target_province", provinceId);
    }

    public Optional<Double> getConversionProgress() {
        return this.item.getVarAsDouble("missionary_progress");
    }

    public void setConversionProgress(Double conversionProgress) {
        this.item.setVariable("missionary_progress", conversionProgress);
    }

    public Optional<SaveReligion> getReligion() {
        return this.item.getVarAsString("religion").map(s -> this.save.getReligions().getReligion(s));
    }

    public Optional<Double> getMaxSpeed() {
        return this.item.getVarAsDouble("max_speed");
    }

    public Optional<String> getType() {
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
