package com.balaeon.groupchat.ui

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.balaeon.groupchat.ui.channel.ChannelViewModel
import com.baleon.groupchat.R
import com.baleon.groupchat.databinding.DialogChannelNameBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CreateChannelDialog :DialogFragment(){
    private var _binding :DialogChannelNameBinding?=null
    private val binding:DialogChannelNameBinding
    get()=_binding!!

    private val channelViewModel:ChannelViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return binding.root
    }


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding=DialogChannelNameBinding.inflate(layoutInflater)
        return MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.choose_channel_name)
            .setView(binding.root)
            .setPositiveButton(R.string.create){_,_->
                channelViewModel.createChannel(binding.etChannelName.text.toString())
            }
            .setNegativeButton(R.string.cancel){dialogInterface,_->
                dialogInterface.cancel()
            }.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }
}