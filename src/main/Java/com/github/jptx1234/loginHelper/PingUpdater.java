package com.github.jptx1234.loginHelper;

import io.netty.util.concurrent.GenericFutureListener;

import java.net.InetAddress;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.network.status.client.C01PacketPing;
import net.minecraft.network.status.server.S00PacketServerInfo;
import net.minecraft.network.status.server.S01PacketPong;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.IChatComponent;

public class PingUpdater implements INetHandlerStatusClient ,Runnable{
	public volatile Long ping = null;
	private NetworkManager networkManager;
	private volatile boolean run = true;
	public volatile long pingDelay = 5000l;
	private ServerAddress serverAddress;
	private InetAddress inetAddress;
	private int port;
	private long lastUpdate = 0l;
	
	public PingUpdater(ServerData serverData){
		System.out.println("------------new");
		serverAddress = ServerAddress.func_78860_a(serverData.serverIP);
		try {
			inetAddress = InetAddress.getByName(serverAddress.getIP());
		} catch (Exception e) {
			inetAddress = null;
		}
		port = serverAddress.getPort();
		new Thread(this).start();
	}

	public void stop(){
		run = false;
		networkManager.closeChannel(new ChatComponentText("PingUpdater Stopped."));
	}
	
	@Override
	public void onDisconnect(IChatComponent p_147231_1_) {
		ping = null;
	}

	@Override
	public void onConnectionStateTransition(EnumConnectionState p_147232_1_,
			EnumConnectionState p_147232_2_) {

	}

	@Override
	public void onNetworkTick() {

	}

	@Override
	public void handleServerInfo(S00PacketServerInfo p_147397_1_) {

	}

	@Override
	public void handlePong(S01PacketPong p_147398_1_) {
		long backTime = Minecraft.getSystemTime();
		long sendTime = p_147398_1_.func_149292_c();
		lastUpdate = backTime;
		ping = backTime - sendTime;
		networkManager.closeChannel(null);
	}

	@Override
	public void run() {
		while (run) {
			try {
				networkManager = NetworkManager.provideLanClient(inetAddress, port); 
				networkManager.setNetHandler(this);
				networkManager.scheduleOutboundPacket(new C00Handshake(5, serverAddress.getIP(), serverAddress.getPort(), EnumConnectionState.STATUS), new GenericFutureListener[0]);
				long nowtime = Minecraft.getSystemTime();
				networkManager.scheduleOutboundPacket(new C01PacketPing(nowtime), new GenericFutureListener[0]);
				if (nowtime - lastUpdate > 15000l) {
					ping = null;
				}
				Thread.sleep(pingDelay);
			} catch (Exception e) {
			}
		}
	}

}
