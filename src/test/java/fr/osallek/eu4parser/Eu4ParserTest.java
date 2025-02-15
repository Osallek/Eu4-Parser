package fr.osallek.eu4parser;

import fr.osallek.eu4parser.model.save.ListOfDates;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.SaveTeam;
import fr.osallek.eu4parser.model.save.country.SaveCountry;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

class Eu4ParserTest {

    static final Path RESOURCE_FOLDER = Path.of("src", "test", "resources");

    @Test
    void testParseModded() throws IOException {
        Configurator.setLevel("fr.osallek", Level.DEBUG);
        Save save = Eu4Parser.loadSave(RESOURCE_FOLDER.resolve("modded.eu4"));

        Assertions.assertNotNull(save);
        Assertions.assertEquals(LocalDate.of(2087, 7, 1), save.getDate());

        String player = save.getPlayer();
        Assertions.assertNotNull(player);
        Assertions.assertEquals("\"JBB\"", player);

        Assertions.assertEquals("\"9ead8f58e6ff8c440a1c0e03e0ad9392\"", save.getChecksum());

        List<String> mods = save.getModEnabled();
        Assertions.assertNotNull(mods);
        Assertions.assertEquals(1, mods.size());
        Assertions.assertTrue(mods.contains("\"mod/ugc_3018294589.mod\""));
        Assertions.assertFalse(mods.contains("\"mod/random_mod_name\""));

        List<SaveTeam> teams = save.getTeams();
        Assertions.assertNotNull(teams);
        Assertions.assertTrue(teams.isEmpty());

        SaveCountry country = save.getPlayedCountry();
        Assertions.assertNotNull(country);
        Assertions.assertTrue(country.isHuman());
        Assertions.assertEquals("american_government", country.getGovernmentName().getName());
        Assertions.assertEquals(LocalDate.of(2050, 8, 13), country.getLastFocusMove());
    }

    @Test
    void testParseFlat() throws IOException {
        Configurator.setLevel("fr.osallek", Level.DEBUG);
        Save save = Eu4Parser.loadSave(RESOURCE_FOLDER.resolve("flat.eu4"));

        Assertions.assertNotNull(save);
        Assertions.assertEquals(LocalDate.of(1645, 8, 2), save.getDate());

        String player = save.getPlayer();
        Assertions.assertNotNull(player);
        Assertions.assertEquals("\"ITA\"", player);

        Assertions.assertEquals("\"46560cc06fadf80495ea41558bc589b2\"", save.getChecksum());

        List<String> dlcs = save.getDlcEnabled();
        Assertions.assertNotNull(dlcs);
        Assertions.assertEquals(21, dlcs.size());
        Assertions.assertTrue(dlcs.contains("\"Conquest of Paradise\""));
        Assertions.assertFalse(dlcs.contains("\"Mare Nostrummm\""));

        ListOfDates flags = save.getFlags();
        Assertions.assertNotNull(flags);

        Map<String, LocalDate> all = flags.getAll();
        Assertions.assertNotNull(all);
        Assertions.assertTrue(all.containsKey("wih_barbaracilli_flag"));

        Map<String, SaveCountry> countries = save.getCountries();
        Assertions.assertNotNull(countries);
        Assertions.assertEquals(1377, countries.size());

        SaveCountry country = countries.get("ITA");
        Assertions.assertNotNull(countries);
        Assertions.assertTrue(country.isHuman());
        Assertions.assertEquals("default_monarchy", country.getGovernmentName().getName());
        Assertions.assertNull(country.getLastFocusMove());

        for (SaveCountry value : countries.values()) {
            Assertions.assertTrue(value.getLandForceLimit() >= 0);
        }
    }

    @Test
    void testParseCompressed() throws IOException {
        Configurator.setLevel("fr.osallek", Level.DEBUG);
        Save save = Eu4Parser.loadSave(RESOURCE_FOLDER.resolve("compressed.eu4"));

        Assertions.assertNotNull(save);
        Assertions.assertEquals(LocalDate.of(1515, 9, 8), save.getDate());

        String player = save.getPlayer();
        Assertions.assertNotNull(player);
        Assertions.assertEquals("\"TUN\"", player);

        Assertions.assertEquals("\"1e8dfdda7ef327f0a7a4bf3a0a39a391\"", save.getChecksum());

        List<String> dlcs = save.getDlcEnabled();
        Assertions.assertNotNull(dlcs);
        Assertions.assertEquals(22, dlcs.size());
        Assertions.assertTrue(dlcs.contains("\"Conquest of Paradise\""));
        Assertions.assertFalse(dlcs.contains("\"Mare Nostrummm\""));

        ListOfDates flags = save.getFlags();
        Assertions.assertNotNull(flags);

        Map<String, LocalDate> all = flags.getAll();
        Assertions.assertNotNull(all);
        Assertions.assertTrue(all.containsKey("wih_barbaracilli_flag"));

        Map<String, SaveCountry> countries = save.getCountries();
        Assertions.assertNotNull(countries);
        Assertions.assertEquals(1380, countries.size());

        SaveCountry country = countries.get("TUN");
        Assertions.assertNotNull(countries);
        Assertions.assertTrue(country.isHuman());
        Assertions.assertEquals("muslim_monarchy", country.getGovernmentName().getName());
        Assertions.assertNotNull(country.getLastFocusMove());

        for (SaveCountry value : countries.values()) {
            Assertions.assertTrue(value.getLandForceLimit() >= 0);
        }
    }

    @Test
    void testGetDateFlat() throws IOException {
        Optional<LocalDate> date = Eu4Parser.getDate(RESOURCE_FOLDER.resolve("flat.eu4"), null);

        Assertions.assertTrue(date.isPresent());
        Assertions.assertEquals(LocalDate.of(1645, 8, 2), date.get());
    }

    @Test
    void testGetDateCompressed() throws IOException {
        Optional<LocalDate> date = Eu4Parser.getDate(RESOURCE_FOLDER.resolve("compressed.eu4"), null);

        Assertions.assertTrue(date.isPresent());
        Assertions.assertEquals(LocalDate.of(1515, 9, 8), date.get());
    }

    @Test
    void testParseCompressedFrance() throws IOException {
        Save save = Eu4Parser.loadSave(RESOURCE_FOLDER.resolve("mp_France.eu4"));

        Assertions.assertNotNull(save);
        Assertions.assertEquals(LocalDate.of(1540, 4, 29), save.getDate());

        String player = save.getPlayer();
        Assertions.assertNotNull(player);
        Assertions.assertEquals("\"FRA\"", player);

        Assertions.assertEquals(149, (int) save.getCountry("FRA").getLandForceLimit());
        Assertions.assertEquals(165, (int) save.getCountry("SPA").getLandForceLimit());
    }
}
