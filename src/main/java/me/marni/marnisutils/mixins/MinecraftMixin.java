package me.marni.marnisutils.mixins;

import me.marni.marnisutils.main;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.PlayerLocal;
import net.minecraft.client.render.TextureManager;
import net.minecraft.core.InventoryAction;
import net.minecraft.core.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
	@Shadow
	public TextureManager textureManager;
	@Unique
	private static boolean isRestocking = false;

	@Unique
	private void tryMoveStack(Minecraft mc, int sourceSlot, int targetSlot, int neededAmount) {
		// pick up the stack
		int[] pickupArgs = new int[] { sourceSlot, 0 }; // slot, mouseButton
		mc.playerController.handleInventoryMouseClick(0, InventoryAction.CLICK_LEFT, pickupArgs, mc.thePlayer);

		ItemStack cursorStack = mc.thePlayer.inventory.getHeldItemStack();
		if (cursorStack == null) return;

		// split if needed
		if (cursorStack.stackSize > neededAmount) {
			int[] splitArgs = new int[] { sourceSlot, 1 }; // right click to split
			mc.playerController.handleInventoryMouseClick(0, InventoryAction.CLICK_RIGHT, splitArgs, mc.thePlayer);
		}

		// place in target
		int[] placeArgs = new int[] { targetSlot, 0 };
		mc.playerController.handleInventoryMouseClick(0, InventoryAction.CLICK_LEFT, placeArgs, mc.thePlayer);

		// return leftovers
		cursorStack = mc.thePlayer.inventory.getHeldItemStack();
		if (cursorStack != null) {
			int[] returnArgs = new int[] { sourceSlot, 0 };
			mc.playerController.handleInventoryMouseClick(0, InventoryAction.CLICK_LEFT, returnArgs, mc.thePlayer);
		}
	}

	@ModifyConstant(method = "loadScreen", constant = @Constant(intValue = 0))
	private int modifyMojrand(int original) {
		if(!main.tweakMojankLoadingIcon.value) return original;

		this.textureManager.loadTexture("/marnisutils/textures/title/mojank.png").bind();
		return original;
	}

	@Inject(
		method = "runTick",
		at = @At("HEAD")
	)
	public void runTick(CallbackInfo ci) {
		Minecraft mc = Minecraft.getMinecraft();
		if (mc == null || mc.thePlayer == null)
			return;

		PlayerLocal player = mc.thePlayer;

		/*
		 * Auto ladder tweak
		 * Keeps climbing if the player is looking up
		 */
		if (main.tweakAutoClimbLadders.value && player.canClimb() && player.xRot <= -50f) {
			player.yd = main.tweakLadderSpeed.value;
		}

		/*
		 * Ladder speed tweak
		 * Changes the speed you can climb ladders at
		 */
		if (player.canClimb() && mc.gameSettings.keyForward.isPressed()) {
			player.yd = main.tweakLadderSpeed.value;
		}

		/*
		 * Hand restock util
		 * If the item ur holding goes below 32 it tries to "restock" it from ur inventory
		 */
		if (!main.utilityRestockHand.value || isRestocking) return;

		ItemStack heldStack = mc.thePlayer.inventory.getCurrentItem();
		if (heldStack == null) return;

		// check if we need to restock (threshold of 32)
		if (heldStack.stackSize < 32) {
			isRestocking = true;
			int currentSlot = mc.thePlayer.inventory.getCurrentItemIndex();
			int neededAmount = 32 - heldStack.stackSize;

			// check hotbar first
			for (int i = 0; i < 9; i++) {
				if (i == currentSlot) continue;

				ItemStack hotbarStack = mc.thePlayer.inventory.mainInventory[i];
				if (hotbarStack != null && hotbarStack.itemID == heldStack.itemID) {
					int amountToMove = Math.min(neededAmount, hotbarStack.stackSize);
					tryMoveStack(mc, 36 + i, 36 + currentSlot, amountToMove);

					neededAmount -= amountToMove;
					if (neededAmount <= 0) {
						isRestocking = false;
						return;
					}
				}
			}

			// check main inventory
			for (int i = 9; i < 36; i++) {
				ItemStack invStack = mc.thePlayer.inventory.mainInventory[i];
				if (invStack != null && invStack.itemID == heldStack.itemID) {
					int amountToMove = Math.min(neededAmount, invStack.stackSize);
					tryMoveStack(mc, i, 36 + currentSlot, amountToMove);

					neededAmount -= amountToMove;
					if (neededAmount <= 0) {
						isRestocking = false;
						return;
					}
				}
			}

			isRestocking = false;
		}
	}
}
