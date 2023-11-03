package com.dpfht.demofbasemvvm.feature_push_message

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.feature_push_message.databinding.FragmentPushMessageBinding
import com.dpfht.demofbasemvvm.framework.commons.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PushMessageFragment : BaseFragment<FragmentPushMessageBinding>(R.layout.fragment_push_message) {

  companion object {
    private val permissions = arrayListOf<String>()
  }

  private val viewModel by viewModels<PushMessageViewModel>()

  private var dialogPerm: AlertDialog? = null
  private var isAlertResult = false

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissions.add(Manifest.permission.POST_NOTIFICATIONS)
    }
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
        binding.tvQuota.text = "Your push message quota for today is $count"
      } else {
        binding.btnSend.isEnabled = false
        binding.tvQuota.text = "You don't have any push message quota for today"
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

  override fun onResume() {
    super.onResume()

    if (permissions.isNotEmpty() && !isAllPermissionsGranted()) {
      doRequestPermissions()
    }
  }

  override fun onPause() {
    super.onPause()

    try {
      isAlertResult = false
      dialogPerm?.dismiss()
    } catch (e: Exception) {
      e.printStackTrace()
    }
  }

  private fun isAllPermissionsGranted(): Boolean {
    return permissions.all { ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED }
  }

  private fun doRequestPermissions() {
    if (isAlertResult) return

    val builder = AlertDialog.Builder(requireContext())
    val msg = resources.getString(R.string.push_message_msg_permission_notification)
    builder.setMessage(msg).setTitle(resources.getString(R.string.push_message_text_title_perm))
    builder.setPositiveButton(resources.getString(R.string.push_message_text_ok)) {
        _, _ ->

      dialogPerm?.dismiss()
      requestMultiplePermission.launch(permissions.toTypedArray())
    }

    builder.setCancelable(false)
    dialogPerm = builder.create()
    dialogPerm?.show()
  }

  private val requestMultiplePermission = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
    if (!isAllPermissionsGranted()) {
      isAlertResult = true

      val builder = AlertDialog.Builder(requireContext())
      builder.setMessage(resources.getString(R.string.push_message_msg_not_granted)).setTitle(resources.getString(R.string.push_message_text_title_perm))
      builder.setPositiveButton(resources.getString(R.string.push_message_text_exit)) {
          _, _ ->

        dialogPerm?.dismiss()
        isAlertResult = false
        requireActivity().finish()
      }
      builder.setNegativeButton(resources.getString(R.string.push_message_text_open_settings)) {
          _, _ ->

        dialogPerm?.dismiss()
        isAlertResult = false
        openSettings()
      }

      builder.setCancelable(false)
      dialogPerm = builder.create()
      dialogPerm?.show()
    }
  }

  private fun openSettings() {
    val itn = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
    val uri = Uri.fromParts("package", requireContext().packageName, null)
    itn.data = uri
    startActivity(itn)
  }
}
