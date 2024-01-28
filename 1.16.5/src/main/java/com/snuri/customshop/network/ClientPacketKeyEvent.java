package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientPacketKeyEvent {
	
	private boolean valid;
	
	private int key;
	
	public ClientPacketKeyEvent() {
		valid = false;
	}
	
	public ClientPacketKeyEvent(int key) {
		valid = true;
		this.key = key;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public static ClientPacketKeyEvent decode(PacketBuffer buf) {
		int len = buf.readInt();
		String data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		ClientPacketKeyEvent packet = new ClientPacketKeyEvent(Integer.parseInt(data));
		return packet;
	}
	
	public void encode(PacketBuffer buf) {
		String data = Integer.toString(key);
		
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
	}
	
	public int getKey() {
		return key;
	}
	
	public static void onPacketReceived(final ClientPacketKeyEvent packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		
	}
}
