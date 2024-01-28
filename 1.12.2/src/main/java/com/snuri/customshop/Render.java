package com.snuri.customshop;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.ITextureObject;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.client.config.GuiUtils;

public class Render {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	public static double zDepth = 0.0D;
	
	public static void setColor(int color) {
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(((color >> 16) & 0xff) / 255.0f, ((color >> 8) & 0xff) / 255.0f, ((color) & 0xff) / 255.0f, ((color >> 24) & 0xff) / 255.0f);
		GlStateManager.disableBlend();
	}
	
	public static int getTextureWidth() {
		return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_WIDTH);
	}

	public static int getTextureHeight() {
		return GL11.glGetTexLevelParameteri(GL11.GL_TEXTURE_2D, 0, GL11.GL_TEXTURE_HEIGHT);
	}
	
	public static void drawTexturedRect(double x, double y, double w, double h) {
		drawTexturedRect(x, y, w, h, 0.0D, 0.0D, 1.0D, 1.0D);
	}
	
	public static void drawTexturedRect(double x, double y, double w, double h, double u1, double v1, double u2, double v2) {
		try {
			GlStateManager.enableTexture2D();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			Tessellator tessellator = Tessellator.getInstance();
			BufferBuilder buffer = tessellator.getBuffer();
			buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
			buffer.pos(x + w, y, zDepth).tex(u2, v1).endVertex();
			buffer.pos(x, y, zDepth).tex(u1, v1).endVertex();
			buffer.pos(x, y + h, zDepth).tex(u1, v2).endVertex();
			buffer.pos(x + w, y + h, zDepth).tex(u2, v2).endVertex();
			tessellator.draw();
			GlStateManager.disableBlend();
		} catch(NullPointerException e) {
			LOGGER.error("Render.drawTexturedRect : Null Pointer Exception");
		}
	}
	
	public static void drawGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        float f = (float)(startColor >> 24 & 255) / 255.0f;
        float f1 = (float)(startColor >> 16 & 255) / 255.0f;
        float f2 = (float)(startColor >> 8 & 255) / 255.0f;
        float f3 = (float)(startColor & 255) / 255.0f;
        float f4 = (float)(endColor >> 24 & 255) / 255.0f;
        float f5 = (float)(endColor >> 16 & 255) / 255.0f;
        float f6 = (float)(endColor >> 8 & 255) / 255.0f;
        float f7 = (float)(endColor & 255) / 255.0f;
        
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos((double)right, (double)top, zDepth).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)top, zDepth).color(f1, f2, f3, f).endVertex();
        bufferbuilder.pos((double)left, (double)bottom, zDepth).color(f5, f6, f7, f4).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, zDepth).color(f5, f6, f7, f4).endVertex();
        tessellator.draw();
        
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }
	
	public static void drawItemStack(ItemStack itemStack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
		renderItem.renderItemAndEffectIntoGUI(itemStack, x, y);
		RenderHelper.disableStandardItemLighting();
	}
	
	public static void drawItemStack(ItemStack itemStack, double x, double y, double width, double height) {
		double scaleX = width / 16.0D;
		double scaleY = height / 16.0D;
		
		GlStateManager.pushMatrix();
		GlStateManager.scale(scaleX, scaleY, 1.0D);
		Render.drawItemStack(itemStack, (int) (x / scaleX), (int) (y / scaleY));
		GlStateManager.popMatrix();
	}
	
	/*
	public static void drawTooltip(List<String> info, int x, int y) {
		Minecraft mc = Minecraft.getMinecraft();
		FontRenderer fr = mc.fontRenderer;
		
		// getMaxStringWidth
		int width = 0;
		for(String s : info) {
			int w = fr.getStringWidth(s);
			if(width < w)
				width = w;
		}
		
		// getHeight
		int height = 8;
		if(info.size() > 1) {
			height += 2 + (info.size() - 1) * (fr.FONT_HEIGHT + 1);
		}
		
		int posX = x + 12;
		int posY = y - 12;
		
		if(posX + width > mc.displayWidth)
			posX -= 28 + width;
		if(posY + height + 6 > mc.displayHeight)
			posY = mc.displayHeight - height - 6;
		
		drawGradientRect(posX - 3, posY - 4, posX + width + 3, posY - 3, -267386864, -267386864);
        drawGradientRect(posX - 3, posY + height + 3, posX + width + 3, posY + height + 4, -267386864, -267386864);
        drawGradientRect(posX - 4, posY - 3, posX + width + 4, posY + height + 3, -267386864, -267386864);
        
        drawGradientRect(posX - 3, posY - 3 + 1, posX - 3 + 1, posY + height + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(posX + width + 2, posY - 3 + 1, posX + width + 3, posY + height + 3 - 1, 1347420415, 1344798847);
        drawGradientRect(posX - 3, posY - 3, posX + width + 3, posY - 3 + 1, 1347420415, 1347420415);
        drawGradientRect(posX - 3, posY + height + 2, posX + width + 3, posY + height + 3, 1344798847, 1344798847);

		int size = info.size();
		for(int i = 0; i < size; i ++) {
			fr.drawStringWithShadow(info.get(i), (float) posX, (float) posY, 0xffffff);
			if(i == 0) 
				posY += 2;
			posY += 10;
		}
	}
	*/
	
	public static void drawTooltip(ItemStack itemStack, int x, int y) {
		Minecraft mc = Minecraft.getMinecraft();
		List<String> list = itemStack.getTooltip(mc.player, mc.gameSettings.advancedItemTooltips ? ITooltipFlag.TooltipFlags.ADVANCED : ITooltipFlag.TooltipFlags.NORMAL);

        for(int i = 0; i < list.size(); ++i) {
            if(i == 0) {
                list.set(i, itemStack.getItem().getForgeRarity(itemStack).getColor() + list.get(i));
            } else {
                list.set(i, TextFormatting.GRAY + list.get(i));
            }
        }
		
		FontRenderer font = itemStack.getItem().getFontRenderer(itemStack);
        GuiUtils.preItemToolTip(itemStack);
        GuiUtils.drawHoveringText(list, x, y, mc.currentScreen.width, mc.currentScreen.height, -1, (font == null ? mc.fontRenderer : font));
        GuiUtils.postItemToolTip();
	}
	
	public static void drawString(String s, float x, float y) {
		drawString(s, x, y, 15.0F, 0, 0xFFFFFFFF);
	}
	
	public static void drawString(String s, float x, float y, float fontSize) {
		drawString(s, x, y, fontSize, 0, 0xFFFFFFFF);
	}
	
	public static void drawString(String s, float x, float y, float fontSize, int alignment) {
		drawString(s, x, y, fontSize, alignment, 0xFFFFFFFF);
	}
	
	public static void drawString(String s, float x, float y, float fontSize, int alignment, int color) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		float scale = fontSize / 15.0F;
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.scale(scale, scale, 1.0f);
		if(alignment == 0) { // LEFT
			fr.drawString(s, x / scale, y / scale, color, false);
		} else if(alignment == 1) { // CENTER
			fr.drawString(s, (x - (fr.getStringWidth(s) * scale) / 2) / scale, y / scale, color, false);
		} else { // RIGHT
			fr.drawString(s, (x - (fr.getStringWidth(s) * scale)) / scale, y / scale, color, false);
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	public static void drawStringWithShadow(String s, float x, float y, float fontSize, int alignment) {
		FontRenderer fr = Minecraft.getMinecraft().fontRenderer;
		float scale = fontSize / 15.0F;
		
		GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.scale(scale, scale, 1.0f);
		if(alignment == 0) { // LEFT
			fr.drawString(s, x / scale, y / scale, 0xFFFFFFFF, true);
		} else if(alignment == 1) { // CENTER
			fr.drawString(s, (x - (fr.getStringWidth(s) * scale) / 2) / scale, y / scale, 0xFFFFFFFF, true);
		} else { // RIGHT
			fr.drawString(s, (x - (fr.getStringWidth(s) * scale)) / scale, y / scale, 0xFFFFFFFF, true);
		}
		GlStateManager.disableBlend();
		GlStateManager.popMatrix();
	}
	
	public static void bindTexture(ResourceLocation resource) {
    	ITextureObject textureObj = Minecraft.getMinecraft().getTextureManager().getTexture(resource);
    	if(textureObj == null) {
    		textureObj = new BlurTexture(resource);
    		Minecraft.getMinecraft().getTextureManager().loadTexture(resource, textureObj);
    	}
    	GlStateManager.bindTexture(textureObj.getGlTextureId());
    }
	
	public static void deleteTexture(ResourceLocation resource) {
		Minecraft.getMinecraft().getTextureManager().deleteTexture(resource);
	}
}
