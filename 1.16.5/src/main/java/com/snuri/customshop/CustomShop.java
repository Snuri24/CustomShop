package com.snuri.customshop;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.IPackFinder;
import net.minecraft.resources.IPackNameDecorator;
import net.minecraft.resources.IResourcePack;
import net.minecraft.resources.ResourcePackInfo;
import net.minecraft.resources.ResourcePackInfo.IFactory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import com.google.common.collect.Sets;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.snuri.customshop.gui.ShopScreen;
import com.snuri.customshop.network.ClientPacketGuiClose;
import com.snuri.customshop.network.ClientPacketKeyEvent;
import com.snuri.customshop.network.ClientPacketMouseEvent;
import com.snuri.customshop.network.PacketType;
import com.snuri.customshop.network.ServerPacketGuiClose;
import com.snuri.customshop.network.ServerPacketGuiOpen;
import com.snuri.customshop.network.ServerPacketUpdate;
import com.snuri.customshop.util.ModResourcePack;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CustomShop.MOD_ID)
public class CustomShop {
	
	public static final String MOD_ID = "customshop";
	public static final String NAME = "CustomShop";
	public static final String VERSION = "1.1.0";
	
    // Directly reference a log4j logger.
    private static final Logger LOGGER = LogManager.getLogger();
    
    public static SimpleChannel simpleChannel;
    
    private Cs cs;

    public CustomShop() {
        // Register the setup method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        // Register the enqueueIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        // Register the processIMC method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        // Register the doClientStuff method for modloading
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);

        // Register ourselves for server and other game events we are interested in
        cs = Cs.getInstance();
        MinecraftForge.EVENT_BUS.register(this);
    }

    // some preinit code
    private void setup(final FMLCommonSetupEvent event) {
		simpleChannel = NetworkRegistry.newSimpleChannel(new ResourceLocation(CustomShop.MOD_ID, "channel"), 
    		   () -> VERSION, (version) -> true, (version) -> true);
		
		simpleChannel.registerMessage(PacketType.SERVER_GUI_OPEN, ServerPacketGuiOpen.class, 
				ServerPacketGuiOpen::encode, ServerPacketGuiOpen::decode, 
				ServerPacketGuiOpen::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		
		simpleChannel.registerMessage(PacketType.SERVER_GUI_CLOSE, ServerPacketGuiClose.class, 
				ServerPacketGuiClose::encode, ServerPacketGuiClose::decode, 
				ServerPacketGuiClose::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
       
		simpleChannel.registerMessage(PacketType.SERVER_UPDATE, ServerPacketUpdate.class, 
				ServerPacketUpdate::encode, ServerPacketUpdate::decode, 
				ServerPacketUpdate::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
		
		simpleChannel.registerMessage(PacketType.CLIENT_GUI_CLOSE, ClientPacketGuiClose.class, 
				ClientPacketGuiClose::encode, ClientPacketGuiClose::decode, 
				ClientPacketGuiClose::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
       
		simpleChannel.registerMessage(PacketType.CLIENT_MOUSE_EVENT, ClientPacketMouseEvent.class, 
				ClientPacketMouseEvent::encode, ClientPacketMouseEvent::decode, 
				ClientPacketMouseEvent::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
       
		simpleChannel.registerMessage(PacketType.CLIENT_KEY_EVENT, ClientPacketKeyEvent.class, 
				ClientPacketKeyEvent::encode, ClientPacketKeyEvent::decode, 
				ClientPacketKeyEvent::onPacketReceived, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
    }

    // do something that can only be done on the client
    private void doClientStuff(final FMLClientSetupEvent event) {
    	Minecraft mc = Minecraft.getInstance();
    	File modFolder = new File(mc.gameDirectory, "mods/CustomShop");
    	if(!modFolder.exists()) {
			modFolder.mkdirs();
			
			try {
				String[] fileArr = { "pack.mcmeta", "arrow_left_clicked.png", "arrow_left_disabled.png", "arrow_left_focused.png", "arrow_left_idle.png",
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
    	
		/*
		IResourcePack pack = new ModResourcePack(modFolder);
		pack.close();
		((SimpleReloadableResourceManager) mc.getResourceManager()).add(pack);
		*/
		
		IResourcePack pack = new ModResourcePack(modFolder);
		pack.close();
		
		synchronized(mc.getResourcePackRepository()) {
			mc.getResourcePackRepository().addPackFinder(new IPackFinder() {
				@Override
				public void loadPacks(Consumer<ResourcePackInfo> consumer, IFactory factory) {
					ResourcePackInfo packInfo = ResourcePackInfo.create(pack.getName(), true, () -> pack, factory, ResourcePackInfo.Priority.TOP, IPackNameDecorator.DEFAULT);
		            if (packInfo != null) {
		               consumer.accept(packInfo);
		            }
				}
			});
			mc.getResourcePackRepository().reload();
			
			Set<String> selected = Sets.newLinkedHashSet();
			selected.addAll(mc.options.resourcePacks);
			selected.add(pack.getName());
			mc.getResourcePackRepository().setSelected(selected);
		}
		
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

				ShopScreen.referenceWidth = jsonObject.get("reference_width").getAsDouble();
				ShopScreen.referenceHeight = jsonObject.get("reference_height").getAsDouble();
				
				jsonReader.close();
			} catch (IOException e) {
				LOGGER.error("Failed to read settings.json");
			}
		}
    }

    // some example code to dispatch IMC to another mod
    private void enqueueIMC(final InterModEnqueueEvent event) {
       
    }

    // some example code to receive and process InterModComms from other mods
    private void processIMC(final InterModProcessEvent event) {
       
    }
    
    // You can use SubscribeEvent and let the Event Bus discover methods to call
    
    @SubscribeEvent
    public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
    	if(event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
			cs.onRender();
		} 
    }
    
    @SubscribeEvent
	public void clientTick(TickEvent.ClientTickEvent event) {
		if(event.phase == TickEvent.Phase.END) {
			cs.onTick();
		}
	}

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
        
    }
}
