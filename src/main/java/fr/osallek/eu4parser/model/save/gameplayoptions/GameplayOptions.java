package fr.osallek.eu4parser.model.save.gameplayoptions;

import fr.osallek.clausewitzparser.model.ClausewitzList;

public record GameplayOptions(ClausewitzList list) {

    public Difficulty getDifficulty() {
        return Difficulty.ofValue(this.list.getAsInt(0).get());
    }

    public void setDifficulty(Difficulty difficulty) {
        this.list.set(0, difficulty.value);
    }

    public LuckyNations getLuckyNations() {
        return LuckyNations.values()[this.list.getAsInt(2).get()];
    }

    public boolean getAllowHotjoin() {
        return this.list.getAsInt(3).filter(integer -> integer == 1).isPresent();
    }

    public void setAllowHotjoin(boolean terraIncognita) {
        this.list.set(3, terraIncognita ? 1 : 0);
    }

    public boolean getAllowCoop() {
        return this.list.getAsInt(4).filter(integer -> integer == 1).isPresent();
    }

    public void setAllowCoop(boolean allowCoop) {
        this.list.set(4, allowCoop ? 1 : 0);
    }

    public boolean getTerraIncognita() {
        return this.list.getAsInt(7).filter(integer -> integer == 1).isPresent();
    }

    public void setTerraIncognita(boolean terraIncognita) {
        this.list.set(7, terraIncognita ? 1 : 0);
    }

    public boolean getOnlyHostAndObserversCanSave() {
        return this.list.getAsInt(8).filter(integer -> integer == 1).isPresent();
    }

    public void setOnlyHostAndObserversCanSave(boolean onlyHostAndObserversCanSave) {
        this.list.set(8, onlyHostAndObserversCanSave ? 1 : 0);
    }

    public boolean getSaveEditable() {
        return this.list.getAsInt(9).filter(integer -> integer == 1).isPresent();
    }

    public void setSaveEditable(boolean saveEditable) {
        this.list.set(9, saveEditable ? 1 : 0);
    }

    public boolean getLockedLedger() {
        return this.list.getAsInt(10).filter(integer -> integer == 1).isPresent();
    }

    public void setLockedLedger(boolean lockedLedger) {
        this.list.set(10, lockedLedger ? 1 : 0);
    }

    public boolean getDynamicProvinceNames() {
        return this.list.getAsInt(11).filter(integer -> integer == 1).isPresent();
    }

    public void setDynamicProvinceNames(boolean dynamicProvinceNames) {
        this.list.set(11, dynamicProvinceNames ? 1 : 0);
    }

    public CustomNationDifficulty getCustomNationDifficulty() {
        return CustomNationDifficulty.values()[this.list.getAsInt(12).get()];
    }

    public void setCustomNationDifficulty(CustomNationDifficulty customNationDifficulty) {
        this.list.set(12, customNationDifficulty.ordinal());
    }

    public NationSetup getNationSetup() {
        return NationSetup.values()[this.list.getAsInt(13).get()];
    }

    public ProvinceTaxManpower getProvinceTaxManpower() {
        return ProvinceTaxManpower.values()[this.list.getAsInt(14).get()];
    }

    public boolean getAddNationsToGame() {
        return this.list.getAsInt(15).filter(integer -> integer == 1).isPresent();
    }

    public void setAddNationsToGame(boolean addNationsToGame) {
        this.list.set(15, addNationsToGame ? 1 : 0);
    }

    public RandomNations getRandomNations() {
        return RandomNations.values()[this.list.getAsInt(16).get()];
    }

    public boolean getShowMonthlyTaxIncome() {
        return this.list.getAsInt(17).filter(integer -> integer == 1).isPresent();
    }

    public void setShowMonthlyTaxIncome(boolean showMonthlyTaxIncome) {
        this.list.set(17, showMonthlyTaxIncome ? 1 : 0);
    }

    public boolean getColorWastelands() {
        return this.list.getAsInt(18).filter(integer -> integer == 1).isPresent();
    }

    public void setColorWastelands(boolean colorWastelands) {
        this.list.set(18, colorWastelands ? 1 : 0);
    }

    public boolean getExclavesRegionName() {
        return this.list.getAsInt(19).filter(integer -> integer == 1).isPresent();
    }

    public void setExclavesRegionName(boolean exclavesRegionName) {
        this.list.set(19, exclavesRegionName ? 1 : 0);
    }

    public boolean getEnableVictoryCards() {
        return this.list.getAsInt(20).filter(integer -> integer == 1).isPresent();
    }

    public boolean getBlockNationRuining() {
        return this.list.getAsInt(21).filter(integer -> integer == 1).isPresent();
    }

    public void setBlockNationRuining(boolean blockNationRuining) {
        this.list.set(21, blockNationRuining ? 1 : 0);
    }

    public FantasyRandomNewWorld getFantasyRandomNewWorld() {
        return FantasyRandomNewWorld.values()[this.list.getAsInt(22).get()];
    }

    public boolean getLimitedLedger() {
        return this.list.getAsInt(23).filter(integer -> integer == 1).isPresent();
    }

    public void setLimitedLedger(boolean limitedLedger) {
        this.list.set(23, limitedLedger ? 1 : 0);
    }

    public boolean getUnlimitedIdeaGroups() {
        return this.list.getAsInt(24).filter(integer -> integer == 1).isPresent();
    }

    public void setUnlimitedIdeaGroups(boolean unlimitedIdeaGroups) {
        this.list.set(24, unlimitedIdeaGroups ? 1 : 0);
    }

    public boolean getAllowNameChange() {
        return this.list.getAsInt(25).filter(integer -> integer == 1).isPresent();
    }

    public void setAllowNameChange(boolean allowNameChange) {
        this.list.set(25, allowNameChange ? 1 : 0);
    }

    public boolean getOnlyHostCanPause() {
        return this.list.getAsInt(26).filter(integer -> integer == 1).isPresent();
    }

    public void setOnlyHostCanPause(boolean onlyHostCanPause) {
        this.list.set(26, onlyHostCanPause ? 1 : 0);
    }

    public boolean getShowLoadingScreen() {
        return this.list.getAsInt(27).filter(integer -> integer == 1).isPresent();
    }

    public boolean getUseAgeScoring() {
        return this.list.getAsInt(28).filter(integer -> integer == 1).isPresent();
    }

    public boolean getAllowTeams() {
        return this.list.getAsInt(29).filter(integer -> integer == 1).isPresent();
    }

    public void setAllowTeams(boolean allowTeams) {
        this.list.set(29, allowTeams ? 1 : 0);
    }

    public boolean getAllowFreeTeamCreation() {
        return this.list.getAsInt(30).filter(integer -> integer == 1).isPresent();
    }

    public void setAllowFreeTeamCreation(boolean allowFreeTeamCreation) {
        this.list.set(30, allowFreeTeamCreation ? 1 : 0);
    }
}
