package io.github.solclient.client.mod.impl.togglesneak;

import io.github.solclient.client.Client;
import io.github.solclient.client.event.EventHandler;
import io.github.solclient.client.event.impl.PostTickEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;

public class ToggleSneakKeyBinding extends KeyBinding {

	private final Minecraft mc = Minecraft.getMinecraft();
	private final ToggleSneakMod mod;
	private boolean wasDown;
	private long startTime;

	public ToggleSneakKeyBinding(ToggleSneakMod mod, String description, int keyCode, String category) {
		super(description, keyCode, category);
		this.mod = mod;
		Client.INSTANCE.bus.register(this);
	}

	@EventHandler
	public void tickBinding(PostTickEvent event) {
		boolean down = super.isKeyDown();
		if(mod.isEnabled()) {
			if(down) {
				if(!wasDown) {
					startTime = System.currentTimeMillis();
					if(mod.getSneak() == ToggleSneakState.TOGGLED) {
						mod.setSneak(ToggleSneakState.HELD);
					}
					else {
						mod.setSneak(ToggleSneakState.TOGGLED);
					}
				}
				else if((System.currentTimeMillis() - startTime) > 250) {
					mod.setSneak(ToggleSneakState.HELD);
				}
			}
			else if(mod.getSneak() == ToggleSneakState.HELD) {
				mod.setSneak(null);
			}

			wasDown = down;
		}
	}

	@Override
	public boolean isKeyDown() {
		if(mod.isEnabled()) {
			return mc.currentScreen == null && mod.getSneak() != null;
		}
		return super.isKeyDown();
	}

}