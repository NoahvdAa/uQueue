package me.noahvdaa.uqueue.impl;

import me.noahvdaa.uqueue.UQueue;
import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import me.noahvdaa.uqueue.api.util.ServerStatus;
import me.noahvdaa.uqueue.util.PermissionUtil;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.*;

public class UQueueableServer implements QueueableServer {

	private String name;
	private List<UUID> queue;
	private Map<UUID, Integer> priorities;
	private boolean isHoldServer;
	private ServerStatus status;
	private Long statusLastUpdated;
	private Integer availableSlots;

	public UQueueableServer(String name) {
		this.name = name;
		this.queue = new ArrayList<UUID>();
		this.priorities = new HashMap<UUID, Integer>();
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public Integer getQueuePosition(QueueablePlayer player) {
		UUID uuid = player.getPlayer().getUniqueId();
		if (!queue.contains(uuid)) return null;
		return queue.indexOf(uuid) + 1;
	}

	@Override
	public Integer getQueueLength() {
		return queue.size();
	}

	@Override
	public Integer getCurrentPriority(QueueablePlayer player) {
		int priority = 0;

		for (String permission : PermissionUtil.getPermissions(player.getPlayer())) {
			// It's not a uQueue priority permission or the format isn't right.
			if (!permission.toLowerCase().startsWith("uqueue.priority") || permission.split("\\.").length != 4)
				continue;
			String[] parts = permission.split("\\.");
			// Extract the parts.
			String target = parts[2];
			String prio = parts[3];
			// Doesn't affect this server or priority is not a number.
			if ((!target.equalsIgnoreCase(getName()) && !target.equals("*")) || !prio.matches("\\d+")) continue;
			int parsedPrio = Integer.parseInt(prio);
			if (parsedPrio > priority) priority = parsedPrio;
		}

		return priority;
	}

	@Override
	public Integer getCurrentPriority(UUID player) {
		return getCurrentPriority(UQueue.getInstance().getPlayer(ProxyServer.getInstance().getPlayer(player)));
	}

	@Override
	public Integer getCachedPriority(QueueablePlayer player) {
		UUID uuid = player.getPlayer().getUniqueId();
		if (!priorities.containsKey(uuid)) return getCurrentPriority(player);
		return priorities.get(uuid);
	}

	@Override
	public Integer getCachedPriority(UUID player) {
		return getCachedPriority(UQueue.getInstance().getPlayer(ProxyServer.getInstance().getPlayer(player)));
	}

	@Override
	public Boolean mayQueue(QueueablePlayer player) {
		ProxiedPlayer p = player.getPlayer();
		String serverListMode = UQueue.getInstance().getConfig().getString("Queueing.ServerListMode");
		// We can use if/else here, because the config validator ensures that the setting
		// is always either 'blacklist' or 'whitelist'.
		if (serverListMode.equalsIgnoreCase("blacklist")) {
			if (UQueue.getInstance().getConfig().getStringList("Queueing.ServerList").contains(getName())) {
				return p.hasPermission("uqueue.server." + getName());
			}
		} else {
			if (!UQueue.getInstance().getConfig().getStringList("Queueing.ServerList").contains(getName())) {
				return p.hasPermission("uqueue.server." + getName());
			}
		}
		return true;
	}

	@Override
	public boolean isHoldServer() {
		return this.isHoldServer;
	}

	@Override
	public ServerStatus getStatus() {
		return this.status;
	}

	@Override
	public Long getStatusLastUpdated() {
		return this.statusLastUpdated;
	}

	@Override
	public Integer getAvailableSlots() {
		return this.availableSlots;
	}

	@Override
	public List<UUID> getQueuedPlayers() {
		return this.queue;
	}

	@Override
	public void setHoldServer(boolean holdServer) {
		this.isHoldServer = holdServer;
	}

	@Override
	public void setStatus(ServerStatus newStatus) {
		if (newStatus != this.status) this.statusLastUpdated = System.currentTimeMillis();
		this.status = newStatus;
	}

	@Override
	public void setAvailableSlots(int newAvailableSlots) {
		if (newAvailableSlots < 1) setStatus(ServerStatus.FULL);
		this.availableSlots = newAvailableSlots;
	}

	@Override
	public void addToQueue(QueueablePlayer player) {
		UUID uuid = player.getPlayer().getUniqueId();

		if (queue.contains(uuid)) return;

		int priority = getCurrentPriority(player);

		// Find queue position to insert into.
		int pos = 0;
		for (UUID queuedPlayer : queue) {
			if (priorities.get(uuid) < priority) break;
			pos++;
		}

		player.setQueuedServer(this);

		// Add player in right queue position.
		queue.add(pos, uuid);
		// Store priority for later comparison.
		priorities.put(uuid, priority);
	}

	@Override
	public void removeFromQueue(QueueablePlayer player) {
		UUID uuid = player.getPlayer().getUniqueId();

		if (!queue.contains(uuid)) return;

		player.setQueuedServer(null);

		priorities.remove(uuid);
		queue.remove(uuid);
	}

}
