package com.dpfht.demofbasemvvm.feature_book_inventory.view.list

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.R
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentBookListBinding
import com.dpfht.demofbasemvvm.feature_book_inventory.view.list.adapter.BooksAdapter
import com.dpfht.demofbasemvvm.framework.Constants
import com.dpfht.demofbasemvvm.framework.commons.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookListFragment : BaseFragment<FragmentBookListBinding>(R.layout.fragment_book_list) {

  private val viewModel by viewModels<BookListViewModel>()

  private lateinit var booksAdapter: BooksAdapter

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
        if (viewModel.books.isEmpty()) {
          binding.tvNoData.visibility = View.VISIBLE
          binding.rvBook.visibility = View.INVISIBLE
        } else {
          binding.tvNoData.visibility = View.GONE
          binding.rvBook.visibility = View.VISIBLE
        }

        booksAdapter.notifyDataSetChanged()
      }
    }
  }

  private fun setListener() {
    binding.fabAddBook.setOnClickListener {
      if (viewModel.books.size >= Constants.BOOK_QUOTA) {
        Toast.makeText(requireContext(), "Book quota exceeded", Toast.LENGTH_SHORT).show()
      } else {
        navigationService.navigateInHomeToAddBook()
      }
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
