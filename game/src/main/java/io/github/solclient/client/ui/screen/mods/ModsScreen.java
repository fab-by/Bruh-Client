package io.github.solclient.client.ui.screen.mods;

import java.io.IOException;

import org.lwjgl.input.Keyboard;

import io.github.solclient.client.Client;
import io.github.solclient.client.mod.Mod;
import io.github.solclient.client.mod.impl.SolClientMod;
import io.github.solclient.client.ui.component.Component;
import io.github.solclient.client.ui.component.ComponentRenderInfo;
import io.github.solclient.client.ui.component.Screen;
import io.github.solclient.client.ui.component.controller.AlignedBoundsController;
import io.github.solclient.client.ui.component.controller.AnimatedColourController;
import io.github.solclient.client.ui.component.handler.KeyHandler;
import io.github.solclient.client.ui.component.impl.ButtonComponent;
import io.github.solclient.client.ui.component.impl.LabelComponent;
import io.github.solclient.client.ui.component.impl.ScaledIconComponent;
import io.github.solclient.client.ui.component.impl.TextFieldComponent;
import io.github.solclient.client.ui.screen.PanoramaBackgroundScreen;
import io.github.solclient.client.ui.screen.SolClientMainMenu;
import io.github.solclient.client.util.Utils;
import io.github.solclient.client.util.data.Alignment;
import io.github.solclient.client.util.data.Colour;
import io.github.solclient.client.util.data.Rectangle;
import lombok.Getter;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public class ModsScreen extends PanoramaBackgroundScreen {

	private ModsScreenComponent component;

	public ModsScreen() {
		this(null);
	}

	public ModsScreen(Mod mod) {
		super(new ModsScreenComponent(mod));

		component = (ModsScreenComponent) root;
		background = false;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		if(mc.theWorld == null) {
			if(SolClientMod.instance.fancyMainMenu) {
				background = false;
				drawPanorama(mouseX, mouseY, partialTicks);
			}
			else {
				background = true;
			}
		}
		else {
			drawDefaultBackground();
		}

		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	public void switchMod(Mod mod) {
		component.switchMod(mod);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		Client.INSTANCE.save();
	}

	@Override
	public void closeAll() {
		if(mc.theWorld == null && SolClientMod.instance.fancyMainMenu) {
			mc.displayGuiScreen(Client.INSTANCE.getMainMenu());
			return;
		}

		super.closeAll();
	}

	public static class ModsScreenComponent extends Component {

		@Getter
		private Mod mod;
		private TextFieldComponent search;
		private ModsScroll scroll;
		private int noModsScroll;
		private boolean singleModMode;

		public ModsScreenComponent(Mod startingMod) {
			if(startingMod != null) {
				singleModMode = true;
			}

			add(new LabelComponent((component, defaultText) -> mod != null ? mod.getName() : I18n.format("sol_client.mod.screen.title")),
					new AlignedBoundsController(Alignment.CENTRE, Alignment.START,
							(component, defaultBounds) -> new Rectangle(defaultBounds.getX(), 10, defaultBounds.getWidth(),
									defaultBounds.getHeight())));

			add(ButtonComponent.done(() -> {
				if(mod == null || singleModMode) {
					if(!search.getText().isEmpty()) {
						search.setText("");
						search.setFocused(false);
						scroll.load();
					}
					else {
						getScreen().close();
						if(getScreen().getParentScreen() instanceof Screen) {
							((Screen) getScreen().getParentScreen()).getRoot().setFont(font);
						}
					}
				}
				else {
					switchMod(null);
				}
			}), new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
					(component, defaultBounds) -> new Rectangle(defaultBounds.getX() - (singleModMode ? 0 : 51),
							getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));

			if(!singleModMode) {
				add(new ButtonComponent("sol_client.hud.edit",
						new AnimatedColourController(
								(component, defaultColour) -> component.isHovered() ? new Colour(255, 165, 65)
										: new Colour(255, 120, 20))).onClick((info, button) -> {
											if(button == 0) {
												Utils.playClickSound(true);
												mc.displayGuiScreen(new MoveHudsScreen());
												return true;
											}

											return false;
										}).withIcon("sol_client_hud"),
						new AlignedBoundsController(Alignment.CENTRE, Alignment.END,
								(component, defaultBounds) -> new Rectangle(defaultBounds.getX() + 51,
								getBounds().getHeight() - defaultBounds.getHeight() - 10, 100, 20)));
			}

			add(scroll = new ModsScroll(this), (component, defaultBounds) -> new Rectangle(0, 25, getBounds().getWidth(), getBounds().getHeight() - 62));

			search = new TextFieldComponent(100, false).autoFlush().onUpdate((value) -> {
				scroll.snapTo(0);
				scroll.load();
				return true;
			}).placeholder("sol_client.mod.screen.search").withIcon("sol_client_search");

			switchMod(startingMod, true);
		}

		public void singleModMode() {
			this.singleModMode = true;
		}

		public void switchMod(Mod mod) {
			switchMod(mod, false);
		}

		public void switchMod(Mod mod, boolean first) {
			this.mod = mod;
			scroll.load();

			if(mod == null) {
				scroll.snapTo(noModsScroll);
				add(search, (component, defaultBounds) -> new Rectangle(6, 6, defaultBounds.getWidth(),
						defaultBounds.getHeight()));
			}
			else {
				noModsScroll = scroll.getScroll();
				scroll.snapTo(0);
				if(!first) {
					remove(search);
				}
			}
		}

		@Override
		public boolean keyPressed(ComponentRenderInfo info, int keyCode, char character) {
			if((screen.getRoot().getDialog() == null && (keyCode == Keyboard.KEY_RETURN || keyCode == Keyboard.KEY_NUMPADENTER)) && !scroll.getSubComponents().isEmpty()) {
				Component firstComponent = scroll.getSubComponents().get(0);

				return firstComponent.mouseClickedAnywhere(info, firstComponent instanceof ModListing ? 1 : 0, true, false);
			}
			else if(mod == null && keyCode == Keyboard.KEY_F && isCtrlKeyDown() && !isShiftKeyDown() && !isAltKeyDown()) {
				search.setFocused(true);
				return true;
			}
			else if (mod != null && (keyCode == Keyboard.KEY_BACK
					|| (keyCode == Keyboard.KEY_LEFT && isAltKeyDown() && !isCtrlKeyDown() && !isShiftKeyDown()))
					&& screen.getRoot().getDialog() == null) {
				switchMod(null);
				return true;
			}

			if(character > 31 && !search.isFocused() && mod == null) {
				search.setFocused(true);
				search.setText("");
			}

			boolean result = super.keyPressed(info, keyCode, character);

			if(!result && keyCode == SolClientMod.instance.modsKey.getKeyCode()) {
				mc.displayGuiScreen(null);
				return true;
			}

			return result;
		}

		public String getQuery() {
			return search.getText();
		}

	}

}
