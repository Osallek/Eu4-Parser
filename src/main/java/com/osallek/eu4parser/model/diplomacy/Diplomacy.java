package com.osallek.eu4parser.model.diplomacy;

import com.osallek.clausewitzparser.common.ClausewitzUtils;
import com.osallek.clausewitzparser.model.ClausewitzItem;
import com.osallek.eu4parser.model.Save;

import java.util.Calendar;
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

    public void addDependency(String first, String second, Date startDate, SubjectType subjectType) {
        if (this.save.getCountry(ClausewitzUtils.removeQuotes(second)).getOverlord() == null) {
            Dependency.addToItem(this.item, first, second, startDate, subjectType);
            this.save.getCountry(ClausewitzUtils.removeQuotes(first)).addSubject(ClausewitzUtils.removeQuotes(second));
            this.save.getCountry(ClausewitzUtils.removeQuotes(second)).setOverlord(ClausewitzUtils.removeQuotes(first));
            refreshAttributes();
        }
    }

    public void removeDependency(String first, String second) {
        for (int i = 0; i < this.dependencies.size(); i++) {
            Dependency dependency = this.dependencies.get(i);
            if (dependency.getFirst().equals(ClausewitzUtils.addQuotes(first)) && dependency.getSecond()
                                                                                            .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeSubject(ClausewitzUtils.removeQuotes(second));
                this.save.getCountry(ClausewitzUtils.removeQuotes(second)).removeOverlord();
                this.item.removeChild("dependency", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getAlliances() {
        return alliances;
    }

    public void addAlliance(String first, String second, Date startDate) {
        DatableRelation.addToItem(this.item, "alliance", first, second, startDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(first)).addAlly(ClausewitzUtils.removeQuotes(second));
        this.save.getCountry(ClausewitzUtils.removeQuotes(second)).addAlly(ClausewitzUtils.removeQuotes(first));
        refreshAttributes();
    }

    public void removeAlliance(String first, String second) {
        for (int i = 0; i < this.alliances.size(); i++) {
            DatableRelation alliance = this.alliances.get(i);
            if (alliance.getFirst().equals(ClausewitzUtils.addQuotes(first)) && alliance.getSecond()
                                                                                        .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeAlly(ClausewitzUtils.removeQuotes(second));
                this.save.getCountry(ClausewitzUtils.removeQuotes(second))
                         .removeAlly(ClausewitzUtils.removeQuotes(first));
                this.item.removeChild("alliance", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getGuarantees() {
        return guarantees;
    }

    public void addGuarantee(String first, String second, Date startDate) {
        DatableRelation.addToItem(this.item, "guarantee", first, second, startDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(first)).addGuarantee(ClausewitzUtils.removeQuotes(second));
        refreshAttributes();
    }

    public void removeGuarantee(String first, String second) {
        for (int i = 0; i < this.guarantees.size(); i++) {
            DatableRelation guarantee = this.guarantees.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeGuarantee(ClausewitzUtils.removeQuotes(second));
                this.item.removeChild("guarantee", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<KnowledgeSharing> getKnowledgeSharing() {
        return knowledgeSharing;
    }

    public void addKnowledgeSharing(String first, String second, Date startDate) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.YEAR, 10);
        KnowledgeSharing.addToItem(this.item, first, second, startDate, calendar.getTime(), false);
        refreshAttributes();
    }

    public void removeKnowledgeSharing(String first, String second) {
        for (int i = 0; i < this.knowledgeSharing.size(); i++) {
            DatableRelation guarantee = this.knowledgeSharing.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.item.removeChild("knowledge_sharing", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<Subsidies> getSubsidies() {
        return subsidies;
    }

    public void addSubsidies(String first, String second, Date startDate, double amount, int duration) {
        Subsidies.addToItem(this.item, first, second, startDate, amount, duration);
        refreshAttributes();
    }

    public void removeSubsidies(String first, String second) {
        for (int i = 0; i < this.subsidies.size(); i++) {
            DatableRelation guarantee = this.subsidies.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.item.removeChild("subsidies", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getRoyalMarriage() {
        return royalMarriage;
    }

    public void addRoyalMarriage(String first, String second, Date startDate) {
        DatableRelation.addToItem(this.item, "royal_marriage", first, second, startDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                 .addRoyalMarriage(ClausewitzUtils.removeQuotes(second));
        refreshAttributes();
    }

    public void removeRoyalMarriage(String first, String second) {
        for (int i = 0; i < this.royalMarriage.size(); i++) {
            DatableRelation guarantee = this.royalMarriage.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeRoyalMarriage(ClausewitzUtils.removeQuotes(second));
                this.item.removeChild("royal_marriage", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<MilitaryAccess> getMilitaryAccesses() {
        return militaryAccesses;
    }

    public void addMilitaryAccess(String first, String second, Date startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "military_access", first, second, startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeMilitaryAccess(String first, String second) {
        for (int i = 0; i < this.militaryAccesses.size(); i++) {
            DatableRelation guarantee = this.militaryAccesses.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.item.removeChild("military_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<MilitaryAccess> getFleetAccesses() {
        return fleetAccesses;
    }

    public void addFleetAccess(String first, String second, Date startDate, boolean enforcePeace) {
        MilitaryAccess.addToItem(this.item, "fleet_access", first, second, startDate, enforcePeace);
        refreshAttributes();
    }

    public void removeFleetAccess(String first, String second) {
        for (int i = 0; i < this.fleetAccesses.size(); i++) {
            DatableRelation guarantee = this.fleetAccesses.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.item.removeChild("fleet_access", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<CasusBelli> getCasusBellis() {
        return casusBellis;
    }

    public void addCasusBelli(String first, String second, Date startDate, Date endDate, String type) {
        CasusBelli.addToItem(this.item, first, second, startDate, endDate, type);
        refreshAttributes();
    }

    public void removeCasusBelli(String first, String second, String type) {
        for (int i = 0; i < this.casusBellis.size(); i++) {
            CasusBelli casusBelli = this.casusBellis.get(i);
            if (casusBelli.getFirst().equals(ClausewitzUtils.addQuotes(first))
                && casusBelli.getSecond().equals(ClausewitzUtils.addQuotes(second))
                && casusBelli.getType().equalsIgnoreCase(type)) {
                this.item.removeChild("casus_belli", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getSupportIndependence() {
        return supportIndependence;
    }

    public void addSupportIndependence(String first, String second, Date startDate) {
        DatableRelation.addToItem(this.item, "support_independence", first, second, startDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(second))
                 .addIndependenceSupportedBy(ClausewitzUtils.removeQuotes(first));
        refreshAttributes();
    }

    public void removeSupportIndependence(String first, String second) {
        for (int i = 0; i < this.supportIndependence.size(); i++) {
            DatableRelation guarantee = this.supportIndependence.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(second))
                         .removeIndependenceSupportedBy(ClausewitzUtils.removeQuotes(first));
                this.item.removeChild("support_independence", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<TransferTradePower> getTransferTradePowers() {
        return transferTradePowers;
    }

    public void addTransferTradePower(String first, String second, Date startDate, double amount, boolean isEnforced) {
        if (this.save.getCountry(ClausewitzUtils.removeQuotes(second)).getTransferTradePowerTo().isEmpty()) {
            TransferTradePower.addToItem(this.item, first, second, startDate, amount, isEnforced);
            this.save.getCountry(ClausewitzUtils.removeQuotes(second))
                     .addTransferTradePowerTo(ClausewitzUtils.removeQuotes(first));
            this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                     .addTransferTradePowerFrom(ClausewitzUtils.removeQuotes(second));
            refreshAttributes();
        }
    }

    public void removeTransferTradePower(String first, String second) {
        for (int i = 0; i < this.transferTradePowers.size(); i++) {
            DatableRelation guarantee = this.transferTradePowers.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(second))
                         .removeTransferTradePowerTo(ClausewitzUtils.removeQuotes(first));
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeTransferTradePowerFrom(ClausewitzUtils.removeQuotes(second));
                this.item.removeChild("transfer_trade_power", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<EndDatableRelation> getWarReparations() {
        return warReparations;
    }

    public void addWarReparations(String first, String second, Date startDate, Date endDate) {
        EndDatableRelation.addToItem(this.item, "war_reparations", first, second, startDate, endDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                 .addWarReparations(ClausewitzUtils.removeQuotes(second));
        refreshAttributes();
    }

    public void removeWarReparations(String first, String second) {
        for (int i = 0; i < this.warReparations.size(); i++) {
            DatableRelation guarantee = this.warReparations.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeWarReparations(ClausewitzUtils.removeQuotes(second));
                this.item.removeChild("war_reparations", i);
                break;
            }
        }

        refreshAttributes();
    }

    public List<DatableRelation> getWarnings() {
        return warnings;
    }


    public void addWarning(String first, String second, Date startDate) {
        DatableRelation.addToItem(this.item, "warning", first, second, startDate);
        this.save.getCountry(ClausewitzUtils.removeQuotes(first)).addWarning(ClausewitzUtils.removeQuotes(second));
        refreshAttributes();
    }

    public void removeWarning(String first, String second) {
        for (int i = 0; i < this.warnings.size(); i++) {
            DatableRelation guarantee = this.warnings.get(i);
            if (guarantee.getFirst().equals(ClausewitzUtils.addQuotes(first)) && guarantee.getSecond()
                                                                                          .equals(ClausewitzUtils.addQuotes(second))) {
                this.save.getCountry(ClausewitzUtils.removeQuotes(first))
                         .removeWarning(ClausewitzUtils.removeQuotes(second));
                this.item.removeChild("warning", i);
                break;
            }
        }

        refreshAttributes();
    }

    private void refreshAttributes() {
        this.dependencies = this.item.getChildren("dependency")
                                     .stream()
                                     .map(Dependency::new)
                                     .collect(Collectors.toList());

        this.alliances = this.item.getChildren("alliance")
                                  .stream()
                                  .map(DatableRelation::new)
                                  .collect(Collectors.toList());

        this.guarantees = this.item.getChildren("guarantee")
                                   .stream()
                                   .map(DatableRelation::new)
                                   .collect(Collectors.toList());

        this.knowledgeSharing = this.item.getChildren("knowledge_sharing")
                                         .stream()
                                         .map(KnowledgeSharing::new)
                                         .collect(Collectors.toList());

        this.subsidies = this.item.getChildren("subsidies")
                                  .stream()
                                  .map(Subsidies::new)
                                  .collect(Collectors.toList());

        this.royalMarriage = this.item.getChildren("royal_marriage")
                                      .stream()
                                      .map(DatableRelation::new)
                                      .collect(Collectors.toList());

        this.militaryAccesses = this.item.getChildren("military_access")
                                         .stream()
                                         .map(MilitaryAccess::new)
                                         .collect(Collectors.toList());

        this.fleetAccesses = this.item.getChildren("fleet_access")
                                      .stream()
                                      .map(MilitaryAccess::new)
                                      .collect(Collectors.toList());

        this.casusBellis = this.item.getChildren("casus_belli")
                                    .stream()
                                    .map(CasusBelli::new)
                                    .collect(Collectors.toList());

        this.supportIndependence = this.item.getChildren("support_independence")
                                            .stream()
                                            .map(DatableRelation::new)
                                            .collect(Collectors.toList());

        this.transferTradePowers = this.item.getChildren("transfer_trade_power")
                                            .stream()
                                            .map(TransferTradePower::new)
                                            .collect(Collectors.toList());

        this.warReparations = this.item.getChildren("war_reparations")
                                       .stream()
                                       .map(EndDatableRelation::new)
                                       .collect(Collectors.toList());

        this.warnings = this.item.getChildren("warning")
                                 .stream()
                                 .map(DatableRelation::new)
                                 .collect(Collectors.toList());
    }
}
