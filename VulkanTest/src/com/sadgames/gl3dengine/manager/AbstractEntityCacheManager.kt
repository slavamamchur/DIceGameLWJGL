package com.sadgames.gl3dengine.manager

import com.sadgames.gl3dengine.GLEngineConsts.NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE
import com.sadgames.gl3dengine.manager.AbstractEntityCacheManager.CachedEntity
import java.util.*

abstract class AbstractEntityCacheManager<T: CachedEntity>
    internal constructor(private val cacheSize: Long, private val cacheRAMSize: Long, private val itemCreator: (key: String) -> T) {

    private inner class Item internal constructor(entity: T, var isImmortal: Boolean) {
        val entity: T = entity; get() { usageCounter++; return field }
        var usageCounter: Long = 0
    }

    private inner class CacheInfo internal constructor(val size: Long, private val cleaner: (item: T) -> Unit) {
        var actualSize = 0L

        fun isEnoughSpace(amount: Long) = size - actualSize >= amount
        fun addItem(item: T) { actualSize += item.size }
        fun removeItem(item: T) { cleaner(item); actualSize -= item.size }
    }

    interface CachedEntity {
        val size: Long
        val name: String
        val isDeleted: Boolean
        val isReleased: Boolean

        fun delete()
        fun release()
        fun reloadData()
    }

    private val items = HashMap<String, Item>()
    private val ramCacheInfo = CacheInfo(cacheRAMSize) {it.release()}
    private val vRamCacheInfo = if (cacheSize > 0) CacheInfo(cacheSize) {it.delete()} else null

    fun getItem(key: String): T {
        val entity = (items[key] ?: addItem(itemCreator(key), key, false)).entity

        if (entity.isDeleted || entity.isReleased) {
            freeNecessarySpace(entity)
            entity.reloadData()

            if (entity.isDeleted) vRamCacheInfo?.addItem(entity)
            if (entity.isReleased && ramCacheInfo.isEnoughSpace(entity.size)) ramCacheInfo.addItem(entity)
        }

        return entity
    }

    inline operator fun get(key: String) = getItem(key)

    private fun addItem(item: T, key: String, isImmortal: Boolean): Item {
        freeNecessarySpace(item)

        val result = Item(item, isImmortal)
        items[key] = result

        vRamCacheInfo?.addItem(item)
        ramCacheInfo.addItem(item)

        return result
    }

    @JvmOverloads
    fun putItem(it: T, immortal: Boolean = true) = (items[it.name] ?: addItem(it, it.name, immortal)).usageCounter++
    fun setWeakCacheMode() = items.values.forEach { it.isImmortal = false }
    fun clearCache() { items.values.forEach { deleteItem(it, true) }; items.clear() }
    fun deleteByName(key: String) = deleteItem(items[key])

    private fun get2KillCandidate(): Item? {
        var mortal: Item? = null

        items.values.forEach {
            mortal = if ((mortal?.usageCounter ?: Long.MAX_VALUE > it.usageCounter) && !it.isImmortal) it else mortal
        }

        return mortal
    }

    private fun deleteItem(item: Item?, release: Boolean = false) {
        if (item != null) {
            if (release && !item.entity.isReleased) {
                ramCacheInfo.removeItem(item.entity)
            }

            if (!item.entity.isDeleted) {
                vRamCacheInfo?.removeItem(item.entity)
            }
        }
    }

    private fun freeNecessarySpace(item: T) {
        require(item.size < vRamCacheInfo?.size ?: Long.MAX_VALUE) { NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE }

        val isEnoughRAM = ramCacheInfo.isEnoughSpace(item.size)
        val isEnoughVRAM = vRamCacheInfo?.isEnoughSpace(item.size) ?: true

        if (!(isEnoughRAM && isEnoughVRAM)) {
            val candidate = get2KillCandidate()
            require(candidate != null || isEnoughVRAM) { NOT_ENOUGH_SPACE_IN_CACHE_ERROR_MESSAGE }

            if (!isEnoughRAM) ramCacheInfo.removeItem(candidate?.entity ?: item)
            if (!isEnoughVRAM) vRamCacheInfo?.removeItem(candidate!!.entity)

            freeNecessarySpace(item)
        }
    }

}