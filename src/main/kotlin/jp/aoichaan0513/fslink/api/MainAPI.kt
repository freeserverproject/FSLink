package jp.aoichaan0513.fslink.api

import io.supabase.postgrest.PostgrestDefaultClient
import jp.aoichaan0513.fslink.Main
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.GameMode
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import java.net.URI
import java.util.*

class MainAPI {
    companion object {

        private val hidePlayers = mutableSetOf<UUID>()

        fun addHidePlayer(p: Player, force: Boolean = true) {
            hidePlayers.add(p.uniqueId)

            if (force)
                for (player: Player in Bukkit.getOnlinePlayers())
                    hidePlayers(player)
        }

        fun removeHidePlayer(p: Player, force: Boolean = true) {
            hidePlayers.remove(p.uniqueId)
            if (force)
                for (player: Player in Bukkit.getOnlinePlayers())
                    hidePlayers(player, true)
        }

        fun hidePlayers(p: Player, isRefresh: Boolean = false) {
            if (isRefresh)
                for (player: Player in Bukkit.getOnlinePlayers().filter { player -> !hidePlayers.contains(player.uniqueId) })
                    p.showPlayer(player)
            for (uuid in hidePlayers.filter { isPlayerOnline(it) }.toSet())
                p.hidePlayer(Bukkit.getPlayer(uuid)!!)
        }

        // プレイヤーがオンラインか
        fun isPlayerOnline(name: String): Boolean {
            return isPlayerOnline(Bukkit.getOfflinePlayer(name))
        }

        fun isPlayerOnline(uuid: UUID): Boolean {
            return isPlayerOnline(Bukkit.getOfflinePlayer(uuid))
        }

        fun isPlayerOnline(p: Player): Boolean {
            return p.isOnline
        }

        fun isPlayerOnline(p: OfflinePlayer): Boolean {
            return p.isOnline
        }

        // プレイヤーが権限を所持しているか
        fun isAdmin(p: Player): Boolean {
            return p.isOp
        }

        // プレフィックス
        fun getPrefix(prefixType: PrefixType): String {
            return getPrefix(prefixType.backColor, prefixType.forwardColor)
        }

        fun getPrefix(backColor: ChatColor, forwardColor: ChatColor = backColor): String {
            return "$backColor> $forwardColor"
        }

        fun getPostgrestClient(): PostgrestDefaultClient?{
            val serviceKey = Main.pluginInstance.config.getString("supabase.serviceKey")
            val uri = Main.pluginInstance.config.getString("supabase.requestURI")
            if (serviceKey == null || uri == null) {
                println("§cserviceKey又はrequestURIが設定されていません,config.ymlを確認してください")
                return null
            }
            return PostgrestDefaultClient(
                    uri = URI(uri),
                    headers = mapOf("Authorization" to "Bearer $serviceKey", "apikey" to serviceKey)
            )
        }
    }

    enum class PrefixType {
        PRIMARY(ChatColor.BLUE),
        SECONDARY(ChatColor.GRAY),
        SUCCESS(ChatColor.GREEN),
        WARNING(ChatColor.YELLOW, ChatColor.GOLD),
        ERROR(ChatColor.RED);

        val backColor: ChatColor
        val forwardColor: ChatColor

        constructor(backColor: ChatColor) {
            this.backColor = backColor
            this.forwardColor = backColor
        }

        constructor(backColor: ChatColor, forwardColor: ChatColor) {
            this.backColor = backColor
            this.forwardColor = forwardColor
        }
    }

    enum class MessageType(val message: String) {
        // 他プロジェクト共通メッセージ
        ARGS("${ChatColor.RED}引数が不正です。"),
        PERMISSION("${ChatColor.RED}権限がありません。"),
        PLAYER("${ChatColor.RED}プレイヤー以外は実行できません。"),
        NUMBER("${ARGS.message}数値以外を指定することはできません。"),
        OFFLINE("${ARGS.message}${ChatColor.BOLD}${ChatColor.UNDERLINE}%s${ChatColor.RESET}${ChatColor.RED} はオフラインです。")
    }

    enum class Gamemode(val gameMode: GameMode, val modeId: Int, val modeAlias: String, val modeName: String) {
        SURVIVAL(GameMode.SURVIVAL, 0, "s", "サバイバル"),
        CREATIVE(GameMode.CREATIVE, 1, "c", "クリエイティブ"),
        ADVENTURE(GameMode.ADVENTURE, 2, "a", "アドベンチャー"),
        SPECTATOR(GameMode.SPECTATOR, 3, "sp", "スペクテイター");

        companion object {

            fun getGamemode(gameMode: GameMode): Gamemode {
                for (gamemode in values())
                    if (gamemode.gameMode == gameMode)
                        return gamemode
                return SURVIVAL
            }
        }
    }
}