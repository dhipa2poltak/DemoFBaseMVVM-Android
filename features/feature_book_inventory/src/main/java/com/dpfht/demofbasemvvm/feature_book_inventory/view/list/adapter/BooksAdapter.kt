package com.dpfht.demofbasemvvm.feature_book_inventory.view.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dpfht.demofbasemvvm.domain.entity.BookEntity
import com.dpfht.demofbasemvvm.feature_book_inventory.databinding.LayoutRowBookBinding
import com.dpfht.demofbasemvvm.feature_book_inventory.view.list.adapter.BooksAdapter.BooksViewHolder
import com.squareup.picasso.Picasso

class BooksAdapter(
  private val books: List<BookEntity>,
  private val onClickBookRow: ((BookEntity) -> Unit)
): RecyclerView.Adapter<BooksViewHolder>() {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
    val binding = LayoutRowBookBinding.inflate(LayoutInflater.from(parent.context), parent, false)

    return BooksViewHolder(binding)
  }

  override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
    val book = books[position]

    holder.binding.tvTitleValue.text = book.title
    holder.binding.tvWriterValue.text = book.writer
    holder.binding.tvStockValue.text = "${book.stock}"

    Picasso.get().load(book.urlImage).into(holder.binding.ivBook)

    holder.binding.root.setOnClickListener {
      onClickBookRow(book)
    }
  }

  override fun getItemCount() = books.size

  class BooksViewHolder(val binding: LayoutRowBookBinding): RecyclerView.ViewHolder(binding.root)
}
