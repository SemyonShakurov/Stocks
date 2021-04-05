package com.sscorp.stocks.callbacks

import androidx.recyclerview.widget.DiffUtil
import com.sscorp.stocks.models.Company

// Для вычисления инструкций по обновлению списка
class CompanyCallback : DiffUtil.ItemCallback<Company>() {

    override fun areItemsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: Company, newItem: Company): Boolean {
        return oldItem == newItem
    }
}