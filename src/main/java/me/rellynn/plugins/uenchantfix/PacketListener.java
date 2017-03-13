package me.rellynn.plugins.uenchantfix;

import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.comphenix.protocol.PacketType.Play.Server.SET_SLOT;
import static com.comphenix.protocol.PacketType.Play.Server.WINDOW_ITEMS;

/**
 * Created by gwennaelguich on 12/03/2017.
 */
public class PacketListener extends PacketAdapter {
    private UEnchantFix plugin;

    PacketListener(UEnchantFix plugin) {
        super(plugin, SET_SLOT, WINDOW_ITEMS);
        this.plugin = plugin;
    }

    private void fixItemStack(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
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
        PacketContainer packet = evt.getPacket();
        if (packet.getType() == SET_SLOT) {
            fixItemStack(packet.getItemModifier().read(0));
        } else if (packet.getType() == WINDOW_ITEMS) {
            ItemStack[] elements = packet.getItemArrayModifier().read(0);
            for (ItemStack item : elements)
                fixItemStack(item);
        }
        evt.setPacket(packet);
    }
}
