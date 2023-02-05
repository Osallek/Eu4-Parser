package fr.osallek.eu4parser.model.game;


import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
    public Country getOwner() {
        return this.items.stream().map(ProvinceHistoryItemI::getOwner).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Country getController() {
        return this.items.stream().map(ProvinceHistoryItemI::getController).filter(Objects::nonNull).findFirst().orElse(null);
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
    public Boolean getCity() {
        return this.items.stream().map(ProvinceHistoryItemI::getCity).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Culture getCulture() {
        return this.items.stream().map(ProvinceHistoryItemI::getCulture).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Religion getReligion() {
        return this.items.stream().map(ProvinceHistoryItemI::getReligion).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getBaseTax() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseTax).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getBaseProduction() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseProduction).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getBaseManpower() {
        return this.items.stream().map(ProvinceHistoryItemI::getBaseManpower).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public TradeGood getTradeGoods() {
        return this.items.stream().map(ProvinceHistoryItemI::getTradeGoods).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Boolean getHre() {
        return this.items.stream().map(ProvinceHistoryItemI::getHre).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public String getCapital() {
        return this.items.stream().map(ProvinceHistoryItemI::getCapital).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public List<TechGroup> getDiscoveredBy() {
        return this.items.stream().map(ProvinceHistoryItemI::getDiscoveredBy).filter(CollectionUtils::isNotEmpty).findFirst().orElse(new ArrayList<>());
    }

    @Override
    public Boolean getReformationCenter() {
        return this.items.stream().map(ProvinceHistoryItemI::getReformationCenter).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Boolean getSeatInParliament() {
        return this.items.stream().map(ProvinceHistoryItemI::getSeatInParliament).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getUnrest() {
        return this.items.stream().map(ProvinceHistoryItemI::getUnrest).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getCenterOfTrade() {
        return this.items.stream().map(ProvinceHistoryItemI::getCenterOfTrade).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getExtraCost() {
        return this.items.stream().map(ProvinceHistoryItemI::getExtraCost).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getNativeSize() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeSize).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getNativeHostileness() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeHostileness).filter(Objects::nonNull).findFirst().orElse(null);
    }

    @Override
    public Integer getNativeFerocity() {
        return this.items.stream().map(ProvinceHistoryItemI::getNativeFerocity).filter(Objects::nonNull).findFirst().orElse(null);
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
    public ProvinceRevolt getRevolt() {
        return this.items.stream().map(ProvinceHistoryItemI::getRevolt).filter(Objects::nonNull).findFirst().orElse(null);
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
