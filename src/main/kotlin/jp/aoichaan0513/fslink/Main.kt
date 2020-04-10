package jp.aoichaan0513.fslink

import jp.aoichaan0513.fslink.API.MainAPI
import jp.aoichaan0513.fslink.Commands.ICommand
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onEnable() {
        instance = this

        loadCommands()
        loadListeners()

        saveDefaultConfig()

        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}プラグインを起動しました。")
    }

    override fun onDisable() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}プラグインを終了しました。")
    }

    private fun loadCommands() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コマンドを読み込んでいます。しばらくお待ちください…")
        val hashMap = mutableMapOf<String, ICommand>()
        hashMap.map { entry -> getCommand(entry.key)?.setExecutor(entry.value) }
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}コマンドを${hashMap.size}件読み込みました。")
        return
    }

    private fun loadListeners() {
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}リスナーを読み込んでいます。しばらくお待ちください…")
        val list = object : ArrayList<Listener>() {
            init {
            }
        }
        list.map { listener -> Bukkit.getPluginManager().registerEvents(listener, this) }
        Bukkit.getConsoleSender().sendMessage("${MainAPI.getPrefix(MainAPI.PrefixType.SECONDARY)}リスナーを${list.size}件読み込みました。")
        return
    }

    companion object {
        lateinit var instance: JavaPlugin
    }
}