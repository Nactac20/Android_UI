package com.example.lab2.presentation.view

import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lab2.R
import com.example.lab2.ThemePrefs
import com.example.lab2.data.sdui.SduiComponentDto
import com.example.lab2.data.sdui.SduiRepositoryImpl
import com.example.lab2.databinding.ActivitySecondBinding
import com.example.lab2.presentation.contract.SduiContract
import com.example.lab2.presentation.presenter.SduiPresenterImpl
import com.example.lab2.presentation.sdui.SduiAdapter

class SecondActivity : AppCompatActivity(), SduiContract.View {

    private lateinit var binding: ActivitySecondBinding
    private lateinit var presenter: SduiContract.Presenter
    private lateinit var adapter: SduiAdapter
    private var progress: ProgressBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ThemePrefs.applySaved(this)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)

        presenter = SduiPresenterImpl(SduiRepositoryImpl())
        presenter.attach(this, lifecycleScope)

        progress = findViewById(R.id.sduiProgress)

        adapter = SduiAdapter(
            onImpression = { presenter.onComponentImpression(it) },
            onClick = { presenter.onComponentClicked(it) }
        )

        binding.sduiRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.sduiRecyclerView.adapter = adapter

        presenter.loadSecondScreen()
    }

    override fun onDestroy() {
        presenter.detach()
        super.onDestroy()
    }

    override fun renderLoading(visible: Boolean) {
        progress?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    override fun renderComponents(components: List<SduiComponentDto>) {
        adapter.submit(components)
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
