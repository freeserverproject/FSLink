package jp.aoichaan0513.fslink.Listeners

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Main
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player
        val user = Main.luckPerms.userManager.getUser(p.uniqueId)!!

        // 参加した際に認証されていなければメッセージを送信
        if (user.primaryGroup.isNotEmpty()) {
            if (Main.luckPerms.groupManager.getGroup(user.primaryGroup)?.displayName.equals("default", true)) {
                p.spigot().sendMessage()
                val textComponent1 = TextComponent("${MainAPI.getPrefix(MainAPI.PrefixType.WARNING)}\"")
                val textComponent2 = TextComponent("/auth")
                textComponent2.clickEvent = ClickEvent(ClickEvent.Action.RUN_COMMAND, "/auth")
                val textComponent3 = TextComponent("\"と送信して認証を行ってください。")
                textComponent1.addExtra(textComponent2)
                textComponent1.addExtra(textComponent3)

                p.spigot().sendMessage(ChatMessageType.CHAT, textComponent1)
            }
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