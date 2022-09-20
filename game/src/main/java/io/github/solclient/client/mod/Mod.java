package io.github.solclient.client.mod;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.Expose;
import com.replaymod.replay.ReplayModReplay;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.GameOverlayElement;
import io.github.solclient.client.event.impl.PostGameOverlayRenderEvent;
import io.github.solclient.client.event.impl.PostGameStartEvent;
import io.github.solclient.client.mod.annotation.AbstractTranslationKey;
import io.github.solclient.client.mod.annotation.Option;
import io.github.solclient.client.mod.hud.HudElement;
import io.github.solclient.client.mod.impl.replay.fix.SCEventRegistrations;
import io.github.solclient.client.ui.screen.mods.MoveHudsScreen;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;

@AbstractTranslationKey("sol_client.mod.generic")
public abstract class Mod {

	protected final Minecraft mc = Minecraft.getMinecraft();
	private List<ModOption> options;
	private boolean blocked;
	@Expose
	@Option(priority = 2)
	private boolean enabled = isEnabledByDefault();
	protected final Logger logger = LogManager.getLogger();

	public void postStart() {
	}

	public void onRegister() {
		try {
			options = ModOption.get(this);
		}
		catch(IOException error) {
			throw new IllegalStateException(error);
		}

		if(this.enabled) {
			tryEnable();
		}
	}

	public String getTranslationKey() {
		return "sol_client.mod." + getId();
	}

	public String getName() {
		return I18n.format(getTranslationKey() + ".name");
	}

	public abstract String getId();

	public String getDescription() {
		return I18n.format("sol_client.mod." + getId() + ".description");
	}

	public abstract ModCategory getCategory();

	public boolean isEnabledByDefault() {
		return false;
	}

	public boolean onOptionChange(String key, Object value) {
		if(key.equals("enabled")) {
			if(isLocked()) {
				return false;
			}
			setEnabled((boolean) value);
		}
		return true;
	}

	public void postOptionChange(String key, Object value) {
	}

	public List<ModOption> getOptions() {
		return options;
	}

	public void setEnabled(boolean enabled) {
		if(blocked) return;
		if(isLocked()) return;

		if(enabled != this.enabled) {
			if(enabled) {
				tryEnable();
			}
			else {
				try {
					onDisable();
				}
				catch(Throwable error) {
					logger.error("Error while disabling mod", error);
				}
			}
		}
		this.enabled = enabled;
	}

	private void tryEnable() {
		try {
			onEnable();
		}
		catch(Throwable error) {
			logger.error("Could not enable mod", error);
			setEnabled(false);
		}
	}

	protected void onEnable() {
		Client.INSTANCE.bus.register(this);
	}

	protected void onDisable() {
		Client.INSTANCE.bus.unregister(this);
	}

	public boolean isBlocked() {
		return blocked;
	}

	public void block() {
		if(enabled && !blocked) {
			onDisable();
		}
		blocked = true;
	}

	public void unblock() {
		if(enabled && blocked) {
			onEnable();
		}
		blocked = false;
	}

	public boolean isEnabled() {
		return enabled && !blocked;
	}

	public void toggle() {
		setEnabled(!isEnabled());
	}

	public void disable() {
		setEnabled(false);
	}

	public void enable() {
		setEnabled(true);
	}

	public int getIndex() {
		return Client.INSTANCE.getMods().indexOf(this);
	}

	public List<HudElement> getHudElements() {
		return Collections.emptyList();
	}

	public boolean isLocked() {
		return false;
	}

	public String getLockMessage() {
		return "";
	}

	public void onFileUpdate(String fieldName) {}

	public void render(boolean editMode) {
		for(HudElement element : getHudElements()) {
			element.render(editMode);
		}
	}

	@EventHandler
	public void onRender(PostGameOverlayRenderEvent event) {
		if(event.type == GameOverlayElement.ALL) {
			render(mc.currentScreen instanceof MoveHudsScreen);
		}
	}

}
