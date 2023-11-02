package com.dpfht.demofbasemvvm.framework.commons.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService
import javax.inject.Inject

abstract class BaseFragment<VDB: ViewDataBinding>(
  @LayoutRes protected val contentLayoutId: Int
): Fragment() {

  protected lateinit var binding: VDB

  @Inject
  protected lateinit var navigationService: NavigationService

  override fun onCreateView(
    inflater: LayoutInflater,
    container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = DataBindingUtil.inflate(inflater, contentLayoutId, container, false)

    return binding.root
  }
}
