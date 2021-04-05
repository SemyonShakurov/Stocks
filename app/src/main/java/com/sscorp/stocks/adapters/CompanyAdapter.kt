package com.sscorp.stocks.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.sscorp.stocks.R
import com.sscorp.stocks.callbacks.CompanyCallback
import com.sscorp.stocks.models.Company
import kotlinx.serialization.ExperimentalSerializationApi

class CompanyAdapter(
    private val context: Context,
) : ListAdapter<Company, CompanyViewHolder>(CompanyCallback()) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CompanyViewHolder {
        return CompanyViewHolder(
            context,
            inflater.inflate(R.layout.list_item_company, parent, false)
        )
    }

    @ExperimentalSerializationApi
    override fun onBindViewHolder(holder: CompanyViewHolder, position: Int) {
        holder.bind(getItem(position), position)
    }
}