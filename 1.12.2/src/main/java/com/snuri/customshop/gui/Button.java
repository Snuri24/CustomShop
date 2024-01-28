package com.snuri.customshop.gui;

import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonArray;
import com.snuri.customshop.Render;
import com.snuri.customshop.network.UpdateType;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;

public class Button extends GuiComponent {

	private static final Logger LOGGER = LogManager.getLogger();
	
	private GuiScreen gui;
	
	private String id;
	private ResourceLocation[] textureArr;
	private double[] pos;
	private double[] size;
	private boolean visible;
	private int state;
	
	// STATE: IDLE(0), FOCUSED(1), CLICKED(2), DISABLED(3)
	
	public Button(GuiScreen gui, String id, JsonArray imgArr, double x, double y, boolean visible) {
		this.gui = gui;
		
		this.id = id;
		textureArr = new ResourceLocation[4];
		pos = new double[2];
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
		size = new double[2];
		size[0] = 0;
		size[1] = 0;
		this.visible = visible;
		state = 0;
		
		if(imgArr.size() != 0) {
			for(int i = 0; i < 4; i ++) {
				String img = imgArr.get(i).getAsString();
				File file = new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop/" + img);
				if(file.exists()) {
					textureArr[i] = new ResourceLocation("customshopmod", img);
				} else {
					LOGGER.error("Couldn't find the image file.");
				}
			}
			
			try {
				Image image = ImageIO.read(new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop/" + imgArr.get(0).getAsString()));
				size[0] = image.getWidth(null) / ShopGui.referenceWidth;
				size[1] = image.getHeight(null) / ShopGui.referenceHeight;
			} catch(Exception e) { }
		}
	}
	
	public void render(int mouseX, int mouseY, boolean mousePressed) {
		if(textureArr[state] != null && visible) {
			if(state != 3) {
				if(AABB(mouseX, mouseY)) {
					if(state != 0 || !mousePressed)
						state = mousePressed ? 2 : 1;
				} else {
					state = 0;
				}
			}
			
			Render.bindTexture(textureArr[state]);
			Render.setColor(0xffffffff);
			Render.drawTexturedRect(gui.width * pos[0], gui.height * pos[1], gui.width * size[0], gui.height * size[1]);
		}
	}
	
	public boolean isClicked() {
		return state == 2;
	}
	
	public String getId() {
		return id;
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
		} else if(type.equals(UpdateType.STATE)) {
			setState(Integer.parseInt(value.substring(2)));
		}
	}
	
	public void setImage(String img) {
		textureArr = new ResourceLocation[4];
		size[0] = 0;
		size[1] = 0;
		state = 0;
		
		if(!img.equals("NULL")) {
			try {
				String[] imgArr = img.split(", ");
				
				for(int i = 0; i < 4; i ++) {
					File file = new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop/" + imgArr[i]);
					if(file.exists()) {
						textureArr[i] = new ResourceLocation("customshopmod", imgArr[i]);
					} else {
						LOGGER.error("Couldn't find the image file.");
					}
				}
				
				Image image = ImageIO.read(new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop/" + imgArr[0]));
				size[0] = image.getWidth(null) / ShopGui.referenceWidth;
				size[1] = image.getHeight(null) / ShopGui.referenceHeight;
			} catch (Exception e) { }
		}
	}
	
	public void setPosition(double x, double y) {
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	public void setState(int state) {
		this.state = state;
	}
	
	private boolean AABB(int mouseX, int mouseY) {
		boolean collisionX = mouseX >= gui.width * pos[0] && gui.width * (pos[0] + size[0]) >= mouseX;
		boolean collisionY = mouseY >= gui.height * pos[1] && gui.height * (pos[1] + size[1]) >= mouseY;
		
		return collisionX && collisionY;
	}
}
