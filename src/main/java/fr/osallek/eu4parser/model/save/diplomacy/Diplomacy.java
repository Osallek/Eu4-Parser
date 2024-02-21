package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.SubjectType;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

public class Diplomacy {

    private final ClausewitzItem item;

    private final Save save;

    public Diplomacy(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
    }

    public Stream<Dependency> getDependenciesStream() {
        return this.item.getChildren("dependency")
                        .stream()
                        .map(child -> new Dependency(child, this.save));
    }

    public List<Dependency> getDependencies() {
        return getDependenciesStream().map(dependency -> {
            dependency.getSecond().setSubjectType(dependency.getSubjectType());
            dependency.getSecond().setSubjectStartDate(dependency.getStartDate());

            return dependency;
        }).toList();
    }

    public void addDependency(SaveCountry first, SaveCountry second, LocalDate startDate, SubjectType subjectType) {
        if (second.getOverlord() == null) {
            Dependency.addToItem(this.item, first.getTag(), second.getTag(), startDate, subjectType);
            first.addSubject(second);
            second.setOverlord(first);
            second.setSubjectStartDate(startDate);
            second.setSubjectType(subjectType);
        }
    }

    public void removeDependency(SaveCountry first, SaveCountry second) {
        List<Dependency> dependencies = getDependencies();
        for (int i = 0; i < dependencies.size(); i++) {
            Dependency dependency = dependencies.get(i);
            if (dependency.getFirst().equals(first) && dependency.getSecond().equals(second)) {
                first.removeSubject(second);
                second.removeOverlord();
                this.item.removeChild("dependency", i);
                break;
            }
        }
    }

    public List<DatableRelation> getAlliances() {
        return this.item.getChildren("alliance").stream().map(child -> new DatableRelation(child, this.save)).toList();
    }

    public void addAlliance(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "alliance", first.getTag(), second.getTag(), startDate);
        first.addAlly(second);
        second.addAlly(first);
    }

    public void removeAlliance(SaveCountry first, SaveCountry second) {
        List<DatableRelation> alliances = getAlliances();
        for (int i = 0; i < alliances.size(); i++) {
            DatableRelation alliance = alliances.get(i);
            if ((alliance.getFirst().equals(first) && alliance.getSecond().equals(second)) || (alliance.getFirst().equals(second) && alliance.getSecond()
                                                                                                                                             .equals(first))) {
                first.removeAlly(second);
                second.removeAlly(first);
                this.item.removeChild("alliance", i);
                break;
            }
        }
    }

    public List<DatableRelation> getGuarantees() {
        return this.item.getChildren("guarantee").stream().map(child -> new DatableRelation(child, this.save)).toList();
    }

    public void addGuarantee(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "guarantee", first.getTag(), second.getTag(), startDate);
        first.addGuarantee(second);
    }

    public void removeGuarantee(SaveCountry first, SaveCountry second) {
        List<DatableRelation> guarantees = getGuarantees();
        for (int i = 0; i < guarantees.size(); i++) {
            DatableRelation guarantee = guarantees.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                first.removeGuarantee(second);
                this.item.removeChild("guarantee", i);
                break;
            }
        }
    }

    public List<KnowledgeSharing> getKnowledgeSharing() {
        return this.item.getChildren("knowledge_sharing").stream().map(child -> new KnowledgeSharing(child, this.save)).toList();
    }

    public void addKnowledgeSharing(SaveCountry first, SaveCountry second, LocalDate startDate) {
        KnowledgeSharing.addToItem(this.item, first.getTag(), second.getTag(), startDate, startDate.plusYears(10), false);
    }

    public void removeKnowledgeSharing(SaveCountry first, SaveCountry second) {
        List<KnowledgeSharing> knowledgeSharing = getKnowledgeSharing();
        for (int i = 0; i < knowledgeSharing.size(); i++) {
            DatableRelation guarantee = knowledgeSharing.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("knowledge_sharing", i);
                break;
            }
        }
    }

    public List<Subsidies> getSubsidies() {
        return this.item.getChildren("subsidies").stream().map(child -> new Subsidies(child, this.save)).toList();
    }

    public void addSubsidies(SaveCountry first, SaveCountry second, LocalDate startDate, double amount, int duration) {
        Subsidies.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, duration);
    }

    public void removeSubsidies(SaveCountry first, SaveCountry second) {
        List<Subsidies> subsidies = getSubsidies();
        for (int i = 0; i < subsidies.size(); i++) {
            DatableRelation guarantee = subsidies.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("subsidies", i);
                break;
            }
        }
    }

    public List<DatableRelation> getRoyalMarriage() {
        return this.item.getChildren("royal_marriage").stream().map(child -> new DatableRelation(child, this.save)).toList();
    }

    public void addRoyalMarriage(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "royal_marriage", first.getTag(), second.getTag(), startDate);
        first.addRoyalMarriage(second);
        second.addRoyalMarriage(first);
    }

    public void removeRoyalMarriage(SaveCountry first, SaveCountry second) {
        List<DatableRelation> royalMarriage = getRoyalMarriage();
        for (int i = 0; i < royalMarriage.size(); i++) {
            DatableRelation guarantee = royalMarriage.get(i);
            if ((guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) || (guarantee.getFirst().equals(second) && guarantee.getSecond()
                                                                                                                                                 .equals(first))) {
                first.removeRoyalMarriage(second);
                second.removeRoyalMarriage(first);
                this.item.removeChild("royal_marriage", i);
                break;
            }
        }
    }

    public List<MilitaryAccess> getMilitaryAccesses() {
        return this.item.getChildren("military_access").stream().map(child -> new MilitaryAccess(child, this.save)).toList();
    }

    public void addMilitaryAccess(SaveCountry first, SaveCountry second, LocalDate startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "military_access", first.getTag(), second.getTag(), startDate, enforcePeace);
    }

    public void removeMilitaryAccess(SaveCountry first, SaveCountry second) {
        List<MilitaryAccess> militaryAccesses = getMilitaryAccesses();
        for (int i = 0; i < militaryAccesses.size(); i++) {
            DatableRelation guarantee = militaryAccesses.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("military_access", i);
                break;
            }
        }
    }

    public List<MilitaryAccess> getFleetAccesses() {
        return this.item.getChildren("fleet_access").stream().map(child -> new MilitaryAccess(child, this.save)).toList();
    }

    public void addFleetAccess(SaveCountry first, SaveCountry second, LocalDate startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "fleet_access", first.getTag(), second.getTag(), startDate, enforcePeace);
    }

    public void removeFleetAccess(SaveCountry first, SaveCountry second) {
        List<MilitaryAccess> fleetAccesses = getFleetAccesses();
        for (int i = 0; i < fleetAccesses.size(); i++) {
            DatableRelation guarantee = fleetAccesses.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("fleet_access", i);
                break;
            }
        }
    }

    public List<CasusBelli> getCasusBellis() {
        return this.item.getChildren("casus_belli").stream().map(child -> new CasusBelli(child, this.save)).toList();
    }

    public void addCasusBelli(SaveCountry first, SaveCountry second, LocalDate startDate, LocalDate endDate, fr.osallek.eu4parser.model.game.CasusBelli type) {
        CasusBelli.addToItem(this.item, first.getTag(), second.getTag(), startDate, endDate, type);
    }

    public void removeCasusBelli(SaveCountry first, SaveCountry second, fr.osallek.eu4parser.model.game.CasusBelli type) {
        List<CasusBelli> casusBellis = getCasusBellis();
        for (int i = 0; i < casusBellis.size(); i++) {
            CasusBelli casusBelli = casusBellis.get(i);
            if (casusBelli.getFirst().equals(first) && casusBelli.getSecond().equals(second) && casusBelli.getType().equals(type)) {
                this.item.removeChild("casus_belli", i);
                break;
            }
        }
    }

    public List<DatableRelation> getSupportIndependence() {
        return this.item.getChildren("support_independence").stream().map(child -> new DatableRelation(child, save)).toList();
    }

    public void addSupportIndependence(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "support_independence", first.getTag(), second.getTag(), startDate);
        second.addIndependenceSupportedBy(first);
    }

    public void removeSupportIndependence(SaveCountry first, SaveCountry second) {
        List<DatableRelation> supportIndependence = getSupportIndependence();
        for (int i = 0; i < supportIndependence.size(); i++) {
            DatableRelation guarantee = supportIndependence.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                second.removeIndependenceSupportedBy(first);
                this.item.removeChild("support_independence", i);
                break;
            }
        }
    }

    public List<TransferTradePower> getTransferTradePowers() {
        return this.item.getChildren("transfer_trade_power").stream().map(child -> new TransferTradePower(child, save)).toList();
    }

    public void addTransferTradePower(SaveCountry first, SaveCountry second, LocalDate startDate, double amount, boolean isEnforced) {
        if (second.getTransferTradePowerTo().isEmpty()) {
            TransferTradePower.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, isEnforced);
            second.addTransferTradePowerTo(first);
            first.addTransferTradePowerFrom(second);
        }
    }

    public void removeTransferTradePower(SaveCountry first, SaveCountry second) {
        List<TransferTradePower> transferTradePowers = getTransferTradePowers();
        for (int i = 0; i < transferTradePowers.size(); i++) {
            DatableRelation guarantee = transferTradePowers.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                second.removeTransferTradePowerTo(first);
                first.removeTransferTradePowerFrom(second);
                this.item.removeChild("transfer_trade_power", i);
                break;
            }
        }
    }

    public List<EndDatableRelation> getWarReparations() {
        return this.item.getChildren("war_reparations").stream().map(child -> new EndDatableRelation(child, save)).toList();
    }

    public void addWarReparations(SaveCountry first, SaveCountry second, LocalDate startDate, LocalDate endDate) {
        EndDatableRelation.addToItem(this.item, "war_reparations", first.getTag(), second.getTag(), startDate, endDate);
        first.addWarReparations(second);
    }

    public void removeWarReparations(SaveCountry first, SaveCountry second) {
        List<EndDatableRelation> warReparations = getWarReparations();
        for (int i = 0; i < warReparations.size(); i++) {
            DatableRelation guarantee = warReparations.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                first.removeWarReparations(second);
                this.item.removeChild("war_reparations", i);
                break;
            }
        }
    }

    public List<DatableRelation> getWarnings() {
        return this.item.getChildren("warning").stream().map(child -> new DatableRelation(child, save)).toList();
    }

    public void addWarning(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "warning", first.getTag(), second.getTag(), startDate);
        first.addWarning(second);
    }

    public void removeWarning(SaveCountry first, SaveCountry second) {
        List<DatableRelation> warnings = getWarnings();
        for (int i = 0; i < warnings.size(); i++) {
            DatableRelation guarantee = warnings.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                first.removeWarning(second);
                this.item.removeChild("warning", i);
                break;
            }
        }
    }

    public List<Integration> getIntegrations() {
        return this.item.getChildren("integration").stream().map(child -> new Integration(child, save)).toList();
    }

    public void addIntegration(SaveCountry first, SaveCountry second, LocalDate startDate, Double progress) {
        Integration.addToItem(this.item, "integration", first.getTag(), second.getTag(), startDate, progress == null ? 0 : progress);
    }

    public void removeIntegration(SaveCountry first, SaveCountry second) {
        List<Integration> integrations = getIntegrations();
        for (int i = 0; i < integrations.size(); i++) {
            Integration integration = integrations.get(i);
            if (integration.getFirst().equals(first) && integration.getSecond().equals(second)) {
                this.item.removeChild("integration", i);
                break;
            }
        }
    }
}
