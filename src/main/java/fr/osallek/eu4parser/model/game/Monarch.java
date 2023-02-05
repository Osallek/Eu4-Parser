package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;

public class Monarch {

    protected final ClausewitzItem item;

    protected final Game game;

    protected final Country country;

    public Monarch(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        this.game = country.getGame();
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public String getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));
    }

    public Country getCountry() {
        return country;
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("adm");
    }

    public void setAdm(int adm) {
        this.item.setVariable("adm", adm);
    }

    public Integer getDip() {
        return this.item.getVarAsInt("dip");
    }

    public void setDip(int dip) {
        this.item.setVariable("dip", dip);
    }

    public Integer getMil() {
        return this.item.getVarAsInt("mil");
    }

    public void setMil(int mil) {
        this.item.setVariable("mil", mil);
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Boolean getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Culture getCulture() {
        return this.game.getCulture(getCultureName());
    }

    public String getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public Religion getReligion() {
        return this.game.getReligion(getReligionName());
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(Religion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public String getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));
    }

    public LocalDate getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);
    }

    public LocalDate getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public Personalities getPersonalities() {
        return this.item.hasChild("personalities") ? new Personalities(this.item.getChild("personalities"), this.game) : null;
    }

    public void addPersonality(RulerPersonality personality) {
        if (!this.item.hasChild("personalities")) {
            this.item.addChild("personalities");
        }

        getPersonalities().addPersonality(personality);
    }

    public void removePersonality(int index) {
        if (getPersonalities() != null) {
            getPersonalities().removePersonality(index);

            if (getPersonalities().item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public void removePersonality(RulerPersonality personality) {
        if (getPersonalities() != null) {
            getPersonalities().removePersonality(personality);

            if (getPersonalities().item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public Leader getLeader() {
        return this.item.hasChild("leader") ? new Leader(this.item.getChild("leader"), this.country) : null;
    }
}
