package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientPacketMouseEvent implements IMessage {
	
	private String id;
	private int mouseButton;
	private boolean isCtrlKeyDown;
	private boolean isAltKeyDown;
	private boolean isShiftKeyDown;
	
	public ClientPacketMouseEvent() {
		
	}
	
	public ClientPacketMouseEvent(String id, int mouseButton, boolean isCtrlKeyDown, boolean isAltKeyDown, boolean isShiftKeyDown) {
		this.id = id;
		this.mouseButton = mouseButton;
		this.isCtrlKeyDown = isCtrlKeyDown;
		this.isAltKeyDown = isAltKeyDown;
		this.isShiftKeyDown = isShiftKeyDown;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		String data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		id = data.substring(0, 3);
		mouseButton = Integer.parseInt(data.substring(3, 4));
		isCtrlKeyDown = data.substring(4, 5).equals("1");
		isAltKeyDown = data.substring(5, 6).equals("1");
		isShiftKeyDown = data.substring(6, 7).equals("1");
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
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
	
	public static class Handle implements IMessageHandler<ClientPacketMouseEvent, IMessage> {
		@Override
		public IMessage onMessage(ClientPacketMouseEvent message, MessageContext ctx) {
			return null;
		}
	}
}
