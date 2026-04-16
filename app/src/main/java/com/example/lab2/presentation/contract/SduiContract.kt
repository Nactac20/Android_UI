package com.example.lab2.presentation.contract

import com.example.lab2.data.sdui.SduiComponentDto
import kotlinx.coroutines.CoroutineScope

interface SduiContract {
    interface View {
        fun renderLoading(visible: Boolean)
        fun renderComponents(components: List<SduiComponentDto>)
        fun showError(message: String)
        fun showToast(message: String)
    }

    interface Presenter {
        fun attach(view: View, coroutineScope: CoroutineScope)
        fun detach()
        fun loadSecondScreen()
        fun onComponentImpression(component: SduiComponentDto)
        fun onComponentClicked(component: SduiComponentDto)
    }
}

