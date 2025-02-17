package fr.osallek.eu4parser.model.save.country;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.Game;
import fr.osallek.eu4parser.model.game.Personalities;
import fr.osallek.eu4parser.model.game.Religion;
import fr.osallek.eu4parser.model.game.RulerPersonality;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.SaveReligion;
import fr.osallek.eu4parser.model.save.counters.Counter;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Monarch {

    protected final ClausewitzItem item;

    protected final Game game;

    protected SaveCountry saveCountry;

    protected LocalDate monarchDate;

    public Monarch(ClausewitzItem item, SaveCountry saveCountry) {
        this.item = item;
        this.saveCountry = saveCountry;
        this.game = saveCountry.getSave().getGame();
    }

    public Monarch(ClausewitzItem item, SaveCountry saveCountry, LocalDate date) {
        this.item = item;
        this.saveCountry = saveCountry;
        this.game = saveCountry.getSave().getGame();
        this.monarchDate = date;
    }

    public Id getId() {
        ClausewitzItem idItem = this.item.getChild("id");

        return idItem != null ? new Id(idItem) : null;
    }

    public String getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setName(name);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setName(name);
        }
    }

    public LocalDate getMonarchDate() {
        return monarchDate;
    }

    public SaveCountry getCountry() {
        return this.saveCountry;
    }

    public Integer getAdm() {
        return this.item.getVarAsInt("ADM");
    }

    public void setAdm(int adm) {
        this.item.setVariable("ADM", adm);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setAdm(adm);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setAdm(adm);
        }
    }

    public Integer getDip() {
        return this.item.getVarAsInt("DIP");
    }

    public void setDip(int dip) {
        this.item.setVariable("DIP", dip);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setDip(dip);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setDip(dip);
        }
    }

    public Integer getMil() {
        return this.item.getVarAsInt("MIL");
    }

    public void setMil(int mil) {
        this.item.setVariable("MIL", mil);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setMil(mil);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setMil(mil);
        }
    }

    public Boolean getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Boolean getRegent() {
        return this.item.getVarAsBool("regent");
    }

    public Culture getCulture() {
        return this.game.getCulture(this.item.getVarAsString("culture"));
    }

    public String getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setCulture(culture);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setCulture(culture);
        }
    }

    public Religion getReligion() {
        return this.game.getReligion(this.item.getVarAsString("religion"));
    }

    public String getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setReligion(religion);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setReligion(religion);
        }
    }

    public String getDynasty() {
        return this.item.getVarAsString("dynasty");
    }

    public void setDynasty(String dynasty) {
        this.item.setVariable("dynasty", ClausewitzUtils.addQuotes(dynasty));

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setDynasty(dynasty);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setDynasty(dynasty);
        }
    }

    public LocalDate getBirthDate() {
        return this.item.getVarAsDate("birth_date");
    }

    public void setBirthDate(LocalDate birthDate) {
        this.item.setVariable("birth_date", birthDate);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setBirthDate(birthDate);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setBirthDate(birthDate);
        }
    }

    public LocalDate getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setDeathDate(deathDate);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setDeathDate(deathDate);
        }
    }

    public Boolean getSucceeded() {
        return this.item.getVarAsBool("succeeded");
    }

    public String getMonarchName() {
        return this.item.getVarAsString("monarch_name");
    }

    public void setMonarchName(String monarchName) {
        this.item.setVariable("monarch_name", ClausewitzUtils.addQuotes(monarchName));

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).setMonarchName(monarchName);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).setMonarchName(monarchName);
        }
    }

    public Personalities getPersonalities() {
        ClausewitzItem personalitiesItem = this.item.getChild("personalities");

        return personalitiesItem != null ? new Personalities(personalitiesItem, this.game) : null;
    }

    public void addPersonality(RulerPersonality personality) {
        Personalities personalities = getPersonalities();
        if (personalities == null) {
            this.item.addChild("personalities");
        }

        personalities.addPersonality(personality);

        if (this.item.getName().endsWith("_heir")) {
            this.saveCountry.getHistory().getHeir(this.getId().getId()).addPersonality(personality);
        } else if (this.item.getName().endsWith("_consort")) {
            this.saveCountry.getHistory().getQueen(this.getId().getId()).addPersonality(personality);
        }
    }

    public void removePersonality(int index) {
        Personalities personalities = getPersonalities();
        if (personalities != null) {
            personalities.removePersonality(index);

            if (this.item.getName().endsWith("_heir")) {
                this.saveCountry.getHistory().getHeir(this.getId().getId()).removePersonality(index);
            } else if (this.item.getName().endsWith("_consort")) {
                this.saveCountry.getHistory().getQueen(this.getId().getId()).removePersonality(index);
            }

            if (personalities.item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public void removePersonality(RulerPersonality personality) {
        Personalities personalities = getPersonalities();
        if (personalities != null) {
            personalities.removePersonality(personality);

            if (this.item.getName().endsWith("_heir")) {
                this.saveCountry.getHistory().getHeir(this.getId().getId()).removePersonality(personality);
            } else if (this.item.getName().endsWith("_consort")) {
                this.saveCountry.getHistory().getQueen(this.getId().getId()).removePersonality(personality);
            }

            if (personalities.item().getAllOrdered().isEmpty()) {
                this.item.removeChild("personalities");
            }
        }
    }

    public HasDeclaredWar getHasDeclaredWar() {
        ClausewitzItem hasDeclaredWarItem = this.item.getChild("has_declared_war");

        return hasDeclaredWarItem != null ? new HasDeclaredWar(hasDeclaredWarItem) : null;
    }

    public Id getLeaderId() {
        ClausewitzItem leaderIdChild = this.item.getChild("leader_id");

        return leaderIdChild != null ? new Id(leaderIdChild) : null;
    }

    public Leader getLeader() {
        ClausewitzItem leaderChild = this.item.getChild("leader");

        return leaderChild != null ? new Leader(leaderChild, this.saveCountry) : null;
    }

    public ListOfDates getRulerFlags() {
        ClausewitzItem rulerFlagsItem = this.item.getChild("ruler_flags");

        return rulerFlagsItem != null ? new ListOfDates(rulerFlagsItem) : null;
    }

    public SaveCountry getWho() {
        return this.saveCountry.getSave().getCountry(ClausewitzUtils.removeQuotes(this.item.getVarAsString("who")));
    }

    public void setWho(SaveCountry who) {
        this.item.setVariable("who", ClausewitzUtils.addQuotes(who.getTag()));
    }

    public Double getAmount() {
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

    public Boolean getHistory() {
        return this.item.getVarAsBool("history");
    }

    public static ClausewitzItem addToItem(ClausewitzItem parent, SaveCountry country) {
        ClausewitzItem toItem = new ClausewitzItem(parent, "monarch", parent.getOrder() + 1);
        toItem.addVariable("country", ClausewitzUtils.addQuotes(country.getTag()));
        toItem.addVariable("culture", ClausewitzUtils.removeQuotes(country.getPrimaryCultureName()));
        toItem.addVariable("religion", ClausewitzUtils.removeQuotes(country.getReligionName()));
        toItem.addVariable("birth_date", country.getSave().getDate().minusYears(16));
        toItem.addVariable("DIP", RandomUtils.insecure()
                                             .randomInt(country.getSave().getGame().getMonarchMinSkill(),
                                                        country.getSave().getGame().getMonarchMaxSkill() + 1));
        toItem.addVariable("ADM", RandomUtils.insecure()
                                             .randomInt(country.getSave().getGame().getMonarchMinSkill(),
                                                        country.getSave().getGame().getMonarchMaxSkill() + 1));
        toItem.addVariable("MIL", RandomUtils.insecure()
                                             .randomInt(country.getSave().getGame().getMonarchMinSkill(),
                                                        country.getSave().getGame().getMonarchMaxSkill() + 1));

        List<String> names = new ArrayList<>(country.getSave().getGame().getCountry(country.getTag()).getMonarchNames().keySet());
        Collections.shuffle(names);
        toItem.addVariable("name", ClausewitzUtils.addQuotes(StringUtils.substringBefore(names.getFirst(), "#") + " I"));

        List<String> dynasties = new ArrayList<>(country.getPrimaryCulture().getDynastyNames());
        Collections.shuffle(dynasties);
        toItem.addVariable("dynasty", ClausewitzUtils.addQuotes(dynasties.getFirst()));

        Id.addToItem(toItem, country.getSave().getIdCounters().getAndIncrement(Counter.MONARCH), 48);

        return toItem;
    }
}
