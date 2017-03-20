package com.github.jptx1234.loginHelper;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent;
import net.minecraftforge.common.MinecraftForge;

public class LoginHelperConfigGui extends GuiScreen{
	private List<String[]> msgList;
	private List<GuiTextField> tfs;  
	private List<GuiButton> itemButtons = new ArrayList<GuiButton>();
	private int nowPg = 0;
	private int itemPerPage = 3;
	private boolean canAddPage = false;
	private int doneButtonId = 0;
	private int addPageButtonId = 1;
	private int dePageButtonId = 2;
	private GuiButton doneButton = new GuiButton(doneButtonId, 0, 0, "完成");
	private GuiButton addPageButton = new GuiButton(addPageButtonId, 0, 0, ">");
	private GuiButton dePageButton = new GuiButton(dePageButtonId, 0, 0, "<");
	private int tfHeight;
	private int tfSeconfWidth = 40;
	private int tfMsgWidth = 200;
	private GuiTextField timeBeforeConnectField;
	private String serverIP;
	
	private GuiScreen parent;
	Minecraft mc = Minecraft.getMinecraft();
	
	
	public LoginHelperConfigGui(String serverIP,GuiScreen parent){
		this.serverIP = serverIP;
		this.msgList = Config.getMsgList(serverIP);
		tfs  = new ArrayList<GuiTextField>(msgList.size());
		this.parent = parent;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		Keyboard.enableRepeatEvents(true);
		super.initGui();
		tfHeight = fontRendererObj.FONT_HEIGHT + 4;
		addPageButton.width = 20;
		addPageButton.height = 20;
		dePageButton.width = 20;
		dePageButton.height = 20;
		buttonList.add(doneButton);
		buttonList.add(addPageButton);
		buttonList.add(dePageButton);
		timeBeforeConnectField = new GuiTextField(fontRendererObj, 0, 0, 40, tfHeight);
		for (String[] strings : msgList) {
			if (strings.length == 2 && strings[0] != null && strings[1] != null) {
				GuiTextField timeField = new GuiTextField(fontRendererObj, 0, 0, tfSeconfWidth, tfHeight);
				timeField.setText(strings[0]);
				tfs.add(timeField);
				timeField.setCanLoseFocus(true);
				timeField.setFocused(false);
				timeField.setMaxStringLength(1024);
				GuiTextField msgField = new GuiTextField(fontRendererObj, 0, 0, tfMsgWidth,tfHeight);
				msgField.setText(strings[1]);
				tfs.add(msgField);
				msgField.setCanLoseFocus(true);
				msgField.setFocused(false);
				msgField.setMaxStringLength(1024);
			}
		}
		if (tfs.size() == 0) {
			GuiTextField tmpSecond = new GuiTextField(fontRendererObj, 0, 0, tfSeconfWidth, tfHeight);
			GuiTextField tmpMsg = new GuiTextField(fontRendererObj, 0, 0, tfMsgWidth, tfHeight);
			tfs.add(tmpSecond);
			tfs.add(tmpMsg);
		}
		timeBeforeConnectField.setText(String.valueOf(Config.secondBeforeReconnect));
		timeBeforeConnectField.setFocused(false);
		timeBeforeConnectField.setCanLoseFocus(true);
		timeBeforeConnectField.setMaxStringLength(1024);
	}
	
	@Override
	public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
		dePageButton.enabled = nowPg <= 0 ? false : true;
		addPageButton.enabled = canAddPage ? true : false;
		int nowPage = nowPg;
		drawDefaultBackground();
		doneButton.xPosition = width/2 - 100;
		doneButton.yPosition = height / 8 * 7;
		addPageButton.xPosition = doneButton.xPosition + doneButton.width - addPageButton.width;
		addPageButton.yPosition = height/3 * 2;
		dePageButton.xPosition = doneButton.xPosition ;
		dePageButton.yPosition = height/3 * 2;
		super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
		drawCenteredString(fontRendererObj, "发送之前延迟秒数", width/5, height/5, 0xFFFFFF);
		drawCenteredString(fontRendererObj,"发送的文本内容", width/5 * 3, height/5, 0xFFFFFF);
		drawCenteredString(fontRendererObj, "第 "+(nowPage+1)+" 页", width/2, height/3 * 2 + 10 - fontRendererObj.FONT_HEIGHT / 2,  0xFFFFFF);
		drawList(p_73863_1_,p_73863_2_);
		drawString(fontRendererObj, "掉线自动登录延迟秒数", doneButton.xPosition + doneButton.width/2 - (fontRendererObj.FONT_HEIGHT * 10 + timeBeforeConnectField.width + 10)/2, doneButton.yPosition - 10 - fontRendererObj.FONT_HEIGHT, 0xFFFFFF);
		timeBeforeConnectField.xPosition = doneButton.xPosition + doneButton.width/2 + (fontRendererObj.FONT_HEIGHT * 10 + timeBeforeConnectField.width + 10)/2 - timeBeforeConnectField.width;
		timeBeforeConnectField.yPosition = doneButton.yPosition - 10 - fontRendererObj.FONT_HEIGHT;
		timeBeforeConnectField.drawTextBox();
	}
	
	public void drawList(int mouseX,int mouseY){
		int nowPage = nowPg;
		List<GuiTextField> tfToShow = new ArrayList<GuiTextField>();
		itemButtons.clear();
		for (int i = 0; i < tfs.size(); i++) {
			GuiTextField tmp = tfs.get(i);
			if (i >= nowPage * itemPerPage * 2 && i < tfs.size() && i < (nowPage + 1 ) * itemPerPage * 2) {
				tfToShow.add(tmp);
			}else {
				tmp.xPosition = -1000;
			}
		}
		int lastHeight = height / 5 + fontRendererObj.FONT_HEIGHT + 10;
		int lastButton = 50;
		for (int i = 0; i < tfToShow.size(); i++) {
			GuiTextField tmp = tfToShow.get(i);
			if (i % 2 == 0) {
				tmp.xPosition = width / 5 - tfSeconfWidth/2;
				tmp.yPosition = lastHeight;
			}else {
				tmp.xPosition = width/5 * 3 - tfMsgWidth/2;
				tmp.yPosition = lastHeight;
				lastHeight += 30;
				GuiButton addButton = new GuiButton(lastButton++, tmp.xPosition + tmp.width + 5, tmp.yPosition - (20 - fontRendererObj.FONT_HEIGHT)/2, 20,20,"+");
				GuiButton deButton  =  new GuiButton(lastButton++,addButton.xPosition + addButton.width + 5, addButton.yPosition, 20,20,"-");
				itemButtons.add(addButton);
				itemButtons.add(deButton);
				addButton.drawButton(mc, mouseX, mouseY);
				deButton.drawButton(mc, mouseX, mouseY);
			}
			tmp.drawTextBox();
		}
		canAddPage = tfs.size() > (nowPage + 1 ) * itemPerPage * 2 ? true : false;
	}
	@Override
	protected void actionPerformed(GuiButton button) {
		if (button.enabled) {
			int id = button.id;
			if (id == doneButtonId) {
				mc.displayGuiScreen(parent);
			}else if (id >= 50 && id <= (50 + itemPerPage * 2)) {
				int nowPage = nowPg;
				if (id %2 == 0) {
					GuiTextField newTimeField = new GuiTextField(fontRendererObj, 0, 0, tfSeconfWidth, tfHeight);
					newTimeField.setFocused(false);
					newTimeField.setCanLoseFocus(true);
					newTimeField.setMaxStringLength(1024);
					tfs.add(nowPage * itemPerPage * 2 + id - 50 +2, newTimeField);
					GuiTextField newMsgField = new GuiTextField(fontRendererObj, 0, 0, tfMsgWidth, tfHeight);
					newMsgField.setFocused(false);
					newMsgField.setCanLoseFocus(true);
					newMsgField.setMaxStringLength(1024);
					tfs.add(nowPage * itemPerPage + id - 50 +3,newMsgField);
				}else {
					tfs.remove(nowPage * itemPerPage + id - 50 - 1);
					tfs.remove(nowPage * itemPerPage + id - 50 - 1);
				}
				button.enabled = false;
//				if (id - 50 + 1== (nowPage + 1) * itemPerPage * 2 - 1) {
//					nowPage++;
//				}
//				if (id - 50 + 1 == (nowPage) * itemPerPage * 2 + 2 && nowPage > 0) {
//					nowPage--;
//				}
			}else if (id == addPageButtonId) {
				nowPg = canAddPage ? nowPg + 1 : nowPg;
			}else if (id == dePageButtonId) {
				nowPg = nowPg <= 0 ? 0 : nowPg - 1;
			}
		}
	}
	
	
	@Override
	protected void keyTyped(char par1, int par2) {
		if (timeBeforeConnectField.textboxKeyTyped(par1, par2)) {
			return;
		}
		for (GuiTextField textField : tfs) {
			if (textField.textboxKeyTyped(par1, par2)) {
				return;
			}
		}
	    super.keyTyped(par1, par2);
	}
	 
	@Override
	protected void mouseClicked(int par1, int par2, int par3) {
		for (GuiTextField textField : tfs) {
			textField.mouseClicked(par1, par2, par3);
		}
		timeBeforeConnectField.mouseClicked(par1, par2, par3);
		for (GuiButton guiButton : itemButtons) {
			if (guiButton.mousePressed(this.mc, par1, par2))
            {
                ActionPerformedEvent.Pre event = new ActionPerformedEvent.Pre(this, guiButton, this.buttonList);
                if (MinecraftForge.EVENT_BUS.post(event))
                    break;
                event.button.func_146113_a(this.mc.getSoundHandler());
                this.actionPerformed(event.button);
                if (this.equals(this.mc.currentScreen))
                    MinecraftForge.EVENT_BUS.post(new ActionPerformedEvent.Post(this, event.button, this.buttonList));
            }
		}
	    super.mouseClicked(par1, par2, par3);
	}
	 
	@Override
	public void onGuiClosed() {
	    Keyboard.enableRepeatEvents(false); 
	    List<String[]> msgList = new ArrayList<String[]>();
	    String[] tmps = new String[2];
	    for (int i = 0; i < tfs.size(); i++) {
	    	String tmp = tfs.get(i).getText();
	    	if (i % 2 == 0) {
	    		try {
					Long.valueOf(tmp);
				} catch (Exception e) {
					i++;
					continue;
				}
	    		tmps[0] = tmp;
			}else {
				if (tmp != null && tmp.length() != 0) {
					tmps[1] = tmp;
				}
			}
	    	if (tmps[0] != null && tmps[1] != null) {
				msgList.add(tmps);
				tmps = new String[2];
			}
		}
	    Config.setMsgList(serverIP, msgList);
	    String timeBeforeConnect = timeBeforeConnectField.getText();
	    try {
	    	timeBeforeConnect = timeBeforeConnect.trim();
			long time = Long.valueOf(timeBeforeConnect);
			Config.secondBeforeReconnect = time; 
		} catch (Exception e) {
		}
	}
}
