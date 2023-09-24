package dev.hasangurbuz.hitalk.presentation.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint
import dev.hasangurbuz.hitalk.databinding.ActivityAuthBinding
import dev.hasangurbuz.hitalk.presentation.base.activities.BindingActivity
import dev.hasangurbuz.hitalk.presentation.viewmodels.AuthViewModel

@AndroidEntryPoint
class AuthActivity : BindingActivity<ActivityAuthBinding>() {

    private val authViewModel: AuthViewModel by viewModels()


    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = ActivityAuthBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestperm()
        authViewModel.setActivity(this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 123) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                finish()
            }
        }
    }

    fun requestperm() {
        val permission = Manifest.permission.READ_CONTACTS
        val requestCode = 123

        if (ContextCompat.checkSelfPermission(
                this,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(permission),
                requestCode
            )
        }

    }

    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
        }
    }

}