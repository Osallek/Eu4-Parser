package fr.osallek.eu4parser.model.save.gameplayoptions;

import fr.osallek.clausewitzparser.model.ClausewitzList;

public class GameplayOptions {

    private final ClausewitzList list;

    public GameplayOptions(ClausewitzList list) {
        this.list = list;
    }

    public Difficulty getDifficulty() {
        return Difficulty.ofValue(this.list.getAsInt(0));
    }

    public void setDifficulty(Difficulty difficulty) {
        this.list.set(0, difficulty.value);
    }

    public LuckyNations getLuckyNations() {
        return LuckyNations.values()[this.list.getAsInt(2)];
    }

    public boolean getAllowHotjoin() {
        return this.list.getAsInt(3) == 1;
    }

    public void setAllowHotjoin(boolean terraIncognita) {
        this.list.set(3, terraIncognita ? 1 : 0);
    }

    public boolean getAllowCoop() {
        return this.list.getAsInt(4) == 1;
    }

    public void setAllowCoop(boolean allowCoop) {
        this.list.set(4, allowCoop ? 1 : 0);
    }

    public boolean getTerraIncognita() {
        return this.list.getAsInt(7) == 1;
    }

    public void setTerraIncognita(boolean terraIncognita) {
        this.list.set(7, terraIncognita ? 1 : 0);
    }

    public boolean getOnlyHostAndObserversCanSave() {
        return this.list.getAsInt(8) == 1;
    }

    public void setOnlyHostAndObserversCanSave(boolean onlyHostAndObserversCanSave) {
        this.list.set(8, onlyHostAndObserversCanSave ? 1 : 0);
    }

    public boolean getSaveEditable() {
        return this.list.getAsInt(9) == 1;
    }

    public void setSaveEditable(boolean saveEditable) {
        this.list.set(9, saveEditable ? 1 : 0);
    }

    public boolean getLockedLedger() {
        return this.list.getAsInt(10) == 1;
    }

    public void setLockedLedger(boolean lockedLedger) {
        this.list.set(10, lockedLedger ? 1 : 0);
    }

    public boolean getDynamicProvinceNames() {
        return this.list.getAsInt(11) == 1;
    }

    public void setDynamicProvinceNames(boolean dynamicProvinceNames) {
        this.list.set(11, dynamicProvinceNames ? 1 : 0);
    }

    public CustomNationDifficulty getCustomNationDifficulty() {
        return CustomNationDifficulty.values()[this.list.getAsInt(12)];
    }

    public void setCustomNationDifficulty(CustomNationDifficulty customNationDifficulty) {
        this.list.set(12, customNationDifficulty.ordinal());
    }

    public NationSetup getNationSetup() {
        return NationSetup.values()[this.list.getAsInt(13)];
    }

    public ProvinceTaxManpower getProvinceTaxManpower() {
        return ProvinceTaxManpower.values()[this.list.getAsInt(14)];
    }

    public boolean getAddNationsToGame() {
        return this.list.getAsInt(15) == 1;
    }

    public void setAddNationsToGame(boolean addNationsToGame) {
        this.list.set(15, addNationsToGame ? 1 : 0);
    }

    public RandomNations getRandomNations() {
        return RandomNations.values()[this.list.getAsInt(16)];
    }

    public boolean getShowMonthlyTaxIncome() {
        return this.list.getAsInt(17) == 1;
    }

    public void setShowMonthlyTaxIncome(boolean showMonthlyTaxIncome) {
        this.list.set(17, showMonthlyTaxIncome ? 1 : 0);
    }

    public boolean getColorWastelands() {
        return this.list.getAsInt(18) == 1;
    }

    public void setColorWastelands(boolean colorWastelands) {
        this.list.set(18, colorWastelands ? 1 : 0);
    }

    public boolean getExclavesRegionName() {
        return this.list.getAsInt(19) == 1;
    }

    public void setExclavesRegionName(boolean exclavesRegionName) {
        this.list.set(19, exclavesRegionName ? 1 : 0);
    }

    public boolean getEnableVictoryCards() {
        return this.list.getAsInt(20) == 1;
    }

    public boolean getBlockNationRuining() {
        return this.list.getAsInt(21) == 1;
    }

    public void setBlockNationRuining(boolean blockNationRuining) {
        this.list.set(21, blockNationRuining ? 1 : 0);
    }

    public FantasyRandomNewWorld getFantasyRandomNewWorld() {
        return FantasyRandomNewWorld.values()[this.list.getAsInt(22)];
    }

    public boolean getLimitedLedger() {
        return this.list.getAsInt(23) == 1;
    }

    public void setLimitedLedger(boolean limitedLedger) {
        this.list.set(23, limitedLedger ? 1 : 0);
    }

    public boolean getUnlimitedIdeaGroups() {
        return this.list.getAsInt(24) == 1;
    }

    public void setUnlimitedIdeaGroups(boolean unlimitedIdeaGroups) {
        this.list.set(24, unlimitedIdeaGroups ? 1 : 0);
    }

    public boolean getAllowNameChange() {
        return this.list.getAsInt(25) == 1;
    }

    public void setAllowNameChange(boolean allowNameChange) {
        this.list.set(25, allowNameChange ? 1 : 0);
    }

    public boolean getOnlyHostCanPause() {
        return this.list.getAsInt(26) == 1;
    }

    public void setOnlyHostCanPause(boolean onlyHostCanPause) {
        this.list.set(23, onlyHostCanPause ? 1 : 0);
    }

    public boolean getShowLoadingScreen() {
        return this.list.getAsInt(27) == 1;
    }

    public boolean getUseAgeScoring() {
        return this.list.getAsInt(28) == 1;
    }

    public boolean getAllowTeams() {
        return this.list.getAsInt(29) == 1;
    }

    public void setAllowTeams(boolean allowTeams) {
        this.list.set(29, allowTeams ? 1 : 0);
    }

    public boolean getAllowFreeTeamCreation() {
        return this.list.getAsInt(30) == 1;
    }

    public void setAllowFreeTeamCreation(boolean allowFreeTeamCreation) {
        this.list.set(30, allowFreeTeamCreation ? 1 : 0);
    }
}
