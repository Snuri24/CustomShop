package com.snuri.customshop.network;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Supplier;

import com.snuri.customshop.Cs;

import net.minecraft.client.world.ClientWorld;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.network.NetworkEvent;

public class ServerPacketUpdate {
	
	private boolean valid;
	
	private String data;
	
	public ServerPacketUpdate() {
		valid = false;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public static ServerPacketUpdate decode(PacketBuffer buf) {
		int len = buf.readInt();
		String data = buf.toString(buf.readerIndex(), len, StandardCharsets.UTF_8);
		buf.readerIndex(buf.readerIndex() + len);
		
		ServerPacketUpdate packet = new ServerPacketUpdate();
		packet.data = data;
		packet.valid = true;
		
		return packet;
	}
	
	public void encode(PacketBuffer buf) {
		
	}
	
	public String getData() {
		return data;
	}
	
	public static void onPacketReceived(final ServerPacketUpdate packet, Supplier<NetworkEvent.Context> ctxSupplier) {
		NetworkEvent.Context ctx = ctxSupplier.get();
		LogicalSide sideReceived = ctx.getDirection().getReceptionSide();
		ctx.setPacketHandled(true);
		
		if(sideReceived != LogicalSide.CLIENT) {
			return;
		}
		if(!packet.isValid()) {
			return;
		}
		Optional<ClientWorld> clientWorld = LogicalSidedProvider.CLIENTWORLD.get(sideReceived);
		if(!clientWorld.isPresent()) {
			return;
		}
		
		ctx.enqueueWork(() -> Cs.getInstance().processPacket(clientWorld.get(), packet));
	}
}
