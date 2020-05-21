package com.osallek.eu4parser.model.save;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.save.religion.MuslimRelation;
import com.osallek.eu4parser.model.save.religion.MuslimRelationSchool;
import com.osallek.eu4parser.model.save.religion.MuslimRelationValue;
import com.osallek.eu4parser.model.save.religion.Papacy;
import com.osallek.eu4parser.model.save.religion.ReformationCenter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Religion {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private List<MuslimRelation> relations;

    private Papacy papacy;

    private List<ReformationCenter> reformationCenters;

    public Religion(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;
        refreshAttributes();
    }

    public String getName() {
        return this.religionsItem.getName();
    }

    public Integer getAmountOfProvinces() {
        return this.religionsItem.getVarAsInt("amount_of_provinces");
    }

    public Date getEnable() {
        return this.religionsItem.getVarAsDate("enable");
    }

    public void setEnable(Date enable) {
        ClausewitzVariable hreHereticReligionVar = this.religionsItem.getVar("enable");

        if (hreHereticReligionVar != null) {
            hreHereticReligionVar.setValue(enable);
        } else {
            this.religionsItem.addVariable("enable", enable);
        }
    }

    void setHreHereticReligion(boolean hreHereticReligion) {
        ClausewitzVariable hreHereticReligionVar = this.religionsItem.getVar("hre_heretic_religion");

        if (hreHereticReligionVar != null) {
            hreHereticReligionVar.setValue(hreHereticReligion);
        } else {
            this.religionsItem.addVariable("hre_heretic_religion", hreHereticReligion);
        }
    }

    void setHreReligion(boolean hreReligion) {
        ClausewitzVariable hreHereticReligionVar = this.religionsItem.getVar("hre_religion");

        if (hreHereticReligionVar != null) {
            hreHereticReligionVar.setValue(hreReligion);
        } else {
            this.religionsItem.addVariable("hre_religion", hreReligion);
        }
    }

    public Boolean getHreHereticReligion() {
        return this.religionsItem.getVarAsBool("hre_heretic_religion");
    }

    public Boolean getHreReligion() {
        return this.religionsItem.getVarAsBool("hre_religion");
    }

    public Boolean getOriginalHreReligion() {
        return this.religionsItem.getVarAsBool("original_hre_religion");
    }

    public Boolean getOriginalHreHereticReligion() {
        return this.religionsItem.getVarAsBool("original_hre_heretic_religion");
    }

    public String getDefender() {
        return this.religionInstanceDataItem.getVarAsString("defender");
    }

    public Date getDefenderDate() {
        return this.religionInstanceDataItem.getVarAsDate("defender_date");
    }

    public void setDefender(String defender) {
        ClausewitzVariable defenderVar = this.religionInstanceDataItem.getVar("defender");
        ClausewitzVariable defenderDateVar = this.religionInstanceDataItem.getVar("defender_date");

        if (defenderDateVar != null) {
            if (defenderVar != null) {
                defenderVar.setValue(defender);
            } else {
                this.religionInstanceDataItem.addVariable("defender", defender);
            }
        }
    }

    public void setDefenderDate(Date defenderDate) {
        ClausewitzVariable defenderVar = this.religionInstanceDataItem.getVar("defender");
        ClausewitzVariable defenderDateVar = this.religionInstanceDataItem.getVar("defender_date");

        if (defenderVar != null) {
            if (defenderDateVar != null) {
                defenderDateVar.setValue(defenderDate);
            } else {
                this.religionInstanceDataItem.addVariable("defender_date", defenderDate);
            }
        }
    }

    public void setDefender(String defender, Date defenderDate) {
        ClausewitzVariable defenderVar = this.religionInstanceDataItem.getVar("defender");
        ClausewitzVariable defenderDateVar = this.religionInstanceDataItem.getVar("defender_date");

        if (defenderVar != null) {
            defenderVar.setValue(defender);
        } else {
            this.religionInstanceDataItem.addVariable("defender", defender);
        }

        if (defenderDateVar != null) {
            defenderDateVar.setValue(defenderDate);
        } else {
            this.religionInstanceDataItem.addVariable("defender_date", defenderDate);
        }
    }

    public List<MuslimRelation> getRelations() {
        return this.relations;
    }

    public void setRelation(MuslimRelationSchool first, MuslimRelationSchool second, MuslimRelationValue relation) {
        if (first == null || second == null || relation == null) {
            return;
        }

        this.relations.stream()
                      .filter(muslimRelation -> first.equals(muslimRelation.getFirst()) &&
                                                second.equals(muslimRelation.getSecond()))
                      .findFirst()
                      .ifPresent(muslimRelation -> muslimRelation.setRelation(relation));
    }

    public Papacy getPapacy() {
        return this.papacy;
    }

    public List<ReformationCenter> getReformationCenters() {
        return this.reformationCenters;
    }

    public void addReformationCenter(Integer provinceId) {
        if (this.religionInstanceDataItem != null) {
            ReformationCenter.addToItem(this.religionInstanceDataItem, provinceId, this.getName(),
                                        this.getName() + "_center_of_reformation");
            refreshAttributes();
        }
    }

    public void removeReformationCenter(Integer index) {
        if (this.religionInstanceDataItem != null) {
            this.religionInstanceDataItem.removeChild("reformation_center", index);
            refreshAttributes();
        }
    }

    private void refreshAttributes() {
        if (this.religionInstanceDataItem != null) {
            List<ClausewitzItem> relationsItems = this.religionInstanceDataItem.getChildren("relation");
            this.relations = relationsItems.stream().map(MuslimRelation::new).collect(Collectors.toList());

            ClausewitzItem papacyItem = this.religionInstanceDataItem.getChild("papacy");

            if (papacyItem != null) {
                this.papacy = new Papacy(papacyItem);
            }

            List<ClausewitzItem> reformationCentersItems = this.religionInstanceDataItem.getChildren("reformation_center");
            this.reformationCenters = reformationCentersItems.stream()
                                                             .map(ReformationCenter::new)
                                                             .collect(Collectors.toList());
        }
    }
}
