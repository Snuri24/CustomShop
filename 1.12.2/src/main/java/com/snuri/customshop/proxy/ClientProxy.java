package com.snuri.customshop.proxy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.snuri.customshop.Cs;
import com.snuri.customshop.EventHandler;
import com.snuri.customshop.gui.ShopGui;
import com.snuri.customshop.network.ClientPacketGuiClose;
import com.snuri.customshop.network.ClientPacketKeyEvent;
import com.snuri.customshop.network.ClientPacketMouseEvent;
import com.snuri.customshop.network.PacketType;
import com.snuri.customshop.network.ServerPacketGuiClose;
import com.snuri.customshop.network.ServerPacketGuiOpen;
import com.snuri.customshop.network.ServerPacketUpdate;
import com.snuri.customshop.util.ModResourcePack;
import com.snuri.customshop.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.SimpleReloadableResourceManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class ClientProxy extends CommonProxy {
	
	private static final Logger LOGGER = LogManager.getLogger();
	public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.CHANNEL);
	
	@Override
	public void preInit(File configFile) {
		File modFolder = new File(Minecraft.getMinecraft().mcDataDir, "mods/CustomShop");
		if(!modFolder.exists()) {
			modFolder.mkdirs();
			
			try {
				String[] fileArr = { "arrow_left_clicked.png", "arrow_left_disabled.png", "arrow_left_focused.png", "arrow_left_idle.png",
						"arrow_right_clicked.png", "arrow_right_disabled.png", "arrow_right_focused.png", "arrow_right_idle.png",
						"button_buy_clicked.png", "button_buy_disabled.png", "button_buy_focused.png", "button_buy_idle.png",
						"button_sell_clicked.png", "button_sell_disabled.png", "button_sell_focused.png", "button_sell_idle.png",
						"item_background.png", "shop_background.png", "shop_background2.png", "shop_buy.json", "shop_sell.json" };
				for(String file : fileArr) {
					Files.copy(getClass().getResourceAsStream("/assets/customshop/" + file), new File(modFolder, file).toPath());
				}
			} catch (IOException e) {
				LOGGER.error(e);
			}
		}
		
		List<IResourcePack> defaultResourcePacks = ObfuscationReflectionHelper.getPrivateValue(FMLClientHandler.class, FMLClientHandler.instance(), "resourcePackList");
		IResourcePack pack = new ModResourcePack(modFolder);
		defaultResourcePacks.add(pack);
		((SimpleReloadableResourceManager) Minecraft.getMinecraft().getResourceManager()).reloadResourcePack(pack);
		
		File json = new File(modFolder, "settings.json");
		if(!json.exists()) {
			try {
				json.createNewFile();
				if(!json.canWrite()) 
					json.setWritable(true);

				JsonWriter jsonWriter = new JsonWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(json), "UTF-8")));
		        jsonWriter.setIndent("    ");
		        jsonWriter.setLenient(false);
		        jsonWriter.beginObject();
				
		        jsonWriter.name("reference_width").value(1280L);
		        jsonWriter.name("reference_height").value(720L);
		        
		        jsonWriter.endObject();
		        jsonWriter.close();
			} catch (IOException e) {
				LOGGER.error("Failed to write settings.json");
			}
		} else {
			try {
				JsonReader jsonReader = new JsonReader(new BufferedReader(new InputStreamReader(new FileInputStream(json), "UTF-8")));
				JsonParser jsonParser = new JsonParser();
				JsonObject jsonObject = jsonParser.parse(jsonReader).getAsJsonObject();

				ShopGui.referenceWidth = jsonObject.get("reference_width").getAsDouble();
				ShopGui.referenceHeight = jsonObject.get("reference_height").getAsDouble();
				
				jsonReader.close();
			} catch (IOException e) {
				LOGGER.error("Failed to read settings.json");
			}
		}
	}
	
	@Override
	public void init() {
		EventHandler eventHandler = new EventHandler(Cs.getInstance());
		MinecraftForge.EVENT_BUS.register(eventHandler);
		
		NETWORK.registerMessage(ServerPacketGuiOpen.Handle.class, ServerPacketGuiOpen.class, PacketType.SERVER_GUI_OPEN, Side.CLIENT);
		NETWORK.registerMessage(ServerPacketGuiClose.Handle.class, ServerPacketGuiClose.class, PacketType.SERVER_GUI_CLOSE, Side.CLIENT);
		NETWORK.registerMessage(ServerPacketUpdate.Handle.class, ServerPacketUpdate.class, PacketType.SERVER_UPDATE, Side.CLIENT);
		
		NETWORK.registerMessage(ClientPacketGuiClose.Handle.class, ClientPacketGuiClose.class, PacketType.CLIENT_GUI_CLOSE, Side.CLIENT);
		NETWORK.registerMessage(ClientPacketMouseEvent.Handle.class, ClientPacketMouseEvent.class, PacketType.CLIENT_MOUSE_EVENT, Side.CLIENT);
		NETWORK.registerMessage(ClientPacketKeyEvent.Handle.class, ClientPacketKeyEvent.class, PacketType.CLIENT_KEY_EVENT, Side.CLIENT);
	}
	
	@Override
	public void postInit() {

	}
}
