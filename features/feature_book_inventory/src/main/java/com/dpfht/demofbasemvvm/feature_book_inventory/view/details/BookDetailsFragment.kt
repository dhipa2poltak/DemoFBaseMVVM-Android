package com.dpfht.demofbasemvvm.feature_book_inventory.view.details

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentBookDetailsBinding
import com.dpfht.demofbasemvvm.feature_book_inventory.di.DaggerBookDetailsComponent
import com.dpfht.demofbasemvvm.framework.di.dependency.NavigationServiceDependency
import com.dpfht.demofbasemvvm.framework.navigation.NavigationService
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import javax.inject.Inject

@AndroidEntryPoint
class BookDetailsFragment : Fragment() {

  private lateinit var binding: FragmentBookDetailsBinding
  private val viewModel by viewModels<BookDetailsViewModel>()

  @Inject
  lateinit var navigationService: NavigationService

  private var bookId = ""

  override fun onAttach(context: Context) {
    super.onAttach(context)

    DaggerBookDetailsComponent.builder()
      .context(requireContext())
      .navDependency(EntryPointAccessors.fromActivity(requireActivity(), NavigationServiceDependency::class.java))
      .build()
      .inject(this)
  }

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentBookDetailsBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeViewModel()
    setListener()

    arguments?.let {
      bookId = it.getString("book_id") ?: ""
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
        findNavController().navigateUp()
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
  }
}
