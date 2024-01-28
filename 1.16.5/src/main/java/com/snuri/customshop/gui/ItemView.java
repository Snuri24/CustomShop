package com.snuri.customshop.gui;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.snuri.customshop.Render;
import com.snuri.customshop.network.UpdateType;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.JsonToNBT;

public class ItemView extends GuiComponent {

    private static final Logger LOGGER = LogManager.getLogger();
	
	private Screen screen;
	
	private ItemStack itemStack;
	private double[] framePos;
	private double[] frameSize;
	private double[] pos;
	private double[] size;
	private boolean visible;
	
	public ItemView(Screen screen, String itemData, double fx, double fy, double fw, double fh, double x, double y, double w, double h, boolean visible) {
		this.screen = screen;
		this.itemStack = null;
		
		framePos = new double[2];
		framePos[0] = fx / ShopScreen.referenceWidth;
		framePos[1] = fy / ShopScreen.referenceHeight;
		frameSize = new double[2];
		frameSize[0] = fw / ShopScreen.referenceWidth;
		frameSize[1] = fh / ShopScreen.referenceHeight;
		
		pos = new double[2];
		pos[0] = x / ShopScreen.referenceWidth;
		pos[1] = y / ShopScreen.referenceHeight;
		size = new double[2];
		size[0] = w / ShopScreen.referenceWidth;
		size[1] = h / ShopScreen.referenceHeight;
		
		this.visible = visible;
		
		if(!itemData.isEmpty()) {
			try {
				itemStack = ItemStack.of(JsonToNBT.parseTag(itemData));
			} catch (CommandSyntaxException e) {
				LOGGER.error(e);
			}
		}
	}
	
	public void render() {
		if(itemStack != null && visible) {
			Render.drawItemStack(itemStack, screen.width * pos[0], screen.height * pos[1], screen.width * size[0], screen.height * size[1]);
		}
	}
	
	public void render(MatrixStack matrixStack, int mouseX, int mouseY) {
		if(itemStack != null && visible && AABB(mouseX, mouseY)) {
			Render.drawTooltip(matrixStack, itemStack, mouseX, mouseY);
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
				itemStack = ItemStack.of(JsonToNBT.parseTag(itemData));
			} catch (CommandSyntaxException e) {
				LOGGER.error(e);
			}
		}
	}
	
	public void setFramePos(int fx, int fy) {
		framePos[0] = fx / ShopScreen.referenceWidth;
		framePos[1] = fy / ShopScreen.referenceHeight;
	}
	
	public void setFrameSize(int fw, int fh) {
		frameSize[0] = fw / ShopScreen.referenceWidth;
		frameSize[1] = fh / ShopScreen.referenceHeight;
	}
	
	public void setPosition(int x, int y) {
		pos[0] = x / ShopScreen.referenceWidth;
		pos[1] = y / ShopScreen.referenceHeight;
	}
	
	public void setSize(int w, int h) {
		size[0] = w / ShopScreen.referenceWidth;
		size[1] = h / ShopScreen.referenceHeight;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	private boolean AABB(int mouseX, int mouseY) {
		boolean collisionX = mouseX >= screen.width * framePos[0] && screen.width * (framePos[0] + frameSize[0]) >= mouseX;
		boolean collisionY = mouseY >= screen.height * framePos[1] && screen.height * (framePos[1] + frameSize[1]) >= mouseY;
		
		return collisionX && collisionY;
	}
}
