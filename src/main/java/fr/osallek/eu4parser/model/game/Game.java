package fr.osallek.eu4parser.model.game;

import fr.osallek.clausewitzparser.common.ClausewitzUtils;
import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.clausewitzparser.model.ClausewitzList;
import fr.osallek.clausewitzparser.parser.ClausewitzParser;
import fr.osallek.clausewitzparser.parser.LuaParser;
import fr.osallek.eu4parser.Eu4Parser;
import fr.osallek.eu4parser.common.Eu4MapUtils;
import fr.osallek.eu4parser.common.Eu4Utils;
import fr.osallek.eu4parser.common.ImageReader;
import fr.osallek.eu4parser.common.ModNotFoundException;
import fr.osallek.eu4parser.common.NumbersUtils;
import fr.osallek.eu4parser.common.TreeNode;
import fr.osallek.eu4parser.model.LauncherSettings;
import fr.osallek.eu4parser.model.Mod;
import fr.osallek.eu4parser.model.ModType;
import fr.osallek.eu4parser.model.Power;
import fr.osallek.eu4parser.model.game.localisation.Eu4Language;
import fr.osallek.eu4parser.model.game.localisation.Localisation;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Game {

    public static final int NB_PARTS = 72;

    private static final Logger LOGGER = LoggerFactory.getLogger(Game.class);

    private final LauncherSettings launcherSettings;

    private List<Mod> mods;

    private final File provincesImage;

    private int provinceImageWidth;

    private int provinceImageHeight;

    private TreeNode<FileNode> filesNode;

    private List<String> graphicalCultures;

    private Map<Integer, Province> provinces;

    private Map<Integer, Province> provincesByColor;

    private ClausewitzItem terrainItem;

    private Map<String, TerrainCategory> terrainCategories;

    private Map<String, Terrain> terrains;

    private Map<String, Tree> trees;

    private Map<String, ProvinceList> continents;

    private ClausewitzItem climateItem;

    private ProvinceList impassableClimate;

    private Map<String, ProvinceList> climates;

    private ProvinceList fakeClimate;

    private Map<String, ProvinceList> winters;

    private ProvinceList fakeWinter;

    private Map<String, ProvinceList> monsoons;

    private ProvinceList fakeMonsoon;

    private Map<String, CultureGroup> cultureGroups;

    private Map<String, ReligionGroup> religionGroups;

    private Map<String, Institution> institutions;

    private Map<String, TradeGood> tradeGoods;

    private Map<String, TradeNode> tradeNodes;

    private Map<String, Building> buildings;

    private Set<String> nativeLocalisations;

    private Map<String, Map<Eu4Language, Localisation>> localisations;

    private Map<String, SpriteType> spriteTypes;

    private Map<String, ImperialReform> imperialReforms;

    private Map<String, Decree> decrees;

    private Map<String, GoldenBull> goldenBulls;

    private Map<String, Event> events;

    private Map<String, Government> governments;

    private SortedMap<Integer, GovernmentRank> governmentRanks;

    private Map<String, GovernmentName> governmentNames;

    private Map<String, GovernmentReform> governmentReforms;

    private Map<String, Unit> units;

    private Map<String, Area> areas;

    private Map<String, Advisor> advisors;

    private Map<String, IdeaGroup> ideaGroups;

    private Set<String> ideas;

    private Map<String, CasusBelli> casusBelli;

    private Map<String, ColonialRegion> colonialRegions;

    private Map<String, TradeCompany> tradeCompanies;

    private Map<String, Region> regions;

    private Map<String, SuperRegion> superRegions;

    private Map<String, TechGroup> techGroups;

    private Map<String, SubjectType> subjectTypes;

    private Map<String, SubjectTypeUpgrade> subjectTypeUpgrades;

    private Map<String, FetishistCult> fetishistCults;

    private Map<String, ChurchAspect> churchAspects;

    private Map<String, MissionsTree> missionsTrees;

    private Map<String, Mission> missions;

    private int maxMissionsSlots;

    private Map<String, EstatePrivilege> estatePrivileges;

    private Map<String, Estate> estates;

    private Map<Power, List<Technology>> technologies;

    private SortedSet<ProfessionalismModifier> professionalismModifiers;

    private Map<String, Map<String, Map<String, Define>>> defines;

    private Map<String, Map<String, Map<String, Define>>> veryEasyDefines;

    private Map<String, Map<String, Map<String, Define>>> easyDefines;

    private Map<String, Map<String, Map<String, Define>>> hardDefines;

    private Map<String, Map<String, Map<String, Define>>> veryHardDefines;

    private Map<String, RulerPersonality> rulerPersonalities;

    private Map<String, LeaderPersonality> leaderPersonalities;

    private Map<String, Investment> investments;

    private Map<String, Policy> policies;

    private Map<String, Hegemon> hegemons;

    private Map<String, Faction> factions;

    private Map<String, Age> ages;

    private SortedMap<String, DefenderOfFaith> defenderOfFaith;

    private SortedMap<String, CenterOfTrade> centersOfTrade;

    private Map<String, Fervor> fervors;

    private Map<String, GreatProject> greatProjects;

    private Map<String, HolyOrder> holyOrders;

    private SortedMap<String, Isolationism> isolationisms;

    private Map<String, NativeAdvancements> nativeAdvancements;

    private Map<String, NavalDoctrine> navalDoctrines;

    private Map<String, ParliamentIssue> parliamentIssues;

    private Map<String, PersonalDeity> personalDeities;

    private Map<String, ReligiousReforms> religiousReforms;

    private Map<String, CrownLandBonus> crownLandBonuses;

    private Map<String, StateEdict> stateEdicts;

    private Map<String, TradePolicy> tradePolicies;

    private Map<String, StaticModifier> staticModifiers;

    private Map<String, EventModifier> eventModifiers;

    private Map<String, TriggeredModifier> provinceTriggeredModifiers;

    private Map<String, TriggeredModifier> triggeredModifiers;

    private Map<String, Country> countries;

    private Map<String, Bookmark> bookmarks;

    private Map<LocalDate, HreEmperor> hreEmperors;

    private Map<LocalDate, CelestialEmperor> celestialEmperors;

    private Map<Province, Map<Polygon, Boolean>> borders = null;

    public Game(Path gameFolderPath) throws IOException {
        this(gameFolderPath, (LauncherSettings) null);
    }

    public Game(Path gameFolderPath, LauncherSettings launcherSettings) throws IOException {
        this(gameFolderPath, launcherSettings, null);
    }

    public Game(Path gameFolderPath, List<String> modEnabled) throws IOException {
        this(gameFolderPath, modEnabled, () -> {});
    }

    public Game(Path gameFolderPath, LauncherSettings launcherSettings, List<String> modEnabled) throws IOException {
        this(gameFolderPath, null, modEnabled, () -> {});
    }

    public Game(Path gameFolderPath, List<String> modEnabled, Runnable runnable) throws IOException {
        this(gameFolderPath, null, modEnabled, runnable);
    }

    public Game(Path gameFolderPath, LauncherSettings launcherSettings, List<String> modEnabled, Runnable runnable) throws IOException {
        this.launcherSettings = Objects.requireNonNullElse(launcherSettings, Eu4Parser.loadSettings(gameFolderPath));

        readMods(modEnabled);
        runnable.run();

        this.provincesImage = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "provinces.bmp");

        CountDownLatch countDownLatch = new CountDownLatch(NB_PARTS);

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readProvincesDefinition();

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readProvinces();
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readBorders();
                    } catch (IOException e) {
                        LOGGER.error(e.getMessage(), e);
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readDefaultMap();
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readClimate();
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readContinents();
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readPositions();
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });

                Eu4Utils.POOL_EXECUTOR.submit(() -> {
                    try {
                        readProvinceHistory();
                    } finally {
                        countDownLatch.countDown();
                        runnable.run();
                    }
                });
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                loadDefines();
            } catch (IOException e) {
                LOGGER.error(e.getMessage(), e);
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                loadNativeLocalisations();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                loadLocalisations();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGraphicalCultures();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });


        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readSpriteTypes();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readEstates();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readFactions();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTradeGoods();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readBuildings();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readAreas();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readRegions();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readSuperRegions();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readCultures();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readReligion();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readInstitutions();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTradeNodes();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readImperialReforms();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readDecrees();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGoldenBulls();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readEvents();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGovernmentRanks();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGovernmentNames();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGovernmentReforms();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGovernments();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readUnits();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTechnologies();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readAdvisors();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readIdeaGroups();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readCasusBelli();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTradeCompanies();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readSubjectTypes();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readSubjectTypeUpgrades();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readFetishistCults();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readChurchAspects();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readMissionsTrees();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readRulerPersonalities();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readLeaderPersonalities();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readProfessionalismModifiers();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readStaticModifiers();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readInvestments();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readPolicies();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readHegemons();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readAges();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readDefenderOfFaith();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readCentersOfTrade();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readFervors();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readGreatProjects();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readHolyOrders();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readIsolationism();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readNativeAdvancements();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readNavalDoctrine();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readParliamentIssue();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readPersonalDeities();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readReligiousReforms();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readCrownLandBonuses();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readStateEdicts();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTradePolicies();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readEventModifiers();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readProvinceTriggeredModifiers();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readTriggeredModifiers();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readCountry();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readColonialRegions();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readDiplomacy();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                readBookmarks();
            } finally {
                countDownLatch.countDown();
                runnable.run();
            }
        });

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while waiting for game files reading : {}", e.getMessage(), e);
            Eu4Utils.POOL_EXECUTOR.shutdownNow();
            Thread.currentThread().interrupt();
        }

        this.continents.values().forEach(continent -> continent.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setContinent(continent)));
        this.areas.values().forEach(area -> area.getProvinces().forEach(provinceId -> this.getProvince(provinceId).setArea(area)));
        this.regions.values().stream().filter(region -> region.getAreas() != null).forEach(region -> region.getAreas().forEach(area -> area.setRegion(region)));
        this.superRegions.values()
                         .stream()
                         .filter(superRegion -> superRegion.getRegions() != null)
                         .forEach(superRegion -> superRegion.getRegions().forEach(region -> region.setSuperRegion(superRegion)));
    }

    public void convertImages(Path destFolder, Path... relativePaths) {
        List<FileNode> paths = new ArrayList<>();

        for (Path relativePath : relativePaths) {
            paths.addAll(getFileNodesList(relativePath, fileNode -> Files.isRegularFile(fileNode.getPath())));
        }

        CountDownLatch countDownLatch = new CountDownLatch(paths.size());

        for (FileNode fileNode : paths) {
            Eu4Utils.POOL_EXECUTOR.submit(() -> {
                try {
                    convertImage(destFolder, fileNode.getRelativePath().getParent(), fileNode.getPath());
                } finally {
                    countDownLatch.countDown();
                }
            });
        }

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while waiting for image conversion : {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    public static Path convertImage(Path destFolder, Path destPath, Path file) {
        return convertImage(destFolder, destPath, null, file);
    }

    public static Path convertImage(Path destFolder, Path destPath, String destName, Path file) {
        try {
            String fileName = file.getFileName().toString();
            String destFileName = StringUtils.isBlank(destName) ? FilenameUtils.removeExtension(fileName) : destName;
            Path finalPath = null;

            String extension = FilenameUtils.getExtension(fileName).toLowerCase();

            if ("tga".equals(extension) || "dds".equals(extension)) {
                finalPath = destFolder.resolve(destPath).resolve(destFileName + ".png");
                File destFile = finalPath.toFile();
                FileUtils.forceMkdirParent(destFile);
                ImageIO.write(ImageReader.convertFileToImage(file.toFile()), "png", destFile);
                Eu4Utils.optimizePng(destFile.getAbsoluteFile().toPath(), destFile.getAbsoluteFile().toPath());
            } else if ("png".equals(extension) || "jpg".equals(extension) || "jpeg".equals(extension)) {
                finalPath = destFolder.resolve(destFileName + "." + FilenameUtils.getExtension(fileName));
                FileUtils.copyFile(file.toFile(), finalPath.toFile());
            } else {
                LOGGER.warn("Unknown: {}", fileName);
            }

            return finalPath == null ? null : destFolder.relativize(finalPath);
        } catch (Exception e) {
            LOGGER.error("An error occurred while converting image {}: {}", file, e.getMessage(), e);
            return null;
        }
    }

    private TreeNode<FileNode> getTreeNode(String relativePath) {
        return getTreeNode(Path.of(relativePath));
    }

    private TreeNode<FileNode> getTreeNode(Path relativePath) {
        return this.filesNode.getRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath()));
    }

    private FileNode getFileNode(String relativePath) {
        return getFileNode(Path.of(relativePath));
    }

    private FileNode getFileNode(Path relativePath) {
        return this.filesNode.getDataRecursive(fileNode -> relativePath.equals(fileNode.getRelativePath()));
    }

    @SafeVarargs
    public final Stream<FileNode> getFileNodes(String relativePath, Predicate<FileNode>... predicates) {
        return getFileNodes(Path.of(relativePath), predicates);
    }

    @SafeVarargs
    public final Stream<FileNode> getFileNodes(Path relativePath, Predicate<FileNode>... predicates) {
        TreeNode<FileNode> treeNode = getTreeNode(relativePath);
        return treeNode == null ? Stream.empty() : treeNode.getLeaves(predicates).stream().map(TreeNode::getData);
    }

    @SafeVarargs
    public final List<FileNode> getFileNodesList(String relativePath, Predicate<FileNode>... predicates) {
        return getFileNodesList(Path.of(relativePath), predicates);
    }

    @SafeVarargs
    public final List<FileNode> getFileNodesList(Path relativePath, Predicate<FileNode>... predicates) {
        return getFileNodes(relativePath, predicates).toList();
    }

    @SafeVarargs
    public final Stream<Path> getPaths(String relativePath, Predicate<FileNode>... predicates) {
        return getPaths(Path.of(relativePath), predicates);
    }

    @SafeVarargs
    public final Stream<Path> getPaths(Path relativePath, Predicate<FileNode>... predicates) {
        Stream<FileNode> treeNode = getFileNodes(relativePath, predicates);
        return treeNode == null ? Stream.empty() : treeNode.map(FileNode::getPath);
    }

    @SafeVarargs
    public final List<Path> getPathsList(String relativePath, Predicate<FileNode>... predicates) {
        return getPathsList(Path.of(relativePath), predicates);
    }

    @SafeVarargs
    public final List<Path> getPathsList(Path relativePath, Predicate<FileNode>... predicates) {
        return getPaths(relativePath, predicates).toList();
    }

    public Path getAbsolutePath(String relativePath) {
        FileNode node = getFileNode(relativePath);
        return node == null ? null : node.getPath();
    }

    public Path getAbsolutePath(Path relativePath) {
        FileNode node = getFileNode(relativePath);
        return node == null ? null : node.getPath();
    }

    public File getAbsoluteFile(String relativePath) {
        return getAbsoluteFile(Path.of(relativePath));
    }

    public File getAbsoluteFile(Path relativePath) {
        Path path = getAbsolutePath(relativePath);
        return path == null ? null : path.toFile();
    }

    public LauncherSettings getLauncherSettings() {
        return launcherSettings;
    }

    public List<Mod> getMods() {
        return mods;
    }

    public File getProvincesImage() {
        return this.provincesImage;
    }

    public int getProvinceImageWidth() {
        return provinceImageWidth;
    }

    public int getProvinceImageHeight() {
        return provinceImageHeight;
    }

    public File getResourcesImage() {
        return getSpriteTypeImageFile("GFX_resource_icon");
    }

    public File getReligionsImage() {
        return getSpriteTypeImageFile("GFX_icon_religion");
    }

    public File getEstatesImage() {
        return getSpriteTypeImageFile("GFX_estates_icons_colour-stroke");
    }

    public File getNormalCursorImage() {
        return getAbsoluteFile(Eu4Utils.GFX_FOLDER_PATH + File.separator + "cursors" + File.separator + "normal.png");
    }

    public File getSelectedCursorImage() {
        return getAbsoluteFile(Eu4Utils.GFX_FOLDER_PATH + File.separator + "cursors" + File.separator + "selected.png");
    }

    public File getGoldImage() {
        return getSpriteTypeImageFile("GFX_icon_gold");
    }

    public File getCountryFlagImage(Country country) {
        return country == null ? null : getAbsoluteFile(country.getFlagPath("tga"));
    }

    public File getBuildingFlagImage(Building building) {
        return building == null ? null : getSpriteTypeImageFile(building.getSpriteName());
    }

    public File getAdvisorFlagImage(Building building) {
        return building == null ? null : getSpriteTypeImageFile(building.getSpriteName());
    }

    public Map<Integer, Province> getProvinces() {
        return provinces;
    }

    public Province getProvince(int i) {
        return this.provinces.get(i);
    }

    public Map<Integer, Province> getProvincesByColor() {
        return provincesByColor;
    }

    public List<ProvinceList> getContinents() {
        return new ArrayList<>(this.continents.values());
    }

    public ProvinceList getContinent(String name) {
        return this.continents.get(name);
    }

    public ProvinceList getContinent(int i) {
        return new ArrayList<>(this.continents.values()).get(i);
    }

    public List<ProvinceList> getClimates() {
        return new ArrayList<>(this.climates.values());
    }

    public ProvinceList getClimate(String name) {
        return this.climates.get(name);
    }

    public ProvinceList getImpassableClimate() {
        return impassableClimate;
    }

    public ProvinceList getFakeClimate() {
        return fakeClimate;
    }

    public List<ProvinceList> getWinters() {
        return new ArrayList<>(this.winters.values());
    }

    public ProvinceList getWinter(String name) {
        return this.winters.get(name);
    }

    public ProvinceList getFakeWinter() {
        return this.fakeWinter;
    }

    public List<ProvinceList> getMonsoons() {
        return new ArrayList<>(this.monsoons.values());
    }

    public ProvinceList getMonsoon(String name) {
        return this.monsoons.get(name);
    }

    public ProvinceList getFakeMonsoon() {
        return fakeMonsoon;
    }

    public Set<String> getNativeLocalisations() {
        return nativeLocalisations;
    }

    public Map<String, Map<Eu4Language, Localisation>> getLocalisations() {
        return localisations;
    }

    public List<Localisation> getAllLocalisations() {
        return this.localisations.values().stream().map(Map::values).flatMap(Collection::stream).toList();
    }

    public Localisation getLocalisation(String key, Eu4Language eu4Language) {
        Map<Eu4Language, Localisation> map = this.localisations.get(key);
        return MapUtils.isEmpty(map) ? null : map.get(eu4Language);
    }

    public Map<Eu4Language, Localisation> getLocalisation(String key) {
        return this.localisations.get(key);
    }

    public String getLocalisationClean(String key, Eu4Language eu4Language) {
        if (key == null) {
            return null;
        }

        Localisation localisation = getLocalisation(key, eu4Language);

        if (localisation == null || StringUtils.isBlank(localisation.getValue())) {
            return key;
        }

        StringBuilder localisationBuilder = new StringBuilder(localisation.getValue());

        if (localisationBuilder.length() == 0) {
            return key;
        }

        int indexOf;
        while (localisationBuilder.toString().indexOf('§') >= 0) {
            for (int i = 0; i < localisationBuilder.length(); i++) {
                if (localisationBuilder.charAt(i) == '§') {
                    localisationBuilder.deleteCharAt(i);//Remove char
                    localisationBuilder.deleteCharAt(i);//Remove color code
                    indexOf = localisationBuilder.indexOf("§", i);
                    localisationBuilder.deleteCharAt(indexOf);//Remove closing char
                    localisationBuilder.deleteCharAt(indexOf);//Remove closing code
                    break;
                }
            }
        }

        if ((indexOf = localisationBuilder.toString().indexOf('$')) >= 0) {
            if (ClausewitzUtils.hasAtLeast(localisationBuilder.toString(), '$', 2)) {
                String[] splits = localisationBuilder.toString().split("\\$");
                localisationBuilder = new StringBuilder();
                for (int i = 0; i < splits.length; i += 2) {
                    localisationBuilder.append(splits[i]).append(" ");
                }
            } else {
                localisationBuilder = new StringBuilder(localisationBuilder.substring(0, indexOf));
            }
        }

        return localisationBuilder.toString().replace("\\r\\n", "")
                                  .replace("\\n", " ")
                                  .replaceAll("[^'.\\p{L}\\p{M}\\p{Alnum}\\p{Space}]", "")
                                  .trim();
    }

    public String getLocalisationCleanNoPunctuation(String key, Eu4Language eu4Language) {
        return getLocalisationClean(key, eu4Language).replaceAll("[\\p{P}]", "").trim();
    }

    public void addLocalisation(Localisation localisation) {
        if (!this.localisations.containsKey(localisation.getKey())) {
            this.localisations.put(localisation.getKey(), new EnumMap<>(Eu4Language.class));
        }

        this.localisations.get(localisation.getKey()).put(localisation.getEu4Language(), localisation);
    }

    public List<String> getGraphicalCultures() {
        return graphicalCultures;
    }

    public List<SpriteType> getSpriteTypes() {
        return new ArrayList<>(this.spriteTypes.values());
    }

    public SpriteType getSpriteType(String key) {
        if (key == null) {
            return null;
        }

        return this.spriteTypes.get(key);
    }

    public File getSpriteTypeImageFile(String key) {
        SpriteType spriteType = getSpriteType(key);

        if (spriteType == null) {
            return null;
        }

        String relativePath = Path.of(ClausewitzUtils.removeQuotes(spriteType.getTextureFile())).toString();
        File file = getAbsoluteFile(relativePath);

        if (file != null && file.exists()) {
            return file;
        }

        //Fix some time files are not rightly registered (I don't know how the game loads them...)
        if (relativePath.endsWith(".tga")) {
            return getAbsoluteFile(relativePath.replace(".tga", ".dds"));
        } else if (relativePath.endsWith(".dds")) {
            return getAbsoluteFile(relativePath.replace(".dds", ".tga"));
        }

        return null;
    }

    public List<TerrainCategory> getTerrainCategories() {
        return new ArrayList<>(this.terrainCategories.values());
    }

    public TerrainCategory getTerrainCategory(String terrainCategory) {
        return terrainCategories.get(terrainCategory);
    }

    public List<Terrain> getTerrains() {
        return new ArrayList<>(this.terrains.values());
    }

    public Terrain getTerrain(String terrain) {
        return this.terrains.get(terrain);
    }

    public List<Tree> getTrees() {
        return new ArrayList<>(this.trees.values());
    }

    public Tree getTree(String tree) {
        return this.trees.get(tree);
    }

    public void writeTerrainItem(BufferedWriter writer) throws IOException {
        this.terrainItem.write(writer, true, 0, new HashMap<>());
    }

    public void writeClimateItem(BufferedWriter writer) throws IOException {
        this.climateItem.write(writer, true, 0, new HashMap<>());
    }

    public Collection<CultureGroup> getCultureGroups() {
        return this.cultureGroups.values();
    }

    public List<Culture> getCultures() {
        return this.cultureGroups.values()
                                 .stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .toList();
    }

    public Culture getCulture(String name) {
        if (name == null) {
            return null;
        }

        return this.cultureGroups.values()
                                 .stream()
                                 .map(CultureGroup::getCultures)
                                 .flatMap(Collection::stream)
                                 .filter(culture -> culture.getName().equalsIgnoreCase(name))
                                 .findFirst()
                                 .orElse(null);
    }

    public Collection<ReligionGroup> getReligionGroups() {
        return this.religionGroups.values();
    }

    public List<Religion> getReligions() {
        return getReligionGroups().stream().map(ReligionGroup::getReligions).flatMap(Collection::stream).toList();
    }

    public Religion getReligion(String name) {
        if (name == null) {
            return null;
        }

        return getReligionGroups().stream()
                                  .map(ReligionGroup::getReligions)
                                  .flatMap(Collection::stream)
                                  .filter(religion -> religion.getName().equalsIgnoreCase(name))
                                  .findFirst()
                                  .orElse(null);
    }

    public List<Institution> getInstitutions() {
        return this.institutions.values()
                                .stream()
                                .sorted(Comparator.comparingInt(Institution::getIndex))
                                .toList();
    }

    public Institution getInstitution(int i) {
        return getInstitutions().get(i);
    }

    public Institution getInstitution(String name) {
        return this.institutions.get(name);
    }

    public List<TradeGood> getTradeGoods() {
        return new ArrayList<>(this.tradeGoods.values());
    }

    public TradeGood getTradeGood(String name) {
        return this.tradeGoods.get(name);
    }

    public TradeGood getTradeGood(int i) {
        return new ArrayList<>(this.tradeGoods.values()).get(i);
    }

    public List<TradeNode> getTradeNodes() {
        return new ArrayList<>(this.tradeNodes.values());
    }

    public TradeNode getTradeNode(String name) {
        return this.tradeNodes.get(name);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(this.buildings.values());
    }

    public Building getBuilding(String name) {
        return this.buildings.get(name);
    }

    public List<ImperialReform> getImperialReforms() {
        return new ArrayList<>(this.imperialReforms.values());
    }

    public ImperialReform getImperialReform(String name) {
        return this.imperialReforms.get(name);
    }

    public List<Decree> getDecrees() {
        return new ArrayList<>(this.decrees.values());
    }

    public Decree getDecree(String name) {
        return this.decrees.get(name);
    }

    public List<GoldenBull> getGoldenBulls() {
        return new ArrayList<>(this.goldenBulls.values());
    }

    public GoldenBull getGoldenBull(String name) {
        return this.goldenBulls.get(name);
    }

    public List<Event> getEvents() {
        return new ArrayList<>(this.events.values());
    }

    public List<Event> getFireOnlyOnceEvents() {
        return this.events.values()
                          .stream()
                          .filter(event -> BooleanUtils.isTrue(event.fireOnlyOnce()))
                          .toList();
    }

    public Event getEvent(String id) {
        return this.events.get(id);
    }

    public Map<String, Map<String, Map<String, Define>>> getDefines() {
        return defines;
    }

    public Map<String, Map<String, Map<String, Define>>> getVeryEasyDefines() {
        return veryEasyDefines;
    }

    public Map<String, Map<String, Map<String, Define>>> getEasyDefines() {
        return easyDefines;
    }

    public Map<String, Map<String, Map<String, Define>>> getHardDefines() {
        return hardDefines;
    }

    public Map<String, Map<String, Map<String, Define>>> getVeryHardDefines() {
        return veryHardDefines;
    }

    public int getDefinesInt(String category, String key) {
        return this.defines.get(Eu4Utils.DEFINE_KEY).get(category).get(key).getAsInt();
    }

    public double getDefinesDouble(String category, String key) {
        return this.defines.get(Eu4Utils.DEFINE_KEY).get(category).get(key).getAsDouble();
    }

    public LocalDate getDefinesLocalDate(String category, String key) {
        return this.defines.get(Eu4Utils.DEFINE_KEY).get(category).get(key).getAsLocalDate();
    }

    public void changeDefine(Mod mod, String category, String key, String value) {
        Define define = this.defines.get(Eu4Utils.DEFINE_KEY).get(category).get(key);

        if (Objects.equals(define.getFileNode().getMod(), mod)) {
            define.changeValue(value);
        } else {
            if (define.getFileNode().getMod() != null) {
                throw new UnsupportedOperationException("Can't change define of other mod!");
            } else {
                Optional<FileNode> fileNode = this.defines.values()
                                                          .stream()
                                                          .map(Map::entrySet)
                                                          .flatMap(Collection::stream)
                                                          .map(Map.Entry::getValue)
                                                          .map(Map::values)
                                                          .flatMap(Collection::stream)
                                                          .filter(d -> d.getFileNode().getMod() != null && d.getFileNode().getMod().equals(mod))
                                                          .findFirst()
                                                          .map(Define::getFileNode); //First modded for this mod

                if (fileNode.isPresent()) {
                    define.setFileNode(fileNode.get());
                } else {
                    FileNode node = new FileNode(mod, Paths.get(Eu4Utils.COMMON_FOLDER_PATH, "defines", "00_defines.lua"));
                    define.setFileNode(node);
                }

                define.changeValue(value);
            }
        }
    }

    public void saveDefines(Mod mod) throws IOException {
        List<Define> moddedDefines = this.defines.values()
                                                 .stream()
                                                 .map(Map::entrySet)
                                                 .flatMap(Collection::stream)
                                                 .map(Map.Entry::getValue)
                                                 .map(Map::values)
                                                 .flatMap(Collection::stream)
                                                 .filter(d -> d.getFileNode().getMod() != null && d.getFileNode().getMod().equals(mod))
                                                 .toList();

        if (CollectionUtils.isNotEmpty(moddedDefines)) {
            Map<FileNode, List<Define>> map = moddedDefines.stream().collect(Collectors.groupingBy(Define::getFileNode));

            for (Map.Entry<FileNode, List<Define>> entry : map.entrySet()) {
                FileUtils.forceMkdirParent(entry.getKey().getPath().toFile());
                try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(entry.getKey().getPath().toFile()))) {
                    for (Define define : entry.getValue()) {
                        define.write(bufferedWriter);
                        bufferedWriter.newLine();
                    }
                } catch (IOException e) {
                    LOGGER.error("An error occurred while writing defines to {}: {}!", entry.getKey().getPath(), e.getMessage(), e);
                }
            }
        }
    }

    public int getMaxGovRank() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_GOV_RANK");
    }

    public int getMaxAspects() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_UNLOCKED_ASPECTS");
    }

    public int getGoldenEraDuration() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "GOLDEN_ERA_YEARS");
    }

    public int getBankruptcyDuration() {
        return getDefinesInt(Eu4Utils.DEFINE_ECONOMY_KEY, "BANKRUPTCY_DURATION");
    }

    public int getNbGreatPowers() {
        return getDefinesInt(Eu4Utils.DEFINE_DIPLOMACY_KEY, "NUM_OF_GREAT_POWERS");
    }

    public int getNomadDevelopmentScale() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "NOMAD_DEVELOPMENT_SCALE");
    }

    public int getLargeColonialNationLimit() {
        return getDefinesInt(Eu4Utils.DEFINE_ECONOMY_KEY, "LARGE_COLONIAL_NATION_LIMIT");
    }

    public int getFortPerDevRatio() {
        return getDefinesInt(Eu4Utils.DEFINE_MILITARY_KEY, "FORT_PER_DEV_RATIO");
    }

    public double getMaxArmyProfessionalism() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_ARMY_PROFESSIONALISM");
    }

    public double getLowArmyProfessionalismMinRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "LOW_ARMY_PROFESSIONALISM_MIN_RANGE");
    }

    public double getLowArmyProfessionalismMaxRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "LOW_ARMY_PROFESSIONALISM_MAX_RANGE");
    }

    public double getHighArmyProfessionalismMinRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "HIGH_ARMY_PROFESSIONALISM_MIN_RANGE");
    }

    public double getHighArmyProfessionalismMaxRange() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "HIGH_ARMY_PROFESSIONALISM_MAX_RANGE");
    }

    public LocalDate getStartDate() {
        return getDefinesLocalDate(Eu4Utils.DEFINE_GAME_KEY, "START_DATE");
    }

    public LocalDate getEndDate() {
        return getDefinesLocalDate(Eu4Utils.DEFINE_GAME_KEY, "END_DATE");
    }

    public int getEstateAngryThreshold() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_ANGRY_THRESHOLD");
    }

    public int getEstateHappyThreshold() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_HAPPY_THRESHOLD");
    }

    public int getEstateInfluenceLevel1() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_1");
    }

    public int getEstateInfluenceLevel2() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_2");
    }

    public int getEstateInfluenceLevel3() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_LEVEL_3");
    }

    public double getEstateInfluencePerDev() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_INFLUENCE_PER_DEV");
    }

    public double getEstateMaxInfluenceFromDev() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_MAX_INFLUENCE_FROM_DEV");
    }

    public double getIdeaToTech() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "IDEA_TO_TECH");
    }

    public double getNeighbourBonus() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "NEIGHBOURBONUS");
    }

    public double getNeighbourBonusCap() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "NEIGHBOURBONUS_CAP");
    }

    public double getSpyNetworkTechEffect() {
        return getDefinesDouble(Eu4Utils.DEFINE_DIPLOMACY_KEY, "SPY_NETWORK_TECH_EFFECT");
    }

    public double getSpyNetworkTechEffectMax() {
        return getDefinesDouble(Eu4Utils.DEFINE_DIPLOMACY_KEY, "SPY_NETWORK_TECH_EFFECT_MAX");
    }

    public int getEstatePrivilegesMaxConcurrent() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "ESTATE_PRIVILEGES_MAX_CONCURRENT");
    }

    public double getInnovativenessMax() {
        return getDefinesDouble(Eu4Utils.DEFINE_COUNTRY_KEY, "INNOVATIVENESS_MAX");
    }

    public int getMonarchMaxSkill() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MONARCH_MAX_SKILL");
    }

    public int getMonarchMinSkill() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MONARCH_MIN_SKILL");
    }

    public int getMaxExtraPersonalities() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_EXTRA_PERSONALITIES");
    }

    public int getAgeOfAdulthood() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "AGE_OF_ADULTHOOD");
    }

    public int getNumPossibleRivals() {
        return getDefinesInt(Eu4Utils.DEFINE_DIPLOMACY_KEY, "NUM_POSSIBLE_RIVALS");
    }

    public int getMaxChristianReligiousCenters() {
        return getDefinesInt(Eu4Utils.DEFINE_RELIGION_KEY, "MAX_CHRISTIAN_RELIGIOUS_CENTERS");
    }

    public int getMaxActivePolicies() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "MAX_ACTIVE_POLICIES");
    }

    public int getBasePossiblePolicies() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "BASE_POSSIBLE_POLICIES");
    }

    public int getFreeIdeaGroupCost() {
        return getDefinesInt(Eu4Utils.DEFINE_COUNTRY_KEY, "FREE_IDEA_GROUP_COST");
    }

    public List<Government> getGovernments() {
        return new ArrayList<>(this.governments.values());
    }

    public Government getGovernment(String name) {
        return this.governments.get(name);
    }

    public Collection<GovernmentRank> getGovernmentRanks() {
        return this.governmentRanks.values();
    }

    public GovernmentRank getGovernmentRank(int level) {
        return this.governmentRanks.get(level);
    }

    public Collection<GovernmentName> getGovernmentNames() {
        return this.governmentNames.values();
    }

    public GovernmentName getGovernmentName(String name) {
        return this.governmentNames.get(name);
    }

    public Collection<GovernmentReform> getGovernmentReforms() {
        return this.governmentReforms.values();
    }

    public GovernmentReform getGovernmentReform(String name) {
        return this.governmentReforms.get(name);
    }

    public Collection<Unit> getUnits() {
        return this.units.values();
    }

    public Unit getUnit(String name) {
        return this.units.get(name);
    }

    public Collection<Area> getAreas() {
        return this.areas.values();
    }

    public Area getArea(String name) {
        return this.areas.get(name);
    }

    public Collection<Region> getRegions() {
        return this.regions.values();
    }

    public Region getRegion(String name) {
        return this.regions.get(name);
    }

    public Collection<SuperRegion> getSuperRegions() {
        return this.superRegions.values();
    }

    public SuperRegion getSuperRegion(String name) {
        return this.superRegions.get(name);
    }

    public Collection<TechGroup> getTechGroups() {
        return this.techGroups.values();
    }

    public TechGroup getTechGroup(String name) {
        return this.techGroups.get(name);
    }

    public List<Advisor> getAdvisors() {
        return new ArrayList<>(this.advisors.values());
    }

    public Advisor getAdvisor(String name) {
        return this.advisors.get(name);
    }

    public List<IdeaGroup> getIdeaGroups() {
        return new ArrayList<>(this.ideaGroups.values());
    }

    public IdeaGroup getIdeaGroup(String name) {
        return this.ideaGroups.get(name);
    }

    public Set<String> getIdeas() {
        return Collections.unmodifiableSet(this.ideas);
    }

    public List<CasusBelli> getCasusBelli() {
        return new ArrayList<>(this.casusBelli.values());
    }

    public CasusBelli getCasusBelli(String name) {
        return this.casusBelli.get(name);
    }

    public List<ColonialRegion> getColonialRegions() {
        return new ArrayList<>(this.colonialRegions.values());
    }

    public ColonialRegion getColonialRegion(String name) {
        return this.colonialRegions.get(name);
    }

    public List<TradeCompany> getTradeCompanies() {
        return new ArrayList<>(this.tradeCompanies.values());
    }

    public TradeCompany getTradeCompany(String name) {
        return this.tradeCompanies.get(name);
    }

    public List<SubjectType> getSubjectTypes() {
        return new ArrayList<>(this.subjectTypes.values());
    }

    public SubjectType getSubjectType(String name) {
        return this.subjectTypes.get(name);
    }

    public List<SubjectTypeUpgrade> getSubjectTypeUpgrades() {
        return new ArrayList<>(this.subjectTypeUpgrades.values());
    }

    public SubjectTypeUpgrade getSubjectTypeUpgrade(String name) {
        return this.subjectTypeUpgrades.get(name);
    }

    public List<FetishistCult> getFetishistCults() {
        return new ArrayList<>(this.fetishistCults.values());
    }

    public FetishistCult getFetishistCult(String name) {
        return this.fetishistCults.get(name);
    }

    public List<ChurchAspect> getChurchAspects() {
        return new ArrayList<>(this.churchAspects.values());
    }

    public ChurchAspect getChurchAspect(String name) {
        return this.churchAspects.get(name);
    }

    public List<MissionsTree> getMissionsTrees() {
        return new ArrayList<>(this.missionsTrees.values());
    }

    public MissionsTree getMissionsTree(String name) {
        return this.missionsTrees.get(name);
    }

    public Mission getMission(String name) {
        if (StringUtils.isBlank(name)) {
            return null;
        }

        return this.missions.get(name);
    }

    public List<Mission> getMissions() {
        return this.missionsTrees.values().stream().map(MissionsTree::getMissions).flatMap(Collection::stream).toList();
    }

    public int getMaxMissionsSlots() {
        return maxMissionsSlots;
    }

    public List<EstatePrivilege> getEstatePrivileges() {
        return new ArrayList<>(this.estatePrivileges.values());
    }

    public EstatePrivilege getEstatePrivilege(String name) {
        return this.estatePrivileges.get(name);
    }

    public List<Estate> getEstates() {
        return new ArrayList<>(this.estates.values());
    }

    public Estate getEstate(String name) {
        return this.estates.get(name);
    }

    public Map<Power, List<Technology>> getTechnologies() {
        return technologies;
    }

    public List<Technology> getTechnologies(Power power) {
        return this.technologies.get(power);
    }

    public Technology getTechnology(Power power, int i) {
        return this.technologies.get(power).get(i);
    }

    public SortedSet<ProfessionalismModifier> getProfessionalismModifiers() {
        return this.professionalismModifiers;
    }

    public List<RulerPersonality> getRulerPersonalities() {
        return new ArrayList<>(this.rulerPersonalities.values());
    }

    public RulerPersonality getRulerPersonality(String name) {
        return this.rulerPersonalities.get(name);
    }

    public List<LeaderPersonality> getLeaderPersonalities() {
        return new ArrayList<>(this.leaderPersonalities.values());
    }

    public LeaderPersonality getLeaderPersonality(String name) {
        return this.leaderPersonalities.get(name);
    }

    public List<Investment> getInvestments() {
        return new ArrayList<>(this.investments.values());
    }

    public Investment getInvestment(String name) {
        return this.investments.get(name);
    }

    public List<Policy> getPolicies() {
        return new ArrayList<>(this.policies.values());
    }

    public Policy getPolicy(String name) {
        return this.policies.get(name);
    }

    public List<Hegemon> getHegemons() {
        return new ArrayList<>(this.hegemons.values());
    }

    public Hegemon getHegemon(String name) {
        return this.hegemons.get(name);
    }

    public List<Faction> getFactions() {
        return new ArrayList<>(this.factions.values());
    }

    public Faction getFaction(String name) {
        return this.factions.get(name);
    }

    public List<Age> getAges() {
        return new ArrayList<>(this.ages.values());
    }

    public Age getAge(String name) {
        return this.ages.get(name);
    }

    public AgeAbility getAgeAbility(String name) {
        if (name == null) {
            return null;
        }

        return this.ages.values()
                        .stream()
                        .map(Age::getAbilities)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(ageAbility -> ageAbility.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public AgeObjective getAgeObjective(String name) {
        if (name == null) {
            return null;
        }

        return this.ages.values()
                        .stream()
                        .map(Age::getObjectives)
                        .map(Map::values)
                        .flatMap(Collection::stream)
                        .filter(ageObjective -> ageObjective.getName().equalsIgnoreCase(name))
                        .findFirst()
                        .orElse(null);
    }

    public Collection<DefenderOfFaith> getDefenderOfFaith() {
        return this.defenderOfFaith.values();
    }

    public Collection<CenterOfTrade> getCentersOfTrade() {
        return this.centersOfTrade.values();
    }

    public List<Fervor> getFervors() {
        return new ArrayList<>(this.fervors.values());
    }

    public Fervor getFervor(String name) {
        return this.fervors.get(name);
    }

    public List<GreatProject> getGreatProjects() {
        return new ArrayList<>(this.greatProjects.values());
    }

    public GreatProject getGreatProject(String name) {
        return this.greatProjects.get(name);
    }

    public List<HolyOrder> getHolyOrders() {
        return new ArrayList<>(this.holyOrders.values());
    }

    public HolyOrder getHolyOrder(String name) {
        return this.holyOrders.get(name);
    }

    public List<Isolationism> getIsolationisms() {
        return new ArrayList<>(this.isolationisms.values());
    }

    public Isolationism getIsolationism(Integer level) {
        if (level == null) {
            return null;
        }

        for (Isolationism isolationism : this.isolationisms.values()) {
            if (isolationism.getIsolationValue() == level) {
                return isolationism;
            }
        }

        return null;
    }

    public List<NativeAdvancements> getNativeAdvancements() {
        return new ArrayList<>(this.nativeAdvancements.values());
    }

    public NativeAdvancements getNativeAdvancements(String name) {
        return this.nativeAdvancements.get(name);
    }

    public List<NavalDoctrine> getNavalDoctrines() {
        return new ArrayList<>(this.navalDoctrines.values());
    }

    public NavalDoctrine getNavalDoctrine(String name) {
        return this.navalDoctrines.get(name);
    }

    public List<ParliamentIssue> getParliamentIssues() {
        return new ArrayList<>(this.parliamentIssues.values());
    }

    public ParliamentIssue getParliamentIssue(String name) {
        return this.parliamentIssues.get(name);
    }

    public List<PersonalDeity> getPersonalDeities() {
        return new ArrayList<>(this.personalDeities.values());
    }

    public PersonalDeity getPersonalDeity(String name) {
        return this.personalDeities.get(name);
    }

    public List<ReligiousReforms> getReligiousReforms() {
        return new ArrayList<>(this.religiousReforms.values());
    }

    public ReligiousReforms getReligiousReforms(String name) {
        return this.religiousReforms.get(name);
    }

    public Collection<CrownLandBonus> getCrownLandBonuses() {
        return this.crownLandBonuses.values();
    }

    public List<StateEdict> getStateEdicts() {
        return new ArrayList<>(this.stateEdicts.values());
    }

    public StateEdict getStateEdict(String name) {
        return this.stateEdicts.get(name);
    }

    public List<TradePolicy> getTradePolicies() {
        return new ArrayList<>(this.tradePolicies.values());
    }

    public TradePolicy getTradePolicy(String name) {
        return this.tradePolicies.get(name);
    }

    public List<StaticModifier> getStaticModifiers() {
        return new ArrayList<>(this.staticModifiers.values());
    }

    public StaticModifier getStaticModifier(String name) {
        return this.staticModifiers.get(name);
    }

    public List<EventModifier> getEventModifiers() {
        return new ArrayList<>(this.eventModifiers.values());
    }

    public EventModifier getEventModifier(String name) {
        return this.eventModifiers.get(name);
    }

    public List<TriggeredModifier> getProvinceTriggeredModifiers() {
        return new ArrayList<>(this.provinceTriggeredModifiers.values());
    }

    public TriggeredModifier getProvinceTriggeredModifier(String name) {
        return this.provinceTriggeredModifiers.get(name);
    }

    public List<TriggeredModifier> getTriggeredModifiers() {
        return new ArrayList<>(this.triggeredModifiers.values());
    }

    public TriggeredModifier getTriggeredModifier(String name) {
        return this.triggeredModifiers.get(name);
    }

    public List<String> getCountriesTags() {
        return new ArrayList<>(this.countries.keySet());
    }

    public List<Country> getCountries() {
        return new ArrayList<>(this.countries.values());
    }

    public Country getCountry(String tag) {
        return this.countries.get(ClausewitzUtils.removeQuotes(tag).toUpperCase());
    }

    public Map<LocalDate, HreEmperor> getHreEmperors() {
        return new TreeMap<>(this.hreEmperors);
    }

    public Map<LocalDate, CelestialEmperor> getCelestialEmperors() {
        return new TreeMap<>(this.celestialEmperors);
    }

    public List<Bookmark> getBookmarks() {
        return new ArrayList<>(this.bookmarks.values());
    }

    public Bookmark getBookmark(String name) {
        return this.bookmarks.get(name);
    }

    public Map<Province, Map<Polygon, Boolean>> getBorders() {
        return this.borders;
    }

    public void setBorders(Map<Province, Map<Polygon, Boolean>> borders) {
        this.borders = borders;
    }

    public GameModifier getModifier(String modifier) {
        modifier = ClausewitzUtils.removeQuotes(modifier).toLowerCase();

        return Eu4Utils.coalesce(modifier, this::getStaticModifier, this::getParliamentIssue, this::getEventModifier, this::getProvinceTriggeredModifier,
                                 this::getTriggeredModifier);
    }

    public Stream<FileNode> getFlagsFileNodes() {
        return getFileNodes(Eu4Utils.GFX_FOLDER_PATH + File.separator + "flags",
                            fileNode -> Files.isRegularFile(fileNode.getPath()),
                            fileNode -> fileNode.getPath().toString().endsWith(".tga"));
    }

    private void readMods(List<String> modsEnabled) throws IOException {
        this.mods = new ArrayList<>();
        this.filesNode = new TreeNode<>(null, new FileNode(this.launcherSettings.getGameFolderPath(), (Mod) null), FileNode::getChildren);

        if (CollectionUtils.isNotEmpty(modsEnabled)) {
            //Compare with path so replace with system separator
            Map<String, Mod> knownMods = new HashMap<>();
            try (Stream<Path> stream = Files.list(this.launcherSettings.getModFolder())) {
                stream.filter(path -> path.getFileName().toString().endsWith(".mod"))
                      .filter(path -> path.toFile().exists() && path.toFile().canRead())
                      .forEach(path -> {
                          Mod mod = new Mod(path.toFile(), ClausewitzParser.parse(path.toFile(), 0), this.launcherSettings);
                          knownMods.put(path.getFileName().toString(), mod);

                          if (!ModType.STEAM.pattern.matcher(path.getFileName().toString()).matches()) {
                              if (StringUtils.isNotBlank(mod.getRemoteFileId())) {
                                  knownMods.put("ugc_" + ClausewitzUtils.removeQuotes(mod.getRemoteFileId()) + ".mod", mod);
                              }
                          }
                      });
            }

            Map<File, Mod> map = new LinkedHashMap<>();

            for (String modName : modsEnabled) {
                Mod mod = knownMods.get(ClausewitzUtils.removeQuotes(modName).replaceAll("^mod/", ""));
                if (mod != null) {
                    map.put(mod.getPath().toFile(), mod);
                    this.mods.add(mod);
                } else {
                    throw new ModNotFoundException(modName);
                }
            }

            Map<Mod, List<String>> replaces = new LinkedHashMap<>();
            map.forEach((key, value) -> {
                if (!key.isAbsolute()) {
                    key = this.launcherSettings.getModFolder().resolve(key.getPath().replaceFirst("^mod\\\\", "")).toFile();
                }

                if (key.exists() && key.canRead()) {
                    replaces.put(value, value.getReplacePath()
                                             .stream()
                                             .map(ClausewitzUtils::removeQuotes)
                                             .map(s -> Path.of(s).toString())
                                             .toList());
                }
            });

            replaces.forEach((mod, replacePaths) -> { //This technique replace only folders, so don't check for files
                this.filesNode.removeChildrenIf(fileNode -> fileNode.getPath().toFile().isDirectory()
                                                            && replacePaths.contains(fileNode.getRelativePath().toString()));
                this.filesNode.merge(new TreeNode<>(null, new FileNode(mod), FileNode::getChildren));
            });
        }
    }

    public void loadDefines() throws IOException {
        this.defines = readDefines(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines.lua"));

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines",
                     fileNode -> !fileNode.getPath().getFileName().toString().startsWith("difficulty") && isRegularLuaFile(fileNode))
                .forEach(fileNode -> {
                    try {
                        Map<String, Map<String, Map<String, Define>>> definesMap = readDefines(fileNode.getRelativePath());

                        definesMap.forEach((define, map) -> map.forEach(
                                (category, categoryMap) -> categoryMap.forEach((key, value) -> this.defines.get(define).get(category).put(key, value))));
                    } catch (IOException e) {
                        LOGGER.error("Could not read file {} because: {} !", fileNode, e.getMessage(), e);
                    }
                });

        //Special case for difficulty
        this.veryEasyDefines = readDefines(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines" + File.separator + "difficulty_very_easy.lua"));
        this.easyDefines = readDefines(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines" + File.separator + "difficulty_easy.lua"));
        this.hardDefines = readDefines(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines" + File.separator + "difficulty_hard.lua"));
        this.veryHardDefines = readDefines(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defines" + File.separator + "difficulty_very_hard.lua"));
    }

    private Map<String, Map<String, Map<String, Define>>> readDefines(Path relativePath) throws IOException {
        Map<String, Map<String, Map<String, Define>>> definesMap = new LinkedHashMap<>();
        Path definesPath = getAbsolutePath(relativePath);
        FileNode fileNode = getFileNode(relativePath);

        if (definesPath != null && fileNode != null && Files.exists(definesPath) && Files.isRegularFile(definesPath) && Files.isReadable(definesPath)) {
            Map<String, Object> luaMap = LuaParser.parse(definesPath.toFile());

            luaMap.forEach((key, value) -> {
                Map<String, Map<String, Define>> map = new LinkedHashMap<>();
                definesMap.put(key, map);

                ((Map<String, Map<String, Object>>) value).forEach((subKey, subValue) -> {
                    Map<String, Define> subMap = new LinkedHashMap<>();
                    map.put(subKey, subMap);
                    subValue.forEach((subSubKey, subSubValue) -> subMap.put(subSubKey, new Define(subKey, subSubKey, subSubValue, fileNode)));
                });

            });
        }

        return definesMap;
    }

    private void loadNativeLocalisations() {
        this.nativeLocalisations = new HashSet<>();

        try (Stream<Path> stream = Files.list(this.launcherSettings.getGameFolderPath().resolve(Eu4Utils.LOCALISATION_FOLDER_PATH))) {
            stream.filter(Files::isRegularFile).forEach(path -> {
                try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                    String line;
                    reader.readLine(); //Skip first line language (and BOM)

                    while ((line = reader.readLine()) != null) {
                        if (StringUtils.isBlank(line)) {
                            continue;
                        }

                        int indexOf;
                        if ((indexOf = line.indexOf('#')) >= 0) { //If has comments
                            if (line.indexOf('"') < 0) { //If has no data
                                continue;
                            }

                            if (line.indexOf('"') < indexOf) { //If data is before comment
                                line = line.substring(0, indexOf);

                                if (ClausewitzUtils.isBlank(line)) {
                                    continue;
                                }
                            } else {
                                continue;
                            }
                        }

                        String[] keys = line.split(":", 2);
                        this.nativeLocalisations.add(keys[0].trim());
                    }
                } catch (IOException e) {
                    LOGGER.error("Could not read file {} because: {} !", path, e.getMessage(), e);
                }
            });
        } catch (IOException e) {
            LOGGER.error("An error occurred while reading native localisations: {}", e.getMessage(), e);
        }
    }

    private void loadLocalisations() {
        List<Localisation> list = new ArrayList<>();

        for (Eu4Language language : Eu4Language.values()) {
            list.addAll(loadLocalisations(language));
        }

        this.localisations = list.stream()
                                 .collect(Collectors.groupingBy(Localisation::getKey))
                                 .entrySet()
                                 .stream()
                                 .collect(Collectors.toMap(Map.Entry::getKey,
                                                           entry -> entry.getValue()
                                                                         .stream()
                                                                         .collect(Collectors.toMap(Localisation::getEu4Language,
                                                                                                   Function.identity(), (l, l2) -> l,
                                                                                                   () -> new EnumMap<>(Eu4Language.class)))));
    }

    private List<Localisation> loadLocalisations(Eu4Language eu4Language) {
        List<Localisation> list = new ArrayList<>();

        getFileNodes(Eu4Utils.LOCALISATION_FOLDER_PATH,
                     fileNode -> Files.isRegularFile(fileNode.getPath()),
                     fileNode -> fileNode.getPath().toString().endsWith(eu4Language.fileEndWith + ".yml"))
                .forEach(fileNode -> {
                    try (BufferedReader reader = Files.newBufferedReader(fileNode.getPath(), StandardCharsets.UTF_8)) {
                        String line;
                        reader.readLine(); //Skip first line language (and BOM)
                        AtomicInteger i = new AtomicInteger(1);

                        while ((line = reader.readLine()) != null) {
                            try {
                                if (StringUtils.isBlank(line)) {
                                    continue;
                                }

                                int indexOf;
                                if ((indexOf = line.indexOf('#')) >= 0) { //If has comments
                                    if (line.indexOf('"') < 0) { //If has no data
                                        continue;
                                    }

                                    if (line.indexOf('"') < indexOf) { //If data is before comment
                                        line = line.substring(0, indexOf);

                                        if (ClausewitzUtils.isBlank(line)) {
                                            continue;
                                        }
                                    } else {
                                        continue;
                                    }
                                }

                                if ((eu4Language.fileEndWith + ":").equals(line.replace(" ", ""))) {
                                    continue;
                                }

                                String[] keys = line.split(":", 2);
                                String key = keys[0].trim();
                                String version = keys[1].substring(0, 1);
                                String value = keys[1].substring(1).trim();
                                int start = value.indexOf('"') + 1;
                                int end = value.lastIndexOf('"');

                                if (start > end) {
                                    continue;
                                }

                                list.add(new Localisation(fileNode, key, eu4Language, version, value.substring(start, end).trim()));
                            } catch (Exception e) {
                                LOGGER.error("Could not read file {} at line {} because: {} !", fileNode.getPath(), i.get(), e.getMessage(), e);
                            } finally {
                                i.incrementAndGet();
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.error("Could not read file {} because: {} !", fileNode.getPath(), e.getMessage(), e);
                    }
                });

        return list;
    }

    private void readGraphicalCultures() {
        this.graphicalCultures = new ArrayList<>();

        Path graphicalCulturesPath = getAbsolutePath(Path.of(Eu4Utils.COMMON_FOLDER_PATH, Eu4Utils.GRAPHICAL_CULTURES_FILE));

        if (graphicalCulturesPath != null && graphicalCulturesPath.toFile().exists() && graphicalCulturesPath.toFile().canRead()) {
            try (BufferedReader reader = Files.newBufferedReader(graphicalCulturesPath, StandardCharsets.UTF_8)) {
                String line;

                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        this.graphicalCultures.add(StringUtils.trim(line));
                    }
                }
            } catch (IOException e) {
                LOGGER.error("Could not read file {} because: {} !", graphicalCulturesPath, e.getMessage(), e);
            }
        }
    }

    private void readSpriteTypes() {
        this.spriteTypes = new HashMap<>();

        getPaths(Eu4Utils.INTERFACE_FOLDER_PATH,
                 fileNode -> Files.isRegularFile(fileNode.getPath()),
                 fileNode -> fileNode.getPath().toString().endsWith(".gfx"))
                .forEach(path -> {
                    ClausewitzItem rootItem = ClausewitzParser.parse(path.toFile(), 0);
                    ClausewitzItem spriteTypesItem = rootItem.getChild("spriteTypes");

                    if (spriteTypesItem != null) {
                        this.spriteTypes.putAll(spriteTypesItem.getChildren("spriteType")
                                                               .stream()
                                                               .map(SpriteType::new)
                                                               .collect(Collectors.toMap(spriteType -> ClausewitzUtils.removeQuotes(spriteType.getName()),
                                                                                         Function.identity(),
                                                                                         (a, b) -> a)));
                    }
                });
    }

    private void readProvincesDefinition() throws IOException {
        File provincesDefinitionFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "definition.csv");

        if (provincesDefinitionFile != null && provincesDefinitionFile.canRead()) {
            this.provinces = new HashMap<>();
            this.provincesByColor = new HashMap<>();
            try (BufferedReader reader = Files.newBufferedReader(provincesDefinitionFile.toPath(), StandardCharsets.ISO_8859_1)) {
                String line;
                reader.readLine(); //Skip csv headers
                while ((line = reader.readLine()) != null) {
                    if (StringUtils.isNotBlank(line)) {
                        String[] csvLine = line.split(";", -1);

                        if (csvLine.length >= 4 && StringUtils.isNoneBlank(Arrays.copyOf(csvLine, 4))) {
                            Province province = new Province(csvLine);
                            this.provinces.put(province.getId(), province);

                            if (province.getColor() != null) {
                                this.provincesByColor.put(new Color(province.getColor()).getRGB(), province);
                            }
                        }

                    }
                }
            }
        }
    }

    private void readDefaultMap() {
        File provincesMapFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "default.map");

        if (provincesMapFile != null && provincesMapFile.canRead()) {
            ClausewitzItem provinceMapItem = ClausewitzParser.parse(provincesMapFile, 0);
            ClausewitzList seaList = provinceMapItem.getList("sea_starts");

            if (seaList != null) {
                seaList.getValuesAsInt().forEach(id -> this.provinces.get(id).setOcean(true));
            }

            ClausewitzList lakesList = provinceMapItem.getList("lakes");

            if (lakesList != null) {
                lakesList.getValuesAsInt().forEach(id -> this.provinces.get(id).setLake(true));
            }
        }
    }

    private void readClimate() {
        FileNode climateFile = getFileNode(Eu4Utils.MAP_FOLDER_PATH + File.separator + "climate.txt");

        if (climateFile != null && climateFile.getPath().toFile().canRead()) {
            ClausewitzItem item = ClausewitzParser.parse(climateFile.getPath().toFile(), 0);
            this.climateItem = item;
            this.climates = new HashMap<>();
            this.winters = new HashMap<>();
            this.monsoons = new HashMap<>();

            item.getLists()
                .forEach(list -> {
                    if (list.getName().endsWith("_winter")) {
                        this.winters.put(list.getName(), new ProvinceList(list, climateFile));
                        list.getValuesAsInt().forEach(id -> this.provinces.get(id).setWinter(list.getName()));
                    } else if (Eu4Utils.IMPASSABLE_CLIMATE.equals(list.getName())) {
                        this.impassableClimate = new ProvinceList(list, Eu4Utils.IMPASSABLE_LOCALIZATION, climateFile);
                        this.climates.put(Eu4Utils.IMPASSABLE_CLIMATE, this.impassableClimate);
                        list.getValuesAsInt().forEach(id -> this.provinces.get(id).setClimate(list.getName()));
                    } else if (list.getName().endsWith("_monsoon")) {
                        this.monsoons.put(list.getName(), new ProvinceList(list, climateFile));
                        list.getValuesAsInt().forEach(id -> this.provinces.get(id).setMonsoon(list.getName()));
                    } else {
                        this.climates.put(list.getName(), new ProvinceList(list, climateFile));
                        list.getValuesAsInt().forEach(id -> this.provinces.get(id).setClimate(list.getName()));
                    }
                });

            this.fakeClimate = new ProvinceList(Eu4Utils.DEFAULT_CLIMATE,
                                                this.provinces.values()
                                                              .stream()
                                                              .filter(province -> province.getClimate() == null)
                                                              .map(Province::getId)
                                                              .toList());
            this.climates.put(Eu4Utils.DEFAULT_CLIMATE, this.fakeClimate);

            this.fakeWinter = new ProvinceList(Eu4Utils.DEFAULT_WINTER,
                                               this.provinces.values()
                                                             .stream()
                                                             .filter(province -> province.getWinter() == null)
                                                             .map(Province::getId)
                                                             .toList());
            this.winters.put(Eu4Utils.DEFAULT_WINTER, this.fakeWinter);

            this.fakeMonsoon = new ProvinceList(Eu4Utils.DEFAULT_MONSOON,
                                                this.provinces.values()
                                                              .stream()
                                                              .filter(province -> province.getMonsoon() == null)
                                                              .map(Province::getId)
                                                              .toList());
            this.monsoons.put(Eu4Utils.DEFAULT_MONSOON, this.fakeMonsoon);
        }
    }

    private void readContinents() {
        FileNode continentFile = getFileNode(Eu4Utils.MAP_FOLDER_PATH + File.separator + "continent.txt");

        if (continentFile != null && continentFile.getPath().toFile().canRead()) {
            ClausewitzItem continentsItem = ClausewitzParser.parse(continentFile.getPath().toFile(), 0);

            this.continents = continentsItem.getListsNot("island_check_provinces")
                                            .stream()
                                            .map(list -> new ProvinceList(list, continentFile))
                                            .collect(Collectors.toMap(ProvinceList::getName, Function.identity(), (a, b) -> a, LinkedHashMap::new));
        }
    }

    private void readPositions() {
        File positionsFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "positions.txt");

        if (positionsFile != null && positionsFile.canRead()) {
            ClausewitzItem positionsItem = ClausewitzParser.parse(positionsFile, 0);

            positionsItem.getChildren().forEach(child -> {
                Integer provinceId = NumbersUtils.toInt(child.getName());

                if (provinceId != null && this.provinces.containsKey(provinceId) && child.hasList("position")) {
                    this.provinces.get(provinceId).setPositions(child.getList("position"));
                }
            });
        }
    }

    private void readProvinceHistory() {

        List<FileNode> fileNodes = getFileNodesList(Eu4Utils.HISTORY_FOLDER_PATH + File.separator + "provinces", this::isRegularTxtFile);
        CountDownLatch countDownLatch = new CountDownLatch(fileNodes.size());

        fileNodes.forEach(fileNode -> Eu4Utils.POOL_EXECUTOR.submit(() -> {
            try {
                String[] fileNameSplit = fileNode.getPath().getFileName().toString().split("[ -]");
                if (fileNameSplit.length >= 1) {
                    int provinceId = Eu4Utils.cleanStringAndParseToInt(fileNameSplit[0]);
                    getProvince(provinceId).setHistory(ClausewitzParser.parse(fileNode.getPath().toFile(), 0), this, fileNode);
                    //Fixme multiple history files with different names are compatible https://eu4.paradoxwikis.com/History_modding#Compatibility_-_Partial_Overwrites
                }
            } catch (NumberFormatException ignored) {
            } finally {
                countDownLatch.countDown();
            }
        }));

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            LOGGER.error("An error occurred while reading province history : {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
        }
    }

    private void readProvinces() throws IOException {
        File terrainMapFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "terrain.bmp");
        File treesMapFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "trees.bmp");

        if (this.provincesImage != null && this.provincesImage.canRead() && terrainMapFile != null && terrainMapFile.canRead()
            && treesMapFile != null && treesMapFile.canRead()) {
            BufferedImage provinceImage = ImageIO.read(this.provincesImage);
            BufferedImage terrainMap = ImageIO.read(terrainMapFile);
            BufferedImage treesMap = ImageIO.read(treesMapFile);
            this.provinceImageWidth = provinceImage.getWidth();
            this.provinceImageHeight = provinceImage.getHeight();

            List<Color> terrainColors = new ArrayList<>();
            IndexColorModel colorModel = (IndexColorModel) terrainMap.getColorModel();

            for (int i = 0; i < colorModel.getMapSize() + 1; i++) {
                terrainColors.add(new Color(colorModel.getRGB(i)));
            }

            List<Color> treesColors = new ArrayList<>();
            colorModel = (IndexColorModel) treesMap.getColorModel();

            for (int i = 0; i < colorModel.getMapSize() + 1; i++) {
                treesColors.add(new Color(colorModel.getRGB(i)));
            }

            Map<Integer, List<Integer>> provinceTerrainColors = new HashMap<>();

            for (int x = 0; x < this.provinceImageWidth; x++) {
                for (int y = 0; y < this.provinceImageHeight; y++) {
                    int rgb = provinceImage.getRGB(x, y);
                    Province province = this.provincesByColor.get(rgb);
                    Province other;

                    if (province != null && province.isColonizable() && !province.isPort()) {
                        if (x > 0) {
                            int leftRgb = provinceImage.getRGB(x - 1, y);
                            if (leftRgb != rgb && (other = this.provincesByColor.get(leftRgb)) != null && other.isOcean()) {
                                province.setPort(true);
                            }
                        }

                        if (x < provinceImage.getWidth() - 1) {
                            int rightRgb = provinceImage.getRGB(x + 1, y);
                            if (rightRgb != rgb && (other = this.provincesByColor.get(rightRgb)) != null && other.isOcean()) {
                                province.setPort(true);
                            }
                        }

                        if (y > 0) {
                            int topRgb = provinceImage.getRGB(x, y - 1);
                            if (topRgb != rgb && (other = this.provincesByColor.get(topRgb)) != null && other.isOcean()) {
                                province.setPort(true);
                            }
                        }

                        if (y < provinceImage.getHeight() - 1) {
                            int bottomRgb = provinceImage.getRGB(x, y + 1);
                            if (bottomRgb != rgb && (other = this.provincesByColor.get(bottomRgb)) != null && other.isOcean()) {
                                province.setPort(true);
                            }
                        }
                    }

                    int terrainColor = terrainMap.getRGB(x, y);
                    int provinceColor = provinceImage.getRGB(x, y);

                    if (!provinceTerrainColors.containsKey(provinceColor)) {
                        provinceTerrainColors.put(provinceColor, new ArrayList<>());
                    }

                    provinceTerrainColors.get(provinceColor).add(terrainColor);
                }
            }

            Map<Integer, Integer> provinceTerrains = provinceTerrainColors.entrySet()
                                                                          .stream()
                                                                          .collect(Collectors.toMap(Map.Entry::getKey,
                                                                                                    entry -> {
                                                                                                        Map<Integer, Long> map = entry.getValue()
                                                                                                                                      .stream()
                                                                                                                                      .collect(
                                                                                                                                              Collectors.groupingBy(
                                                                                                                                                      Function.identity(),
                                                                                                                                                      Collectors.counting()));
                                                                                                        return map.entrySet()
                                                                                                                  .stream()
                                                                                                                  .max(Comparator.comparingLong(
                                                                                                                          Map.Entry::getValue))
                                                                                                                  .get()
                                                                                                                  .getKey();
                                                                                                    }));

            FileNode terrainFile = getFileNode(Eu4Utils.MAP_FOLDER_PATH + File.separator + "terrain.txt");

            if (terrainFile != null && terrainFile.getPath() != null && terrainFile.getPath().toFile().canRead()) {
                ClausewitzItem terrainItem = ClausewitzParser.parse(terrainFile.getPath().toFile(), 0);
                this.terrainItem = terrainItem;
                ClausewitzItem terrainsItem = terrainItem.getChild("terrain");

                this.terrains = new HashMap<>();
                this.terrains.putAll(terrainsItem.getChildren()
                                                 .stream()
                                                 .map(item -> new Terrain(item, terrainFile, this, terrainColors))
                                                 .collect(Collectors.toMap(Terrain::getName, Function.identity(), (a, b) -> b)));

                ClausewitzItem treesItem = terrainItem.getChild("tree");

                this.trees = new HashMap<>();
                this.trees.putAll(treesItem.getChildren()
                                           .stream()
                                           .map(item -> new Tree(item, terrainFile, this, treesColors))
                                           .collect(Collectors.toMap(Tree::getName, Function.identity(), (a, b) -> b)));

                ClausewitzItem categories = terrainItem.getChild("categories");

                this.terrainCategories = new HashMap<>();
                this.terrainCategories.putAll(categories.getChildren()
                                                        .stream()
                                                        .map(item -> new TerrainCategory(item, terrainFile))
                                                        .collect(Collectors.toMap(TerrainCategory::getName, Function.identity(), (a, b) -> b)));

                provinceTerrains.forEach((provinceColor, color) ->
                                                 this.terrains.values()
                                                              .stream()
                                                              .filter(t -> t.getFileColors().contains(new Color(color)))
                                                              .findFirst()
                                                              .ifPresent(terrain -> {
                                                                  Province p = this.provincesByColor.get(provinceColor);
                                                                  p.setTerrainCategory(terrain.getCategory());
                                                                  terrain.getCategory().getComputedProvinces().add(p.getId());
                                                              }));

                this.terrainCategories.values()
                                      .stream()
                                      .filter(terrainCategory -> CollectionUtils.isNotEmpty(terrainCategory.getProvinces()))
                                      .forEach(terrainCategory -> terrainCategory.getProvinces()
                                                                                 .forEach(id -> this.provinces.get(id)
                                                                                                              .setTerrainCategory(terrainCategory)));
            }
        }
    }

    private void readBorders() throws IOException {
        this.borders = Eu4MapUtils.imageToBorders(this, ImageIO.read(new File(getProvincesImage().getAbsolutePath())));
    }

    private void readCultures() {
        this.cultureGroups = new HashMap<>();
        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "cultures", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cultureGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                    cultureGroupsItem.getChildren()
                                     .stream()
                                     .map(CultureGroup::new)
                                     .forEach(cultureGroup -> {
                                         this.cultureGroups.compute(cultureGroup.getName(), (s, group) -> {
                                             if (group == null) {
                                                 return cultureGroup;
                                             } else {
                                                 group.addItem(cultureGroup.item);
                                                 return group;
                                             }
                                         });
                                     });
                });
    }

    private void readReligion() {
        this.religionGroups = new LinkedHashMap<>();
        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "religions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem religionGroupsItem = ClausewitzParser.parse(path.toFile(), 0);
                    religionGroupsItem.getChildren()
                                      .stream()
                                      .map(item -> new ReligionGroup(item, this))
                                      .forEach(religionGroup -> this.religionGroups.put(religionGroup.getName(), religionGroup));
                });
    }

    private void readInstitutions() {
        this.institutions = new LinkedHashMap<>();
        AtomicInteger i = new AtomicInteger();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "institutions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem institutionsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.institutions.putAll(institutionsItem.getChildren()
                                                             .stream()
                                                             .map(item -> new Institution(item, this, i.getAndIncrement()))
                                                             .collect(Collectors.toMap(Institution::getName, Function.identity(), (a, b) -> b,
                                                                                       LinkedHashMap::new)));
                });
    }

    private void readTradeGoods() {
        this.tradeGoods = new LinkedHashMap<>();
        AtomicInteger i = new AtomicInteger();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "tradegoods", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradeGoodsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradeGoods.putAll(tradeGoodsItem.getChildren()
                                                         .stream()
                                                         .map(item -> new TradeGood(item, i.getAndIncrement(), this))
                                                         .collect(Collectors.toMap(TradeGood::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new)));
                });

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "prices", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem pricesItem = ClausewitzParser.parse(path.toFile(), 0);
                    pricesItem.getChildren().forEach(priceItem -> {
                        if (this.tradeGoods.containsKey(priceItem.getName())) {
                            this.tradeGoods.get(priceItem.getName()).setPriceItem(priceItem);
                        }
                    });
                });
    }

    private void readTradeNodes() {
        this.tradeNodes = new LinkedHashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "tradenodes", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem tradeNodesItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.tradeNodes.putAll(tradeNodesItem.getChildren()
                                                         .stream()
                                                         .map(item -> new TradeNode(item, fileNode))
                                                         .collect(Collectors.toMap(TradeNode::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new)));
                });
    }

    private void readBuildings() {
        this.buildings = new LinkedHashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "buildings", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem buildingsItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.buildings.putAll(buildingsItem.getChildren()
                                                       .stream()
                                                       .map(item -> new Building(item, this, fileNode))
                                                       .collect(Collectors.toMap(Building::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readImperialReforms() {
        this.imperialReforms = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "imperial_reforms", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem imperialReformsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.imperialReforms.putAll(imperialReformsItem.getChildren()
                                                                   .stream()
                                                                   .map(item -> new ImperialReform(item, this))
                                                                   .collect(Collectors.toMap(ImperialReform::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readDecrees() {
        this.decrees = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "decrees", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem decreesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.decrees.putAll(decreesItem.getChildren()
                                                   .stream()
                                                   .map(Decree::new)
                                                   .collect(Collectors.toMap(Decree::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readGoldenBulls() {
        this.goldenBulls = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "golden_bulls", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem goldenBullsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.goldenBulls.putAll(
                            goldenBullsItem.getChildren()
                                           .stream()
                                           .map(GoldenBull::new)
                                           .collect(Collectors.toMap(GoldenBull::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readEvents() {
        this.events = new HashMap<>();

        getPaths("events", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem eventsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.events.putAll(eventsItem.getChildren()
                                                 .stream()
                                                 .map(Event::new)
                                                 .filter(event -> event.getId() != null)
                                                 .collect(Collectors.toMap(Event::getId, Function.identity(), (e1, e2) -> e2)));
                });
    }

    private void readGovernments() {
        this.governments = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "governments", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governments.putAll(governmentsItem.getChildrenNot("pre_dharma_mapping")
                                                           .stream()
                                                           .map(item -> new Government(item, this))
                                                           .collect(Collectors.toMap(Government::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readGovernmentRanks() {
        this.governmentRanks = new TreeMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "government_ranks", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentRanksItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governmentRanks.putAll(governmentRanksItem.getChildren()
                                                                   .stream()
                                                                   .map(GovernmentRank::new)
                                                                   .collect(Collectors.toMap(GovernmentRank::getLevel, Function.identity())));
                });
    }

    private void readGovernmentNames() {
        this.governmentNames = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "government_names", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem governmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.governmentNames.putAll(governmentsItem.getChildren()
                                                               .stream()
                                                               .map(GovernmentName::new)
                                                               .collect(Collectors.toMap(GovernmentName::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readGovernmentReforms() {
        this.governmentReforms = new HashMap<>();

        List<ClausewitzItem> reformsItems = getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "government_reforms", this::isRegularTxtFile)
                .map(path -> ClausewitzParser.parse(path.toFile(), 0))
                .toList();

        AtomicReference<GovernmentReform> defaultReform = new AtomicReference<>();

        reformsItems.stream().filter(item -> item.hasChild("defaults_reform")).findFirst().ifPresent(item -> {
            defaultReform.set(new GovernmentReform(item.getChild("defaults_reform"), this, null));
        });

        this.governmentReforms.putAll(reformsItems.stream()
                                                  .map(item -> item.getChildrenNot("defaults_reform"))
                                                  .flatMap(Collection::stream)
                                                  .map(item -> new GovernmentReform(item, this, defaultReform.get()))
                                                  .collect(Collectors.toMap(GovernmentReform::getName, Function.identity(), (a, b) -> b)));
    }

    private void readUnits() {
        this.units = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "units", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem unitItem = ClausewitzParser.parse(path.toFile(), 0);
                    unitItem.setName(FilenameUtils.removeExtension(path.getFileName().toString()));
                    Unit unit = new Unit(unitItem);
                    this.units.put(unit.getName(), unit);
                });
    }

    private void readAreas() {
        this.areas = new HashMap<>();
        FileNode areasFile = getFileNode(Eu4Utils.MAP_FOLDER_PATH + File.separator + "area.txt");

        if (areasFile != null && areasFile.getPath() != null && areasFile.getPath().toFile().canRead()) {
            ClausewitzItem areasItem = ClausewitzParser.parse(areasFile.getPath().toFile(), 0);

            this.areas.putAll(areasItem.getLists()
                                       .stream()
                                       .map(list -> new Area(list, areasFile))
                                       .collect(Collectors.toMap(Area::getName, Function.identity(), (a, b) -> b)));
            this.areas.putAll(areasItem.getChildren()
                                       .stream()
                                       .map(item -> new Area(item, areasFile))
                                       .collect(Collectors.toMap(Area::getName, Function.identity(), (a, b) -> b)));
        }
    }

    private void readRegions() {
        this.regions = new HashMap<>();
        File regionsFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "region.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            this.regions.putAll(regionsItem.getChildren()
                                           .stream()
                                           .map(item -> new Region(item, this))
                                           .collect(Collectors.toMap(Region::getName, Function.identity(), (a, b) -> b)));
        }
    }

    private void readSuperRegions() {
        this.superRegions = new HashMap<>();
        File regionsFile = getAbsoluteFile(Eu4Utils.MAP_FOLDER_PATH + File.separator + "superregion.txt");

        if (regionsFile != null && regionsFile.canRead()) {
            ClausewitzItem regionsItem = ClausewitzParser.parse(regionsFile, 0);
            this.superRegions.putAll(regionsItem.getLists()
                                                .stream()
                                                .map(list -> new SuperRegion(list, this))
                                                .collect(Collectors.toMap(SuperRegion::getName, Function.identity(), (a, b) -> b)));
        }
    }

    private void readTechnologies() {
        this.techGroups = new HashMap<>();
        this.technologies = new EnumMap<>(Power.class);
        FileNode techGroupsFile = getFileNode(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "technology.txt");

        if (techGroupsFile != null && techGroupsFile.getPath().toFile().canRead()) {
            ClausewitzItem techGroupsItem = ClausewitzParser.parse(techGroupsFile.getPath().toFile(), 0);
            this.techGroups.putAll(techGroupsItem.getChild("groups")
                                                 .getChildren()
                                                 .stream()
                                                 .map(item -> new TechGroup(item, techGroupsFile))
                                                 .collect(Collectors.toMap(TechGroup::getName, Function.identity(), (a, b) -> b)));

            ClausewitzItem technologiesItem = techGroupsItem.getChild("tables");

            if (technologiesItem != null) {
                for (Power power : Power.values()) {
                    String filePath = null;
                    List<Technology> techs = new ArrayList<>();
                    AtomicInteger i = new AtomicInteger(0);

                    switch (power) { //Don't read others because it is useless, AI can't take them
                        case ADM -> filePath = ClausewitzUtils.removeQuotes(technologiesItem.getVarAsString("adm_tech"));
                        case DIP -> filePath = ClausewitzUtils.removeQuotes(technologiesItem.getVarAsString("dip_tech"));
                        case MIL -> filePath = ClausewitzUtils.removeQuotes(technologiesItem.getVarAsString("mil_tech"));
                    }

                    if (filePath != null) {
                        Path path = getAbsolutePath(Eu4Utils.COMMON_FOLDER_PATH + File.separator + filePath);

                        if (path != null && path.toFile().canRead()) {
                            ClausewitzItem techItem = ClausewitzParser.parse(path.toFile(), 0);
                            Modifiers aheadOfTime = new Modifiers(techItem.getChild("ahead_of_time"));

                            techItem.getChildren("technology").forEach(item -> techs.add(new Technology(item, power, aheadOfTime, i.getAndIncrement())));
                            this.technologies.put(power, techs);
                        }
                    }
                }
            }
        }
    }

    private void readAdvisors() {
        this.advisors = new LinkedHashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "advisortypes", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem advisorsItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.advisors.putAll(advisorsItem.getChildren()
                                                     .stream()
                                                     .map(item -> new Advisor(item, this, fileNode))
                                                     .collect(Collectors.toMap(Advisor::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readIdeaGroups() {
        this.ideaGroups = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "ideas", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem ideasItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.ideaGroups.putAll(ideasItem.getChildren()
                                                    .stream()
                                                    .map(item -> new IdeaGroup(item, this))
                                                    .collect(Collectors.toMap(IdeaGroup::getName, Function.identity(), (a, b) -> b)));
                });

        this.ideas = this.ideaGroups.values()
                                    .stream()
                                    .map(IdeaGroup::getIdeas)
                                    .map(Map::keySet)
                                    .flatMap(Collection::stream)
                                    .collect(Collectors.toSet());
    }

    private void readCasusBelli() {
        this.casusBelli = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "cb_types", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cbItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.casusBelli.putAll(cbItem.getChildren()
                                                 .stream()
                                                 .map(CasusBelli::new)
                                                 .collect(Collectors.toMap(CasusBelli::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readColonialRegions() {
        this.colonialRegions = new LinkedHashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "colonial_regions", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem colonialRegionsItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.colonialRegions.putAll(colonialRegionsItem.getChildren()
                                                                   .stream()
                                                                   .map(item -> new ColonialRegion(item, fileNode, this))
                                                                   .collect(Collectors.toMap(ColonialRegion::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readTradeCompanies() {
        this.tradeCompanies = new HashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "trade_companies", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem tradeCompaniesItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.tradeCompanies.putAll(tradeCompaniesItem.getChildren()
                                                                 .stream()
                                                                 .map(item -> new TradeCompany(item, fileNode))
                                                                 .collect(Collectors.toMap(TradeCompany::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readSubjectTypes() {
        this.subjectTypes = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "subject_types", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem subjectTypesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.subjectTypes.putAll(subjectTypesItem.getChildren()
                                                             .stream()
                                                             .map(item -> new SubjectType(item, this.subjectTypes.values()))
                                                             .collect(Collectors.toMap(SubjectType::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readSubjectTypeUpgrades() {
        this.subjectTypeUpgrades = new HashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "subject_type_upgrades", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem clausewitzItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.subjectTypeUpgrades.putAll(clausewitzItem.getChildren()
                                                                  .stream()
                                                                  .map(item -> new SubjectTypeUpgrade(item, fileNode))
                                                                  .collect(Collectors.toMap(SubjectTypeUpgrade::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readFetishistCults() {
        this.fetishistCults = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "fetishist_cults", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem cultsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.fetishistCults.putAll(cultsItem.getChildren()
                                                        .stream()
                                                        .map(FetishistCult::new)
                                                        .collect(Collectors.toMap(FetishistCult::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readChurchAspects() {
        this.churchAspects = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "church_aspects", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem aspectsItems = ClausewitzParser.parse(path.toFile(), 0);
                    this.churchAspects.putAll(aspectsItems.getChildren()
                                                          .stream()
                                                          .map(ChurchAspect::new)
                                                          .collect(Collectors.toMap(ChurchAspect::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readMissionsTrees() {
        this.missionsTrees = new HashMap<>();
        this.missions = new HashMap<>();

        getFileNodesList(Eu4Utils.MISSIONS_FOLDER_PATH, this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem missionsTreeItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.missionsTrees.putAll(missionsTreeItem.getChildren()
                                                              .stream()
                                                              .map(item -> new MissionsTree(item, this, fileNode))
                                                              .collect(Collectors.toMap(MissionsTree::getName, Function.identity(), (a, b) -> b)));
                });

        this.missionsTrees.values()
                          .forEach(missionsTree -> this.missions.putAll(missionsTree.getMissions()
                                                                                    .stream()
                                                                                    .collect(Collectors.toMap(Mission::getName, Function.identity()))));

        Path guiPath = getAbsolutePath(Eu4Utils.INTERFACE_FOLDER_PATH + File.separator + "countrymissionsview.gui");

        if (guiPath != null && guiPath.toFile().exists() && guiPath.toFile().canRead()) {
            ClausewitzItem guiItem = ClausewitzParser.parse(guiPath.toFile(), 0);

            if ((guiItem = guiItem.getChild("guiTypes")) != null) {
                if ((guiItem = guiItem.getChildren("windowType")
                                      .stream()
                                      .filter(child -> "\"countrymissionsview_missions_gridbox_listbox_entry\"".equals(child.getVarAsString("name")))
                                      .findFirst()
                                      .orElse(null)) != null) {
                    if ((guiItem = guiItem.getChildren("gridBoxType")
                                          .stream()
                                          .filter(child -> "\"countrymissionsview_missions_gridbox\"".equals(child.getVarAsString("name")))
                                          .findFirst()
                                          .orElse(null)) != null) {
                        this.maxMissionsSlots = guiItem.getVarAsInt("max_slots_horizontal");
                    }
                }
            }
        }
    }

    private void readEstates() {
        //Read estates modifiers before necessary for privileges
        Map<String, List<ModifierDefinition>> modifierDefinitions = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "estates_preload", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem estatePreloadItem = ClausewitzParser.parse(path.toFile(), 0);
                    estatePreloadItem.getChildren().forEach(item -> modifierDefinitions.put(item.getName(),
                                                                                            item.getChildren("modifier_definition")
                                                                                                .stream()
                                                                                                .map(ModifierDefinition::new)
                                                                                                .toList()));
                });

        modifierDefinitions.values()
                           .stream()
                           .flatMap(Collection::stream)
                           .forEach(modifierDefinition -> ModifiersUtils.addModifier(modifierDefinition.getKey(), ModifierType.MULTIPLICATIVE,
                                                                                     ModifierScope.COUNTRY));

        this.estatePrivileges = new HashMap<>();
        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "estate_privileges", this::isRegularTxtFile)
                .forEach(path -> {
                    try {
                        ClausewitzItem estatePrivilegesItem = ClausewitzParser.parse(path.toFile(), 0);
                        this.estatePrivileges.putAll(estatePrivilegesItem.getChildren()
                                                                         .stream()
                                                                         .map(item -> new EstatePrivilege(item, this))
                                                                         .collect(Collectors.toMap(EstatePrivilege::getName, Function.identity(), (a, b) -> b)));
                    } catch (Exception e) {
                        LOGGER.error("{}", e.getMessage(), e);
                    }
                });

        this.estates = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "estates", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem estatesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.estates.putAll(estatesItem.getChildren()
                                                   .stream()
                                                   .map(item -> new Estate(item, modifierDefinitions.get(item.getName()), this))
                                                   .collect(Collectors.toMap(Estate::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readRulerPersonalities() {
        this.rulerPersonalities = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "ruler_personalities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem rulerPersonalityItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.rulerPersonalities.putAll(rulerPersonalityItem.getChildren()
                                                                       .stream()
                                                                       .map(item -> new RulerPersonality(item, this))
                                                                       .collect(Collectors.toMap(RulerPersonality::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readLeaderPersonalities() {
        this.leaderPersonalities = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "leader_personalities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem leaderPersonalityItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.leaderPersonalities.putAll(leaderPersonalityItem.getChildren()
                                                                         .stream()
                                                                         .map(LeaderPersonality::new)
                                                                         .collect(Collectors.toMap(LeaderPersonality::getName, Function.identity(),
                                                                                                   (a, b) -> b)));
                });
    }

    private void readProfessionalismModifiers() {
        this.professionalismModifiers = new TreeSet<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "professionalism", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem professionalismItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.professionalismModifiers.addAll(professionalismItem.getChildren()
                                                                            .stream()
                                                                            .map(ProfessionalismModifier::new)
                                                                            .collect(Collectors.toSet()));
                });
    }

    private void readStaticModifiers() {
        this.staticModifiers = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "static_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem modifiersItem = ClausewitzParser.parse(path.toFile(), 0);
                    modifiersItem.getChildrenNot("null_modifier").forEach(item -> {
                        if (StaticModifiers.value(item.getName()) != null) {
                            StaticModifiers.value(item.getName()).setModifiers(new Modifiers(item));
                            StaticModifier staticModifier = new StaticModifier(item);
                            this.staticModifiers.put(staticModifier.getName().toLowerCase(), staticModifier);
                        }
                    });
                });
    }

    private void readInvestments() {
        this.investments = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "tradecompany_investments", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.investments.putAll(investmentsItem.getChildren()
                                                           .stream()
                                                           .map(item -> new Investment(item, this))
                                                           .collect(Collectors.toMap(Investment::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readPolicies() {
        this.policies = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "policies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.policies.putAll(investmentsItem.getChildren()
                                                        .stream()
                                                        .map(Policy::new)
                                                        .collect(Collectors.toMap(Policy::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readHegemons() {
        this.hegemons = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "hegemons", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.hegemons.putAll(hegemonsItem.getChildren()
                                                     .stream()
                                                     .map(Hegemon::new)
                                                     .collect(Collectors.toMap(Hegemon::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readFactions() {
        this.factions = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "factions", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem hegemonsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.factions.putAll(hegemonsItem.getChildren()
                                                     .stream()
                                                     .map(Faction::new)
                                                     .collect(Collectors.toMap(Faction::getName, Function.identity(), (a, b) -> b)));

                });

        this.factions.values()
                     .forEach(faction -> ModifiersUtils.addModifier(ClausewitzUtils.removeQuotes(faction.getName()) + "_influence", ModifierType.ADDITIVE,
                                                                    ModifierScope.COUNTRY));
    }

    private void readAges() {
        this.ages = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "ages", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem agesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.ages.putAll(agesItem.getChildren()
                                             .stream()
                                             .map(Age::new)
                                             .collect(Collectors.toMap(Age::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readDefenderOfFaith() {
        this.defenderOfFaith = new TreeMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "defender_of_faith", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.defenderOfFaith.putAll(defenderOfFaithItem.getChildren()
                                                                   .stream()
                                                                   .map(DefenderOfFaith::new)
                                                                   .collect(Collectors.toMap(DefenderOfFaith::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readCentersOfTrade() {
        this.centersOfTrade = new TreeMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "centers_of_trade", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem centersOfTradeItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.centersOfTrade.putAll(centersOfTradeItem.getChildren()
                                                                 .stream()
                                                                 .map(CenterOfTrade::new)
                                                                 .collect(Collectors.toMap(CenterOfTrade::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readFervors() {
        this.fervors = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "fervor", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem fervorsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.fervors.putAll(fervorsItem.getChildren()
                                                   .stream()
                                                   .map(Fervor::new)
                                                   .collect(Collectors.toMap(Fervor::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readGreatProjects() {
        this.greatProjects = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "great_projects", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem greatProjectsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.greatProjects.putAll(greatProjectsItem.getChildren()
                                                               .stream()
                                                               .map(GreatProject::new)
                                                               .collect(Collectors.toMap(GreatProject::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readHolyOrders() {
        this.holyOrders = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "holy_orders", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem holyOrdersItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.holyOrders.putAll(holyOrdersItem.getChildren()
                                                         .stream()
                                                         .map(HolyOrder::new)
                                                         .collect(Collectors.toMap(HolyOrder::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readIsolationism() {
        this.isolationisms = new TreeMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "isolationism", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem isolationismItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.isolationisms.putAll(isolationismItem.getChildren()
                                                              .stream()
                                                              .map(Isolationism::new)
                                                              .collect(Collectors.toMap(Isolationism::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readNativeAdvancements() {
        this.nativeAdvancements = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "native_advancement", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem nativeAdvancementItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.nativeAdvancements.putAll(nativeAdvancementItem.getChildren()
                                                                        .stream()
                                                                        .map(NativeAdvancements::new)
                                                                        .collect(Collectors.toMap(NativeAdvancements::getName, Function.identity(),
                                                                                                  (a, b) -> b)));
                });
    }

    private void readNavalDoctrine() {
        this.navalDoctrines = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "naval_doctrines", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem navalDoctrineItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.navalDoctrines.putAll(navalDoctrineItem.getChildren()
                                                                .stream()
                                                                .map(NavalDoctrine::new)
                                                                .collect(Collectors.toMap(NavalDoctrine::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readParliamentIssue() {
        this.parliamentIssues = new LinkedHashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "parliament_issues", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem parliamentIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.parliamentIssues.putAll(parliamentIssueItem.getChildren()
                                                                    .stream()
                                                                    .map(ParliamentIssue::new)
                                                                    .collect(Collectors.toMap(i -> i.getName().toLowerCase(), Function.identity(),
                                                                                              (a, b) -> b)));
                });
    }

    private void readPersonalDeities() {
        this.personalDeities = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "personal_deities", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem personalIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.personalDeities.putAll(personalIssueItem.getChildren()
                                                                 .stream()
                                                                 .map(PersonalDeity::new)
                                                                 .collect(Collectors.toMap(PersonalDeity::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readReligiousReforms() {
        this.religiousReforms = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "religious_reforms", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem religiousReformItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.religiousReforms.putAll(religiousReformItem.getChildren()
                                                                    .stream()
                                                                    .map(ReligiousReforms::new)
                                                                    .collect(Collectors.toMap(ReligiousReforms::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readCrownLandBonuses() {
        this.crownLandBonuses = new TreeMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "estate_crown_land", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem defenderOfFaithItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.crownLandBonuses.putAll(defenderOfFaithItem.getChildren()
                                                                    .stream()
                                                                    .map(CrownLandBonus::new)
                                                                    .collect(Collectors.toMap(CrownLandBonus::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readStateEdicts() {
        this.stateEdicts = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "state_edicts", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem stateEdictsItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.stateEdicts.putAll(stateEdictsItem.getChildren()
                                                           .stream()
                                                           .map(StateEdict::new)
                                                           .collect(Collectors.toMap(StateEdict::getName, Function.identity(), (a, b) -> b)));
                });
    }

    private void readTradePolicies() {
        this.tradePolicies = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "trading_policies", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem tradePoliciesItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.tradePolicies.putAll(tradePoliciesItem.getChildren()
                                                               .stream()
                                                               .map(TradePolicy::new)
                                                               .collect(Collectors.toMap(TradePolicy::getName, Function.identity(), (a, b) -> b)));

                });
    }

    private void readEventModifiers() {
        this.eventModifiers = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "event_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem eventModifierItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.eventModifiers.putAll(eventModifierItem.getChildren()
                                                                .stream()
                                                                .map(EventModifier::new)
                                                                .collect(Collectors.toMap(m -> m.getName().toLowerCase(), Function.identity(), (a, b) -> b)));
                });
    }

    private void readProvinceTriggeredModifiers() {
        this.provinceTriggeredModifiers = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "province_triggered_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem provinceTriggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.provinceTriggeredModifiers.putAll(provinceTriggeredIssueItem.getChildren()
                                                                                     .stream()
                                                                                     .map(TriggeredModifier::new)
                                                                                     .collect(Collectors.toMap(m -> m.getName().toLowerCase(),
                                                                                                               Function.identity(), (a, b) -> b)));
                });
    }

    private void readTriggeredModifiers() {
        this.triggeredModifiers = new HashMap<>();

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "triggered_modifiers", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem triggeredIssueItem = ClausewitzParser.parse(path.toFile(), 0);
                    this.triggeredModifiers.putAll(triggeredIssueItem.getChildren()
                                                                     .stream()
                                                                     .map(TriggeredModifier::new)
                                                                     .collect(Collectors.toMap(m -> m.getName().toLowerCase(), Function.identity(),
                                                                                               (a, b) -> b)));

                });
    }

    private void readCountry() {
        this.countries = new HashMap<>();

        Map<String, FileNode> countriesHistory = new HashMap<>();
        getFileNodes(Eu4Utils.HISTORY_FOLDER_PATH + File.separator + "countries", this::isRegularTxtFile)
                .forEach(fileNode -> countriesHistory.put(fileNode.getPath().toFile().getName().substring(0, 3).toUpperCase(), fileNode));

        getPaths(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "country_tags", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem countryTagsItem = ClausewitzParser.parse(path.toFile(), 0);
                    countryTagsItem.getVariables()
                                   .forEach(variable -> {
                                       FileNode commonFileNode = getFileNode(Path.of(Eu4Utils.COMMON_FOLDER_PATH + File.separator
                                                                                     + ClausewitzUtils.removeQuotes(variable.getValue())).toString());
                                       Country country = new Country(variable.getName().toUpperCase(), commonFileNode,
                                                                     ClausewitzParser.parse(commonFileNode.getPath().toFile(), 0), this);
                                       FileNode historyFileNode = countriesHistory.get(country.getTag());
                                       country.setHistory(ClausewitzParser.parse(historyFileNode.getPath().toFile(), 0), historyFileNode);
                                       this.countries.put(country.getTag(), country);
                                   });
                });
    }

    private void readDiplomacy() {
        this.hreEmperors = new HashMap<>();
        this.celestialEmperors = new HashMap<>();

        getPaths(Eu4Utils.HISTORY_FOLDER_PATH + File.separator + "diplomacy", this::isRegularTxtFile)
                .forEach(path -> {
                    ClausewitzItem diplomacyItem = ClausewitzParser.parse(path.toFile(), 0);

                    diplomacyItem.getChildren()
                                 .stream()
                                 .filter(item -> ClausewitzUtils.DATE_PATTERN.matcher(item.getName()).matches())
                                 .forEach(item -> {
                                     if (item.hasVar("emperor")) {
                                         this.hreEmperors.put(ClausewitzUtils.stringToDate(ClausewitzUtils.removeQuotes(item.getName())), new HreEmperor(item));
                                     }

                                     if (item.hasVar("celestial_emperor")) {
                                         this.celestialEmperors.put(ClausewitzUtils.stringToDate(ClausewitzUtils.removeQuotes(item.getName())),
                                                                    new CelestialEmperor(item));
                                     }
                                 });
                });
    }

    private void readBookmarks() {
        this.bookmarks = new HashMap<>();

        getFileNodes(Eu4Utils.COMMON_FOLDER_PATH + File.separator + "bookmarks", this::isRegularTxtFile)
                .forEach(fileNode -> {
                    ClausewitzItem investmentsItem = ClausewitzParser.parse(fileNode.getPath().toFile(), 0);
                    this.bookmarks.putAll(investmentsItem.getChildren()
                                                         .stream()
                                                         .map(item -> new Bookmark(fileNode, item, this))
                                                         .collect(Collectors.toMap(Bookmark::getName, Function.identity())));
                });
    }

    public boolean isRegularTxtFile(FileNode fileNode) {
        return Eu4Utils.isRegularTxtFile(fileNode.getPath());
    }

    public boolean isRegularLuaFile(FileNode fileNode) {
        return Eu4Utils.isRegularLuaFile(fileNode.getPath());
    }
}
