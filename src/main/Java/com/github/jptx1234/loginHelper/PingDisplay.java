package com.github.jptx1234.loginHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PingDisplay {
	private static PingDisplay instance = null;
	Minecraft mc = Minecraft.getMinecraft();
	private volatile PingUpdater pingUpdater;
	private StringBuilder sb = new StringBuilder();
	private FontRenderer fontRenderer = mc.fontRenderer;
	
	public static PingDisplay getInstance(){
		if (instance == null) {
			synchronized (PingDisplay.class) {
				if (instance == null) {
					instance = new PingDisplay();
				}
			}
		}
		return instance;
	}
	private PingDisplay(){
		
	}
	
	public void start(){
		if (LoginHelper.serverData == null) {
			return;
		}
		pingUpdater = new PingUpdater(LoginHelper.serverData);
		MinecraftForge.EVENT_BUS.register(this);
//		FMLCommonHandler.instance().bus().register(this);
	}
	
	public void stop(){
		if (pingUpdater != null) {
			pingUpdater.stop();
		}
		MinecraftForge.EVENT_BUS.unregister(this);
//		FMLCommonHandler.instance().bus().register(this);
	}
	
	
	
	@SubscribeEvent
	public void showPing(RenderGameOverlayEvent.Text event){
		if (mc.theWorld == null || !mc.theWorld.isRemote) {
			stop();
			return;
		}
		if (!mc.gameSettings.showDebugInfo) {
			boolean unicodeFlag = fontRenderer.getUnicodeFlag();
			fontRenderer.setUnicodeFlag(true);
			String info = getPingInformation();
			ScaledResolution res = new ScaledResolution(mc,mc.displayWidth,mc.displayHeight);
			int stringWidth = fontRenderer.getStringWidth(info);
			int width = res.getScaledWidth()-stringWidth;
			int height = 0;
			fontRenderer.drawStringWithShadow(info, width, height, 0x00ffff);
			fontRenderer.setUnicodeFlag(unicodeFlag);
		}
	}
	
	private String getPingInformation(){
		sb.delete(0, sb.length());
		Long time = pingUpdater.ping;
		if (time == null) {
			sb.append("Pinging...");
		}else {
			sb.append("Ping: ");
			sb.append(time);
			sb.append(" ms");
		}
		String info = sb.toString();
		return info;

   }
}
