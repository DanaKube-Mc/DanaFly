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
            case "max-time":
            case "max_time": {
                double max = plugin.getTimeManager().getMaxTime(p.getUniqueId());
                return max == -1 ? "-1" : String.valueOf((long) max);
            }
            case "max-time-formatted":
            case "max_time_formatted": {
                double max = plugin.getTimeManager().getMaxTime(p.getUniqueId());
                if (max == -1) {
                    return fr.danakube.danafly.util.V.infinity != null ? fr.danakube.danafly.util.V.infinity : "Illimité";
                }
                return plugin.getTimeManager().regexString("{FORMATTED_TIME}", max);
            }
            case "time-ratio":
            case "time_ratio": {
                double max = plugin.getTimeManager().getMaxTime(p.getUniqueId());
                double current = plugin.getTimeManager().getTime(p.getUniqueId());
                String curFmt = plugin.getTimeManager().regexString("{FORMATTED_TIME}", current);
                if (max == -1) {
                    String inf = fr.danakube.danafly.util.V.infinity != null ? fr.danakube.danafly.util.V.infinity : "Illimité";
                    return curFmt + " / " + inf;
                }
                String maxFmt = plugin.getTimeManager().regexString("{FORMATTED_TIME}", max);
                return curFmt + " / " + maxFmt;
            }
            case "time-percent":
            case "time_percent": {
                double max = plugin.getTimeManager().getMaxTime(p.getUniqueId());
                double current = plugin.getTimeManager().getTime(p.getUniqueId());
                if (max <= 0) {
                    return "100%";
                }
                long pct = Math.min(100, Math.round((current / max) * 100.0));
                return pct + "%";
            }
            case "time-remaining-reserve":
            case "time_remaining_reserve": {
                double max = plugin.getTimeManager().getMaxTime(p.getUniqueId());
                if (max == -1) {
                    return "-1";
                }
                double current = plugin.getTimeManager().getTime(p.getUniqueId());
                double remaining = max - current;
                return remaining <= 0 ? "0" : String.valueOf((long) remaining);
            }
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
