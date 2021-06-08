package com.sadgames.sysutils.common

    fun chain(j: Int, i: Int, iMax: Int) = chain(i, j, iMax, 1).toShort()
    fun chain(i: Int, j: Int, iMax: Int, elSize: Int) = i * elSize + j * (iMax + 1) * elSize
