package com.snuri.customshop.gui;

import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.snuri.customshop.Render;
import com.snuri.customshop.network.UpdateType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.ResourceLocation;

public class ImageView extends GuiComponent {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private Screen screen;
	
	private ResourceLocation texture;
	private double[] pos;
	private double[] size;
	private boolean visible;
	
	public ImageView(Screen screen, String imgName, double x, double y, boolean visible) {
		this.screen = screen;
		
		texture = null;
		pos = new double[2];
		pos[0] = x / ShopScreen.referenceWidth;
		pos[1] = y / ShopScreen.referenceHeight;
		size = new double[2];
		size[0] = 0;
		size[1] = 0;
		this.visible = visible;
		
		if(!imgName.isEmpty()) {
			Minecraft mc = Minecraft.getInstance();
			
			File file = new File(mc.gameDirectory, "mods/CustomShop/" + imgName);
			if(file.exists()) {
				texture = new ResourceLocation("customshopmod", imgName);
				
				try {
					Image image = ImageIO.read(file);
					size[0] = image.getWidth(null) / ShopScreen.referenceWidth;
					size[1] = image.getHeight(null) / ShopScreen.referenceHeight;
				} catch (IOException e) { }
			} else {
				LOGGER.error("Couldn't find the image file.");
			}
		}
	}
	
	public void render() {
		if(texture != null && visible) {
			Render.bindTexture(texture);
			Render.setColor(0xffffffff);
			Render.drawTexturedRect(screen.width * pos[0], screen.height * pos[1], screen.width * size[0], screen.height * size[1]);
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
		} else if(type.equals(UpdateType.IMAGE)) {
			setImage(value.substring(2));
		}
	}
	
	public void setImage(String imgName) {
		texture = null;
		size[0] = 0;
		size[1] = 0;
		
		if(!imgName.equals("NULL")) {
			Minecraft mc = Minecraft.getInstance();
			
			File file = new File(mc.gameDirectory, "mods/CustomShop/" + imgName);
			if(file.exists()) {
				texture = new ResourceLocation("customshopmod", imgName);
				
				try {
					Image image = ImageIO.read(file);
					size[0] = image.getWidth(null) / ShopScreen.referenceWidth;
					size[1] = image.getHeight(null) / ShopScreen.referenceHeight;
				} catch (IOException e) { }
			} else {
				LOGGER.error("Couldn't find the image file.");
			}
		}
	}
	
	public void setPosition(double x, double y) {
		pos[0] = x / ShopScreen.referenceWidth;
		pos[1] = y / ShopScreen.referenceHeight;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
