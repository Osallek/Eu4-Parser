package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.Personalities;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.SaveReligion;

import java.time.LocalDate;
import java.util.Optional;

public class Monarch {

    protected final ClausewitzItem item;

    protected SaveCountry saveCountry;

    protected LocalDate monarchDate;
    protected Personalities personalities;

    protected Leader leader;

    public Monarch(ClausewitzItem item, SaveCountry saveCountry) {
        this.item = item;
        this.saveCountry = saveCountry;
    }

    public Monarch(ClausewitzItem item, SaveCountry saveCountry, LocalDate date) {
        this.item = item;
        this.saveCountry = saveCountry;
        this.monarchDate = date;
    }

    public Optional<Id> getId() {
        return this.item.getChild("id").map(Id::new);
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setName(name));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setName(name));
        }
    }

    public LocalDate getMonarchDate() {
        return monarchDate;
    }

    public SaveCountry getCountry() {
        return this.saveCountry;
    }

    public Optional<Integer> getAdm() {
        return this.item.getVarAsInt("ADM");
    }

    public void setAdm(int adm) {
        this.item.setVariable("ADM", adm);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setAdm(adm));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setAdm(adm));
        }
    }

    public Optional<Integer> getDip() {
        return this.item.getVarAsInt("DIP");
    }

    public void setDip(int dip) {
        this.item.setVariable("DIP", dip);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setDip(dip));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setDip(dip));
        }
    }

    public Optional<Integer> getMil() {
        return this.item.getVarAsInt("MIL");
    }

    public void setMil(int mil) {
        this.item.setVariable("MIL", mil);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setMil(mil));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setMil(mil));
        }
    }

    public Optional<Boolean> getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Optional<Boolean> getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Optional<Culture> getCulture() {
        return this.item.getVarAsString("culture").map(s -> this.saveCountry.getSave().getGame().getCulture(s));
    }

    public Optional<String> getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setCulture(culture));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setCulture(culture));
        }
    }

    public Optional<Religion> getReligion() {
        return this.item.getVarAsString("religion").map(s -> this.saveCountry.getSave().getGame().getReligion(s));
    }

    public Optional<String> getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setReligion(religion));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setReligion(religion));
        }
    }

    public Optional<String> getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setDynasty(dynasty));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setDynasty(dynasty));
        }
    }

    public Optional<LocalDate> getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setBirthDate(birthDate));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setBirthDate(birthDate));
        }
    }

    public Optional<LocalDate> getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setDeathDate(deathDate));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setDeathDate(deathDate));
        }
    }

    public Optional<Boolean> getSucceeded() {
        return this.item.getVarAsBool("succeeded");
    }

    public Optional<String> getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).setMonarchName(monarchName));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).setMonarchName(monarchName));
        }
    }

    public Optional<Personalities> getPersonalities() {
        return this.item.getChild("personalities").map(i -> new Personalities(i, this.saveCountry.getSave().getGame()));
    }

    public void addPersonality(RulerPersonality personality) {
        if (this.personalities == null) {
            this.item.addChild("personalities");
        }

        this.personalities.addPersonality(personality);

        if (this.item.getName().endsWith("_heir")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).addPersonality(personality));
        } else if (this.item.getName().endsWith("_consort")) {
            getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).addPersonality(personality));
        }
    }

    public void removePersonality(int index) {
        if (this.personalities != null) {
            this.personalities.removePersonality(index);

            if (this.item.getName().endsWith("_heir")) {
                getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).removePersonality(index));
            } else if (this.item.getName().endsWith("_consort")) {
                getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).removePersonality(index));
            }

            if (this.personalities.item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public void removePersonality(RulerPersonality personality) {
        if (this.personalities != null) {
            this.personalities.removePersonality(personality);

            if (this.item.getName().endsWith("_heir")) {
                getId().ifPresent(id -> this.saveCountry.getHistory().getHeir(id.getId()).removePersonality(personality));
            } else if (this.item.getName().endsWith("_consort")) {
                getId().ifPresent(id -> this.saveCountry.getHistory().getQueen(id.getId()).removePersonality(personality));
            }

            if (this.personalities.item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public Optional<HasDeclaredWar> getHasDeclaredWar() {
        return this.item.getChild("has_declared_war").map(HasDeclaredWar::new);
    }

    public Optional<Id> getLeaderId() {
        return this.item.getChild("leader_id").map(Id::new);
    }

    public Optional<Leader> getLeader() {
        return this.item.getChild("leader").map(i -> new Leader(i, this.saveCountry));
    }

    public Optional<ListOfDates> getRulerFlags() {
        return this.item.getChild("ruler_flags").map(ListOfDates::new);
    }

    public Optional<SaveCountry> getWho() {
        return this.item.getVarAsString("who").map(s -> this.saveCountry.getSave().getCountry(s));
    }

    public void setWho(SaveCountry who) {
        this.item.setVariable("who", ClausewitzUtils.addQuotes(who.getTag()));
    }

    public Optional<Double> getAmount() {
        return this.item.getVarAsDouble("amount");
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            amount = 0;
        } else if (amount > 100) {
            amount = 100;
        }

        this.item.setVariable("amount", amount);
    }

    public Optional<Boolean> getHistory() {
        return this.item.getVarAsBool("history");
    }
}
