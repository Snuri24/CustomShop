package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;

import com.snuri.customshop.Cs;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerPacketGuiOpen implements IMessage {
	
	private String data;
	
	public ServerPacketGuiOpen() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public String getData() {
		return data;
	}
	
	public static class Handle implements IMessageHandler<ServerPacketGuiOpen, IMessage> {
		@Override
		public IMessage onMessage(ServerPacketGuiOpen message, MessageContext ctx) {
			Cs.getInstance().handleMessage(message);
			return null;
		}
	}
}
