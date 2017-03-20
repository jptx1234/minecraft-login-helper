package com.github.jptx1234.loginHelper;

import java.lang.reflect.Field;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class DisconnectedScreenTimer{

	public ServerData serverData;
	private volatile long startTime = -1L;
	private volatile long endTime = -1L;
	private volatile long secondLeft = 0L;
	GuiScreen serverListScreen;
	Minecraft mc = Minecraft.getMinecraft();
	FontRenderer fontRenderer;
	
	public DisconnectedScreenTimer(GuiDisconnected disconnected,ServerData serverData){
		try {
			this.serverData = serverData;
			Field field_146307_h_Field = GuiDisconnected.class.getDeclaredField("field_146307_h");
			field_146307_h_Field.setAccessible(true);
			serverListScreen = (GuiScreen) field_146307_h_Field.get(disconnected);
			Field fontRendererField = GuiDisconnected.class.getSuperclass().getDeclaredField("fontRendererObj");
			fontRendererField.setAccessible(true);
			fontRenderer = (FontRenderer)fontRendererField.get(disconnected);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		
	}
	
	/*public DisconnectedScreenTimer(GuiScreen p_i45020_1_, String p_i45020_2_,
			IChatComponent p_i45020_3_) {
		super(p_i45020_1_, p_i45020_2_, p_i45020_3_);
		serverListScreen = p_i45020_1_;
		FMLCommonHandler.instance().bus().register(this);
	}
	
	


	public static DisconnectedScreenTimer getInstance(GuiDisconnected guiDisconnected,ServerData serverData){
		DisconnectedScreenTimer instance = null;
		
		try {
			Field field_146307_h_Field = GuiDisconnected.class.getDeclaredField("field_146307_h");
			field_146307_h_Field.setAccessible(true);
			GuiScreen field_146307_h_obj = (GuiScreen) field_146307_h_Field.get(guiDisconnected);
			Field field_146306_a_field = GuiDisconnected.class.getDeclaredField("field_146306_a");
			field_146306_a_field.setAccessible(true);
			String field_146306_a_obj = (String)field_146306_a_field.get(guiDisconnected);
			Field field_146304_f_field = GuiDisconnected.class.getDeclaredField("field_146304_f");
			field_146304_f_field.setAccessible(true);
			IChatComponent field_146304_f_obj = (IChatComponent)field_146304_f_field.get(guiDisconnected);
			
			instance = new DisconnectedScreenTimer(field_146307_h_obj,"",field_146304_f_obj);
			instance.serverData = serverData;
			Field field_146306_a_father_field = instance.getClass().getSuperclass().getDeclaredField("field_146306_a");
			field_146306_a_father_field.setAccessible(true);
			field_146306_a_father_field.set(instance, field_146306_a_obj);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return instance;
	}


	public void drawScreen(int par1, int par2, float par3) {

		super.drawScreen(par1, par2, par3);
		long secondLeft = (endTime - System.currentTimeMillis())/1000+1;
		this.drawCenteredString(fontRendererObj, secondLeft + " 秒后自动连接", width/2, height/2, 0xFFFF00);
	}*/
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event){
		GuiScreen screen = mc.currentScreen;
		if (screen instanceof GuiDisconnected) {
			if (startTime == -1L) {
				startTime = System.currentTimeMillis();
				endTime = startTime + Config.secondBeforeReconnect * 1000;
			}else if (System.currentTimeMillis() >= endTime) {
				mc.displayGuiScreen(new GuiConnecting(serverListScreen, mc, serverData));
			}else {
				secondLeft = (endTime - System.currentTimeMillis())/1000+1;
			}
		}else {
			FMLCommonHandler.instance().bus().unregister(this);
		}
	}
	
	public long getSecondLeft(){
		return secondLeft;
	}
	
	public void ExcuteNow(){
		endTime = startTime;
	}
}
