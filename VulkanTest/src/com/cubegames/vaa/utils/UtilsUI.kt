package com.cubegames.vaa.utils

import com.cubegames.engine.domain.rest.responses.BasicResponse
import com.cubegames.vaa.client.exceptions.ClientException
import com.google.common.base.Strings

object UtilsUI {

    @JvmStatic fun checkServiceError(response: BasicResponse?) {
        if (response == null) {
            throw ClientException("Empty response")
        }

        if (!Strings.isNullOrEmpty(response.error)) {
            throw ClientException(response.error)
        }

        if (response.errorCode != 0) {
            throw ClientException("Error code: " + response.errorCode)
        }
    }
}