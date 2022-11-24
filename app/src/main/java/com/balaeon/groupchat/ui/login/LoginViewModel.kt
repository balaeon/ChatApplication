package com.balaeon.groupchat.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balaeon.groupchat.util.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import io.getstream.chat.android.client.ChatClient
import io.getstream.chat.android.client.call.await
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val client: ChatClient
) : ViewModel(){

    private val _logInEvent=MutableSharedFlow<LogInEvent>()
    val logInEvent=_logInEvent.asSharedFlow()

    fun isValidUserName(name: String)=name.length > Constants.MIN_USER_LENGTH

    fun connectUser(userName:String)
    {
        val trimmedUserName=userName.trim()
        viewModelScope.launch {
            if (isValidUserName(userName))
            {
                val result=client.connectGuestUser(
                    trimmedUserName,trimmedUserName
                ).await()
                if (result.isError)
                {
                    _logInEvent.emit(LogInEvent.ErrorLogIn(result.error().message ?: "Unknown Error!"))
                    return@launch
                }
                _logInEvent.emit(LogInEvent.Success)
            }
            else
            {
                _logInEvent.emit(LogInEvent.ErrorInputTooShort)
            }
        }
    }

    sealed class LogInEvent{
        object ErrorInputTooShort : LogInEvent()
        data class ErrorLogIn(val error:String) : LogInEvent()
        object Success :LogInEvent()
    }
}