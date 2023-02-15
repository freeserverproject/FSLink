package jp.aoichaan0513.fslink.commands.main

import jp.aoichaan0513.fslink.api.MainAPI
import jp.aoichaan0513.fslink.api.MainAPI.Companion.getPostgrestClient
import jp.aoichaan0513.fslink.commands.ICommand
import jp.aoichaan0513.fslink.listener.BotListener
import jp.aoichaan0513.fslink.Main
import jp.aoichaan0513.fslink.consts.FreeserverUser
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.ClickEvent
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.chat.HoverEvent
import net.md_5.bungee.api.chat.TextComponent
import org.apache.commons.lang3.RandomStringUtils
import org.bukkit.ChatColor
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class Auth(name: String) : ICommand(name) {

    override fun onPlayerCommand(sp: Player, cmd: Command, label: String, args: Array<String>) {
        val discord_id = getPostgrestClient()
            ?.from<FreeserverUser>(MainAPI.TableName.FREESERVER_USER.table)
            ?.select("discord_id")
            ?.eq("mcuuid", sp.uniqueId)
            ?.executeAndGetSingle<FreeserverUser>()
            ?.discord_id

        if (discord_id == null) {
            val code = RandomStringUtils.randomAlphanumeric(6)
            BotListener.hashMap[code] = sp.uniqueId

            val inviteLink = Main.pluginInstance.config.getString("inviteLink", "https://discord.gg/WudKwEj")

            val textComponent1 = TextComponent(
                "${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}下記のコードを${ChatColor.GOLD}${ChatColor.BOLD}#${
                    Main.botInstance.getTextChannelById(Main.pluginInstance.config.getLong("channelId"))?.name
                }${ChatColor.RESET}${ChatColor.GRAY}に送信してください。\n"
            )
            val textComponent2 =
                TextComponent("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コード: ${ChatColor.GOLD}${ChatColor.BOLD}")
            val textComponent3 = TextComponent(code)
            textComponent3.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}クリックでコードをコピー").create()
            )
            textComponent3.clickEvent = ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, code)
            val textComponent4 =
                TextComponent("\n${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}招待リンク: ${ChatColor.GOLD}${ChatColor.BOLD}")
            val textComponent5 = TextComponent(inviteLink)
            textComponent5.hoverEvent = HoverEvent(
                HoverEvent.Action.SHOW_TEXT,
                ComponentBuilder("${ChatColor.GOLD}${ChatColor.BOLD}${ChatColor.UNDERLINE}クリックでリンクを開く").create()
            )
            textComponent5.clickEvent = ClickEvent(ClickEvent.Action.OPEN_URL, inviteLink)

            textComponent1.addExtra(textComponent2)
            textComponent1.addExtra(textComponent3)
            textComponent1.addExtra(textComponent4)
            textComponent1.addExtra(textComponent5)

            sp.spigot().sendMessage(ChatMessageType.CHAT, textComponent1)
            return
        }
        sp.sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.ERROR)}すでに連携しています。")
        return
    }

    override fun onBlockCommand(bs: BlockCommandSender, cmd: Command, label: String, args: Array<String>) {}
    override fun onConsoleCommand(cs: ConsoleCommandSender, cmd: Command, label: String, args: Array<String>) {}

    override fun onPlayerTabComplete(sp: Player, cmd: Command, alias: String, args: Array<String>): List<String> {
        return emptyList()
    }

    override fun onBlockTabComplete(
        bs: BlockCommandSender,
        cmd: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return emptyList()
    }

    override fun onConsoleTabComplete(
        cs: ConsoleCommandSender,
        cmd: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        return emptyList()
    }
}