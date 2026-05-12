package com.example.myapplication.autofill

import android.app.assist.AssistStructure
import android.os.CancellationSignal
import android.service.autofill.AutofillService
import android.service.autofill.Dataset
import android.service.autofill.FillCallback
import android.service.autofill.FillRequest
import android.service.autofill.FillResponse
import android.service.autofill.SaveCallback
import android.service.autofill.SaveRequest
import android.view.autofill.AutofillValue
import android.widget.RemoteViews

class MyAutofillService : AutofillService() {

    override fun onFillRequest(
        request: FillRequest,
        cancellationSignal: CancellationSignal,
        callback: FillCallback
    ) {
        val context = request.fillContexts.lastOrNull() ?: run {
            callback.onSuccess(null)
            return
        }

        val structure = context.structure

        val usernameField = findField(structure, arrayOf("username", "email", "login"))
        val passwordField = findField(structure, arrayOf("password"))

        if (usernameField == null || passwordField == null) {
            callback.onSuccess(null)
            return
        }

        val usernameId = usernameField.autofillId ?: run {
            callback.onSuccess(null)
            return
        }

        val passwordId = passwordField.autofillId ?: run {
            callback.onSuccess(null)
            return
        }

        val savedUsername = "user@example.com"
        val savedPassword = "password123"

        val dataset = Dataset.Builder()
            .setValue(usernameId, AutofillValue.forText(savedUsername), createRemoteView("Логин: $savedUsername"))
            .setValue(passwordId, AutofillValue.forText(savedPassword), createRemoteView("Пароль: ••••••••"))
            .build()

        callback.onSuccess(FillResponse.Builder().addDataset(dataset).build())
    }

    override fun onSaveRequest(request: SaveRequest, callback: SaveCallback) {
        callback.onSuccess()
    }

    override fun onConnected() {
        super.onConnected()
    }

    override fun onDisconnected() {
        super.onDisconnected()
    }

    private fun findField(
        structure: AssistStructure,
        hints: Array<String>
    ): AssistStructure.ViewNode? {
        val nodes = mutableListOf<AssistStructure.ViewNode>()
        val rootNode = structure.run {
            (0 until windowNodeCount).map { getWindowNodeAt(it) }.firstOrNull()?.rootViewNode
        }
        collectNodes(rootNode, nodes)

        return nodes.firstOrNull { node ->
            val hint = node.hint?.lowercase() ?: ""
            val idEntry = node.idEntry?.lowercase() ?: ""
            hints.any { hint.contains(it) || idEntry.contains(it) }
        }
    }

    private fun collectNodes(
        node: AssistStructure.ViewNode?,
        result: MutableList<AssistStructure.ViewNode>
    ) {
        if (node == null) return
        result.add(node)
        for (i in 0 until node.childCount) {
            collectNodes(node.getChildAt(i), result)
        }
    }

    private fun createRemoteView(text: String): RemoteViews {
        return RemoteViews(packageName, android.R.layout.simple_list_item_1).apply {
            setTextViewText(android.R.id.text1, text)
        }
    }
}