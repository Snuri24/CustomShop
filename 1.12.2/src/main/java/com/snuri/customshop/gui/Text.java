package com.snuri.customshop.gui;

import com.snuri.customshop.Render;
import com.snuri.customshop.network.UpdateType;

import net.minecraft.client.gui.GuiScreen;

public class Text extends GuiComponent {

	private GuiScreen gui;
	
	private String text;
	private String displayText;
	private float fontSize;
	private int alignment;
	private int maxLength;
	private double[] pos;
	private boolean visible;
	
	public Text(GuiScreen gui, String text, float fontSize, int alignment, int maxLength, double x, double y, boolean visible) {
		this.gui = gui;
		
		this.text = null;
		this.fontSize = (float) (fontSize / ShopGui.referenceHeight * 2.0D);
		this.alignment = alignment;
		this.maxLength = maxLength;
		pos = new double[2];
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
		this.visible = visible;
		
		if(text != null) {
			if(text.length() > maxLength) {
				displayText = text.substring(0, maxLength) + "...";
			} else {
				displayText = text;
			}
		}
	}
	
	public void render() {
		if(text != null && visible) {
			Render.drawString(displayText, (float) (gui.width * pos[0]), (float) (gui.height * pos[1]), gui.height * fontSize, alignment);
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
		} else if(type.equals(UpdateType.TEXT)) {
			setText(value.substring(2));
		} else if(type.equals(UpdateType.FONT_SIZE)) {
			setFontSize(Integer.parseInt(value.substring(2)));
		} else if(type.equals(UpdateType.ALIGNMENT)) {
			setAlignment(Integer.parseInt(value.substring(2)));
		} else if(type.equals(UpdateType.MAX_LENGTH)) {
			setMaxLength(Integer.parseInt(value.substring(2)));
		}
	}
	
	public void setText(String text) {
		if(text.equals("NULL")) {
			this.text = null;
			displayText = null;
		} else {
			this.text = text;
			if(text != null) {
				if(text.length() > maxLength) {
					displayText = text.substring(0, maxLength) + "...";
				} else {
					displayText = text;
				}
			}
		}
	}
	
	public void setFontSize(int fontSize) {
		this.fontSize = (float) (fontSize / ShopGui.referenceHeight * 2);
	}
	
	public void setAlignment(int alignment) {
		this.alignment = alignment;
	}
	
	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if(text != null) {
			if(text.length() > maxLength) {
				displayText = text.substring(0, maxLength) + "...";
			} else {
				displayText = text;
			}
		}
	}
	
	public void setPosition(int x, int y) {
		pos[0] = x / ShopGui.referenceWidth;
		pos[1] = y / ShopGui.referenceHeight;
	}
	
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}
