package me.marni.marnisutils;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.gui.options.components.BooleanOptionComponent;
import net.minecraft.client.gui.options.components.FloatOptionComponent;
import net.minecraft.client.gui.options.components.OptionsCategory;
import net.minecraft.client.gui.options.data.OptionsPage;
import net.minecraft.client.gui.options.data.OptionsPages;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionFloat;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.item.ItemStack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import turniplabs.halplibe.util.ClientStartEntrypoint;

public class main implements ModInitializer, ClientStartEntrypoint {
	public static final String MOD_ID = "marnisutils";
	public static final String MOD_VERSION = "1.0.0";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static OptionsPage optionsPage;
	public static OptionBoolean tweakDisableBlockBreakCooldown;
	public static OptionBoolean tweakAutoClimbLadders;
	public static OptionBoolean tweakMojankLoadingIcon;
	public static OptionBoolean tweakDisableSeasonalGrowth;
	public static OptionFloat tweakLadderSpeed;
	public static OptionBoolean utilityAutoTool;
	public static OptionBoolean utilityRestockHand;

	@Override
	public void onInitialize() {
		LOGGER.info("Marnis Utils v{} initialized.", MOD_VERSION);
	}

	public static void optionsInit(GameSettings settings){
		// tweaks - disabled by default
		tweakDisableBlockBreakCooldown = new OptionBoolean(settings, MOD_ID + ".tweaks" + ".disableBlockBreakCooldown", false);
		tweakAutoClimbLadders = new OptionBoolean(settings, MOD_ID + ".tweaks" + ".autoClimbLadders", false);
		tweakDisableSeasonalGrowth = new OptionBoolean(settings, MOD_ID + ".tweaks" + ".disableSeasonalGrowth", false);
		tweakLadderSpeed = new OptionFloat(settings, MOD_ID + ".tweaks" + ".ladderSpeed", 0.15F);

		// utility - disabled by default
		utilityAutoTool = new OptionBoolean(settings, MOD_ID + ".utility" + ".autoTool", false);
		utilityRestockHand = new OptionBoolean(settings, MOD_ID + ".utility" + ".restockHand", false);

		// enabled by default cuz fooni
		tweakMojankLoadingIcon = new OptionBoolean(settings, MOD_ID + ".tweaks" + ".mojankLoadingIcon", true);
	}

	@Override
	public void afterClientStart() {
		optionsPage = new OptionsPage(MOD_ID + ".title", new ItemStack(Blocks.BLOCK_DIAMOND));
		OptionsPages.register(optionsPage);

		// tweaks
		optionsPage.withComponent(
			new OptionsCategory(MOD_ID + ".category" + ".tweaks")
				.withComponent(new BooleanOptionComponent(tweakDisableBlockBreakCooldown))
				.withComponent(new BooleanOptionComponent(tweakAutoClimbLadders))
				.withComponent(new BooleanOptionComponent(tweakMojankLoadingIcon))
				.withComponent(new BooleanOptionComponent(tweakDisableSeasonalGrowth))
				.withComponent(new FloatOptionComponent(tweakLadderSpeed))
		);

		// utility
		optionsPage.withComponent(
			new OptionsCategory(MOD_ID + ".category" + ".utility")
				.withComponent(new BooleanOptionComponent(utilityAutoTool))
				.withComponent(new BooleanOptionComponent(utilityRestockHand))
		);
	}

	@Override
	public void beforeClientStart() {}
}
