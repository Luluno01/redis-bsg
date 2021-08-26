package cyou.untitled.redisbsg

import cyou.untitled.bungeesafeguard.helpers.DependencyFixer
import cyou.untitled.bungeesafeguard.storage.Backend
import cyou.untitled.redisbsg.commands.RedisBSGCommand
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.runBlocking
import net.md_5.bungee.api.plugin.Plugin
import org.bstats.bungeecord.Metrics

class RedisBSG: Plugin() {
    companion object {
        init {
            DependencyFixer.fixLibraryLoader(RedisBSG::class.java.classLoader)
        }
    }

    val config = Config(this)

    @Suppress("DEPRECATION")
    override fun onEnable() {
        Metrics(this, 12253)
        runBlocking(executorService.asCoroutineDispatcher()) {
            config.saveDefaultConfig()
            config.load(null)
            val backend = RedisBackend(this@RedisBSG, config.url)
            backend.init(null)
            Backend.registerBackend(backend, this@RedisBSG)
        }
        proxy.pluginManager.registerCommand(this, RedisBSGCommand(this))
    }

    override fun onDisable() {
        proxy.pluginManager.unregisterCommands(this)
    }
}