package fr.osallek.eu4parser.model.save;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.model.game.DefenderOfFaith;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.ReligionGroup;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import fr.osallek.eu4parser.model.save.province.SaveProvince;
import fr.osallek.eu4parser.model.save.religion.MuslimRelation;
import fr.osallek.eu4parser.model.save.religion.MuslimRelationSchool;
import fr.osallek.eu4parser.model.save.religion.MuslimRelationValue;
import fr.osallek.eu4parser.model.save.religion.ReformationCenter;
import fr.osallek.eu4parser.model.save.religion.SavePapacy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaveReligion {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private final Save save;

    private final Religion gameReligion;

    public SaveReligion(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem, Save save) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;
        this.save = save;
        this.gameReligion = this.save.getGame().getReligion(getName());
    }

    public String getName() {
        return this.religionsItem == null ? this.religionInstanceDataItem.getName() : this.religionsItem.getName();
    }

    public Religion getGameReligion() {
        return gameReligion;
    }

    public boolean hasSpecialAttribute() {
        return hasDate() || hasDefenderOfFaith() || hasPapacy() || hasRelations();
    }

    public boolean hasDate() {
        return this.gameReligion != null && this.gameReligion.getDate() != null;
    }

    public boolean hasDefenderOfFaith() {
        return this.gameReligion != null && this.gameReligion.getReligionGroup().defenderOfFaith();
    }

    public boolean hasPapacy() {
        return getPapacy() != null;
    }

    public boolean hasRelations() {
        return getRelations() != null;
    }

    public ReligionGroup getReligionGroup() {
        return this.gameReligion == null ? null : this.gameReligion.getReligionGroup();
    }

    public Integer getAmountOfProvinces() {
        return this.religionsItem.getVarAsInt("amount_of_provinces");
    }

    public LocalDate getEnable() {
        return this.religionsItem.getVarAsDate("enable");
    }

    public void setEnable(LocalDate enable) {
        this.religionsItem.setVariable("enable", enable);
    }

    public List<SaveCountry> getInLeague() {
        return this.religionsItem.getVarsAsStrings("league").stream().map(ClausewitzUtils::removeQuotes).map(this.save::getCountry).toList();
    }

    public void addToLeague(SaveCountry country) {
        if (!getInLeague().contains(country)) {
            this.religionsItem.addVariable("league", ClausewitzUtils.addQuotes(country.getTag()));
        }
    }

    public void removeFromLeague(SaveCountry country) {
        this.religionsItem.removeVariable("league", ClausewitzUtils.addQuotes(country.getTag()));
    }

    void setHreHereticReligion(boolean hreHereticReligion) {
        this.religionsItem.setVariable("hre_heretic_religion", hreHereticReligion);
    }

    void setHreReligion(boolean hreReligion) {
        this.religionsItem.setVariable("hre_religion", hreReligion);
    }

    public Boolean isHreHereticReligion() {
        return this.religionsItem.getVarAsBool("hre_heretic_religion");
    }

    public Boolean isHreReligion() {
        return this.religionsItem.getVarAsBool("hre_religion");
    }

    public Boolean getOriginalHreReligion() {
        return this.religionsItem.getVarAsBool("original_hre_religion");
    }

    public Boolean getOriginalHreHereticReligion() {
        return this.religionsItem.getVarAsBool("original_hre_heretic_religion");
    }

    public SaveCountry getDefender() {
        String defenderTag = this.religionInstanceDataItem.getVarAsString("defender");

        return defenderTag == null ? null : this.save.getCountry(ClausewitzUtils.removeQuotes(defenderTag));
    }

    public LocalDate getDefenderDate() {
        return this.religionInstanceDataItem.getVarAsDate("defender_date");
    }

    public void setDefender(SaveCountry defender) {
        if (defender == null || Eu4Utils.DEFAULT_TAG.equals(defender.getTag())) {
            this.religionInstanceDataItem.removeVariable("defender");
            this.religionInstanceDataItem.removeVariable("defender_date");
        } else {
            LocalDate defenderDate = this.religionInstanceDataItem.getVarAsDate("defender_date");

            if (defenderDate != null) {
                this.religionInstanceDataItem.setVariable("defender", ClausewitzUtils.addQuotes(defender.getTag()));
            }
        }
    }

    public void setDefenderDate(LocalDate defenderDate) {
        String defender = this.religionInstanceDataItem.getVarAsString("defender");

        if (defender != null) {
            this.religionInstanceDataItem.setVariable("defender_date", defenderDate);
        }
    }

    public void setDefender(SaveCountry defender, LocalDate defenderDate) {
        if (defender == null || Eu4Utils.DEFAULT_TAG.equals(defender.getTag())) {
            this.religionInstanceDataItem.removeVariable("defender");
            this.religionInstanceDataItem.removeVariable("defender_date");
        } else {
            this.religionInstanceDataItem.setVariable("defender", ClausewitzUtils.addQuotes(defender.getTag()));
            this.religionInstanceDataItem.setVariable("defender_date", defenderDate);
        }
    }

    public DefenderOfFaith getDefenderOfFaith() {
        SaveCountry defender = getDefender();

        if (defender == null) {
            return null;
        }

        int nbCountries = (int) this.save.getCountries()
                                         .values()
                                         .stream()
                                         .filter(SaveCountry::isAlive)
                                         .filter(country -> defender.getReligion().equals(country.getReligion()))
                                         .count();

        return this.save.getGame()
                        .getDefenderOfFaith()
                        .stream()
                        .filter(defenderOfFaith -> defenderOfFaith.isInRange(nbCountries))
                        .findFirst()
                        .orElse(this.save.getGame().getDefenderOfFaith().iterator().next());
    }

    public List<MuslimRelation> getRelations() {
        if (this.religionInstanceDataItem == null) {
            return new ArrayList<>();
        }

        List<ClausewitzItem> relationsItems = this.religionInstanceDataItem.getChildren("relation");
        return relationsItems.stream().map(MuslimRelation::new).toList();
    }

    public void setRelation(MuslimRelationSchool first, MuslimRelationSchool second, MuslimRelationValue relation) {
        if (first == null || second == null || relation == null) {
            return;
        }

        getRelations().stream()
                      .filter(muslimRelation -> first.equals(muslimRelation.getFirst()) && second.equals(muslimRelation.getSecond()))
                      .findFirst()
                      .ifPresent(muslimRelation -> muslimRelation.setRelation(relation));
    }

    public SavePapacy getPapacy() {
        if (this.religionInstanceDataItem == null) {
            return null;
        }

        ClausewitzItem papacyItem = this.religionInstanceDataItem.getChild("papacy");

        if (papacyItem != null) {
            return new SavePapacy(papacyItem, this, this.save);
        }

        return null;
    }

    public List<ReformationCenter> getReformationCenters() {
        if (this.religionInstanceDataItem == null) {
            return new ArrayList<>();
        }

        List<ClausewitzItem> reformationCentersItems = this.religionInstanceDataItem.getChildren("reformation_center");
        return reformationCentersItems.stream().map(item -> new ReformationCenter(this.save, item)).toList();
    }

    public void addReformationCenter(SaveProvince saveProvince) {
        if (this.religionInstanceDataItem != null) {
            ReformationCenter.addToItem(this.religionInstanceDataItem, saveProvince.getId(), this);
            saveProvince.setCenterOfReligion(true);
        }
    }

    public void removeReformationCenter(ReformationCenter reformationCenter) {
        if (this.religionInstanceDataItem != null) {
            Integer index = null;

            List<ReformationCenter> reformationCenters = getReformationCenters();
            for (int i = 0; i < reformationCenters.size(); i++) {
                if (reformationCenters.get(i).getProvince().equals(reformationCenter.getProvince())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                this.religionInstanceDataItem.removeChild("reformation_center", index);
                reformationCenter.getProvince().setCenterOfReligion(false);
            }
        }
    }

    public Save getSave() {
        return save;
    }
}
