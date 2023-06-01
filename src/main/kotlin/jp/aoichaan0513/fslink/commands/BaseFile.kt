package jp.aoichaan0513.fslink.commands

import org.bukkit.command.BlockCommandSender
import org.bukkit.command.Command
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player

class BaseFile(name: String) : ICommand(name) {

    override fun onPlayerCommand(sp: Player, cmd: Command, label: String, args: Array<String>) {}
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