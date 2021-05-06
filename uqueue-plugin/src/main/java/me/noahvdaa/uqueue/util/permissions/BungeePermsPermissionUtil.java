package me.noahvdaa.uqueue.util.permissions;

import net.alpenblock.bungeeperms.BPPermission;
import net.alpenblock.bungeeperms.BungeePerms;
import net.alpenblock.bungeeperms.PermissionsManager;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BungeePermsPermissionUtil {

	public static Collection<String> getBungeePermsPermissions(ProxiedPlayer p) {
		PermissionsManager bpPermissionsManager = BungeePerms.getInstance().getPermissionsManager();

		List<BPPermission> permissions = bpPermissionsManager.getUser(p.getUniqueId()).getEffectivePerms(p.getServer().getInfo().getName(), null);
		List<String> out = new ArrayList<>();

		for (BPPermission permission : permissions) {
			out.add(permission.getPermission());
		}

		return out;
	}

}
