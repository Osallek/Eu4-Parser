module fr.osallek.eu4parser {
    requires fr.osallek.clausewitzparser;
    requires org.apache.commons.collections4;
    requires org.apache.commons.lang3;
    requires org.apache.commons.io;
    requires java.desktop;
    requires org.slf4j;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.core;
    requires org.apache.logging.log4j.slf4j;

    exports fr.osallek.eu4parser;
    exports fr.osallek.eu4parser.common;
    exports fr.osallek.eu4parser.model;
    exports fr.osallek.eu4parser.model.game;
    exports fr.osallek.eu4parser.model.game.localisation;
    exports fr.osallek.eu4parser.model.save;
    exports fr.osallek.eu4parser.model.save.changeprices;
    exports fr.osallek.eu4parser.model.save.combat;
    exports fr.osallek.eu4parser.model.save.counters;
    exports fr.osallek.eu4parser.model.save.country;
    exports fr.osallek.eu4parser.model.save.diplomacy;
    exports fr.osallek.eu4parser.model.save.empire;
    exports fr.osallek.eu4parser.model.save.events;
    exports fr.osallek.eu4parser.model.save.gameplayoptions;
    exports fr.osallek.eu4parser.model.save.institutions;
    exports fr.osallek.eu4parser.model.save.province;
    exports fr.osallek.eu4parser.model.save.religion;
    exports fr.osallek.eu4parser.model.save.revolution;
    exports fr.osallek.eu4parser.model.save.trade;
    exports fr.osallek.eu4parser.model.save.war;
}
