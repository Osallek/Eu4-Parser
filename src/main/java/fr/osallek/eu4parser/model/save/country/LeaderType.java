package fr.osallek.eu4parser.model.save.country;

public enum LeaderType {
    GENERAL, ADMIRAL, CONQUISTADOR, EXPLORER;

    public static LeaderType value(String s) {
        try {
            return LeaderType.valueOf(s);
        } catch (Exception e) {
            return null;
        }
    }
}
