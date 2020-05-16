package com.osallek.eu4parser.model;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.clausewitzparser.model.ClausewitzList;
import com.osallek.clausewitzparser.model.ClausewitzVariable;
import com.osallek.eu4parser.model.changeprices.ChangePrices;
import com.osallek.eu4parser.model.counters.IdCounters;
import com.osallek.eu4parser.model.country.Country;
import com.osallek.eu4parser.model.empire.CelestialEmpire;
import com.osallek.eu4parser.model.empire.Hre;
import com.osallek.eu4parser.model.empire.HreReligionStatus;
import com.osallek.eu4parser.model.events.FiredEvents;
import com.osallek.eu4parser.model.events.PendingEvents;
import com.osallek.eu4parser.model.gameplayoptions.GameplayOptions;
import com.osallek.eu4parser.model.institutions.Institutions;
import com.osallek.eu4parser.model.province.Province;
import com.osallek.eu4parser.model.religion.Religions;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class Save {

    //Todo in countries: map_area_data, trade, rebel_faction(only edit, not remove(in provinces too)), great_power, provinces
    //Todo Teams

    private static final Logger LOGGER = Logger.getLogger(Save.class.getName());

    private final ClausewitzItem item;

    private GameplayOptions gameplayOptions;

    private IdCounters idCounters;

    private Institutions institutions;

    private ChangePrices changePrices;

    private Hre hre;

    private CelestialEmpire celestialEmpire;

    private Religions religions;

    private FiredEvents firedEvents;

    private PendingEvents pendingEvents;

    private Map<Integer, Province> provinces;

    private Map<String, Country> countries;

    public Save(ClausewitzItem item) {
        this.item = item;
        refreshAttributes();
    }

    public GameplayOptions getGameplayOptions() {
        return this.gameplayOptions;
    }

    public IdCounters getIdCounters() {
        return idCounters;
    }

    public Integer getUnitIdCounter() {
        return this.item.getVarAsInt("unit");
    }

    /**
     * Used for units and armies
     */
    public int getAndIncrementUnitIdCounter() {
        ClausewitzVariable var = this.item.getVar("unit");

        if (var == null) {
            this.item.addVariable("unit", 2);

            return 1;
        } else {
            int value = var.getAsInt();
            var.setValue(value + 1);

            return value;
        }
    }

    public Date getStartDate() {
        return this.item.getVarAsDate("start_date");
    }

    public void setStartDate(Date startDate) {
        this.item.setVariable("start_date", startDate);
    }

    public Institutions getInstitutions() {
        return institutions;
    }

    public ChangePrices getChangePrices() {
        return changePrices;
    }

    public Hre getHre() {
        return hre;
    }

    public CelestialEmpire getCelestialEmpire() {
        return celestialEmpire;
    }

    public Boolean getHreLeaguesActive() {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_leagues_status");

        if (hreLeaguesStatus != null) {
            return hreLeaguesStatus.getAsInt() == 1;
        }

        return null;
    }

    public void setHreLeaguesActive(boolean hreLeaguesActive) {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_leagues_status");

        if (hreLeaguesStatus != null) {
            hreLeaguesStatus.setValue(hreLeaguesActive ? 1 : 0);
        } else {
            this.item.addVariable("hre_leagues_status", hreLeaguesActive ? 1 : 0);
        }
    }

    public HreReligionStatus getHreReligionStatus() {
        ClausewitzVariable hreLeaguesStatus = this.item.getVar("hre_religion_status");

        if (hreLeaguesStatus != null) {
            return HreReligionStatus.values()[hreLeaguesStatus.getAsInt()];
        }

        return null;
    }

    public void setHreReligionStatus(HreReligionStatus hreReligionStatus) {
        ClausewitzVariable hreReligionStatusVar = this.item.getVar("hre_religion_status");

        if (hreReligionStatusVar != null) {
            hreReligionStatusVar.setValue(hreReligionStatus.ordinal());
        } else {
            this.item.addVariable("hre_religion_status", hreReligionStatus.ordinal());
        }

        if (hreReligionStatus == HreReligionStatus.CATHOLIC) {
            if (this.religions != null) {
                Religion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(false);
                    catholic.setHreReligion(true);
                }

                Religion protestant = this.religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(true);
                    protestant.setHreReligion(false);
                }
            }
        } else if (hreReligionStatus == HreReligionStatus.PROTESTANT) {
            if (this.religions != null) {
                Religion catholic = this.religions.getReligion("catholic");
                if (catholic != null) {
                    catholic.setHreHereticReligion(true);
                    catholic.setHreReligion(false);
                }

                Religion protestant = this.religions.getReligion("protestant");
                if (protestant != null && protestant.getEnable() != null) {
                    protestant.setHreHereticReligion(false);
                    protestant.setHreReligion(true);
                }
            }
        }
    }

    public Religions getReligions() {
        return religions;
    }

    public FiredEvents getFiredEvents() {
        return firedEvents;
    }

    public PendingEvents getPendingEvents() {
        return pendingEvents;
    }

    public Province getProvince(int id) {
        return this.provinces.get(id);
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    public Country getCountry(String tag) {
        return this.countries.get(tag);
    }

    public Map<String, Country> getCountries() {
        return countries;
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties,
                                                  boolean quickPeace, boolean moveTraders, boolean takeDecisions,
                                                  boolean embraceInstitutions, boolean developProvinces,
                                                  boolean disbandUnits, boolean changeFleetMissions,
                                                  boolean sendMissionaries, boolean convertCultures,
                                                  boolean promoteCultures, boolean braindead) {
        setAiPrefsForNotConfiguredPlayers(startWars, keepAlliances, keepTreaties, quickPeace, moveTraders,
                                          takeDecisions, embraceInstitutions, developProvinces, disbandUnits,
                                          changeFleetMissions, sendMissionaries, convertCultures, promoteCultures,
                                          braindead, -1);
    }

    public void setAiPrefsForNotConfiguredPlayers(boolean startWars, boolean keepAlliances, boolean keepTreaties,
                                                  boolean quickPeace, boolean moveTraders, boolean takeDecisions,
                                                  boolean embraceInstitutions, boolean developProvinces,
                                                  boolean disbandUnits, boolean changeFleetMissions,
                                                  boolean sendMissionaries, boolean convertCultures,
                                                  boolean promoteCultures, boolean braindead, int timeout) {
        this.countries.values()
                      .stream()
                      .filter(country -> country.getPlayerAiPrefsCommand() == null
                                         && Boolean.TRUE.equals(country.isHuman()))
                      .forEach(country -> country.setPlayerAiPrefsCommand(startWars, keepAlliances, keepTreaties,
                                                                          quickPeace, moveTraders, takeDecisions,
                                                                          embraceInstitutions, developProvinces,
                                                                          disbandUnits, changeFleetMissions,
                                                                          sendMissionaries, convertCultures,
                                                                          promoteCultures, braindead, timeout));
    }

    private void refreshAttributes() {
        ClausewitzItem gameplaySettings = this.item.getChild("gameplaysettings");

        if (gameplaySettings != null) {
            ClausewitzList gameplayOptionsList = gameplaySettings.getList("setgameplayoptions");

            if (gameplayOptionsList != null && !gameplayOptionsList.isEmpty()) {
                this.gameplayOptions = new GameplayOptions(gameplayOptionsList);
            }
        }

        ClausewitzList idCountersList = this.item.getList("id_counters");

        if (idCountersList != null) {
            this.idCounters = new IdCounters(idCountersList);
        }

        ClausewitzList institutionOrigins = this.item.getList("institution_origin");
        ClausewitzList institutionAvailable = this.item.getList("institutions");

        if (institutionOrigins != null && institutionAvailable != null) {
            this.institutions = new Institutions(institutionOrigins, institutionAvailable);
        }

        ClausewitzItem changePricesItem = this.item.getChild("change_price");

        if (changePricesItem != null) {
            this.changePrices = new ChangePrices(changePricesItem);
        }

        ClausewitzItem hreItem = this.item.getChild("empire");

        if (hreItem != null) {
            this.hre = new Hre(hreItem);
        }

        ClausewitzItem celestialEmpireItem = this.item.getChild("celestial_empire");

        if (celestialEmpireItem != null) {
            this.celestialEmpire = new CelestialEmpire(celestialEmpireItem);
        }

        ClausewitzItem religionsItem = this.item.getChild("religions");
        ClausewitzItem religionInstantDateItem = this.item.getChild("religion_instance_data");

        if (religionsItem != null || religionInstantDateItem != null) {
            this.religions = new Religions(religionsItem, religionInstantDateItem);
        }

        ClausewitzItem firedEventsItem = this.item.getChild("fired_events");

        if (firedEventsItem != null) {
            this.firedEvents = new FiredEvents(firedEventsItem);
        }

        ClausewitzItem pendingEventsItem = this.item.getChild("pending_events");

        if (pendingEventsItem != null) {
            this.pendingEvents = new PendingEvents(pendingEventsItem);
        }

        ClausewitzItem countriesItem = this.item.getChild("countries");

        if (countriesItem != null) {
            this.countries = countriesItem.getChildren()
                                          .stream()
                                          .map(countryItem -> new Country(countryItem, this))
                                          .collect(Collectors.toMap(Country::getTag, Function.identity(), (x, y) -> y, LinkedHashMap::new));
        }

        ClausewitzItem provincesItems = this.item.getChild("provinces");

        if (provincesItems != null) {
            this.provinces = provincesItems.getChildren()
                                           .stream()
                                           .map(provinceItem -> new Province(provinceItem, this))
                                           .collect(Collectors.toMap(Province::getId, Function.identity(), (x, y) -> y, LinkedHashMap::new));
        }
    }
}
