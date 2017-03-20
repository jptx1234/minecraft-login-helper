package com.github.jptx1234.loginHelper;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiScreenServerList;
import net.minecraft.util.ChatComponentText;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;

public class SendMsg {
	private volatile boolean runAble = true;
	private List<String[]> msgList;
	private int msgListPoint = 0;
	long lastTime = 0;
	long thisDelay = -1;
	String thisMsg = null;
	Minecraft mc = Minecraft.getMinecraft();
	EntityClientPlayerMP player;
	
	public SendMsg(List<String[]> msgList){
		this.msgList = msgList;
		FMLCommonHandler.instance().bus().register(this);
	}
	
	@SubscribeEvent
	public void send(ClientTickEvent event){
		GuiScreen screen = mc.currentScreen;
		if (screen != null && screen instanceof GuiScreenServerList) {
			runAble = false;
		}
		if (runAble) {
			if ((player = mc.thePlayer) != null) {
				if (msgListPoint > msgList.size()-1) {
					runAble = false;
					if (msgList.size() != 0) {
						player.addChatComponentMessage(new ChatComponentText("[LoginHelper] 已发送完所有消息"));
					}
					return;
				}
				if (lastTime == 0) {
					lastTime = System.currentTimeMillis();
				}
				String[] msgConfig = msgList.get(msgListPoint);
				if (msgConfig.length != 2 || msgConfig[0] == null || msgConfig[1] == null) {
					return;
				}
				if (thisDelay == -1 || thisMsg == null) {
					thisDelay = Long.valueOf(msgConfig[0]) * 1000;
					thisMsg = msgConfig[1];
				}
				if (System.currentTimeMillis() - lastTime >= thisDelay) {
					player.sendChatMessage(thisMsg);
					msgListPoint++;
					lastTime = System.currentTimeMillis();
					thisDelay = -1;
					thisMsg = null;
				}
			}
		}else {
			FMLCommonHandler.instance().bus().unregister(this);
		}
	}
	
	public void shutDown(){
		this.runAble = false;
	}
}
