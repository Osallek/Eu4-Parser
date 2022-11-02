package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.GreatProject;
import fr.osallek.eu4parser.model.game.GreatProjectTier;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;

import java.time.LocalDate;
import java.util.Optional;

public class SaveGreatProject {

    private final Save save;

    private final ClausewitzItem item;

    public SaveGreatProject(Save save, ClausewitzItem item) {
        this.save = save;
        this.item = item;
    }

    public String getName() {
        return ClausewitzUtils.removeQuotes(this.item.getName());
    }

    public void setName(String name) {
        this.item.setName(ClausewitzUtils.addQuotes(name));
    }

    public Integer getProvinceId() {
        return this.item.getVarAsInt("province");
    }

    public void setProvinceId(Integer provinceId) {
        this.item.setVariable("province", provinceId);
    }

    public SaveProvince getProvince() {
        return Optional.ofNullable(getProvinceId()).map(this.save::getProvince).orElse(null);
    }

    public void setProvince(SaveProvince province) {
        setProvinceId(Optional.ofNullable(province).map(SaveProvince::getId).orElse(null));
    }

    public Integer getDevelopmentTier() {
        return this.item.getVarAsInt("development_tier");
    }

    public void setDevelopmentTier(Integer developmentTier) {
        this.item.setVariable("development_tier", developmentTier);
    }

    public LocalDate getDateBuilt() {
        return this.item.getVarAsDate("date_built");
    }

    public void setDateBuilt(LocalDate developmentTier) {
        this.item.setVariable("date_built", developmentTier);
    }

    public LocalDate getDateDestroyed() {
        return this.item.getVarAsDate("date_destroyed");
    }

    public void setDateDestroyed(LocalDate developmentTier) {
        this.item.setVariable("date_destroyed", developmentTier);
    }

    public GreatProject getGreatProject() {
        return this.save.getGame().getGreatProject(getName());
    }

    public GreatProjectTier getTier() {
        GreatProject greatProject = getGreatProject();

        if (getDevelopmentTier() == null || greatProject == null || CollectionUtils.isEmpty(greatProject.getTiers())) {
            return null;
        }

        return IterableUtils.get(greatProject.getTiers(), getDevelopmentTier());
    }
}
