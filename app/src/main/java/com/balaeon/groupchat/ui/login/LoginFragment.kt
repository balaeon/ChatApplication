package com.balaeon.groupchat.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.balaeon.groupchat.ui.BindingFragment
import com.balaeon.groupchat.util.Constants
import com.balaeon.groupchat.util.navigateSafely
import com.baleon.groupchat.R
import com.baleon.groupchat.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment: BindingFragment<FragmentLoginBinding>() {
    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentLoginBinding::inflate

    private val loginViewModel:LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnConfirm.setOnClickListener {
            setUpConnectingUiState()
            loginViewModel.connectUser(binding.etUsername.text.toString())
        }
        binding.etUsername.addTextChangedListener{
            binding.etUsername.error=null
        }
        subscribeToEvents()
    }

    private fun subscribeToEvents(){
        lifecycleScope.launchWhenStarted {
            loginViewModel.logInEvent.collect{ event->
                when(event)
                {
                    is LoginViewModel.LogInEvent.ErrorInputTooShort->{
                        setUpIdleUiState()
                        binding.etUsername.error=getString(R.string.error_username_too_short,Constants.MIN_USER_LENGTH)
                    }
                    is LoginViewModel.LogInEvent.ErrorLogIn->{
                        setUpIdleUiState()
                        Log.d("LogInEvent=>",event.error)
                        Toast.makeText(requireContext(),event.error,Toast.LENGTH_LONG).show()
                    }
                    is LoginViewModel.LogInEvent.Success->{
                        setUpIdleUiState()
                        findNavController().navigateSafely(R.id.action_loginFragment_to_channelFragment)
                    }
                    else->{
                        //need to take some future enhancement
                    }
                }
            }
        }
    }

    private fun setUpConnectingUiState()
    {
        binding.progressBar.isVisible=true
        binding.btnConfirm.isEnabled=false
    }

    private fun setUpIdleUiState()
    {
        binding.progressBar.isVisible=false
        binding.btnConfirm.isEnabled=true
    }
}