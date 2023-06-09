package fr.osallek.eu4parser.model.save.province;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.Advisor;
import fr.osallek.eu4parser.model.game.Culture;
import fr.osallek.eu4parser.model.game.Modifiers;
import fr.osallek.eu4parser.model.game.ModifiersUtils;
import fr.osallek.eu4parser.model.save.Id;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveReligion;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

public class SaveAdvisor {

    protected final Save save;

    protected final ClausewitzItem item;

    private final Advisor gameAdvisor;

    private Id id;

    public SaveAdvisor(ClausewitzItem item, Save save) {
        this.save = save;
        this.item = item;
        this.gameAdvisor = getType().map(s -> this.save.getGame().getAdvisor(s)).orElse(null);
        refreshAttributes();
    }

    public Save getSave() {
        return save;
    }

    public Id getId() {
        return id;
    }

    public Optional<String> getName() {
        return this.item.getVarAsString("name");
    }

    public void setName(String name) {
        this.item.setVariable("name", ClausewitzUtils.addQuotes(name));
    }

    public Advisor getGameAdvisor() {
        return gameAdvisor;
    }

    public Optional<String> getType() {
        return this.item.getVarAsString("type");
    }

    public void setType(String type) {
        this.item.setVariable("type", type);
    }

    public Optional<Integer> getSkill() {
        return this.item.getVarAsInt("skill");
    }

    public void setSkill(int skill) {
        this.item.setVariable("skill", skill);
    }

    public Optional<SaveProvince> getLocation() {
        return this.item.getVarAsInt("location").map(this.save::getProvince);
    }

    public void setLocation(SaveProvince location) {
        this.item.setVariable("location", location.getId());
    }

    public Optional<Boolean> getFemale() {
        return this.item.getVarAsBool("female");
    }

    public Optional<Culture> getCulture() {
        return this.item.getVarAsString("culture").map(s -> this.save.getGame().getCulture(s));
    }

    public Optional<String> getCultureName() {
        return this.item.getVarAsString("culture");
    }

    public void setCulture(Culture culture) {
        this.item.setVariable("culture", culture.getName());
    }

    public Optional<SaveReligion> getReligion() {
        return this.item.getVarAsString("religion").map(s -> this.save.getReligions().getReligion(s));
    }

    public Optional<String> getReligionName() {
        return this.item.getVarAsString("religion");
    }

    public void setReligion(SaveReligion religion) {
        this.item.setVariable("religion", religion.getName());
    }

    public Optional<LocalDate> getDate() {
        return this.item.getVarAsDate("date");
    }

    public void setDate(LocalDate date) {
        this.item.setVariable("date", date);
    }

    public Optional<LocalDate> getHireDate() {
        return this.item.getVarAsDate("hire_date");
    }

    public void setHireDate(LocalDate hireDate) {
        this.item.setVariable("hire_date", hireDate);
    }

    public Optional<LocalDate> getDeathDate() {
        return this.item.getVarAsDate("death_date");
    }

    public void setDeathDate(LocalDate deathDate) {
        this.item.setVariable("death_date", deathDate);
    }

    public File getImage() {
        return this.gameAdvisor.getDefaultImage();
    }

    public Modifiers getModifiers() {
        return getGameAdvisor().getSkillScaledModifier()
                               .map(modifiers -> ModifiersUtils.sumModifiers(getGameAdvisor().getModifiers(),
                                                                             ModifiersUtils.scaleModifiers(modifiers, getSkill().orElse(0))))
                               .orElse(getGameAdvisor().getModifiers());
    }

    private void refreshAttributes() {
        this.id = this.item.getChild("id").map(Id::new).orElse(null);
    }
}
