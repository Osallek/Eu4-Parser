package com.osallek.eu4parser.model.save.diplomacy;

import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.save.Save;
import com.osallek.eu4parser.model.save.country.Country;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

    public void addDependency(Country first, Country second, Date startDate, String subjectType) {
        if (second.getOverlord() == null) {
            Dependency.addToItem(this.item, first.getTag(), second.getTag(), startDate, subjectType);
            first.addSubject(second);
            second.setOverlord(first);
            refreshAttributes();
        }
    }

    public void removeDependency(Country first, Country second) {
        for (int i = 0; i < this.dependencies.size(); i++) {
            Dependency dependency = this.dependencies.get(i);
            if (dependency.getFirst().equals(first) && dependency.getSecond().equals(second)) {
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

    public void addAlliance(Country first, Country second, Date startDate) {
        DatableRelation.addToItem(this.item, "alliance", first.getTag(), second.getTag(), startDate);
        first.addAlly(second);
        second.addAlly(first);
        refreshAttributes();
    }

    public void removeAlliance(Country first, Country second) {
        for (int i = 0; i < this.alliances.size(); i++) {
            DatableRelation alliance = this.alliances.get(i);
            if ((alliance.getFirst().equals(first) && alliance.getSecond().equals(second))
                || (alliance.getFirst().equals(second) && alliance.getSecond().equals(first))) {
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

    public void addGuarantee(Country first, Country second, Date startDate) {
        DatableRelation.addToItem(this.item, "guarantee", first.getTag(), second.getTag(), startDate);
        first.addGuarantee(second);
        refreshAttributes();
    }

    public void removeGuarantee(Country first, Country second) {
        for (int i = 0; i < this.guarantees.size(); i++) {
            DatableRelation guarantee = this.guarantees.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
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

    public void addKnowledgeSharing(Country first, Country second, Date startDate) {
        KnowledgeSharing.addToItem(this.item, first.getTag(), second.getTag(), startDate, DateUtils.addYears(startDate, 10), false);
        refreshAttributes();
    }

    public void removeKnowledgeSharing(Country first, Country second) {
        for (int i = 0; i < this.knowledgeSharing.size(); i++) {
            DatableRelation guarantee = this.knowledgeSharing.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("knowledge_sharing", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<Subsidies> getSubsidies() {
        return subsidies;
    }

    public void addSubsidies(Country first, Country second, Date startDate, double amount, int duration) {
        Subsidies.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, duration);
        refreshAttributes();
    }

    public void removeSubsidies(Country first, Country second) {
        for (int i = 0; i < this.subsidies.size(); i++) {
            DatableRelation guarantee = this.subsidies.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("subsidies", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getRoyalMarriage() {
        return royalMarriage;
    }

    public void addRoyalMarriage(Country first, Country second, Date startDate) {
        DatableRelation.addToItem(this.item, "royal_marriage", first.getTag(), second.getTag(), startDate);
        first.addRoyalMarriage(second);
        second.addRoyalMarriage(first);
        refreshAttributes();
    }

    public void removeRoyalMarriage(Country first, Country second) {
        for (int i = 0; i < this.royalMarriage.size(); i++) {
            DatableRelation guarantee = this.royalMarriage.get(i);
            if ((guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second))
                || (guarantee.getFirst().equals(second) && guarantee.getSecond().equals(first))) {
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

    public void addMilitaryAccess(Country first, Country second, Date startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "military_access", first.getTag(), second.getTag(), startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeMilitaryAccess(Country first, Country second) {
        for (int i = 0; i < this.militaryAccesses.size(); i++) {
            DatableRelation guarantee = this.militaryAccesses.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("military_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<MilitaryAccess> getFleetAccesses() {
        return fleetAccesses;
    }

    public void addFleetAccess(Country first, Country second, Date startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "fleet_access", first.getTag(), second.getTag(), startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeFleetAccess(Country first, Country second) {
        for (int i = 0; i < this.fleetAccesses.size(); i++) {
            DatableRelation guarantee = this.fleetAccesses.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                this.item.removeChild("fleet_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<CasusBelli> getCasusBellis() {
        return casusBellis;
    }

    public void addCasusBelli(Country first, Country second, Date startDate, Date endDate, String type) {
        CasusBelli.addToItem(this.item, first.getTag(), second.getTag(), startDate, endDate, type);
        refreshAttributes();
    }

    public void removeCasusBelli(Country first, Country second, String type) {
        for (int i = 0; i < this.casusBellis.size(); i++) {
            CasusBelli casusBelli = this.casusBellis.get(i);
            if (casusBelli.getFirst().equals(first) && casusBelli.getSecond().equals(second) && casusBelli.getType().equalsIgnoreCase(type)) {
                this.item.removeChild("casus_belli", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getSupportIndependence() {
        return supportIndependence;
    }

    public void addSupportIndependence(Country first, Country second, Date startDate) {
        DatableRelation.addToItem(this.item, "support_independence", first.getTag(), second.getTag(), startDate);
        second.addIndependenceSupportedBy(first);
        refreshAttributes();
    }

    public void removeSupportIndependence(Country first, Country second) {
        for (int i = 0; i < this.supportIndependence.size(); i++) {
            DatableRelation guarantee = this.supportIndependence.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
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

    public void addTransferTradePower(Country first, Country second, Date startDate, double amount, boolean isEnforced) {
        if (second.getTransferTradePowerTo().isEmpty()) {
            TransferTradePower.addToItem(this.item, first.getTag(), second.getTag(), startDate, amount, isEnforced);
            second.addTransferTradePowerTo(first);
            first.addTransferTradePowerFrom(second);
            refreshAttributes();
        }
    }

    public void removeTransferTradePower(Country first, Country second) {
        for (int i = 0; i < this.transferTradePowers.size(); i++) {
            DatableRelation guarantee = this.transferTradePowers.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
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

    public void addWarReparations(Country first, Country second, Date startDate, Date endDate) {
        EndDatableRelation.addToItem(this.item, "war_reparations", first.getTag(), second.getTag(), startDate, endDate);
        first.addWarReparations(second);
        refreshAttributes();
    }

    public void removeWarReparations(Country first, Country second) {
        for (int i = 0; i < this.warReparations.size(); i++) {
            DatableRelation guarantee = this.warReparations.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
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


    public void addWarning(Country first, Country second, Date startDate) {
        DatableRelation.addToItem(this.item, "warning", first.getTag(), second.getTag(), startDate);
        first.addWarning(second);
        refreshAttributes();
    }

    public void removeWarning(Country first, Country second) {
        for (int i = 0; i < this.warnings.size(); i++) {
            DatableRelation guarantee = this.warnings.get(i);
            if (guarantee.getFirst().equals(first) && guarantee.getSecond().equals(second)) {
                first.removeWarning(second);
                this.item.removeChild("warning", i);
                break;
            }
        }

        refreshAttributes();
    }

    private void refreshAttributes() {
        this.dependencies = this.item.getChildren("dependency")
                                     .stream()
                                     .map(child -> new Dependency(child, this.save))
                                     .collect(Collectors.toList());

        this.alliances = this.item.getChildren("alliance")
                                  .stream()
                                  .map(child -> new DatableRelation(child, this.save))
                                  .collect(Collectors.toList());

        this.guarantees = this.item.getChildren("guarantee")
                                   .stream()
                                   .map(child -> new DatableRelation(child, this.save))
                                   .collect(Collectors.toList());

        this.knowledgeSharing = this.item.getChildren("knowledge_sharing")
                                         .stream()
                                         .map(child -> new KnowledgeSharing(child, this.save))
                                         .collect(Collectors.toList());

        this.subsidies = this.item.getChildren("subsidies")
                                  .stream()
                                  .map(child -> new Subsidies(child, this.save))
                                  .collect(Collectors.toList());

        this.royalMarriage = this.item.getChildren("royal_marriage")
                                      .stream()
                                      .map(child -> new DatableRelation(child, this.save))
                                      .collect(Collectors.toList());

        this.militaryAccesses = this.item.getChildren("military_access")
                                         .stream()
                                         .map(child -> new MilitaryAccess(child, this.save))
                                         .collect(Collectors.toList());

        this.fleetAccesses = this.item.getChildren("fleet_access")
                                      .stream()
                                      .map(child -> new MilitaryAccess(child, this.save))
                                      .collect(Collectors.toList());

        this.casusBellis = this.item.getChildren("casus_belli")
                                    .stream()
                                    .map(child -> new CasusBelli(child, this.save))
                                    .collect(Collectors.toList());

        this.supportIndependence = this.item.getChildren("support_independence")
                                            .stream()
                                            .map(child -> new DatableRelation(child, save))
                                            .collect(Collectors.toList());

        this.transferTradePowers = this.item.getChildren("transfer_trade_power")
                                            .stream()
                                            .map(child -> new TransferTradePower(child, save))
                                            .collect(Collectors.toList());

        this.warReparations = this.item.getChildren("war_reparations")
                                       .stream()
                                       .map(child -> new EndDatableRelation(child, save))
                                       .collect(Collectors.toList());

        this.warnings = this.item.getChildren("warning")
                                 .stream()
                                 .map(child -> new DatableRelation(child, save))
                                 .collect(Collectors.toList());
    }
}
