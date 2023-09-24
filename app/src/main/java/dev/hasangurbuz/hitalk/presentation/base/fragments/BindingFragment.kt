package dev.hasangurbuz.hitalk.presentation.base.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import com.google.android.material.snackbar.Snackbar

abstract class BindingFragment<out T : ViewBinding> : Fragment() {

    private var _binding: ViewBinding? = null
    protected val binding: T get() = _binding as T
    protected abstract val bindingInflater: (LayoutInflater) -> ViewBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = bindingInflater(inflater)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    protected fun snackBar(text: String): Snackbar {
        return Snackbar.make(
            binding.root,
            text,
            Snackbar.LENGTH_SHORT
        ).setAction("Ok") {

        }
    }
}