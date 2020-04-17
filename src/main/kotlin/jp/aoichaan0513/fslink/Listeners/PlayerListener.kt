package jp.aoichaan0513.fslink.Listeners

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Main
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

class PlayerListener : Listener {

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        val p = e.player

        val user = Main.luckPerms.userManager.getUser(p.uniqueId)!!

        if (user.primaryGroup.isNotEmpty()) {
            if (Main.luckPerms.groupManager.getGroup(user.primaryGroup)?.displayName.equals("default", true)) {
                p.sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.WARNING)}\"/auth\"と送信して認証を行ってください。")
            }
        }
    }
}