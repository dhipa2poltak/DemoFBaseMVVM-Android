package com.dpfht.demofbasemvvm.feature_book_inventory.view.add_edit

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentAddEditBookBinding
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditBookFragment : Fragment() {

  private lateinit var binding: FragmentAddEditBookBinding
  private val viewModel by viewModels<AddEditBookViewModel>()

  override fun onCreateView(
    inflater: LayoutInflater, container: ViewGroup?,
    savedInstanceState: Bundle?
  ): View {
    binding = FragmentAddEditBookBinding.inflate(inflater, container, false)

    return binding.root
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeViewModel()
    setListener()

    arguments?.let {
      viewModel.theBook = it.getSerializable("book_arg") as BookEntity?
      resetFormBook()
    }

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

    viewModel.resetFormBook.observe(viewLifecycleOwner) { isReset ->
      if (isReset) {
        resetFormBook()
      }
    }

    viewModel.closeScreenData.observe(viewLifecycleOwner) { isClose ->
      if (isClose) {
        findNavController().navigateUp()
      }
    }
  }

  private fun setListener() {
    binding.btnSubmit.setOnClickListener {
      if (isValidInputForm()) {
        val title = binding.etBookTitle.text.toString().trim()
        val writer = binding.etBookWriter.text.toString().trim()
        val desc = binding.etBookDescription.text.toString().trim()
        val stock = binding.etBookStock.text.toString().trim().toInt()

        if (viewModel.theBook == null) {
          viewModel.addBook(title, writer, desc, stock)
        } else {
          viewModel.updateBook(title, writer, desc, stock)
        }
      }
    }

    binding.btnReset.setOnClickListener {
      resetFormBook()
    }
  }

  private fun isValidInputForm(): Boolean {
    var retval = true

    if (binding.etBookTitle.text.toString().trim().isEmpty()) {
      binding.etBookTitle.error = "this field is required"
      retval = false
    }

    if (binding.etBookWriter.text.toString().trim().isEmpty()) {
      binding.etBookWriter.error = "this field is required"
      retval = false
    }

    if (binding.etBookDescription.text.toString().trim().isEmpty()) {
      binding.etBookDescription.error = "this field is required"
      retval = false
    }

    if (binding.etBookStock.text.toString().trim().isEmpty()) {
      binding.etBookStock.error = "this field is required"
      retval = false
    }

    return retval
  }

  private fun resetFormBook() {
    if (viewModel.theBook != null) {
      binding.tvTitleScreen.text = "Update Book"

      viewModel.theBook?.let {
        binding.etBookTitle.setText(it.title)
        binding.etBookWriter.setText(it.writer)
        binding.etBookDescription.setText(it.description)
        binding.etBookStock.setText("${it.stock}")

        binding.etBookTitle.requestFocus()
      }
    } else {
      binding.tvTitleScreen.text = "Add Book"

      binding.etBookTitle.setText("")
      binding.etBookWriter.setText("")
      binding.etBookDescription.setText("")
      binding.etBookStock.setText("")

      binding.etBookTitle.requestFocus()
    }
  }
}
