package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientPacketMouseEvent {
	
	private boolean valid;
	
	private String id;
	private int mouseButton;
	private boolean isCtrlKeyDown;
	private boolean isAltKeyDown;
	private boolean isShiftKeyDown;
	
	public ClientPacketMouseEvent() {
		valid = false;
	}
	
	public ClientPacketMouseEvent(String id, int mouseButton, boolean isCtrlKeyDown, boolean isAltKeyDown, boolean isShiftKeyDown) {
		valid = true;
		this.id = id;
		this.mouseButton = mouseButton;
		this.isCtrlKeyDown = isCtrlKeyDown;
		this.isAltKeyDown = isAltKeyDown;
		this.isShiftKeyDown = isShiftKeyDown;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public static ClientPacketMouseEvent decode(PacketBuffer buf) {
		int len = buf.readInt();
		String data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		ClientPacketMouseEvent packet = new ClientPacketMouseEvent(data.substring(0, 3), Integer.parseInt(data.substring(3, 4)), data.substring(4, 5).equals("1"), data.substring(5, 6).equals("1"), data.substring(6, 7).equals("1"));
		return packet;
	}
	
	public void encode(PacketBuffer buf) {
		String data = new StringBuilder(id).append(mouseButton).append(isCtrlKeyDown ? 1 : 0).append(isAltKeyDown ? 1 : 0).append(isShiftKeyDown ? 1 : 0).toString();
		
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
	}
	
	public String getId() {
		return id;
	}
	
	public int getMouseButton() {
		return mouseButton;
	}
	
	public boolean isCtrlKeyDown() {
		return isCtrlKeyDown;
	}
	
	public boolean isAltKeyDown() {
		return isAltKeyDown;
	}
	
	public boolean isShiftKeyDown() {
		return isShiftKeyDown;
	}
	
	public static void onPacketReceived(final ClientPacketMouseEvent packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		
	}
}
