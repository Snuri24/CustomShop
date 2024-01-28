package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientPacketKeyEvent implements IMessage {
	
	private int key;
	
	public ClientPacketKeyEvent() {
		
	}
	
	public ClientPacketKeyEvent(int key) {
		this.key = key;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		String data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		key = Integer.parseInt(data);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		String data = Integer.toString(key);
		
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
	}
	
	public int getKey() {
		return key;
	}
	
	public static class Handle implements IMessageHandler<ClientPacketKeyEvent, IMessage> {
		@Override
		public IMessage onMessage(ClientPacketKeyEvent message, MessageContext ctx) {
			return null;
		}
	}
}
