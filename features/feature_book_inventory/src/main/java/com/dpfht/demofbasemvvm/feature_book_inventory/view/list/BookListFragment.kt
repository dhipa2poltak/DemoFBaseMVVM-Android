package com.dpfht.demofbasemvvm.feature_book_inventory.view.list

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentBookListBinding
import com.dpfht.demofbasemvvm.feature_book_inventory.di.DaggerBookListComponent
import com.dpfht.demofbasemvvm.feature_book_inventory.view.list.adapter.BooksAdapter
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@AndroidEntryPoint
class BookListFragment : Fragment() {

  private lateinit var binding: FragmentBookListBinding
  private val viewModel by viewModels<BookListViewModel>()

  @Inject
  lateinit var navigationService: NavigationService

  private lateinit var booksAdapter: BooksAdapter

  override fun onAttach(context: Context) {
    super.onAttach(context)

    DaggerBookListComponent.builder()
      .context(requireContext())
      .navDependency(EntryPointAccessors.fromActivity(requireActivity(), NavigationServiceDependency::class.java))
      .build()
      .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentBookListBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    setView()
    observeViewModel()
    setListener()

    viewModel.start()
  }

  private fun setView() {
    val layoutManager = LinearLayoutManager(requireContext())
    layoutManager.orientation = LinearLayoutManager.VERTICAL

    booksAdapter = BooksAdapter(viewModel.books, this::onClickBookRow)

    binding.rvBook.layoutManager = layoutManager
    binding.rvBook.adapter = booksAdapter
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

    viewModel.refreshBooksData.observe(viewLifecycleOwner) { isRefresh ->
      if (isRefresh) {
        booksAdapter.notifyDataSetChanged()
      }
    }
  }

  private fun setListener() {
    binding.fabAddBook.setOnClickListener {
      navigationService.navigateInHomeToAddBook()
    }
  }

  private fun onClickBookRow(book: BookEntity) {
    navigationService.navigateInHomeToBookDetails(book.documentId)
  }

  override fun onResume() {
    super.onResume()
    viewModel.getAllBooks()
  }
}
