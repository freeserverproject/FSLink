package jp.aoichaan0513.fslink.Listeners

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Main
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import java.util.*

class BotListener : ListenerAdapter() {

    override fun onPrivateMessageReceived(e: PrivateMessageReceivedEvent) {
        val channel = e.channel
        val user = e.author
        val msg = e.message

        if (msg.contentRaw.equals("再認証")) {

        } else {
            if (hashMap.containsKey(msg.contentRaw)) {
                val uuid = hashMap[msg.contentRaw]!!
                val p = Bukkit.getOfflinePlayer(uuid)
                val lpUser = Main.luckPerms.userManager.getUser(uuid)!!
                lpUser.setPrimaryGroup(Main.pluginInstance.config.getString("authedGroup", "sabamin")!!)
                Main.pluginInstance.config.set("authedUsers.${uuid}", user.idLong)
                Main.pluginInstance.saveConfig()

                for (cmd in Main.pluginInstance.config.getStringList("runCommands"))
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("{replace:player_name}", p.name!!).replace("{replace:player_uuid}", uuid.toString()))

                channel.sendMessage("あなたのDiscord アカウントを${p.name}と連携しました。").queue()
                if (p.isOnline)
                    p.player!!.sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SUCCESS)}あなたのMinecraft アカウントを${ChatColor.BOLD}${user.asTag}${ChatColor.RESET}${ChatColor.GREEN}と連携しました。")
            } else {
                channel.sendMessage("そのコードは見つかりませんでした。").queue()
            }
        }
    }

    companion object {
        val hashMap = mutableMapOf<String, UUID>()
    }
}