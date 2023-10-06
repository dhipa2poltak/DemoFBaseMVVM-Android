package com.dpfht.demofbasemvvm.feature_user_profile

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.feature_user_profile.databinding.FragmentUserProfileBinding
import com.dpfht.demofbasemvvm.feature_user_profile.di.DaggerUserProfileComponent
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment() {

  private lateinit var binding: FragmentUserProfileBinding
  private val viewModel by viewModels<UserProfileViewModel>()

  @Inject
  lateinit var navigationService: NavigationService

  override fun onAttach(context: Context) {
    super.onAttach(context)

    DaggerUserProfileComponent.builder()
      .context(requireContext())
      .navDependency(EntryPointAccessors.fromActivity(requireActivity(), NavigationServiceDependency::class.java))
      .build()
      .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentUserProfileBinding.inflate(inflater, container, false)

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
