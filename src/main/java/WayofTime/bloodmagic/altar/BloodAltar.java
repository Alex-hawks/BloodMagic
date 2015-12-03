package WayofTime.bloodmagic.altar;

import WayofTime.bloodmagic.api.BlockStack;
import WayofTime.bloodmagic.api.altar.*;
import WayofTime.bloodmagic.block.BlockBloodRune;
import WayofTime.bloodmagic.util.Utils;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BloodAltar {

    static {
        EnumAltarTier.ONE.buildComponents();
        EnumAltarTier.TWO.buildComponents();
        EnumAltarTier.THREE.buildComponents();
        EnumAltarTier.FOUR.buildComponents();
        EnumAltarTier.FIVE.buildComponents();
        EnumAltarTier.SIX.buildComponents();
    }

    public static EnumAltarTier getAltarTier(World world, BlockPos pos) {
        for (int i = EnumAltarTier.MAXTIERS - 1; i >= 1; i--) {
            if (checkAltarIsValid(world, pos, i)) {
                return EnumAltarTier.values()[i];
            }
        }

        return EnumAltarTier.ONE;
    }

    public static boolean checkAltarIsValid(World world, BlockPos worldPos, int altarTier) {

        for (AltarComponent altarComponent : EnumAltarTier.values()[altarTier].getAltarComponents()) {

            BlockPos componentPos = worldPos.add(altarComponent.getOffset());
            BlockStack worldBlock = new BlockStack(world.getBlockState(componentPos).getBlock(), world.getBlockState(componentPos).getBlock().getMetaFromState(world.getBlockState(componentPos)));

            if (altarComponent.getComponent() != EnumAltarComponent.NOTAIR) {
                if (worldBlock.getBlock() instanceof IAltarComponent) {
                    EnumAltarComponent component = ((IAltarComponent) worldBlock.getBlock()).getType(worldBlock.getMeta());
                    if (component != altarComponent.getComponent())
                        return false;
                } else if (worldBlock.getBlock() != Utils.getBlockForComponent(altarComponent.getComponent())) {
                    return false;
                }
            } else {
                if (world.isAirBlock(componentPos))
                    return false;
            }
        }

        return true;
    }

    public static AltarUpgrade getUpgrades(World world, BlockPos pos, EnumAltarTier altarTier) {
        if (world.isRemote) {
            return null;
        }

        AltarUpgrade upgrades = new AltarUpgrade();
        List<AltarComponent> list = altarTier.getAltarComponents();

        for (AltarComponent altarComponent : list) {
            BlockPos componentPos = pos.add(altarComponent.getOffset());

            if (altarComponent.isUpgradeSlot()) {
                BlockStack worldBlock = new BlockStack(world.getBlockState(componentPos).getBlock(), world.getBlockState(componentPos).getBlock().getMetaFromState(world.getBlockState(componentPos)));

                if (worldBlock.getBlock() instanceof BlockBloodRune) {
                    switch (((BlockBloodRune) worldBlock.getBlock()).getRuneEffect(worldBlock.getMeta())) {
                        case 1:
                            upgrades.addSpeed();
                            break;

                        case 2:
                            upgrades.addEfficiency();
                            break;

                        case 3:
                            upgrades.addSacrifice();
                            break;

                        case 4:
                            upgrades.addSelfSacrifice();
                            break;

                        case 5:
                            upgrades.addDisplacement();
                            break;

                        case 6:
                            upgrades.addCapacity();
                            break;

                        case 7:
                            upgrades.addBetterCapacity();
                            break;

                        case 8:
                            upgrades.addOrbCapacity();
                            break;

                        case 9:
                            upgrades.addAcceleration();
                            break;
                    }
                }
            }
        }

        return upgrades;
    }
}