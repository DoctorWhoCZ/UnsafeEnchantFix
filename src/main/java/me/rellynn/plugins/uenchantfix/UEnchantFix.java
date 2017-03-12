package me.rellynn.plugins.uenchantfix;

import com.comphenix.protocol.ProtocolLibrary;
import net.md_5.bungee.api.chat.TranslatableComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by gwennaelguich on 12/03/2017.
 */
public class UEnchantFix extends JavaPlugin {
    private final Map<Enchantment, TranslatableComponent> TRANSLATIONS = new HashMap<>();

    @Override
    public void onEnable() {
        Reflection.MethodInvoker a = Reflection.getMethod("{nms}.Enchantment", "a");
        Reflection.MethodInvoker getRaw = Reflection.getMethod("{obc}.enchantments.CraftEnchantment", "getRaw", Enchantment.class);
        for (Enchantment enchant : Enchantment.values()) {
            String localeKey = a.invoke(getRaw.invoke(null, enchant)).toString();
            TRANSLATIONS.put(enchant, new TranslatableComponent(localeKey));
        }
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketListener(this));
    }

    public TranslatableComponent getTranslation(Enchantment enchant) {
        return TRANSLATIONS.get(enchant);
    }
}
