package jp.aoichaan0513.fslink

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Commands.ICommand
import jp.aoichaan0513.fslink.Commands.Main.Auth
import jp.aoichaan0513.fslink.Listeners.BotListener
import jp.aoichaan0513.fslink.Listeners.PlayerListener
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.JDABuilder
import net.dv8tion.jda.api.OnlineStatus
import net.luckperms.api.LuckPerms
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin


class Main : JavaPlugin() {

    override fun onEnable() {
        pluginInstance = this

        saveDefaultConfig()

        val provider = Bukkit.getServicesManager().getRegistration(LuckPerms::class.java)
        if (provider != null)
            luckPerms = provider.provider

        loadCommands()
        loadListeners()
        loadBot()

        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}プラグインを起動しました。")
    }

    override fun onDisable() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}プラグインを終了しました。")
    }

    private fun loadCommands() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コマンドを読み込んでいます。しばらくお待ちください…")
        val hashMap = mutableMapOf<String, ICommand>("auth" to Auth("name"))
        hashMap.map { entry -> getCommand(entry.key)?.setExecutor(entry.value) }
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コマンドを${hashMap.size}件読み込みました。")
        return
    }

    private fun loadListeners() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}リスナーを読み込んでいます。しばらくお待ちください…")
        val list = arrayListOf<Listener>(PlayerListener())
        list.map { listener -> Bukkit.getPluginManager().registerEvents(listener, this) }
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}リスナーを${list.size}件読み込みました。")
        return
    }

    private fun loadBot() {
        botInstance = JDABuilder()
                .setToken(config.getString("token"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListeners(BotListener())
                .build()
    }

    companion object {
        lateinit var pluginInstance: JavaPlugin
        lateinit var botInstance: JDA

        lateinit var luckPerms: LuckPerms
    }
}