package com.dpfht.demofbasemvvm.feature_push_message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.feature_push_message.databinding.FragmentPushMessageBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PushMessageFragment : Fragment() {

  private lateinit var binding: FragmentPushMessageBinding
  private val viewModel by viewModels<PushMessageViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentPushMessageBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeViewModel()
    setListener()

    viewModel.start()
  }

  private fun observeViewModel() {
    viewModel.isShowDialogLoading.observe(viewLifecycleOwner) { isShow ->
      binding.clProgressBar.visibility = if (isShow) View.VISIBLE else View.GONE
    }

    viewModel.toastMessage.observe(viewLifecycleOwner) { msg ->
      if (msg.isNotEmpty()) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
      }
    }

    viewModel.modalMessage.observe(viewLifecycleOwner) { msg ->
      if (msg.isNotEmpty()) {
        Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
      }
    }

    viewModel.fcmTokenData.observe(viewLifecycleOwner) {
      binding.etToken.setText(it)
    }

    viewModel.pushMessageData.observe(viewLifecycleOwner) {
      val title = it.first.ifEmpty { "-" }
      val message = it.second.ifEmpty { "-" }

      binding.etReceivedTitle.setText(title)
      binding.etReceivedMessage.setText(message)
    }

    viewModel.fcmQuotaData.observe(viewLifecycleOwner) { count ->
      if (count > 0) {
        binding.btnSend.isEnabled = true
        binding.tvQuota.setText("Your push message quota for today is $count")
      } else {
        binding.btnSend.isEnabled = false
        binding.tvQuota.setText("You don't have any push message quota for today")
      }
    }
  }

  private fun setListener() {
    binding.btnSend.setOnClickListener {
      if (isValidInputMessage()) {
        viewModel.postFCMMessage(binding.etToken.text.toString(), binding.etTitle.text.toString(), binding.etMessage.text.toString())
      }
    }

    binding.btnClear.setOnClickListener {
      binding.etTitle.setText("")
      binding.etMessage.setText("")
      binding.etTitle.requestFocus()
    }

    binding.btnClearReceivedData.setOnClickListener {
      binding.etReceivedTitle.setText("-")
      binding.etReceivedMessage.setText("-")
    }
  }

  private fun isValidInputMessage(): Boolean {
    var retval = true

    if (binding.etTitle.text.toString().trim().isEmpty()) {
      binding.etTitle.error = "this field is required"
      retval = false
    }

    if (binding.etMessage.text.toString().trim().isEmpty()) {
      binding.etMessage.error = "this field is required"
      retval = false
    }

    return retval
  }
}
