package com.example.lab2.presentation.presenter

import com.example.lab2.data.sdui.AnalyticsEventDto
import com.example.lab2.data.sdui.SduiComponentDto
import com.example.lab2.data.sdui.SduiRepository
import com.example.lab2.presentation.contract.SduiContract
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SduiPresenterImpl(
    private val repository: SduiRepository
) : SduiContract.Presenter {

    private var view: SduiContract.View? = null
    private var scope: CoroutineScope? = null
    private val jobs = mutableListOf<Job>()
    private val sentImpressions = HashSet<String>()

    override fun attach(view: SduiContract.View, coroutineScope: CoroutineScope) {
        this.view = view
        this.scope = coroutineScope
    }

    override fun detach() {
        jobs.forEach { it.cancel() }
        jobs.clear()
        scope = null
        view = null
    }

    override fun loadSecondScreen() {
        val v = view ?: return
        val sc = scope ?: return
        v.renderLoading(true)
        jobs += sc.launch {
            val result = runCatching { repository.getSecondScreen() }
            result
                .onSuccess { screen ->
                    v.renderComponents(screen.components)
                }
                .onFailure { e ->
                    v.showError("SDUI load error: ${e.message}")
                }
            v.renderLoading(false)
        }
    }

    override fun onComponentImpression(component: SduiComponentDto) {
        val event = component.analytics?.impressionEvent ?: return
        if (!sentImpressions.add(component.id)) return
        postEvent(event, params = mapOf("componentId" to component.id, "type" to component.type))
    }

    override fun onComponentClicked(component: SduiComponentDto) {
        component.analytics?.clickEvent?.let { event ->
            postEvent(event, params = mapOf("componentId" to component.id, "type" to component.type))
        }

        val action = component.action ?: return
        when (action.type) {
            "show_toast" -> {
                val message = action.payload?.get("message")?.toString().orEmpty()
                view?.showToast(message.ifBlank { "Action executed" })
            }
        }
    }

    private fun postEvent(event: String, params: Map<String, String>) {
        val sc = scope ?: return
        val now = System.currentTimeMillis()
        sc.launch {
            runCatching {
                repository.postEvent(
                    AnalyticsEventDto(
                        event = event,
                        params = params,
                        timestamp = now
                    )
                )
            }
        }
    }
}

