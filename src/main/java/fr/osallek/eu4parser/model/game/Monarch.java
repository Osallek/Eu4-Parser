package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;

import java.time.LocalDate;
import java.util.Optional;

public class Monarch {

    protected final ClausewitzItem item;

    protected final Game game;

    protected final Country country;

    public Monarch(ClausewitzItem item, Country country) {
        this.item = item;
        this.country = country;
        this.game = country.getGame();
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Optional<String> getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));
    }

    public Country getCountry() {
        return country;
    }

    public Optional<Integer> getAdm() {
        return this.item.getVarAsInt("adm");
    }

    public void setAdm(int adm) {
        this.item.setVariable("adm", adm);
    }

    public Optional<Integer> getDip() {
        return this.item.getVarAsInt("dip");
    }

    public void setDip(int dip) {
        this.item.setVariable("dip", dip);
    }

    public Optional<Integer> getMil() {
        return this.item.getVarAsInt("mil");
    }

    public void setMil(int mil) {
        this.item.setVariable("mil", mil);
    }

    public Optional<Boolean> getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Optional<Boolean> getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Optional<Culture> getCulture() {
        return getCultureName().map(this.game::getCulture);
    }

    public Optional<String> getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public Optional<Religion> getReligion() {
        return getReligionName().map(this.game::getReligion);
    }

    public Optional<String> getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(Religion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public Optional<String> getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));
    }

    public Optional<LocalDate> getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);
    }

    public Optional<LocalDate> getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public Optional<Personalities> getPersonalities() {
        return this.item.getChild("personalities").map(i -> new Personalities(i, this.game));
    }

    public void addPersonality(RulerPersonality personality) {
        if (!this.item.hasChild("personalities")) {
            this.item.addChild("personalities");
        }

        getPersonalities().ifPresent(p -> p.addPersonality(personality));
    }

    public void removePersonality(int index) {
        Optional<Personalities> personalities = getPersonalities();
        if (personalities.isPresent()) {
            personalities.get().removePersonality(index);

            if (personalities.get().item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public void removePersonality(RulerPersonality personality) {
        Optional<Personalities> personalities = getPersonalities();
        if (personalities.isPresent()) {
            personalities.get().removePersonality(personality);

            if (personalities.get().item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public Optional<Leader> getLeader() {
        return this.item.getChild("leader").map(i -> new Leader(i, this.country));
    }
}
