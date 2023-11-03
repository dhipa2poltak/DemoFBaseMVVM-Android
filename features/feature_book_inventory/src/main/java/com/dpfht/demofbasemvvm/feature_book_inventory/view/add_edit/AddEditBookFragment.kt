package com.dpfht.demofbasemvvm.feature_book_inventory.view.add_edit

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia.ImageOnly
import androidx.fragment.app.viewModels
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.R
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.FragmentAddEditBookBinding
import com.dpfht.demofbasemvvm.framework.commons.base.BaseFragment
import com.google.android.material.snackbar.Snackbar
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddEditBookFragment : BaseFragment<FragmentAddEditBookBinding>(R.layout.fragment_add_edit_book) {

  private val viewModel by viewModels<AddEditBookViewModel>()

  private val pickMedia = registerForActivityResult(PickVisualMedia()) { uri ->
    if (uri != null) {
      viewModel.uriLocalImage = uri
      binding.ivBookImage.setImageURI(uri)
      binding.tvNoBookImage.visibility = View.GONE
    }
  }

  override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    super.onViewCreated(view, savedInstanceState)

    observeViewModel()
    setListener()

    arguments?.let {
      viewModel.theBook = it.getSerializable("book_arg") as BookEntity?
    }

    resetFormBook()
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
        navigationService.navigateUp()
      }
    }

    viewModel.isQuotaExceeded.observe(viewLifecycleOwner) { isQuotaExceeded ->
      if (isQuotaExceeded) {
        binding.btnSubmit.isEnabled = false
        Toast.makeText(requireContext(), "Book quota exceeded", Toast.LENGTH_SHORT).show()
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

    binding.ivBookImage.setOnClickListener {
      pickMedia.launch(PickVisualMediaRequest(ImageOnly))
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

    if (viewModel.theBook == null && viewModel.uriLocalImage == null) {
      Toast.makeText(requireContext(), "please select the book image", Toast.LENGTH_SHORT).show()
      retval = false
    }

    return retval
  }

  private fun resetFormBook() {
    viewModel.uriLocalImage = null

    if (viewModel.theBook != null) {
      binding.tvTitleScreen.text = "Update Book"

      viewModel.theBook?.let {
        binding.etBookTitle.setText(it.title)
        binding.etBookWriter.setText(it.writer)
        binding.etBookDescription.setText(it.description)
        binding.etBookStock.setText("${it.stock}")

        binding.etBookTitle.requestFocus()

        Picasso.get().load(it.urlImage).into(binding.ivBookImage)
        binding.tvNoBookImage.visibility = View.GONE
      }
    } else {
      binding.tvTitleScreen.text = "Add Book"

      binding.etBookTitle.setText("")
      binding.etBookWriter.setText("")
      binding.etBookDescription.setText("")
      binding.etBookStock.setText("")

      binding.etBookTitle.requestFocus()

      binding.ivBookImage.setImageURI(null)
      binding.tvNoBookImage.visibility = View.VISIBLE

      viewModel.onResetAddingBookForm()
    }
  }
}
