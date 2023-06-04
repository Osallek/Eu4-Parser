package fr.osallek.eu4parser.model.game;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProvinceHistoryItems implements ProvinceHistoryItemI {

    private final List<ProvinceHistoryItemI> items;

    private final Province province;

    public ProvinceHistoryItems(List<ProvinceHistoryItemI> items, Province province) {
        this.items = items;
        this.province = province;
    }

    @Override
    public Province getProvince() {
        return this.province;
    }

    @Override
    public Optional<Country> getOwner() {
        return this.items.stream().map(ProvinceHistoryItemI::getOwner).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Country> getController() {
        return this.items.stream().map(ProvinceHistoryItemI::getController).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<Country> getCumulatedCores() {
        List<Country> countries = new ArrayList<>();

        for (ProvinceHistoryItemI item : this.items) {
            countries.addAll(item.getAddCores());
            countries.removeAll(item.getRemoveCores());
        }

        return countries;
    }

    @Override
    public List<Country> getAddCores() {
        return this.items.stream().map(ProvinceHistoryItemI::getAddCores).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Country> getRemoveCores() {
        return this.items.stream().map(ProvinceHistoryItemI::getRemoveCores).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Optional<Boolean> getCity() {
        return this.items.stream().map(ProvinceHistoryItemI::getCity).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Culture> getCulture() {
        return this.items.stream().map(ProvinceHistoryItemI::getCulture).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Religion> getReligion() {
        return this.items.stream().map(ProvinceHistoryItemI::getReligion).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getBaseTax() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseTax).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getBaseProduction() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseProduction).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getBaseManpower() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseManpower).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<TradeGood> getTradeGoods() {
        return this.items.stream().map(ProvinceHistoryItemI::getTradeGoods).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Boolean> getHre() {
        return this.items.stream().map(ProvinceHistoryItemI::getHre).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<String> getCapital() {
        return this.items.stream().map(ProvinceHistoryItemI::getCapital).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<TechGroup> getDiscoveredBy() {
        return this.items.stream().map(ProvinceHistoryItemI::getDiscoveredBy).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Optional<Boolean> getReformationCenter() {
        return this.items.stream().map(ProvinceHistoryItemI::getReformationCenter).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Boolean> getSeatInParliament() {
        return this.items.stream().map(ProvinceHistoryItemI::getSeatInParliament).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getUnrest() {
        return this.items.stream().map(ProvinceHistoryItemI::getUnrest).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getCenterOfTrade() {
        return this.items.stream().map(ProvinceHistoryItemI::getCenterOfTrade).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getExtraCost() {
        return this.items.stream().map(ProvinceHistoryItemI::getExtraCost).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getNativeSize() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeSize).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getNativeHostileness() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeHostileness).flatMap(Optional::stream).findFirst();
    }

    @Override
    public Optional<Integer> getNativeFerocity() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeFerocity).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<ModifierApply> getPermanentModifier() {
        return this.items.stream().map(ProvinceHistoryItemI::getPermanentModifier).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<ModifierApply> getRemoveModifier() {
        return this.items.stream().map(ProvinceHistoryItemI::getRemoveModifier).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Optional<ProvinceRevolt> getRevolt() {
        return this.items.stream().map(ProvinceHistoryItemI::getRevolt).flatMap(Optional::stream).findFirst();
    }

    @Override
    public List<Building> getBuildings() {
        return this.items.stream().map(ProvinceHistoryItemI::getBuildings).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Building> getRemoveBuildings() {
        return this.items.stream().map(ProvinceHistoryItemI::getRemoveBuildings).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public List<Building> getCumulatedBuildings() {
        List<Building> buildings = new ArrayList<>();

        for (ProvinceHistoryItemI item : this.items) {
            buildings.addAll(item.getBuildings());
            buildings.removeAll(item.getRemoveBuildings());
        }

        return buildings;
    }
}
