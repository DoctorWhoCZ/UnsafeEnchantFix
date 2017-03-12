package me.rellynn.plugins.uenchantfix;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by gwennaelguich on 12/03/2017.
 */
public class PacketListener extends PacketAdapter {
    private UEnchantFix plugin;

    PacketListener(UEnchantFix plugin) {
        super(plugin, PacketType.Play.Server.SET_SLOT, PacketType.Play.Server.WINDOW_ITEMS);
        this.plugin = plugin;
    }

    private void fixItemStack(ItemStack item) {
        if (item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            List<String> lore = new ArrayList<>();
            for (Map.Entry<Enchantment, Integer> entry : meta.getEnchants().entrySet()) {
                TextComponent component = new TextComponent(plugin.getTranslation(entry.getKey()), new TextComponent(" " + entry.getValue()));
                component.setColor(ChatColor.GRAY);
                lore.add(component.toLegacyText());
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    @Override
    public void onPacketSending(PacketEvent evt) {
        PacketContainer packet = evt.getPacket().deepClone();
        if (packet.getType() == PacketType.Play.Server.SET_SLOT) {
            StructureModifier<ItemStack> modifier = packet.getItemModifier();
            for (int i = 0; i < modifier.size(); i++)
                fixItemStack(modifier.getValues().get(i));
        } else {
            StructureModifier<ItemStack[]> modifier = packet.getItemArrayModifier();
            for (int i = 0; i < modifier.size(); i++) {
                for (int j = 0; j < modifier.getValues().size(); j++) {
                    if (modifier.getValues().get(i)[j] != null)
                        fixItemStack(modifier.getValues().get(i)[j]);
                }
            }
        }
        evt.setPacket(packet);
    }
}
