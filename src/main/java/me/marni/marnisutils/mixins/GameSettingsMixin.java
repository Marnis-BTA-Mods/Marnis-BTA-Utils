package me.marni.marnisutils.mixins;

import me.marni.marnisutils.main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.option.GameSettings;
import net.minecraft.client.option.OptionBoolean;
import net.minecraft.client.option.OptionFloat;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(value = GameSettings.class, remap = false)
public abstract class GameSettingsMixin {
	@Shadow
	@Final
	public Minecraft mc;

	@Unique
	public OptionBoolean tweakDisableBlockBreakCooldown;
	@Unique
	public OptionBoolean tweakAutoClimbLadders;
	@Unique
	public OptionBoolean tweakMojankLoadingIcon;
	@Unique
	public OptionBoolean tweakDisableSeasonalGrowth;
	@Unique
	public OptionFloat tweakLadderSpeed;

	@Unique
	public OptionBoolean utilityAutoTool;
	@Unique
	public OptionBoolean utilityRestockHand;

	@Inject(method = "<init>", at = @At(value = "NEW", target = "(Ljava/io/File;Ljava/lang/String;)Ljava/io/File;"))
	public void addOptions(Minecraft minecraft, File file, CallbackInfo ci){
		main.optionsInit((GameSettings) (Object)this);

		tweakDisableBlockBreakCooldown = main.tweakDisableBlockBreakCooldown;
		tweakAutoClimbLadders = main.tweakAutoClimbLadders;
		tweakMojankLoadingIcon = main.tweakMojankLoadingIcon;
		tweakDisableSeasonalGrowth = main.tweakDisableSeasonalGrowth;
		tweakLadderSpeed = main.tweakLadderSpeed;
		utilityAutoTool = main.utilityAutoTool;
		utilityRestockHand = main.utilityRestockHand;
	}
}
