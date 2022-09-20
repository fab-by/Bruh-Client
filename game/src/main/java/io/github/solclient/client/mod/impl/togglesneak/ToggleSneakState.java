package io.github.solclient.client.mod.impl.togglesneak;

import net.minecraft.client.resources.I18n;

public enum ToggleSneakState {
	HELD,
	TOGGLED;

	@Override
	public String toString() {
		return I18n.format("sol_client.mod.toggle_sneak." + name().toLowerCase());
	}

}
