package fr.osallek.eu4parser.model.save.diplomacy;

import fr.osallek.clausewitzparser.model.ClausewitzItem;
import fr.osallek.eu4parser.model.game.SubjectType;
import fr.osallek.eu4parser.model.save.Save;
import fr.osallek.eu4parser.model.save.country.SaveCountry;

import java.time.LocalDate;
import java.util.List;

public class Diplomacy {

    private final ClausewitzItem item;

    private final Save save;

    private List<Dependency> dependencies;

    private List<DatableRelation> alliances;

    private List<DatableRelation> guarantees;

    private List<KnowledgeSharing> knowledgeSharing;

    private List<Subsidies> subsidies;

    private List<DatableRelation> royalMarriage;

    private List<MilitaryAccess> militaryAccesses;

    private List<MilitaryAccess> fleetAccesses;

    private List<CasusBelli> casusBellis;

    private List<DatableRelation> supportIndependence;

    private List<TransferTradePower> transferTradePowers;

    private List<EndDatableRelation> warReparations;

    private List<DatableRelation> warnings;

    public Diplomacy(ClausewitzItem item, Save save) {
        this.item = item;
        this.save = save;
        refreshAttributes();
    }

    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public void addDependency(SaveCountry first, SaveCountry second, LocalDate startDate, SubjectType subjectType) {
        if (second.getOverlord() == null) {
            Dependency.addToItem(this.item, first.getTag(), second.getTag(), startDate, subjectType);
            first.addSubject(second);
            second.setOverlord(first);
            second.setSubjectStartDate(startDate);
            second.setSubjectType(subjectType);
            refreshAttributes();
        }
    }

    public void removeDependency(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.dependencies.size(); i++) {
            Dependency dependency = this.dependencies.get(i);
            if (dependency.getFirst().filter(country -> country.equals(first)).isPresent() &&
                dependency.getSecond().filter(country -> country.equals(second)).isPresent()) {
                first.removeSubject(second);
                second.removeOverlord();
                this.item.removeChild("dependency", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getAlliances() {
        return alliances;
    }

    public void addAlliance(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "alliance", first.getTag(), second.getTag(), startDate);
        first.addAlly(second);
        second.addAlly(first);
        refreshAttributes();
    }

    public void removeAlliance(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.alliances.size(); i++) {
            DatableRelation alliance = this.alliances.get(i);
            if ((alliance.getFirst().filter(country -> country.equals(first)).isPresent() &&
                 alliance.getSecond().filter(country -> country.equals(second)).isPresent())
                || (alliance.getFirst().filter(country -> country.equals(second)).isPresent() &&
                    alliance.getSecond().filter(country -> country.equals(first)).isPresent())) {
                first.removeAlly(second);
                second.removeAlly(first);
                this.item.removeChild("alliance", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getGuarantees() {
        return guarantees;
    }

    public void addGuarantee(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "guarantee", first.getTag(), second.getTag(), startDate);
        first.addGuarantee(second);
        refreshAttributes();
    }

    public void removeGuarantee(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.guarantees.size(); i++) {
            DatableRelation guarantee = this.guarantees.get(i);
            if (guarantee.getFirst().filter(country -> country.equals(first)).isPresent() &&
                guarantee.getSecond().filter(country -> country.equals(second)).isPresent()) {
                first.removeGuarantee(second);
                this.item.removeChild("guarantee", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<KnowledgeSharing> getKnowledgeSharing() {
        return knowledgeSharing;
    }

    public void addKnowledgeSharing(SaveCountry first, SaveCountry second, LocalDate startDate) {
        KnowledgeSharing.addToItem(this.item, first.getTag(), second.getTag(), startDate, startDate.plusYears(10), false);
        refreshAttributes();
    }

    public void removeKnowledgeSharing(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.knowledgeSharing.size(); i++) {
            DatableRelation knowledgeSharing = this.knowledgeSharing.get(i);
            if (knowledgeSharing.getFirst().filter(country -> country.equals(first)).isPresent() &&
                knowledgeSharing.getSecond().filter(country -> country.equals(second)).isPresent()) {
                this.item.removeChild("knowledge_sharing", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<Subsidies> getSubsidies() {
        return subsidies;
    }

    public void addSubsidies(SaveCountry first, SaveCountry second, LocalDate startDate, double amount, int duration) {
        Subsidies.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, duration);
        refreshAttributes();
    }

    public void removeSubsidies(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.subsidies.size(); i++) {
            DatableRelation subsidie = this.subsidies.get(i);
            if (subsidie.getFirst().filter(country -> country.equals(first)).isPresent() &&
                subsidie.getSecond().filter(country -> country.equals(second)).isPresent()) {
                this.item.removeChild("subsidies", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getRoyalMarriage() {
        return royalMarriage;
    }

    public void addRoyalMarriage(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "royal_marriage", first.getTag(), second.getTag(), startDate);
        first.addRoyalMarriage(second);
        second.addRoyalMarriage(first);
        refreshAttributes();
    }

    public void removeRoyalMarriage(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.royalMarriage.size(); i++) {
            DatableRelation royalMarriage = this.royalMarriage.get(i);
            if ((royalMarriage.getFirst().filter(country -> country.equals(first)).isPresent() &&
                 royalMarriage.getSecond().filter(country -> country.equals(second)).isPresent())
                || (royalMarriage.getFirst().filter(country -> country.equals(second)).isPresent() &&
                    royalMarriage.getSecond().filter(country -> country.equals(first)).isPresent())) {
                first.removeRoyalMarriage(second);
                second.removeRoyalMarriage(first);
                this.item.removeChild("royal_marriage", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<MilitaryAccess> getMilitaryAccesses() {
        return militaryAccesses;
    }

    public void addMilitaryAccess(SaveCountry first, SaveCountry second, LocalDate startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "military_access", first.getTag(), second.getTag(), startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeMilitaryAccess(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.militaryAccesses.size(); i++) {
            DatableRelation militaryAccess = this.militaryAccesses.get(i);
            if (militaryAccess.getFirst().filter(country -> country.equals(first)).isPresent() &&
                militaryAccess.getSecond().filter(country -> country.equals(second)).isPresent()) {
                this.item.removeChild("military_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<MilitaryAccess> getFleetAccesses() {
        return fleetAccesses;
    }

    public void addFleetAccess(SaveCountry first, SaveCountry second, LocalDate startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "fleet_access", first.getTag(), second.getTag(), startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeFleetAccess(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.fleetAccesses.size(); i++) {
            DatableRelation access = this.fleetAccesses.get(i);
            if (access.getFirst().filter(country -> country.equals(first)).isPresent() &&
                access.getSecond().filter(country -> country.equals(second)).isPresent()) {
                this.item.removeChild("fleet_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<CasusBelli> getCasusBellis() {
        return casusBellis;
    }

    public void addCasusBelli(SaveCountry first, SaveCountry second, LocalDate startDate, LocalDate endDate, fr.osallek.eu4parser.model.game.CasusBelli type) {
        CasusBelli.addToItem(this.item, first.getTag(), second.getTag(), startDate, endDate, type);
        refreshAttributes();
    }

    public void removeCasusBelli(SaveCountry first, SaveCountry second, fr.osallek.eu4parser.model.game.CasusBelli type) {
        for (int i = 0; i < this.casusBellis.size(); i++) {
            CasusBelli casusBelli = this.casusBellis.get(i);
            if (casusBelli.getFirst().filter(country -> country.equals(first)).isPresent() &&
                casusBelli.getSecond().filter(country -> country.equals(second)).isPresent() && casusBelli.getType().equals(type)) {
                this.item.removeChild("casus_belli", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getSupportIndependence() {
        return supportIndependence;
    }

    public void addSupportIndependence(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "support_independence", first.getTag(), second.getTag(), startDate);
        second.addIndependenceSupportedBy(first);
        refreshAttributes();
    }

    public void removeSupportIndependence(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.supportIndependence.size(); i++) {
            DatableRelation support = this.supportIndependence.get(i);
            if (support.getFirst().filter(country -> country.equals(first)).isPresent() &&
                support.getSecond().filter(country -> country.equals(second)).isPresent()) {
                second.removeIndependenceSupportedBy(first);
                this.item.removeChild("support_independence", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<TransferTradePower> getTransferTradePowers() {
        return transferTradePowers;
    }

    public void addTransferTradePower(SaveCountry first, SaveCountry second, LocalDate startDate, double amount, boolean isEnforced) {
        if (second.getTransferTradePowerTo().isEmpty()) {
            TransferTradePower.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, isEnforced);
            second.addTransferTradePowerTo(first);
            first.addTransferTradePowerFrom(second);
            refreshAttributes();
        }
    }

    public void removeTransferTradePower(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.transferTradePowers.size(); i++) {
            DatableRelation transfer = this.transferTradePowers.get(i);
            if (transfer.getFirst().filter(country -> country.equals(first)).isPresent() &&
                transfer.getSecond().filter(country -> country.equals(second)).isPresent()) {
                second.removeTransferTradePowerTo(first);
                first.removeTransferTradePowerFrom(second);
                this.item.removeChild("transfer_trade_power", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<EndDatableRelation> getWarReparations() {
        return warReparations;
    }

    public void addWarReparations(SaveCountry first, SaveCountry second, LocalDate startDate, LocalDate endDate) {
        EndDatableRelation.addToItem(this.item, "war_reparations", first.getTag(), second.getTag(), startDate, endDate);
        first.addWarReparations(second);
        refreshAttributes();
    }

    public void removeWarReparations(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.warReparations.size(); i++) {
            DatableRelation warReparation = this.warReparations.get(i);
            if (warReparation.getFirst().filter(country -> country.equals(first)).isPresent() &&
                warReparation.getSecond().filter(country -> country.equals(second)).isPresent()) {
                first.removeWarReparations(second);
                this.item.removeChild("war_reparations", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getWarnings() {
        return warnings;
    }


    public void addWarning(SaveCountry first, SaveCountry second, LocalDate startDate) {
        DatableRelation.addToItem(this.item, "warning", first.getTag(), second.getTag(), startDate);
        first.addWarning(second);
        refreshAttributes();
    }

    public void removeWarning(SaveCountry first, SaveCountry second) {
        for (int i = 0; i < this.warnings.size(); i++) {
            DatableRelation warning = this.warnings.get(i);
            if (warning.getFirst().filter(country -> country.equals(first)).isPresent() &&
                warning.getSecond().filter(country -> country.equals(second)).isPresent()) {
                first.removeWarning(second);
                this.item.removeChild("warning", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<Integration> getIntegrations() {
        return this.item.getChildren("integration")
                        .stream()
                        .map(child -> new Integration(child, save))
                        .toList();
    }


    public void addIntegration(SaveCountry first, SaveCountry second, LocalDate startDate, Double progress) {
        Integration.addToItem(this.item, "integration", first.getTag(), second.getTag(), startDate, progress == null ? 0 : progress);
    }

    public void removeIntegration(SaveCountry first, SaveCountry second) {
        List<Integration> integrations = getIntegrations();
        for (int i = 0; i < integrations.size(); i++) {
            Integration integration = integrations.get(i);
            if (integration.getFirst().filter(country -> country.equals(first)).isPresent() &&
                integration.getSecond().filter(country -> country.equals(second)).isPresent()) {
                this.item.removeChild("integration", i);
                break;
            }
        }

        refreshAttributes();
    }

    private void refreshAttributes() {
        this.dependencies = this.item.getChildren("dependency")
                                     .stream()
                                     .map(child -> new Dependency(child, this.save))
                                     .toList();
        this.dependencies.forEach(dependency -> dependency.getSecond().ifPresent(country -> {
            dependency.getSubjectType().ifPresent(country::setSubjectType);
            dependency.getStartDate().ifPresent(country::setSubjectStartDate);
        }));

        this.alliances = this.item.getChildren("alliance")
                                  .stream()
                                  .map(child -> new DatableRelation(child, this.save))
                                  .toList();

        this.guarantees = this.item.getChildren("guarantee")
                                   .stream()
                                   .map(child -> new DatableRelation(child, this.save))
                                   .toList();

        this.knowledgeSharing = this.item.getChildren("knowledge_sharing")
                                         .stream()
                                         .map(child -> new KnowledgeSharing(child, this.save))
                                         .toList();

        this.subsidies = this.item.getChildren("subsidies")
                                  .stream()
                                  .map(child -> new Subsidies(child, this.save))
                                  .toList();

        this.royalMarriage = this.item.getChildren("royal_marriage")
                                      .stream()
                                      .map(child -> new DatableRelation(child, this.save))
                                      .toList();

        this.militaryAccesses = this.item.getChildren("military_access")
                                         .stream()
                                         .map(child -> new MilitaryAccess(child, this.save))
                                         .toList();

        this.fleetAccesses = this.item.getChildren("fleet_access")
                                      .stream()
                                      .map(child -> new MilitaryAccess(child, this.save))
                                      .toList();

        this.casusBellis = this.item.getChildren("casus_belli")
                                    .stream()
                                    .map(child -> new CasusBelli(child, this.save))
                                    .toList();

        this.supportIndependence = this.item.getChildren("support_independence")
                                            .stream()
                                            .map(child -> new DatableRelation(child, save))
                                            .toList();

        this.transferTradePowers = this.item.getChildren("transfer_trade_power")
                                            .stream()
                                            .map(child -> new TransferTradePower(child, save))
                                            .toList();

        this.warReparations = this.item.getChildren("war_reparations")
                                       .stream()
                                       .map(child -> new EndDatableRelation(child, save))
                                       .toList();

        this.warnings = this.item.getChildren("warning")
                                 .stream()
                                 .map(child -> new DatableRelation(child, save))
                                 .toList();
    }
}
