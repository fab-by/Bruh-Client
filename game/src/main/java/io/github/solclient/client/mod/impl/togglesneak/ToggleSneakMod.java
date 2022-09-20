package io.github.solclient.client.mod.impl.togglesneak;

import com.google.gson.annotations.Expose;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import io.github.solclient.client.mod.ModCategory;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.SimpleHudMod;
import io.github.solclient.client.util.data.Position;
import io.github.solclient.client.util.data.Rectangle;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSneakMod extends SimpleHudMod {

	private ToggleSneakState sneak;
	@Expose
	@Option
	private boolean hud;

	@Override
	public void onRegister() {
		super.onRegister();

		Client.INSTANCE.unregisterKeyBinding(mc.gameSettings.keyBindSneak);
		mc.gameSettings.keyBindSneak = new ToggleSneakKeyBinding(this, mc.gameSettings.keyBindSneak.getKeyDescription(), 29,
				mc.gameSettings.keyBindSneak.getKeyCategory());
		Client.INSTANCE.registerKeyBinding(mc.gameSettings.keyBindSneak);
	}

	@Override
	public String getId() {
		return "toggle_sneak";
	}

	@Override
	public ModCategory getCategory() {
		return ModCategory.UTILITY;
	}

	@Override
	public boolean isVisible() {
		return hud;
	}

	@Override
	public String getText(boolean editMode) {
		if(!hud) {
			return null;
		}
		if(editMode) {
			return ToggleSneakState.TOGGLED.toString();
		}
		return getSneak() == null ? null : getSneak().toString();
	}

	public ToggleSneakState getSneak() {
		return sneak;
	}

	public void setSneak(ToggleSneakState sneak) {
		this.sneak = sneak;
	}

}
