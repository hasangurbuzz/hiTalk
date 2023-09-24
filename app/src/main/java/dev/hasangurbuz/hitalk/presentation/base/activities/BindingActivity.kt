package dev.hasangurbuz.hitalk.presentation.base.activities

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding

abstract class BindingActivity<out T : ViewBinding> : AppCompatActivity() {
    private var _binding: ViewBinding? = null
    protected val binding get() = _binding as T
    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = bindingInflater(layoutInflater) as T
        setContentView(binding.root)
    }
}