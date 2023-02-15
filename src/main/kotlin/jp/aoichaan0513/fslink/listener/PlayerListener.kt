package jp.aoichaan0513.fslink.listener

import jp.aoichaan0513.fslink.api.MainAPI
import jp.aoichaan0513.fslink.Main
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.ChatColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        val user = Main.luckPerms.userManager.getUser(p.uniqueId)!!

        // 参加した際に連携されていなければメッセージを送信
        if (user.primaryGroup.equals("default", true)) {
            val textComponent1 = TextComponent("${MainAPI.getPrefix(MainAPI.PrefixType.WARNING)}${ChatColor.YELLOW}\"")
            val textComponent2 = TextComponent("${ChatColor.GOLD}${ChatColor.BOLD}/auth")
            textComponent2.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, ComponentBuilder("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}クリックでコマンドを実行").create())
            textComponent2.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth")
            val textComponent3 = TextComponent("${ChatColor.RESET}${ChatColor.YELLOW}\"と実行して連携を行ってください。")
            textComponent1.addExtra(textComponent2)
            textComponent1.addExtra(textComponent3)

            p.spigot().sendMessage(ChatMessageType.CHAT, textComponent1)
        }
    }

    @EventHandler
    fun onQuit(e: PlayerQuitEvent) {
        val p = e.player

        // 退出時に認証をキャンセル
        if (BotListener.hashMap.containsValue(p.uniqueId)) {
            for ((key, value) in BotListener.hashMap) {
                if (value == p.uniqueId) {
                    BotListener.hashMap.remove(key)
                    break
                }
            }
        }
    }
}