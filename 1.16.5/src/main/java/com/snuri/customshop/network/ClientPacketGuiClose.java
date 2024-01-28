package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientPacketGuiClose {
	
	private boolean valid;
	
	public ClientPacketGuiClose() {
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public static ClientPacketGuiClose decode(PacketBuffer buf) {
		int len = buf.readInt();
		buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		ClientPacketGuiClose packet = new ClientPacketGuiClose();
		packet.valid = true;
		
		return packet;
	}
	
	public void encode(PacketBuffer buf) {
		String data = new StringBuilder().toString();
		
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
	}
	
	public static void onPacketReceived(final ClientPacketGuiClose packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		
	}
}
