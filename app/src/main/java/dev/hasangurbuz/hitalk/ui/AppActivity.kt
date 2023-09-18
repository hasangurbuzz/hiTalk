package dev.hasangurbuz.hitalk.ui

import android.os.Bundle
import android.os.PersistableBundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.navigation.navArgs
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.ActivityAppBinding

@AndroidEntryPoint
class AppActivity : BindingActivity<ActivityAppBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = ActivityAppBinding::inflate

    private val appViewModel: AppViewModel by viewModels()
    private val args: AppActivityArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        appViewModel.setAppUser(args.AppUser)

    }
}