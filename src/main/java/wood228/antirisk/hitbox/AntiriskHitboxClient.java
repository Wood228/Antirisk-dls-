package wood228.antirisk.hitbox;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public final class AntiriskHitboxClient implements ClientModInitializer {
    public static boolean enabled = true;
    public static double scale = 1.5D;

    private static KeyBinding toggleKey;
    private static KeyBinding increaseKey;
    private static KeyBinding decreaseKey;

    @Override
    public void onInitializeClient() {
        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antirisk_hitbox.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "category.antirisk_hitbox"
        ));

        increaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antirisk_hitbox.increase",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_ADD,
                "category.antirisk_hitbox"
        ));

        decreaseKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.antirisk_hitbox.decrease",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_KP_SUBTRACT,
                "category.antirisk_hitbox"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleKey.wasPressed()) {
                enabled = !enabled;
                if (client.player != null) {
                    client.player.sendMessage(Text.literal("Hitbox: " + (enabled ? "ON" : "OFF")), true);
                }
            }

            while (increaseKey.wasPressed()) {
                scale = Math.min(5.0D, scale + 0.1D);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(String.format("Hitbox size: %.1fx", scale)), true);
                }
            }

            while (decreaseKey.wasPressed()) {
                scale = Math.max(1.0D, scale - 0.1D);
                if (client.player != null) {
                    client.player.sendMessage(Text.literal(String.format("Hitbox size: %.1fx", scale)), true);
                }
            }
        });
    }
}
