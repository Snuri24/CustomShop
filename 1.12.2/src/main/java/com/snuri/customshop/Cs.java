package com.snuri.customshop;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.snuri.customshop.gui.GuiComponent;
import com.snuri.customshop.gui.ShopGui;
import com.snuri.customshop.network.ServerPacketGuiClose;
import com.snuri.customshop.network.ServerPacketGuiOpen;
import com.snuri.customshop.network.ServerPacketUpdate;

import net.minecraft.client.Minecraft;

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
		mc = Minecraft.getMinecraft();
	}
	
	public void onTick() {
		
	}
	
	public void onRender() {
		if(mc.player == null)
			return;
	}
	
	/* PacketHandle */
	public void handleMessage(ServerPacketGuiOpen message) {
		String[] dataArr = message.getData().split("//");
		int l = dataArr.length;
		
		try {
			ShopGui gui = new ShopGui(dataArr[0], dataArr[1].equals("1"));
			for(int i = 2; i < l; i ++) {
				GuiComponent comp = gui.getComponentMap().get(dataArr[i].substring(0, 3));
				if(comp != null) {
					comp.setValue(dataArr[i].substring(3));
				}
			}
			mc.addScheduledTask(() -> { mc.displayGuiScreen(gui); });
		} catch(Exception e) {
			for(StackTraceElement s : e.getStackTrace()) {
				LOGGER.error(s);
			}
		}
	}
	
	public void handleMessage(ServerPacketGuiClose message) {
		mc.displayGuiScreen(null);
	}
	
	public void handleMessage(ServerPacketUpdate message) {
		if(mc.currentScreen instanceof ShopGui) {
			try {
				ShopGui gui = (ShopGui) mc.currentScreen;
				String[] dataArr = message.getData().split("//");
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
