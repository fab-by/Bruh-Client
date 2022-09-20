package io.github.solclient.client.mod.impl.discordrpc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.hud.BaseHudElement;
import io.github.solclient.client.mod.hud.HudPosition;
import io.github.solclient.client.mod.impl.discordrpc.socket.User;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class DiscordVoiceChatHud extends BaseHudElement {

	private static final int USER_HEIGHT = 20;

	protected final FontRenderer font;
	private final DiscordIntegrationMod mod;

	public DiscordVoiceChatHud(DiscordIntegrationMod mod) {
		this.mod = mod;
		font = Minecraft.getMinecraft().fontRendererObj;
	}

	@Override
	public boolean isVisible() {
		return mod.isEnabled() && mod.voiceChatHud;
	}

	@Override
	public Rectangle getBounds(Position position) {
		int yOffset = 0;

		switch(mod.voiceChatHudAlignment) {
			case MIDDLE:
				yOffset = (USER_HEIGHT * 4) / 2;
				break;
			case BOTTOM:
				yOffset = USER_HEIGHT * 4;
				break;
			default:
				break;
		}

		return position.offset(0, yOffset).rectangle(20 + font.getStringWidth("Fabra") + 4, 76);
	}

	@Override
	public void render(Position position, boolean editMode) {
		Collection<User> users;

		if(editMode) {
			users = new ArrayList<>();

			User fabra = new User("0");
			fabra.setName("Fabra");
			users.add(fabra);

			User yakisn0w = new User("0");
			yakisn0w.setName("Yakisn0w");
			users.add(yakisn0w);

			User nottaz = new User("0");
			nottaz.setName("NotTaz");
			users.add(nottaz);

			User nutnut = new User("0");
			nutnut.setName("NutNut");
			users.add(nutnut);
		}
		else if(mod.socket == null) {
			return;
		}
		else {
			users = mod.socket.getVoiceCallUsers();
		}

		int y = position.getY();

		switch(mod.voiceChatHudAlignment) {
			case MIDDLE:
				y -= (USER_HEIGHT * (users.size())) / 2;
				break;
			case BOTTOM:
				y -= USER_HEIGHT * users.size();
				break;
			default:
				break;
		}

		for(User user : users) {
			user.bindTexture();
			Gui.drawModalRectWithCustomSizedTexture(position.getX(), y, 0, 0, 16, 16, 16, 16);

			if(user.isSpeaking()) {
				Utils.drawOutline(position.getX() - 1, y - 1, position.getX() + 17, y + 17, mod.speakingColour.getValue());
			}

			font.drawString(user.getName(), position.getX() + 20, y + 4, user.isMuted() ? mod.mutedColour.getValue() : mod.usernameColour.getValue(), mod.shadow);
			y += 20;
		}
	}

	@Override
	public Mod getMod() {
		return mod;
	}

	@Override
	public boolean isShownInReplay() {
		return false;
	}

	@Override
	public HudPosition getHudPosition() {
		return mod.voiceChatHudPosition;
	}

	@Override
	public void setHudPosition(HudPosition position) {
		mod.voiceChatHudPosition = position;
	}

	@Override
	public float getHudScale() {
		return mod.voiceChatHudScale / 100F;
	}

}
