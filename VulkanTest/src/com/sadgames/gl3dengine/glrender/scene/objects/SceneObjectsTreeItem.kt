package com.sadgames.gl3dengine.glrender.scene.objects

import java.util.*
import javax.vecmath.Vector2f

abstract class SceneObjectsTreeItem

    protected constructor(itemNumber: Long = -1,
                               itemName: String? = null,
                               var parent: SceneObjectsTreeItem? = null) {

    interface ISceneObjectsTreeHandler { fun onProcessItem(item: SceneObjectsTreeItem?): Void }
    interface ISceneObjectsCondition { fun checkCondition(item: SceneObjectsTreeItem?): Boolean }

    var itemNumber = itemNumber
        set(value) {
            val number = if (value < 0) System.currentTimeMillis() else value
            for (item in childs.values) if (item.itemNumber >= number) item.itemNumber = item.itemNumber + 1
            field = number
        }

    var itemName = itemName; set(value) { field = value ?: String.format("ITEM_#_%d", itemNumber) }
    var isVisible = true
    val childs: MutableMap<String, SceneObjectsTreeItem> = HashMap()

    open fun getPlaceHeight(pos: Vector2f): Float { return 0f }
    fun hideObject() { isVisible = false }
    fun showObject() { isVisible = true }

    fun deleteChild(name: String?) {
        val item = getChild(name)

        if (item != null) {
            childs.remove(name)
            item.parent = null

            val inum = item.itemNumber
            for (titem in childs.values)
                if (titem.itemNumber > inum)
                    titem.itemNumber = titem.itemNumber - 1
        }
    }

    fun getChild(name: String?): SceneObjectsTreeItem? {
        val items: Iterator<SceneObjectsTreeItem> = childs.values.iterator()
        var result = childs[name]

        while (result == null && items.hasNext()) result = items.next().getChild(name)

        return result
    }

    @JvmOverloads
    fun putChild(item: SceneObjectsTreeItem, name: String? = item.itemName, number: Long = childs.size.toLong()) {
            item.parent?.deleteChild(item.itemName)
            item.itemName = name
            item.itemNumber = number
            item.parent = this

            childs[name!!] = item
    }

    //todo: change to func ref???
    fun proceesTreeItems(itemHandler: (item: SceneObjectsTreeItem?) -> Unit, condition: (item: SceneObjectsTreeItem?) -> Boolean) {
        if (isVisible) {
            val sortedItems = ArrayList(childs.values)
            sortedItems.sortWith(Comparator
                { i1: SceneObjectsTreeItem, i2: SceneObjectsTreeItem -> (i1.itemNumber - i2.itemNumber).toInt() })

            for (item in sortedItems)
                if (item.isVisible) {
                    if (condition(item)) itemHandler(item)
                    item.proceesTreeItems(itemHandler, condition)
                }
        }
    }

}