package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.lab2.R
import com.example.lab2.data.sdui.SduiComponentDto
import com.example.lab2.data.sdui.SduiRepositoryImpl
import com.example.lab2.presentation.contract.SduiContract
import com.example.lab2.presentation.presenter.SduiPresenterImpl
import com.example.lab2.presentation.sdui.SduiAdapter

class SduiFragment : Fragment(R.layout.fragment_sdui), SduiContract.View {

    private lateinit var presenter: SduiContract.Presenter
    private lateinit var adapter: SduiAdapter

    private var recycler: RecyclerView? = null
    private var progress: ProgressBar? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = SduiPresenterImpl(SduiRepositoryImpl())
        presenter.attach(this, viewLifecycleOwner.lifecycleScope)

        recycler = view.findViewById(R.id.sduiRecyclerView)
        progress = view.findViewById(R.id.sduiProgress)

        adapter = SduiAdapter(
            onImpression = { presenter.onComponentImpression(it) },
            onClick = { presenter.onComponentClicked(it) }
        )

        recycler?.layoutManager = LinearLayoutManager(requireContext())
        recycler?.adapter = adapter

        presenter.loadSecondScreen()
    }

    override fun onDestroyView() {
        presenter.detach()
        recycler = null
        progress = null
        super.onDestroyView()
    }

    override fun renderLoading(visible: Boolean) {
        progress?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun renderComponents(components: List<SduiComponentDto>) {
        adapter.submit(components)
    }

    override fun showError(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }

    override fun showToast(message: String) {
        context?.let { Toast.makeText(it, message, Toast.LENGTH_SHORT).show() }
    }
}

