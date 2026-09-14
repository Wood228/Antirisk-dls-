package wood228.antirisk.hitbox.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wood228.antirisk.hitbox.AntiriskHitboxClient;

@Mixin(GameRenderer.class)
public abstract class GameRendererMixin {
    @Inject(method = "findCrosshairTarget", at = @At("HEAD"), cancellable = true)
    private void antirisk$useExpandedHitbox(
            Entity camera,
            double blockInteractionRange,
            double entityInteractionRange,
            float tickDelta,
            CallbackInfoReturnable<HitResult> cir
    ) {
        if (!AntiriskHitboxClient.enabled || AntiriskHitboxClient.scale <= 1.0D) {
            return;
        }

        Vec3d cameraPos = camera.getCameraPosVec(tickDelta);
        HitResult blockHit = camera.raycast(blockInteractionRange, tickDelta, false);

        double maxDistance = entityInteractionRange;
        double blockDistanceSq = maxDistance * maxDistance;
        if (blockHit.getType() != HitResult.Type.MISS) {
            blockDistanceSq = cameraPos.squaredDistanceTo(blockHit.getPos());
            maxDistance = Math.sqrt(blockDistanceSq);
        }

        Vec3d rotation = camera.getRotationVec(tickDelta);
        Vec3d end = cameraPos.add(rotation.multiply(maxDistance));
        Box searchBox = camera.getBoundingBox()
                .stretch(rotation.multiply(maxDistance))
                .expand(1.0D);

        EntityHitResult entityHit = ProjectileUtil.raycast(
                camera,
                cameraPos,
                end,
                searchBox,
                entity -> entity != camera && !entity.isSpectator() && entity.canHit(),
                maxDistance
        );

        if (entityHit != null && entityHit.getPos().squaredDistanceTo(cameraPos) < blockDistanceSq) {
            cir.setReturnValue(entityHit);
        }
    }
}
