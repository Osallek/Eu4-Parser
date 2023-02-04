package fr.osallek.eu4parser.model.game;

import java.util.List;

public interface ProvinceHistoryItemI {

    Province getProvince();

    Country getOwner();

    Country getController();

    List<Country> getCumulatedCores();

    List<Country> getAddCores();

    List<Country> getRemoveCores();

    Boolean getCity();

    Culture getCulture();

    Religion getReligion();

    Integer getBaseTax();

    Integer getBaseProduction();

    Integer getBaseManpower();

    TradeGood getTradeGoods();

    Boolean getHre();

    String getCapital();

    List<TechGroup> getDiscoveredBy();

    Boolean getReformationCenter();

    Boolean getSeatInParliament();

    Integer getUnrest();

    Integer getCenterOfTrade();

    Integer getExtraCost();

    Integer getNativeSize();

    Integer getNativeHostileness();

    Integer getNativeFerocity();

    List<ModifierApply> getPermanentModifier();

    List<ModifierApply> getRemoveModifier();

    ProvinceRevolt getRevolt();

    List<Building> getBuildings();
}
