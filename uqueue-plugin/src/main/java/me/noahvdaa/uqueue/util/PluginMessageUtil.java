package me.noahvdaa.uqueue.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;

public class PluginMessageUtil {

	public static class PluginMessage {
		public boolean queued;
		public String server;
		public String serverDisplayName;
		public int queuePosition;
		public int queueLength;
	}

	public static PluginMessage parseBytes(byte[] bytes) {
		PluginMessage out = new PluginMessage();

		DataInputStream in = new DataInputStream(new ByteArrayInputStream(bytes));

		try {
			out.queued = in.readBoolean();
			out.server = in.readUTF();
			out.serverDisplayName = in.readUTF();
			out.queuePosition = in.readInt();
			out.queueLength = in.readInt();
		} catch (IOException e) {
			return null;
		}

		return out;
	}

	public static byte[] toBytes(boolean queued, String server, String serverDisplayName, int queuePosition, int queueLength){
		ByteArrayDataOutput out = ByteStreams.newDataOutput();

		out.writeBoolean(queued);
		out.writeUTF(server);
		out.writeUTF(serverDisplayName);
		out.writeInt(queuePosition);
		out.writeInt(queueLength);

		return out.toByteArray();
	}

}
