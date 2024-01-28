package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;

import com.snuri.customshop.Cs;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ServerPacketGuiClose implements IMessage {
	
	public ServerPacketGuiClose() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		
	}
	
	public static class Handle implements IMessageHandler<ServerPacketGuiClose, IMessage> {
		@Override
		public IMessage onMessage(ServerPacketGuiClose message, MessageContext ctx) {
			Cs.getInstance().handleMessage(message);
			return null;
		}
	}
}
