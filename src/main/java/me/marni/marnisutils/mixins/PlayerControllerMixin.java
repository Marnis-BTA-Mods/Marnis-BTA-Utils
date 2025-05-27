package me.marni.marnisutils.mixins;

import me.marni.marnisutils.main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.player.controller.PlayerController;
import net.minecraft.core.block.Block;
import net.minecraft.core.item.ItemStack;
import net.minecraft.core.item.tool.ItemTool;
import net.minecraft.core.player.gamemode.Gamemode;
import net.minecraft.core.util.helper.Side;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = PlayerController.class, remap = false)
public class PlayerControllerMixin {
	@Unique
	private final Minecraft client = Minecraft.getMinecraft();

	@Shadow
	protected int blockHitDelay;

	/**
	 * Removes block breaking cooldown if tweak is enabled
	 */
	@Inject(method = "continueDestroyBlock", at = @At("HEAD"))
	private void BlockHitDelay(CallbackInfo callbackInfo) {
		if (main.tweakDisableBlockBreakCooldown.value) {
			blockHitDelay = 0;
		}
	}

	/**
	 * Switches to best tool when starting to break a block
	 * If auto tool utility is enabled
	 */
	@Inject(method = "startDestroyBlock", at = @At("HEAD"))
	private void onStartBreakBlock(int x, int y, int z, Side side, double xHit, double yHit, boolean repeat, CallbackInfo ci) {
		if (!main.utilityAutoTool.value) return;

		PlayerLocal player = client.thePlayer;
		if (player == null || player.getGamemode() != Gamemode.survival) return;

		Block<?> block = client.currentWorld.getBlock(x, y, z);
		ItemStack bestTool = findBestToolForBlock(player, block);

		if (bestTool != null) {
			ItemStack currentItem = player.inventory.getCurrentItem();
			if (currentItem == null || !currentItem.equals(bestTool)) {
				player.inventory.setCurrentItem(bestTool, true);
			}
		}
	}

	/**
	 * Finds the most efficient tool for breaking a block
	 */
	@Unique
	private ItemStack findBestToolForBlock(PlayerLocal player, Block<?> block) {
		float bestSpeed = 1.0F;
		ItemStack bestTool = null;

		for (int i = 0; i < player.inventory.getContainerSize(); i++) {
			ItemStack itemStack = player.inventory.getItem(i);

			if (itemStack != null && itemStack.getItem() instanceof ItemTool) {
				float speed = itemStack.getItem().getStrVsBlock(itemStack, block);

				if (speed > bestSpeed) {
					bestSpeed = speed;
					bestTool = itemStack;
				}
			}
		}

		return bestTool;
	}
}
