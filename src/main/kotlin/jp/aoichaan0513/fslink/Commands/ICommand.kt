package jp.aoichaan0513.fslink.Commands

import org.bukkit.command.*
import org.bukkit.entity.Player
import java.util.*

abstract class ICommand(private val name: String) : CommandExecutor, TabCompleter {

    // コマンド
    abstract fun onPlayerCommand(sp: Player, cmd: Command, label: String, args: Array<String>)
    abstract fun onBlockCommand(bs: BlockCommandSender, cmd: Command, label: String, args: Array<String>)
    abstract fun onConsoleCommand(cs: ConsoleCommandSender, cmd: Command, label: String, args: Array<String>)

    override fun onCommand(sender: CommandSender, cmd: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player)
            onPlayerCommand(sender, cmd, label, args)
        else if (sender is BlockCommandSender)
            onBlockCommand(sender, cmd, label, args)
        else if (sender is ConsoleCommandSender)
            onConsoleCommand(sender, cmd, label, args)
        return true
    }

    // タブ補完
    abstract fun onPlayerTabComplete(sp: Player, cmd: Command, alias: String, args: Array<String>): List<String>
    abstract fun onBlockTabComplete(bs: BlockCommandSender, cmd: Command, alias: String, args: Array<String>): List<String>
    abstract fun onConsoleTabComplete(cs: ConsoleCommandSender, cmd: Command, alias: String, args: Array<String>): List<String>

    override fun onTabComplete(sender: CommandSender, cmd: Command, alias: String, args: Array<String>): List<String> {
        if (!cmd.name.equals(name, true)) return Collections.emptyList()

        if (sender is Player)
            return onPlayerTabComplete(sender, cmd, alias, args)
        else if (sender is BlockCommandSender)
            return onBlockTabComplete(sender, cmd, alias, args)
        else if (sender is ConsoleCommandSender)
            return onConsoleTabComplete(sender, cmd, alias, args)
        return Collections.emptyList()
    }
}