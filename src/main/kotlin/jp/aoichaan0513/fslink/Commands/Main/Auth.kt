package jp.aoichaan0513.fslink.Commands.Main

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Commands.ICommand
import jp.aoichaan0513.fslink.Listeners.BotListener
import jp.aoichaan0513.fslink.Main
import org.apache.commons.lang3.RandomStringUtils
import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Auth(name: String) : ICommand(name) {

    override fun onPlayerCommand(sp: Player, cmd: Command, label: String, args: Array<String>) {
        val user = Main.luckPerms.userManager.getUser(sp.uniqueId)!!

        if (user.primaryGroup.isNotEmpty()) {
            if (Main.luckPerms.groupManager.getGroup(user.primaryGroup)?.displayName.equals("default", true)) {
                val code = RandomStringUtils.randomAlphanumeric(6)
                BotListener.hashMap[code] = sp.uniqueId
                sp.sendMessage("""
                    ${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}下記のコードを${Main.botInstance.selfUser.asTag}のDMにて送信してください。
                    ${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コード: ${ChatColor.GOLD}${ChatColor.BOLD}${code}
                    ${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}招待リンク: ${ChatColor.GOLD}${ChatColor.BOLD}${Main.pluginInstance.config.getString("inviteLink", "https://discord.gg/WudKwEj")}
                """.trimIndent())
            }
        }
    }

    override fun onBlockCommand(bs: BlockCommandSender, cmd: Command, label: String, args: Array<String>) {}
    override fun onConsoleCommand(cs: ConsoleCommandSender, cmd: Command, label: String, args: Array<String>) {}

    override fun onPlayerTabComplete(sp: Player, cmd: Command, alias: String, args: Array<String>): List<String> {
        return emptyList()
    }

    override fun onBlockTabComplete(bs: BlockCommandSender, cmd: Command, alias: String, args: Array<String>): List<String> {
        return emptyList()
    }

    override fun onConsoleTabComplete(cs: ConsoleCommandSender, cmd: Command, alias: String, args: Array<String>): List<String> {
        return emptyList()
    }
}