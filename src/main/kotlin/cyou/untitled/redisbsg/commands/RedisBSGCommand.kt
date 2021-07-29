package cyou.untitled.redisbsg.commands

import cyou.untitled.bungeesafeguard.commands.subcommands.SubcommandRegistry
import cyou.untitled.redisbsg.RedisBSG
import cyou.untitled.redisbsg.commands.main.ReloadCommand
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.chat.TextComponent
import net.md_5.bungee.api.plugin.Command

@Suppress("MemberVisibilityCanBePrivate")
open class RedisBSGCommand(context: RedisBSG) : Command("redis-bsg", "redisbsg.main", "redisbsg") {
    companion object {
        open class Usage: SubcommandRegistry.Companion.UsageSender {
            override fun sendUsage(sender: CommandSender) {
                sender.sendMessage(TextComponent("${ChatColor.YELLOW}Usage:"))
                sender.sendMessage(TextComponent("${ChatColor.AQUA}  /redis-bsg reload"))
            }
        }
    }
    protected val cmdReg = SubcommandRegistry(context, Usage())

    init {
        cmdReg.registerSubcommand(ReloadCommand(context))
    }

    override fun execute(sender: CommandSender, args: Array<out String>) {
        cmdReg.getSubcommand(sender, args)?.execute(sender, args.sliceArray(IntRange(0, args.size - 1)))
    }
}