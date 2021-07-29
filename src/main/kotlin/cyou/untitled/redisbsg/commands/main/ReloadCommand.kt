package cyou.untitled.redisbsg.commands.main

import cyou.untitled.bungeesafeguard.commands.subcommands.Subcommand
import cyou.untitled.bungeesafeguard.helpers.RedirectedLogger
import cyou.untitled.bungeesafeguard.storage.Backend
import cyou.untitled.redisbsg.RedisBSG
import cyou.untitled.redisbsg.RedisBackend
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.launch
import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.CommandSender

open class ReloadCommand(override val context: RedisBSG) : Subcommand(context, "reload") {
    @Suppress("DEPRECATION")
    @OptIn(DelicateCoroutinesApi::class)
    override fun execute(sender: CommandSender, realArgs: Array<out String>) {
        GlobalScope.launch(context.executorService.asCoroutineDispatcher()) {
            val config = context.config
            config.reload(sender)
            val logger = RedirectedLogger.get(context, sender)
            logger.info("Reloading RedisBackend")
            val backend = Backend.getBackend() as RedisBackend
            backend.url = config.url
            try {
                backend.reload(sender)
            } catch (err: Throwable) {
                logger.severe("Failed to reload RedisBackend: $err")
            }
            logger.info("${ChatColor.GREEN}$backend reloaded")
        }
    }
}