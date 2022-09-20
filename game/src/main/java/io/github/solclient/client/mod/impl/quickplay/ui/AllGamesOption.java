package io.github.solclient.client.mod.impl.quickplay.ui;

import io.github.solclient.client.mod.impl.quickplay.QuickPlayMod;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class AllGamesOption implements QuickPlayOption {

	@Override
	public String getText() {
		return "All Games >";
	}

	@Override
	public void onClick(QuickPlayPalette palette, QuickPlayMod mod) {
		palette.openAllGames();
	}

	@Override
	public ItemStack getIcon() {
		return new ItemStack(Items.compass);
	}

}
