package fr.danakube.danafly.hook.region.plugins;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import fr.danakube.danafly.DanaFly;
import fr.danakube.danafly.hook.region.CompatRegion;
import fr.danakube.danafly.hook.region.RegionProvider;

public class WorldGuardHook implements RegionProvider {

    private final boolean enabled;

    public WorldGuardHook(DanaFly plugin) {
        Plugin wgPlugin = Bukkit.getPluginManager().getPlugin("WorldGuard");
        this.enabled = wgPlugin != null && wgPlugin.isEnabled();
    }

    public ApplicableRegionSet getRegionSet(Location location) {
        if (!enabled || location == null || location.getWorld() == null) {
            return null;
        }
        try {
            RegionQuery query = WorldGuard.getInstance().getPlatform().getRegionContainer().createQuery();
            return query.getApplicableRegions(BukkitAdapter.adapt(location));
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public CompatRegion[] getApplicableRegions(Location loc) {
        ApplicableRegionSet set = getRegionSet(loc);
        if (set == null) {
            return new CompatRegion[0];
        }
        List<CompatRegion> list = new ArrayList<>();
        for (ProtectedRegion r : set) {
            list.add(new CompatRegion(r.getId()));
        }
        return list.toArray(new CompatRegion[0]);
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
