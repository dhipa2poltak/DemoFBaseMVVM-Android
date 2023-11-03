package com.dpfht.demofbasemvvm.feature_book_inventory.view.details

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.R
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentBookDetailsBinding
import com.dpfht.demofbasemvvm.framework.commons.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookDetailsFragment : BaseFragment<FragmentBookDetailsBinding>(R.layout.fragment_book_details) {

  private val viewModel by viewModels<BookDetailsViewModel>()

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeViewModel()
    setListener()

    arguments?.let {
      val bookId = it.getString("book_id") ?: ""
      if (bookId.isNotEmpty()) {
        viewModel.bookId = bookId
        viewModel.start()
      }
    }
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

    viewModel.bookData.observe(viewLifecycleOwner) { book ->
      showBookDetails(book)
    }

    viewModel.closeScreenData.observe(viewLifecycleOwner) { isClose ->
      if (isClose) {
        navigationService.navigateUp()
      }
    }
  }

  private fun setListener() {
    binding.btnUpdate.setOnClickListener {
      viewModel.theBook?.let {
        navigationService.navigateInHomeFromDetailsToEditBook(it)
      }
    }

    binding.btnDelete.setOnClickListener {
      showDeleteBookConfirmation()
    }
  }

  private fun showDeleteBookConfirmation() {
    val str = "Are you sure you want to delete this book?"

    AlertDialog.Builder(requireContext())
      .setTitle("Message")
      .setMessage(str)
      .setPositiveButton("Yes") { dialog, _ ->
        dialog.dismiss()
        viewModel.deleteBook()
      }
      .setNegativeButton("No") { dialog, _ ->
        dialog.dismiss()
      }.show()
  }

  private fun showBookDetails(book: BookEntity) {
    binding.tvBookTitle.text = book.title
    binding.tvBookWriter.text = "written by ${book.writer}"
    binding.tvBookStock.text = "Stock: ${book.stock}"
    binding.tvBookDescription.text = book.description

    Picasso.get().load(book.urlImage).into(binding.ivBookImage)
  }
}
