package dev.hasangurbuz.hitalk.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.ActivityAuthBinding
import dev.hasangurbuz.hitalk.ui.BindingActivity
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AuthActivity : BindingActivity<ActivityAuthBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = ActivityAuthBinding::inflate


}