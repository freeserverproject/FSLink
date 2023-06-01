package jp.aoichaan0513.fslink.commands.main

import jp.aoichaan0513.fslink.Main
import jp.aoichaan0513.fslink.api.MainAPI
import jp.aoichaan0513.fslink.api.MainAPI.Companion.getPostgrestClient
import jp.aoichaan0513.fslink.commands.ICommand
import jp.aoichaan0513.fslink.consts.FreeserverUser
import net.dv8tion.jda.api.EmbedBuilder
import net.luckperms.api.model.data.DataMutateResult
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import java.lang.Error
import java.lang.NullPointerException
import java.time.Instant

class CheckVerified(name: String) : ICommand(name){
    override fun onPlayerCommand(sp: Player, cmd: Command, label: String, args: Array<String>) {
        val playerList = getPostgrestClient()
            ?.from<FreeserverUser>(MainAPI.TableName.FREESERVER_USER.table)
            ?.select("discord_id")
            ?.executeAndGetList<FreeserverUser>()
        val userManager = Main.luckPerms.userManager
        val groupName = Main.pluginInstance.config.getString("authedGroup", "sabamin")!!
        val defaultGroupName = "default"

        if (playerList != null) {
            for (player in playerList) {
                if (player.discord_id != null) {
                    val userFuture = userManager.loadUser(player.mcuuid)
                    userFuture.thenAcceptAsync { lpUser ->
                        val result1 = lpUser.data().remove(Node.builder("group.${defaultGroupName}").build())
                        val result2 = lpUser.data().add(Node.builder("group.${groupName}").build())
                        if (result1 == DataMutateResult.SUCCESS && result2 == DataMutateResult.SUCCESS) {
                            lpUser.primaryGroup = defaultGroupName
                            Main.luckPerms.userManager.saveUser(lpUser)
                            Bukkit.getLogger().info("The discordID of ${player.mcuuid} is ${player.discord_id}.")
                        } else {
                            Bukkit.getLogger().severe("予期しないエラーが発生しました")
                            return@thenAcceptAsync
                        }
                    }
                } else {
                    Bukkit.getLogger().info("${player.mcuuid} is not verified.")
                }
            }
            return
        }
        throw NullPointerException("playerList is null")
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