package com.snuri.customshop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.snuri.customshop.gui.GuiComponent;
import com.snuri.customshop.gui.ShopScreen;
import com.snuri.customshop.network.ServerPacketGuiClose;
import com.snuri.customshop.network.ServerPacketGuiOpen;
import com.snuri.customshop.network.ServerPacketUpdate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.ClientWorld;

public class Cs {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private static Cs instance;
	
	public Minecraft mc = null;
	
	public static Cs getInstance() {
		if(instance == null) {
			instance = new Cs();
		}
		
		return instance;
	}
	
	private Cs() {
		mc = Minecraft.getInstance();
	}
	
	public void onTick() {
		
	}
	
	public void onRender() {
		if(mc.player == null)
			return;
	}
	
	/* PacketHandle */
	public void processPacket(ClientWorld clientWorld, ServerPacketGuiOpen packet) {
		String[] dataArr = packet.getData().split("//");
		int l = dataArr.length;
		
		try {
			ShopScreen screen = new ShopScreen(dataArr[0], dataArr[1].equals("1"));
			for(int i = 2; i < l; i ++) {
				GuiComponent comp = screen.getComponentMap().get(dataArr[i].substring(0, 3));
				if(comp != null) {
					comp.setValue(dataArr[i].substring(3));
				}
			}
			
			if(mc.screen != null) {
				mc.screen.onClose();
			}
			
			mc.pushGuiLayer(screen);
		} catch(Exception e) {
			for(StackTraceElement s : e.getStackTrace()) {
				LOGGER.error(s);
			}
		}
	}
	
	public void processPacket(ClientWorld clientWorld, ServerPacketGuiClose packet) {
		if(mc.screen != null) {
			mc.screen.onClose();
		}
	}
	
	public void processPacket(ClientWorld clientWorld, ServerPacketUpdate packet) {
		if(mc.screen instanceof ShopScreen) {
			try {
				ShopScreen gui = (ShopScreen) mc.screen;
				String[] dataArr = packet.getData().split("//");
				for(String data : dataArr) {
					GuiComponent comp = gui.getComponentMap().get(data.substring(0, 3));
					if(comp != null) {
						comp.setValue(data.substring(3));
					}
				}
			} catch(Exception e) {
				for(StackTraceElement s : e.getStackTrace()) {
					LOGGER.error(s);
				}
			}
		}
	}
}
