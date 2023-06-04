package fr.osallek.eu4parser.model.game;

import java.util.List;
import java.util.Optional;

public interface ProvinceHistoryItemI {

    Province getProvince();

    Optional<Country> getOwner();

    Optional<Country> getController();

    List<Country> getCumulatedCores();

    List<Country> getAddCores();

    List<Country> getRemoveCores();

    Optional<Boolean> getCity();

    Optional<Culture> getCulture();

    Optional<Religion> getReligion();

    Optional<Integer> getBaseTax();

    Optional<Integer> getBaseProduction();

    Optional<Integer> getBaseManpower();

    Optional<TradeGood> getTradeGoods();

    Optional<Boolean> getHre();

    Optional<String> getCapital();

    List<TechGroup> getDiscoveredBy();

    Optional<Boolean> getReformationCenter();

    Optional<Boolean> getSeatInParliament();

    Optional<Integer> getUnrest();

    Optional<Integer> getCenterOfTrade();

    Optional<Integer> getExtraCost();

    Optional<Integer> getNativeSize();

    Optional<Integer> getNativeHostileness();

    Optional<Integer> getNativeFerocity();

    List<ModifierApply> getPermanentModifier();

    List<ModifierApply> getRemoveModifier();

    Optional<ProvinceRevolt> getRevolt();

    List<Building> getBuildings();

    List<Building> getRemoveBuildings();

    List<Building> getCumulatedBuildings();
}
