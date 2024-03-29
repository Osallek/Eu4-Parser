package fr.osallek.eu4parser.model.save.institutions;

import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.eu4parser.model.game.Institution;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.province.SaveProvince;

import java.util.List;

public record Institutions(ClausewitzList origins, ClausewitzList available, Save save) {

    public int getNbInstitutions() {
        return Math.min(this.origins.size(), this.available.size());
    }

    public boolean isAvailable(int institution) {
        return this.available.getAsInt(institution) == 1;
    }

    public boolean isAvailable(Institution institution) {
        return isAvailable(institution.getIndex());
    }

    public long getNbAvailable() {
        return this.available.getValuesAsInt().stream().filter(integer -> integer == 1).count();
    }

    public List<SaveProvince> getOrigins() {
        return this.origins.getValuesAsInt().stream().map(this.save::getProvince).toList();
    }

    public SaveProvince getOrigin(int institution) {
        return this.save.getProvince(this.origins.getAsInt(institution));
    }

    public SaveProvince getOrigin(Institution institution) {
        return getOrigin(institution.getIndex());
    }

    public void changeOrigin(int institution, SaveProvince province) {
        if (isAvailable(institution)) {
            this.origins.set(institution, province.getId());
        }
    }

    public void changeOrigin(Institution institution, SaveProvince province) {
        changeOrigin(institution.getIndex(), province);
    }

    public void availableIn(int institution, SaveProvince province) {
        this.available.set(institution, 1);
        this.origins.set(institution, province == null ? 0 : province.getId());
    }

    public void availableIn(Institution institution, SaveProvince province) {
        availableIn(institution.getIndex(), province);
    }

    public void disable(int institution) {
        this.available.set(institution, 0);
        this.origins.set(institution, 0);
    }

    public void disable(Institution institution) {
        disable(institution.getIndex());
    }
}
