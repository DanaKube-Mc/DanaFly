package fr.danakube.danafly.hook;

import org.bukkit.entity.Player;
import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.time.TimeManager.Placeholder;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook {

    private static DanaFly plugin;
    private static DanaFlyExpansion danaFlyExpansion;
    private static TempFlyLegacyExpansion tempFlyExpansion;

    public static void initialize(DanaFly danaFly) {
        plugin = danaFly;
        if (danaFlyExpansion == null) {
            danaFlyExpansion = new DanaFlyExpansion();
            try {
                danaFlyExpansion.register();
            } catch (Exception ignored) {}
        }
        if (tempFlyExpansion == null) {
            tempFlyExpansion = new TempFlyLegacyExpansion();
            try {
                tempFlyExpansion.register();
            } catch (Exception ignored) {}
        }
    }

    public static String resolvePlaceholder(Player p, String identifier) {
        if (p == null || plugin == null) {
            return null;
        }
        switch (identifier.toLowerCase()) {
            case "time-formatted":
            case "time_formatted":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_FORMATTED);
            case "time-days":
            case "time_days":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_DAYS);
            case "time-hours":
            case "time_hours":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_HOURS);
            case "time-minutes":
            case "time_minutes":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_MINUTES);
            case "time-seconds":
            case "time_seconds":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_SECONDS);
            case "time-seconds-total":
            case "time_seconds_total":
                return plugin.getTimeManager().getPlaceHolder(p, Placeholder.TIME_SECONDS_TOTAL);
            case "list-name":
            case "list_name":
                return p.isOnline() && plugin.getFlightManager().getUser(p) != null
                        ? plugin.getFlightManager().getUser(p).getListPlaceholder()
                        : "";
            case "name-tag":
            case "name_tag":
                return p.isOnline() && plugin.getFlightManager().getUser(p) != null
                        ? plugin.getFlightManager().getUser(p).getTagPlaceholder()
                        : "";
            default:
                return null;
        }
    }

    public static class DanaFlyExpansion extends PlaceholderExpansion {

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public String getAuthor() {
            return "DanaKube";
        }

        @Override
        public String getIdentifier() {
            return "danafly";
        }

        @Override
        public String getVersion() {
            return plugin != null ? plugin.getDescription().getVersion() : "4.0.0";
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            return resolvePlaceholder(player, identifier);
        }
    }

    public static class TempFlyLegacyExpansion extends PlaceholderExpansion {

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public boolean canRegister() {
            return true;
        }

        @Override
        public String getAuthor() {
            return "DanaKube";
        }

        @Override
        public String getIdentifier() {
            return "tempfly";
        }

        @Override
        public String getVersion() {
            return plugin != null ? plugin.getDescription().getVersion() : "4.0.0";
        }

        @Override
        public String onPlaceholderRequest(Player player, String identifier) {
            return resolvePlaceholder(player, identifier);
        }
    }
}
