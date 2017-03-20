package com.github.jptx1234.loginHelper;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.GuiConnecting;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;

@Mod(modid="loginHelper", name="loginHelper", version="v1.0")
@SideOnly(Side.CLIENT)
public class LoginHelper {
	public static final int joinNowButtonId = 3254;
	public static final int configButtonId = 3255; 
	public static Minecraft mc = Minecraft.getMinecraft();
	public static ServerData serverData = null;
	int counter = 0;
	SendMsg sendmsg;
	DisconnectedScreenTimer disconnectedScreenTimer;
	LoginHelperConfigGui loginHelperConfigGui = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		FMLCommonHandler.instance().bus().register(this);
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	
	
	@SubscribeEvent
	public void onJoin(ClientConnectedToServerEvent event){
		if (event.isLocal) {
			return;
		}
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				sendmsg = new SendMsg(Config.getMsgList(serverData.serverIP));
				PingDisplay.getInstance().start();
			}
		}).start();
	}
	@SubscribeEvent
	public void onLogout(ClientDisconnectionFromServerEvent event){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				if (sendmsg != null) {
					sendmsg.shutDown();
				}
				PingDisplay.getInstance().stop();
				serverData = null;
			}
		}).start();
	}
	

	
	@SuppressWarnings("unchecked")
	@SubscribeEvent
    public void checkGUI(InitGuiEvent.Post event){
    	GuiScreen screen = event.gui;
    	if (screen == null) {
    		return ;
    	}
    	if (screen instanceof GuiDisconnected && serverData != null) {
    		disconnectedScreenTimer = new DisconnectedScreenTimer((GuiDisconnected)screen,serverData);
    		event.buttonList.add(new GuiButton(joinNowButtonId, screen.width / 2 - 100, screen.height / 4 + 90 + 12, "立即连接"){
    			@Override
    			public void drawButton(Minecraft p_146112_1_, int p_146112_2_,
    					int p_146112_3_) {
    				this.displayString = "立即连接 ( "+disconnectedScreenTimer.getSecondLeft()+" )";
    				super.drawButton(p_146112_1_, p_146112_2_, p_146112_3_);
    			}
    		});
    		FMLCommonHandler.instance().bus().register(disconnectedScreenTimer);
    		if (sendmsg != null) {
    			sendmsg.shutDown();
			}
    		PingDisplay.getInstance().stop();
    	}else if (screen instanceof GuiConnecting ) {
    		serverData = mc.func_147104_D();
		}else if (screen instanceof GuiOptions && mc.theWorld != null && mc.theWorld.isRemote && serverData != null) {
			event.buttonList.add(new GuiButton(configButtonId, screen.width / 2 - 155, screen.height / 6 + 48 - 6, 150, 20, "LoginHelper 设置..."));
		}
    }
	
	@SubscribeEvent
	public void checkAction(ActionPerformedEvent.Post event){
		GuiScreen screen = event.gui;
		if (screen instanceof GuiDisconnected && event.button.id == joinNowButtonId) {
			disconnectedScreenTimer.ExcuteNow();
		}else if (screen instanceof GuiOptions && event.button.id == configButtonId && mc.theWorld != null && mc.theWorld.isRemote && serverData != null) {
//			new Thread(new Runnable() {
//				
//				@Override
//				public void run() {
//					ConfigWin.showOn(serverData.serverIP);
//				}
//			}).start();
			loginHelperConfigGui = new LoginHelperConfigGui(serverData.serverIP,screen);
			mc.displayGuiScreen(loginHelperConfigGui);
		}
	}
	

	
}
