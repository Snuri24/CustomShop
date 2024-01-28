package com.snuri.customshop.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.snuri.customshop.Render;
import com.snuri.customshop.network.UpdateType;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;

public class ItemView extends GuiComponent {
	
	private static final Logger LOGGER = LogManager.getLogger();

	private GuiScreen gui;
	
	private ItemStack itemStack;
	private double[] framePos;
	private double[] frameSize;
	private double[] pos;
	private double[] size;
	private boolean visible;
	
	public ItemView(GuiScreen gui, String itemData, double fx, double fy, double fw, double fh, double x, double y, double w, double h, boolean visible) {
		this.gui = gui;	
		this.itemStack = null;
		
		framePos = new double[2];
		framePos[0] = fx / ShopGui.referenceWidth;
		framePos[1] = fy / ShopGui.referenceHeight;
		frameSize = new double[2];
		frameSize[0] = fw / ShopGui.referenceWidth;
		frameSize[1] = fh / ShopGui.referenceHeight;
		
		pos = new double[2];
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
		size = new double[2];
		size[0] = w / ShopGui.referenceWidth;
		size[1] = h / ShopGui.referenceHeight;
		
		this.visible = visible;
		
		if(!itemData.isEmpty()) {
			try {
				itemStack = new ItemStack(JsonToNBT.getTagFromJson(itemData));
			} catch (NBTException e) {
				LOGGER.error(e);
			}
		}
	}
	
	public void render() {
		if(itemStack != null && visible) {
			Render.drawItemStack(itemStack, gui.width * pos[0], gui.height * pos[1], gui.width * size[0], gui.height * size[1]);
		}
	}
	
	public void render(int mouseX, int mouseY) {
		if(itemStack != null && visible && AABB(mouseX, mouseY)) {
			Render.drawTooltip(itemStack, mouseX, mouseY);
		}
	}
	
	@Override
	public void setValue(String value) {
		String type = value.substring(0, 2);
		if(type.equals(UpdateType.POSITION)) {
			String[] strArr = value.substring(2).split(", ");
			setPosition(Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1]));
		} else if(type.equals(UpdateType.VISIBLE)) {
			setVisible(value.substring(2).equals("1") ? true : false);
		} else if(type.equals(UpdateType.ITEM)) {
			setItem(value.substring(2));
		} else if(type.equals(UpdateType.FRAME_POS)) {
			String[] strArr = value.substring(2).split(", ");
			setFramePos(Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1]));
		} else if(type.equals(UpdateType.FRAME_SIZE)) {
			String[] strArr = value.substring(2).split(", ");
			setFrameSize(Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1]));
		} else if(type.equals(UpdateType.SIZE)) {
			String[] strArr = value.substring(2).split(", ");
			setSize(Integer.parseInt(strArr[0]), Integer.parseInt(strArr[1]));
		}
	}
	
	public void setItem(String itemData) {
		itemStack = null;
		if(!itemData.equals("NULL")) {
			try {
				itemStack = new ItemStack(JsonToNBT.getTagFromJson(itemData));
			} catch (NBTException e) {
				LOGGER.error(e);
			}
		}
	}
	
	public void setFramePos(int fx, int fy) {
		framePos[0] = fx / ShopGui.referenceWidth;
		framePos[1] = fy / ShopGui.referenceHeight;
	}
	
	public void setFrameSize(int fw, int fh) {
		frameSize[0] = fw / ShopGui.referenceWidth;
		frameSize[1] = fh / ShopGui.referenceHeight;
	}
	
	public void setPosition(int x, int y) {
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
	}
	
	public void setSize(int w, int h) {
		size[0] = w / ShopGui.referenceWidth;
		size[1] = h / ShopGui.referenceHeight;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	private boolean AABB(int mouseX, int mouseY) {
		boolean collisionX = mouseX >= gui.width * framePos[0] && gui.width * (framePos[0] + frameSize[0]) >= mouseX;
		boolean collisionY = mouseY >= gui.height * framePos[1] && gui.height * (framePos[1] + frameSize[1]) >= mouseY;
		
		return collisionX && collisionY;
	}
}
