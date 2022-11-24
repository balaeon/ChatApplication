package com.balaeon.groupchat.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.activity.addCallback
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.viewbinding.ViewBinding
import com.balaeon.groupchat.ui.BindingFragment
import com.baleon.groupchat.databinding.FragmentChatBinding
import com.getstream.sdk.chat.viewmodel.MessageInputViewModel
import com.getstream.sdk.chat.viewmodel.messages.MessageListViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.getstream.chat.android.ui.message.input.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.header.viewmodel.MessageListHeaderViewModel
import io.getstream.chat.android.ui.message.list.header.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.bindView
import io.getstream.chat.android.ui.message.list.viewmodel.factory.MessageListViewModelFactory

@AndroidEntryPoint
class ChatFragment : BindingFragment<FragmentChatBinding> (){

    private val args : ChatFragmentArgs by navArgs()

    override val bindingInflater: (LayoutInflater) -> ViewBinding
        get() = FragmentChatBinding::inflate

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val factory =MessageListViewModelFactory(args.channelId)
        val messageListHeaderViewModel:MessageListHeaderViewModel by viewModels { factory }
        val messageListViewModel: MessageListViewModel by viewModels { factory }
        val messageListInputViewModel:MessageInputViewModel by viewModels { factory }
        messageListHeaderViewModel.bindView(binding.messageListHeaderView,viewLifecycleOwner)
        messageListViewModel.bindView(binding.messageListView,viewLifecycleOwner)
        messageListInputViewModel.bindView(binding.messageInputView,viewLifecycleOwner)

        messageListViewModel.mode.observe(viewLifecycleOwner){ mode->
            when(mode)
            {
                is MessageListViewModel.Mode.Thread->{
                    messageListHeaderViewModel.setActiveThread(mode.parentMessage)
                    messageListInputViewModel.setActiveThread(mode.parentMessage)
                }
                is MessageListViewModel.Mode.Normal->{
                    messageListHeaderViewModel.resetThread()
                    messageListInputViewModel.resetThread()
                }
            }

        }

        binding.messageListView.setMessageEditHandler(messageListInputViewModel::postMessageToEdit)

        messageListViewModel.state.observe(viewLifecycleOwner) { state ->
            when(state)
            {
                is MessageListViewModel.State.NavigateUp->{
                    findNavController().navigateUp()
                }
                MessageListViewModel.State.Loading -> {
                    //future enhancement
                }
                is MessageListViewModel.State.Result -> {
                    //future enhancement
                }
            }
        }

        val backHandler={
            messageListViewModel.onEvent(MessageListViewModel.Event.BackButtonPressed)
        }
        binding.messageListHeaderView.setBackButtonClickListener(backHandler)
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner){
            backHandler()
        }
    }
}