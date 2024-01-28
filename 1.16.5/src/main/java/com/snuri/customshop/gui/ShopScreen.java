package com.snuri.customshop.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.glfw.GLFW;

import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.snuri.customshop.CustomShop;
import com.snuri.customshop.network.ClientPacketGuiClose;
import com.snuri.customshop.network.ClientPacketKeyEvent;
import com.snuri.customshop.network.ClientPacketMouseEvent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ShopScreen extends Screen {
	
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

	public ShopScreen(String guiName, boolean drawBackground) throws Exception {
		super(new StringTextComponent("ShopScreen"));
		
		this.guiName = guiName;
		this.drawBackground = drawBackground;
		
		Minecraft mc = Minecraft.getInstance();
		
		File json = new File(mc.gameDirectory, "mods/CustomShop/" + guiName + ".json");
		if(!json.exists())
			throw new Exception("ShopScreen : Couldn't find the gui json file.");

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
	public void init() {
		minecraft.mouseHandler.releaseMouse();
	}

	@Override
	public void tick() {

	}
	
	@SuppressWarnings("deprecation")
	@Override
	public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        if(drawBackground)
        	renderBackground(matrixStack);
		
        matrixStack.pushPose();
		RenderSystem.disableLighting();
		
		for(ImageView view : viewList)
			view.render();
		for(Text text : textList)
			text.render(matrixStack);
		for(ItemView item : itemList)
			item.render();
		for(Button button : buttonList)
			button.render(mouseX, mouseY, mousePressed);
		for(ItemView item : itemList)
			item.render(matrixStack, mouseX, mouseY);
		
		matrixStack.popPose();
		
		super.render(matrixStack, mouseX, mouseY, partialTicks);
	}
	
	@Override
	public void onClose() {
		CustomShop.simpleChannel.sendToServer(new ClientPacketGuiClose());
		super.onClose();
	}

	// 0 = LMB, 1 = RMB, 2 = MMB
	@Override
	public boolean mouseClicked(double x, double y, int button) {
		if(button == 0) {
			
		} else if(button == 1) {
			
		}
		
		mousePressed = true;
		return super.mouseClicked(x, y, button);
	}
	
	// 0 = LMB, 1 = RMB, 2 = MMB
	@Override
	public boolean mouseReleased(double x, double y, int button) {
		String id = null;
		for(Button btn : buttonList) {
			if(btn.isClicked()) {
				id = btn.getId();
				break;
			}
		}
		
		if(id != null) {
			CustomShop.simpleChannel.sendToServer(new ClientPacketMouseEvent(id, button, hasControlDown(), hasAltDown(), hasShiftDown()));
		}
		
		mousePressed = false;
		return super.mouseReleased(x, y, button);
	}
	
	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		CustomShop.simpleChannel.sendToServer(new ClientPacketKeyEvent(keyCode));
		
		switch(keyCode) {
		case GLFW.GLFW_KEY_ESCAPE:
			onClose();
			return true;
		}
		
		return false;
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

