package com.lowdragmc.lowdraglib.core.mixins.accessor;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;


/**
 * @author KilaBash
 * @date 2023/2/11
 * @implNote EntityAccessor
 */
@Mixin(Entity.class)
public interface EntityAccessor {
    @Accessor("level")
    void invokeSetLevel(Level level);

}
