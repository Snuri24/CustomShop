package com.snuri.customshop.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.lwjgl.input.Keyboard;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.snuri.customshop.network.ClientPacketGuiClose;
import com.snuri.customshop.network.ClientPacketKeyEvent;
import com.snuri.customshop.network.ClientPacketMouseEvent;
import com.snuri.customshop.proxy.ClientProxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

@SideOnly(Side.CLIENT)
public class ShopGui extends GuiScreen {
	
	public static double referenceWidth = 1280.0D;
	public static double referenceHeight = 720.0D;
	
	private String guiName;
	private boolean drawBackground;
	
	private List<ImageView> viewList = new ArrayList<ImageView>();
	private List<Text> textList = new ArrayList<Text>();
	private List<Button> buttonList = new ArrayList<Button>();
	private List<ItemView> itemList = new ArrayList<ItemView>();
	private Map<String, GuiComponent> componentMap = new HashMap<String, GuiComponent>();
	
	private boolean mousePressed = false;

	public ShopGui(String guiName, boolean drawBackground) throws Exception {
		this.guiName = guiName;
		this.drawBackground = drawBackground;
		
		File json = new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop/" + guiName + ".json");
		if(!json.exists())
			throw new Exception("ShopGui : Couldn't find the gui json file.");
		
		JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(json), "UTF-8")));
		JsonParser jsonParser = new JsonParser();
		JsonObject jsonObject = jsonParser.parse(jsonReader).getAsJsonObject();

		Set<Entry<String, JsonElement>> entrySet = jsonObject.entrySet();
		for(Entry<String, JsonElement> entry : entrySet) {
			loadObj(entry.getKey(), entry.getValue().getAsJsonObject());
		}
		
		jsonReader.close();
	}
	
	@Override
	public void initGui() {

	}

	@Override
	public void updateScreen() {

	}
	
	@Override
	public void drawScreen(int mouseX, int mouseY, float f) {
        if(drawBackground)
        	drawDefaultBackground();
		
		GlStateManager.pushMatrix();
		GlStateManager.disableLighting();
		
		for(ImageView view : viewList)
			view.render();
		for(Text text : textList)
			text.render();
		for(ItemView item : itemList)
			item.render();
		for(Button button : buttonList)
			button.render(mouseX, mouseY, mousePressed);
		for(ItemView item : itemList)
			item.render(mouseX, mouseY);
		
		GlStateManager.popMatrix();
		
		super.drawScreen(mouseX, mouseY, f);
	}

	// 0 = LMB, 1 = RMB, 2 = MMB
	@Override
	protected void mouseClicked(int x, int y, int button) throws IOException {
		if(button == 0) {
			
		} else if(button == 1) {
			
		}
		
		mousePressed = true;
		super.mouseClicked(x, y, button);
	}
	
	// 0 = LMB, 1 = RMB, 2 = MMB
	@Override
	protected void mouseReleased(int x, int y, int button) {
		String id = null;
		for(Button btn : buttonList) {
			if(btn.isClicked()) {
				id = btn.getId();
				break;
			}
		}
		
		if(id != null) {
			ClientProxy.NETWORK.sendToServer(new ClientPacketMouseEvent(id, button, isCtrlKeyDown(), isAltKeyDown(), isShiftKeyDown()));
		}
		
		mousePressed = false;
		super.mouseReleased(x, y, button);
	}
	
	@Override
	protected void keyTyped(char c, int key) {
		ClientProxy.NETWORK.sendToServer(new ClientPacketKeyEvent(key));
		
		switch(key) {
		case Keyboard.KEY_ESCAPE:
			mc.displayGuiScreen(null);
			break;
		}
	}
	
	@Override
	public void onGuiClosed() {
		ClientProxy.NETWORK.sendToServer(new ClientPacketGuiClose());
	}
	
	public String getName() {
		return guiName;
	}
	
	public boolean shouldDrawBackground() {
		return drawBackground;
	}
	
	public Map<String, GuiComponent> getComponentMap() {
		return componentMap;
	}
	
	private void loadObj(String id, JsonObject jsonObject) {
		String type = jsonObject.get("type").getAsString();
		
		if(type.equals("ImageView")) {
			String img = jsonObject.get("image").getAsString();
			JsonArray arr = jsonObject.getAsJsonArray("position");
			boolean visible = jsonObject.get("visible").getAsBoolean();
			
			ImageView comp = new ImageView(this, img, arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), visible);
			viewList.add(comp);
			componentMap.put(id, comp);
		} else if(type.equals("Text")) {
			String text = jsonObject.get("text").getAsString();
			int fontSize = jsonObject.get("fontSize").getAsInt();
			String str = jsonObject.get("alignment").getAsString();
			int alignment = 0;
			if(str.equals("CENTER")) {
				alignment = 1;
			} else if(str.equals("RIGHT")) {
				alignment = 2;
			}
			int maxLength = jsonObject.get("maxLength").getAsInt();
			JsonArray arr = jsonObject.getAsJsonArray("position");
			boolean visible = jsonObject.get("visible").getAsBoolean();
			
			Text comp = new Text(this, text, fontSize, alignment, maxLength, arr.get(0).getAsDouble(), arr.get(1).getAsDouble(), visible);
			textList.add(comp);
			componentMap.put(id, comp);
		} else if(type.equals("Button")) {
			JsonArray imgArr = jsonObject.getAsJsonArray("image");
			JsonArray posArr = jsonObject.getAsJsonArray("position");
			boolean visible = jsonObject.get("visible").getAsBoolean();
			
			Button comp = new Button(this, id, imgArr, posArr.get(0).getAsDouble(),posArr.get(1).getAsDouble(), visible);
			buttonList.add(comp);
			componentMap.put(id, comp);
		} else if(type.equals("ItemView")) {
			String itemData = jsonObject.get("item").getAsString();
			JsonArray framePosArr = jsonObject.getAsJsonArray("framePos");
			JsonArray frameSizeArr = jsonObject.getAsJsonArray("frameSize");
			JsonArray posArr = jsonObject.getAsJsonArray("position");
			JsonArray sizeArr = jsonObject.getAsJsonArray("size");
			boolean visible = jsonObject.get("visible").getAsBoolean();
			
			ItemView comp = new ItemView(this, itemData, framePosArr.get(0).getAsDouble(), framePosArr.get(1).getAsDouble(), frameSizeArr.get(0).getAsDouble(), frameSizeArr.get(1).getAsDouble(), posArr.get(0).getAsDouble(), posArr.get(1).getAsDouble(), sizeArr.get(0).getAsDouble(), sizeArr.get(1).getAsDouble(), visible);
			itemList.add(comp);
			componentMap.put(id, comp);
		}
	}
}

