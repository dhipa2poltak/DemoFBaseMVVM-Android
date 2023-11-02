package com.dpfht.demofbasemvvm.feature_user_profile

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.feature_user_profile.databinding.FragmentUserProfileBinding
import com.dpfht.demofbasemvvm.framework.commons.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UserProfileFragment : BaseFragment<FragmentUserProfileBinding>(R.layout.fragment_user_profile) {

  private val viewModel by viewModels<UserProfileViewModel>()

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

    viewModel.userProfileData.observe(viewLifecycleOwner) { userProfile ->
      binding.tvDisplayName.text = userProfile.displayName.ifEmpty { "-" }
      binding.tvEmail.text = userProfile.email.ifEmpty { "-" }
      binding.tvPhoneNumber.text = userProfile.phoneNumber.ifEmpty { "-" }

      if (userProfile.photoUrl.isNotEmpty()) {
        Picasso.get()
          .load(userProfile.photoUrl)
          .error(R.drawable.ic_account)
          .into(binding.ivProfile)
      }
    }

    viewModel.doNavigateToLoginData.observe(viewLifecycleOwner) {
      if (it) {
        navigationService.navigateToLogin()
      }
    }
  }

  private fun setListener() {
    binding.btnLogout.setOnClickListener {
      viewModel.logout()
    }
  }
}
