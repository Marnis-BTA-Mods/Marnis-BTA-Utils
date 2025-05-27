package me.marni.marnisutils.mixins;

import me.marni.marnisutils.main;
import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogicGrass;
import net.minecraft.core.block.Blocks;
import net.minecraft.core.data.gamerule.GameRules;
import net.minecraft.core.world.World;
import net.minecraft.core.world.biome.Biome;
import net.minecraft.core.world.biome.Biomes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

/**
 * handles the DisableSeasonalGrowth tweak
 */
@Mixin(BlockLogicGrass.class)
public abstract class BlockGrassMixin {
	@Shadow(remap = false)
	@Final
	public Block<?> dirt;

	@Inject(
		method = "updateTick",
		at = @At("HEAD"),
		cancellable = true,
		remap = false
	)
	private void handleSeasonalGrowth(World world, int x, int y, int z, Random rand, CallbackInfo ci) {
		// This a direct ctrl c ctrl v from BTA with an additional check added lol
		// I know this can be done better, but I'm lazy
		if (!world.isClientSide) {
			if (world.getBlockLightValue(x, y + 1, z) < 4 && Blocks.lightBlock[world.getBlockId(x, y + 1, z)] > 2) {
				if (rand.nextInt(4) != 0) {
					return;
				}

				world.setBlockWithNotify(x, y, z, this.dirt.id());
			} else if (world.getBlockLightValue(x, y + 1, z) >= 9) {
				int idToSpawn;
				int r;
				for(idToSpawn = 0; idToSpawn < 4; ++idToSpawn) {
					r = x + rand.nextInt(3) - 1;
					int y1 = y + rand.nextInt(5) - 3;
					int z1 = z + rand.nextInt(3) - 1;
					if (world.getBlockId(r, y1, z1) == this.dirt.id() && world.getBlockLightValue(r, y1 + 1, z1) >= 4 && Blocks.lightBlock[world.getBlockId(r, y1 + 1, z1)] <= 2) {
						world.setBlockWithNotify(r, y1, z1, this.dirt.id());
					}
				}

				if ((Boolean)world.getGameRuleValue(GameRules.DO_SEASONAL_GROWTH) && world.getBlockId(x, y + 1, z) == 0 && world.getSeasonManager().getCurrentSeason() != null && world.getSeasonManager().getCurrentSeason().growFlowers && rand.nextInt(256) == 0  && !main.tweakDisableSeasonalGrowth.value) {
					idToSpawn = 0;
					r = rand.nextInt(400);
					if (r < 26) {
						idToSpawn = Blocks.FLOWER_RED.id();
					} else if (r < 41) {
						idToSpawn = Blocks.FLOWER_YELLOW.id();
					} else if (r < 60) {
						Biome biome = world.getBlockBiome(x, y + 1, z);
						if (biome != Biomes.OVERWORLD_BIRCH_FOREST && biome != Biomes.OVERWORLD_SEASONAL_FOREST) {
							if (biome != Biomes.OVERWORLD_MEADOW && biome != Biomes.OVERWORLD_BOREAL_FOREST && biome != Biomes.OVERWORLD_SHRUBLAND) {
								if (biome == Biomes.OVERWORLD_FOREST || biome == Biomes.OVERWORLD_SWAMPLAND || biome == Biomes.OVERWORLD_RAINFOREST || biome == Biomes.OVERWORLD_CAATINGA) {
									idToSpawn = Blocks.FLOWER_LIGHT_BLUE.id();
								}
							} else {
								idToSpawn = Blocks.FLOWER_PURPLE.id();
							}
						} else {
							idToSpawn = Blocks.FLOWER_PINK.id();
						}
					} else if (rand.nextInt(8) == 0) {
						idToSpawn = Blocks.TALLGRASS_FERN.id();
					} else {
						idToSpawn = Blocks.TALLGRASS.id();
					}

					world.setBlockWithNotify(x, y + 1, z, idToSpawn);
				}
			}

		}

		// cancel original code from running
		ci.cancel();
	}
}
