package me.noahvdaa.uqueue.util.permissions;

import me.TechsCode.UltraPermissions.UltraPermissionsAPI;
import me.TechsCode.UltraPermissions.bungee.UltraPermissionsBungee;
import me.TechsCode.UltraPermissions.storage.objects.Permission;
import me.TechsCode.UltraPermissions.storage.objects.User;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UltraPermissionsPermissionUtil {

	public static Collection<String> getPermissions(ProxiedPlayer p) {
		UltraPermissionsAPI api = UltraPermissionsBungee.getAPI();
		User user = api.getUsers().uuid(p.getUniqueId()).get();

		List<String> out = new ArrayList<>();

		for(Permission permission : user.getPermissions().bungee()){
			if(!permission.isPositive()) continue;
			out.add(permission.getName());
		}
		for(Permission permission : user.getAdditionalPermissions().bungee()){
			if(!permission.isPositive()) continue;
			out.add(permission.getName());
		}

		return out;
	}

}
