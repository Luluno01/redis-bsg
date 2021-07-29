package cyou.untitled.redisbsg

import cyou.untitled.bungeesafeguard.storage.Backend
import io.lettuce.core.ExperimentalLettuceCoroutinesApi
import io.lettuce.core.RedisClient
import io.lettuce.core.api.coroutines
import io.lettuce.core.api.coroutines.RedisCoroutinesCommands
import kotlinx.coroutines.flow.toCollection
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.plugin.Plugin
import java.io.File
import java.util.*

@Suppress("MemberVisibilityCanBePrivate")
open class RedisBackend(context: Plugin, var url: String) : Backend(context) {
    protected var redis: RedisClient? = null
    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    protected var api: RedisCoroutinesCommands<String, String>? = null
    protected val lock = Mutex()

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun add(path: Array<String>, rawRecord: String): Boolean {
        return when (val res = api!!.sadd(path.joinToString("."), rawRecord) ?: error("SADD returns null!")) {
            0L -> false
            1L -> true
            else -> error("SADD returns $res!")
        }
    }

    override suspend fun close(commandSender: CommandSender?) {
        lock.withLock {
            redis!!.shutdownAsync().await()
            redis = null
            api = null
        }
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun get(path: Array<String>): Set<String> {
        return api!!.smembers(path.joinToString(".")).toCollection(mutableSetOf())
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun getSize(path: Array<String>): Int {
        return api!!.scard(path.joinToString("."))!!.toInt()
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun has(path: Array<String>, rawRecord: String): Boolean {
        return api!!.sismember(path.joinToString("."), rawRecord) ?: error("SISMEMBER returns null!")
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun init(commandSender: CommandSender?) {
        lock.withLock {
            assert(redis == null || api == null) { "Concurrent initialization, or reinitialize without first closing" }
            redis = RedisClient.create(url)
            api = redis!!.connect().coroutines()
        }
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun moveToListIfInLazyList(
        username: String,
        id: UUID,
        mainPath: Array<String>,
        lazyPath: Array<String>,
    ): Boolean {
        return if (remove(lazyPath, username)) {
            add(mainPath, id.toString())
            true
        } else false
    }

    override suspend fun onReloadConfigFile(newConfig: File, commandSender: CommandSender?) {
        // Do nothing
    }

    override suspend fun reload(commandSender: CommandSender?) {
        close(commandSender)
        init(commandSender)
    }

    @OptIn(ExperimentalLettuceCoroutinesApi::class)
    override suspend fun remove(path: Array<String>, rawRecord: String): Boolean {
        return when (val res = api!!.srem(path.joinToString("."), rawRecord) ?: error("SREM returns null!")) {
            0L -> false
            1L -> true
            else -> error("SREM returns $res!")
        }
    }

    override fun toString(): String {
        return "RedisBackend(\"$url\")"
    }
}