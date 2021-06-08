package com.sadgames.dicegame.desktop

import com.sadgames.sysutils.common.GdxDbInterface
import com.sadgames.sysutils.common.SysUtilsConsts
import java.io.File
import java.sql.Connection
import java.sql.Driver
import java.sql.DriverManager.getConnection
import java.sql.DriverManager.registerDriver
import java.sql.SQLException

object DesktopGdxDbWrapper: GdxDbInterface {

    private const val SQLITE_JDBC_DRIVER_CLASS_NAME = "org.sqlite.JDBC"
    private const val name = SysUtilsConsts.DB_NAME + ".sq3"

    private val handle = File("${SysUtilsConsts.EXTERNAL_PATH}$name")

    init { initDataBase() }

    private fun initDataBase() {
        try {
            if (!handle.exists()) {
                val local = this.javaClass.getResource("/db").path
                File("$local/$name").copyTo(handle)
            }
        } catch (e: Exception) {
            throw RuntimeException("Failed to create DataBase.")
        }

        try {
            registerDriver(Class.forName(SQLITE_JDBC_DRIVER_CLASS_NAME).newInstance() as Driver)
        } catch (e: Exception) {
            throw RuntimeException("Failed to register SQLDroidDriver")
        }
    }

    override val jDBCConnection; get() =
        try {
            getConnection("jdbc:sqlite:${handle.absolutePath}")
        } catch (e: SQLException) {
            throw RuntimeException(e)
        }
}