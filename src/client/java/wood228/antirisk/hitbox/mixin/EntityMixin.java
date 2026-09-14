package wood228.antirisk.hitbox.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Box;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wood228.antirisk.hitbox.AntiriskHitboxClient;

@Mixin(Entity.class)
public abstract class EntityMixin {
    @Inject(method = "getBoundingBox", at = @At("RETURN"), cancellable = true)
    private void antirisk$expandHitbox(CallbackInfoReturnable<Box> cir) {
        if (!AntiriskHitboxClient.enabled) return;

        Entity entity = (Entity) (Object) this;
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || entity == client.player || !(entity instanceof LivingEntity)) return;

        double scale = AntiriskHitboxClient.scale;
        if (scale <= 1.0D) return;

        Box box = cir.getReturnValue();
        double centerX = (box.minX + box.maxX) * 0.5D;
        double centerY = (box.minY + box.maxY) * 0.5D;
        double centerZ = (box.minZ + box.maxZ) * 0.5D;
        double halfX = (box.maxX - box.minX) * 0.5D * scale;
        double halfY = (box.maxY - box.minY) * 0.5D * scale;
        double halfZ = (box.maxZ - box.minZ) * 0.5D * scale;

        cir.setReturnValue(new Box(
                centerX - halfX, centerY - halfY, centerZ - halfZ,
                centerX + halfX, centerY + halfY, centerZ + halfZ
        ));
    }
}
