package jp.aoichaan0513.fslink.listener

import jp.aoichaan0513.fslink.api.MainAPI
import jp.aoichaan0513.fslink.api.MainAPI.Companion.getPostgrestClient
import jp.aoichaan0513.fslink.Main
import jp.aoichaan0513.fslink.consts.FreeserverUser
import net.dv8tion.jda.api.EmbedBuilder
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter
import net.dv8tion.jda.api.utils.MarkdownUtil
import net.luckperms.api.model.data.DataMutateResult
import net.luckperms.api.node.Node
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.scheduler.BukkitRunnable
import java.time.Instant
import java.util.*


class BotListener : ListenerAdapter() {

    override fun onGuildMessageReceived(e: GuildMessageReceivedEvent) {
        val channel = e.channel
        val user = e.author
        val msg = e.message

        if (user.isBot || e.isWebhookMessage || channel.idLong != Main.pluginInstance.config.getLong("channelId")) return

        val groupName = Main.pluginInstance.config.getString("authedGroup", "sabamin")!!
        val defaultGroupName = "default"
        if (msg.contentRaw.equals("連携解除")) {
            if (Main.pluginInstance.config.contains("authedUsers.${user.id}")) {
                val uuid = UUID.fromString(Main.pluginInstance.config.getString("authedUsers.${user.idLong}"))
                val userManager = Main.luckPerms.userManager
                val userFuture = userManager.loadUser(uuid)

                userFuture.thenAcceptAsync { lpUser ->
                    val result1 = lpUser.data().remove(Node.builder("group.${groupName}").build())
                    val result2 = lpUser.data().add(Node.builder("group.${defaultGroupName}").build())
                    if (result1 == DataMutateResult.SUCCESS && result2 == DataMutateResult.SUCCESS) {
                        lpUser.primaryGroup = defaultGroupName
                        Main.luckPerms.userManager.saveUser(lpUser)

                        Main.pluginInstance.config.set("authedUsers.${user.idLong}", null)
                        Main.pluginInstance.saveConfig()

                        getPostgrestClient()?.from<FreeserverUser>(MainAPI.TableName.FREESERVER_USER.table)
                            ?.update(mapOf("discord_id" to null))
                            ?.eq("mcuuid", uuid)
                            ?.execute()

                        val embedBuilder = EmbedBuilder().setTimestamp(Instant.now()).setTitle("成功")
                            .setDescription("${user.asMention} とMinecraft アカウントとの連携を解除しました。\nMinecraft サーバーに再度参加して認証を行うことが出来ます。")
                        channel.sendMessage(embedBuilder.build()).queue()
                        return@thenAcceptAsync
                    } else {
                        val embedBuilder = EmbedBuilder().setTimestamp(Instant.now()).setTitle("エラー")
                            .setDescription("予期しないエラーが発生しました。")
                        channel.sendMessage(embedBuilder.build()).queue()
                        return@thenAcceptAsync
                    }
                }
                return
            }
            val embedBuilder = EmbedBuilder().setTimestamp(Instant.now()).setTitle("エラー")
                .setDescription("連携されていないため連携の解除ができませんでした。\nMinecraft サーバーに参加して連携を開始してください。")
            channel.sendMessage(embedBuilder.build()).queue()
            return
        } else {
            if (!msg.contentRaw.matches(Regex("([A-z0-9]{6})"))) return
            if (hashMap.containsKey(msg.contentRaw)) {
                val uuid = hashMap[msg.contentRaw]!!
                val p = Bukkit.getOfflinePlayer(uuid)
                val lpUser = Main.luckPerms.userManager.getUser(uuid)!!

                val result1 = lpUser.data().remove(Node.builder("group.${defaultGroupName}").build())
                val result2 = lpUser.data().add(Node.builder("group.${groupName}").build())
                if (result1 == DataMutateResult.SUCCESS && result2 == DataMutateResult.SUCCESS) {
                    lpUser.primaryGroup = groupName
                    Main.luckPerms.userManager.saveUser(lpUser)

                    Main.pluginInstance.config.set("authedUsers.${user.id}", uuid.toString())
                    Main.pluginInstance.saveConfig()

                    object : BukkitRunnable() {
                        override fun run() {
                            for (map in Main.pluginInstance.config.getMapList("runCommands")) {
                                val cmd = (map.get("command") as String).replace("{replace:player_name}", p.name!!)
                                    .replace("{replace:player_uuid}", uuid.toString())
                                val type = map.getOrDefault("type", "console") as String

                                Bukkit.dispatchCommand(
                                    if (type.equals(
                                            "player",
                                            true
                                        ) && p.isOnline
                                    ) p.player!! else Bukkit.getConsoleSender(), cmd
                                )
                            }
                        }
                    }.runTaskLater(Main.pluginInstance, 0)

                    val embedBuilder = EmbedBuilder().setTimestamp(Instant.now())
                        .setThumbnail("https://minotar.net/helm/${p.uniqueId}").setTitle("成功")
                        .setDescription("${user.asMention} のDiscord アカウントを${MarkdownUtil.bold(p.name!!)}と連携しました。")
                    channel.sendMessage(embedBuilder.build()).queue()

                    var response = getPostgrestClient()
                        ?.from<FreeserverUser>(MainAPI.TableName.FREESERVER_USER.table)
                        ?.update(mapOf("discord_id" to user.id))
                        ?.eq("mcuuid", uuid)
                        ?.execute()

                    Bukkit.getLogger().info("$response")

                    if (p.isOnline)
                        p.player!!.sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SUCCESS)}あなたのMinecraft アカウントを${ChatColor.BOLD}${user.asTag}${ChatColor.RESET}${ChatColor.GREEN}と連携しました。")
                    hashMap.remove(msg.contentRaw)
                    return
                } else {
                    val embedBuilder = EmbedBuilder().setTimestamp(Instant.now()).setTitle("エラー")
                        .setDescription("予期しないエラーが発生しました。")
                    channel.sendMessage(embedBuilder.build()).queue()
                    return
                }
            } else {
                val embedBuilder =
                    EmbedBuilder().setTimestamp(Instant.now()).setTitle("エラー").setDescription("そのコードは見つかりませんでした。")
                channel.sendMessage(embedBuilder.build()).queue()
            }
            return
        }
    }

    companion object {
        val hashMap = mutableMapOf<String, UUID>()
    }
}