package me.noahvdaa.uqueue.impl;

import me.noahvdaa.uqueue.api.util.QueueablePlayer;
import me.noahvdaa.uqueue.api.util.QueueableServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class UQueueablePlayer implements QueueablePlayer {

	private QueueableServer server;
	private ProxiedPlayer player;
	private Integer connectionAttempts;

	public UQueueablePlayer(ProxiedPlayer p) {
		this.player = p;
	}

	@Override
	public Boolean isQueued() {
		return this.server != null;
	}

	@Override
	public QueueableServer getQueuedServer() {
		return this.server;
	}

	@Override
	public ProxiedPlayer getPlayer() {
		return this.player;
	}

	@Override
	public Integer getConnectionAttempts() {
		return this.connectionAttempts;
	}

	@Override
	public void setQueuedServer(QueueableServer server) {
		if (server == null) connectionAttempts = null;
		this.server = server;
	}

	@Override
	public void setConnectionAttempts(int newConnectionAttempts) {
		this.connectionAttempts = newConnectionAttempts;
	}
}
