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
import java.util.List;
import java.util.Optional;

public class SaveReligion {

    private final ClausewitzItem religionsItem;

    private final ClausewitzItem religionInstanceDataItem;

    private final Save save;

    private final Religion gameReligion;

    private List<MuslimRelation> relations;

    private SavePapacy papacy;

    private List<ReformationCenter> reformationCenters;

    public SaveReligion(ClausewitzItem religionsItem, ClausewitzItem religionInstanceDataItem, Save save) {
        this.religionsItem = religionsItem;
        this.religionInstanceDataItem = religionInstanceDataItem;
        this.save = save;
        this.gameReligion = this.save.getGame().getReligion(getName());
        refreshAttributes();
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
        return this.gameReligion != null && this.gameReligion.getDate().isPresent();
    }

    public boolean hasDefenderOfFaith() {
        return this.gameReligion != null && this.gameReligion.getReligionGroup().defenderOfFaith();
    }

    public boolean hasPapacy() {
        return this.papacy != null;
    }

    public boolean hasRelations() {
        return this.relations != null;
    }

    public ReligionGroup getReligionGroup() {
        return this.gameReligion == null ? null : this.gameReligion.getReligionGroup();
    }

    public Optional<Integer> getAmountOfProvinces() {
        return this.religionsItem.getVarAsInt("amount_of_provinces");
    }

    public Optional<LocalDate> getEnable() {
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

    public Optional<Boolean> isHreHereticReligion() {
        return this.religionsItem.getVarAsBool("hre_heretic_religion");
    }

    public Optional<Boolean> isHreReligion() {
        return this.religionsItem.getVarAsBool("hre_religion");
    }

    public Optional<Boolean> getOriginalHreReligion() {
        return this.religionsItem.getVarAsBool("original_hre_religion");
    }

    public Optional<Boolean> getOriginalHreHereticReligion() {
        return this.religionsItem.getVarAsBool("original_hre_heretic_religion");
    }

    public Optional<SaveCountry> getDefender() {
        return this.religionInstanceDataItem.getVarAsString("defender").map(ClausewitzUtils::removeQuotes).map(this.save::getCountry);
    }

    public Optional<LocalDate> getDefenderDate() {
        return this.religionInstanceDataItem.getVarAsDate("defender_date");
    }

    public void setDefender(SaveCountry defender) {
        if (defender == null || Eu4Utils.DEFAULT_TAG.equals(defender.getTag())) {
            this.religionInstanceDataItem.removeVariable("defender");
            this.religionInstanceDataItem.removeVariable("defender_date");
        } else {
            this.religionInstanceDataItem.getVarAsDate("defender_date")
                                         .ifPresent(localDate -> this.religionInstanceDataItem.setVariable("defender",
                                                                                                           ClausewitzUtils.addQuotes(defender.getTag())));
        }
    }

    public void setDefenderDate(LocalDate defenderDate) {
        this.religionInstanceDataItem.getVarAsString("defender").ifPresent(s -> this.religionInstanceDataItem.setVariable("defender_date", defenderDate));
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

    public Optional<DefenderOfFaith> getDefenderOfFaith() {
        Optional<SaveCountry> defender = getDefender();

        if (defender.isEmpty()) {
            return Optional.empty();
        }

        int nbCountries = (int) this.save.getCountries()
                                         .values()
                                         .stream()
                                         .filter(SaveCountry::isAlive)
                                         .filter(country -> defender.get().getReligion().equals(country.getReligion()))
                                         .count();

        return Optional.ofNullable(this.save.getGame()
                                            .getDefenderOfFaith()
                                            .stream()
                                            .filter(defenderOfFaith -> defenderOfFaith.isInRange(nbCountries))
                                            .findFirst()
                                            .orElse(this.save.getGame().getDefenderOfFaith().iterator().next()));
    }

    public List<MuslimRelation> getRelations() {
        return this.relations;
    }

    public void setRelation(MuslimRelationSchool first, MuslimRelationSchool second, MuslimRelationValue relation) {
        if (first == null || second == null || relation == null) {
            return;
        }

        this.relations.stream()
                      .filter(muslimRelation -> muslimRelation.getFirst().isPresent() && muslimRelation.getSecond().isPresent())
                      .filter(muslimRelation -> first.equals(muslimRelation.getFirst().get()) && second.equals(muslimRelation.getSecond().get()))
                      .findFirst()
                      .ifPresent(muslimRelation -> muslimRelation.setRelation(relation));
    }

    public SavePapacy getPapacy() {
        return this.papacy;
    }

    public List<ReformationCenter> getReformationCenters() {
        return this.reformationCenters;
    }

    public void addReformationCenter(SaveProvince saveProvince) {
        if (this.religionInstanceDataItem != null) {
            ReformationCenter.addToItem(this.religionInstanceDataItem, saveProvince.getId(), this);
            saveProvince.setCenterOfReligion(true);
            refreshAttributes();
        }
    }

    public void removeReformationCenter(ReformationCenter reformationCenter) {
        if (this.religionInstanceDataItem != null) {
            Integer index = null;

            for (int i = 0; i < this.reformationCenters.size(); i++) {
                if (this.reformationCenters.get(i).getProvince().equals(reformationCenter.getProvince())) {
                    index = i;
                    break;
                }
            }

            if (index != null) {
                this.religionInstanceDataItem.removeChild("reformation_center", index);
                reformationCenter.getProvince().ifPresent(p -> p.setCenterOfReligion(false));
                refreshAttributes();
            }
        }
    }

    public Save getSave() {
        return save;
    }

    private void refreshAttributes() {
        if (this.religionInstanceDataItem != null) {
            this.relations = this.religionInstanceDataItem.getChildren("relation").stream().map(MuslimRelation::new).toList();
            this.papacy = this.religionInstanceDataItem.getChild("papacy").map(item -> new SavePapacy(item, this, this.save)).orElse(null);
            this.reformationCenters = this.religionInstanceDataItem.getChildren("reformation_center")
                                                                   .stream()
                                                                   .map(item -> new ReformationCenter(this.save, item))
                                                                   .toList();
        }
    }
}
