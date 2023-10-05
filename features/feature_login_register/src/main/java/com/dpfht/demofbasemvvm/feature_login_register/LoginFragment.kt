package com.dpfht.demofbasemvvm.feature_login_register

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.feature_login_register.databinding.FragmentLoginBinding
import com.dpfht.demofbasemvvm.feature_login_register.di.DaggerLoginComponent
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

  private lateinit var binding: FragmentLoginBinding
  private val viewModel by viewModels<LoginViewModel>()

  @Inject
  lateinit var navigationService: NavigationService

  override fun onAttach(context: Context) {
    super.onAttach(context)

    DaggerLoginComponent.builder()
      .context(requireContext())
      .navDependency(EntryPointAccessors.fromActivity(requireActivity(), NavigationServiceDependency::class.java))
      .build()
      .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentLoginBinding.inflate(inflater, container, false)

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
      Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    viewModel.modalMessage.observe(viewLifecycleOwner) { msg ->
      Snackbar.make(binding.root, msg, Snackbar.LENGTH_SHORT).show()
    }

    viewModel.configData.observe(viewLifecycleOwner) {
      if (it.isNotEmpty()) {
        binding.tvTitleScreen.text = it
      }
    }

    viewModel.doEnterVerificationCode.observe(viewLifecycleOwner) {
      if (it) {
        binding.btnLogin.visibility = View.INVISIBLE
        //binding.btnGoogleSignIn.visibility = View.GONE
        //binding.tvOr.visibility = View.GONE

        binding.btnVerify.visibility = View.VISIBLE
        binding.btnResendCode.visibility = View.VISIBLE
        binding.etVerificationCode.visibility = View.VISIBLE
      }
    }

    viewModel.doNavigateToHome.observe(viewLifecycleOwner) {
      if (it) {
        navigationService.navigateToHome()
      }
    }
  }

  private fun setListener() {
    binding.btnGoogleSignIn.setOnClickListener {
      viewModel.signInWithGoogle()
    }

    binding.btnLogin.setOnClickListener {
      if (isValidPhoneNumber()) {
        val phoneNumber = "+${binding.etPhoneNumber.text}"

        viewModel.startLoginWithPhoneNumber(phoneNumber)
      }
    }

    binding.btnVerify.setOnClickListener {
      val code = binding.etVerificationCode.text.toString()
      if (TextUtils.isEmpty(code)) {
        binding.etVerificationCode.error = "Cannot be empty."
        return@setOnClickListener
      }

      viewModel.verifyPhoneNumber(code)
    }

    binding.btnResendCode.setOnClickListener {
      viewModel.resendVerificationCode()
    }
  }

  private fun isValidPhoneNumber(): Boolean {
    val phoneNumber = binding.etPhoneNumber.text.toString()

    if (TextUtils.isEmpty(phoneNumber)) {
      binding.etPhoneNumber.error = "Invalid phone number."
      return false
    } else if (!phoneNumber.isDigitsOnly()) {
      binding.etPhoneNumber.error = "Invalid phone number."
      return false
    }

    return true
  }
}
