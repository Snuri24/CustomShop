package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ClientPacketGuiClose implements IMessage {
	
	public ClientPacketGuiClose() {
		
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int len = buf.readInt();
		buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		String data = new StringBuilder().toString();
		
		byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
	}
	
	public static class Handle implements IMessageHandler<ClientPacketGuiClose, IMessage> {
		@Override
		public IMessage onMessage(ClientPacketGuiClose message, MessageContext ctx) {
			return null;
		}
	}
}
