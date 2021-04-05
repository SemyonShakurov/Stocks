package com.sscorp.stocks.adapters

import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.ImageViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.sscorp.stocks.R
import com.sscorp.stocks.models.Company
import coil.load
import com.sscorp.stocks.activities.MainActivity
import com.sscorp.stocks.viewmodels.CompaniesViewModel
import kotlinx.serialization.ExperimentalSerializationApi

class CompanyViewHolder(private val context: Context, private val view: View) :
    RecyclerView.ViewHolder(view) {

    private val companyIconView = view.findViewById<ImageView>(R.id.image_company_icon)
    private val tickerView = view.findViewById<TextView>(R.id.text_company_ticker)
    private val companyNameView = view.findViewById<TextView>(R.id.text_company_name)
    private val stockPriceView = view.findViewById<TextView>(R.id.text_stock_price)
    private val changePriceView = view.findViewById<TextView>(R.id.text_change_price)
    private val starIconView = view.findViewById<ImageView>(R.id.image_star)

    @ExperimentalSerializationApi
    fun bind(company: Company, position: Int) {
        setStyle(company, position)

        // загрузка лого компании
        companyIconView.load(company.icon) {
            crossfade(true)
        }

        // Вывод информации
        tickerView.text = company.ticker
        companyNameView.text = nameOutput(company.name)
        stockPriceView.text = priceOutput(company.cost)
        changePriceView.text = changePriceOutput(company.priceChange, company.cost)

        // Добавление / удаление в избранные акции
        starIconView.setOnClickListener {
            if (company.isFavourite) {
                company.isFavourite = false
                setStarIconTint(R.color.grey)
                Toast.makeText(context, "Removed from favourites", Toast.LENGTH_SHORT).show()
            } else {
                company.isFavourite = true
                setStarIconTint(R.color.yellow)
                Toast.makeText(context, "Added to favourites", Toast.LENGTH_SHORT).show()
            }
            (context as MainActivity).onChanged(CompaniesViewModel.companies)
            context.updateItem(company)
        }
    }

    /**
     * Форматирование строки с именем компании
     * @param name - имя компании
     */
    private fun nameOutput(name: String): String {
        if (name.length > 20)
            return name.substring(0..17).trim() + "..."
        return name
    }

    /**
     * форматирование строки с ценой акции
     * @param price - цена акции
     */
    private fun priceOutput(price: Double): String {
        return String.format("$%.2f", price)
    }

    /**
     * Форматирование строки с изменением акции
     * @param changePrice - Изменение цены акции
     * @param price - текущая цена акции
     */
    private fun changePriceOutput(changePrice: Double, price: Double): String {
        val percents = changePrice / ((price - changePrice) / 100.0)
        return if (changePrice >= 0) {
            String.format("+$%.2f (%.2f%%)", changePrice, percents)
        } else {
            String.format("-$%.2f (%.2f%%)", -changePrice, -percents)
        }
    }

    /**
     * Стиль элемента списка
     * @param company - Компания
     * @param position - Позиция компании
     */
    private fun setStyle(company: Company, position: Int) {
        if (position % 2 == 0) {
            view.setBackgroundResource(R.color.back_item)
        } else {
            view.setBackgroundResource(R.color.white)
        }

        if (company.priceChange < 0) {
            changePriceView
                .setTextColor(context.resources.getColor(R.color.red, null))
        } else changePriceView
            .setTextColor(context.resources.getColor(R.color.green, null))

        if (!company.isFavourite) {
            setStarIconTint(R.color.grey)
        } else {
            setStarIconTint(R.color.yellow)
        }
    }

    /**
     * Изменение цвета звезды
     * @param colorId - id цвета звезды
     */
    private fun setStarIconTint(colorId: Int) {
        ImageViewCompat.setImageTintList(
            starIconView, ColorStateList.valueOf(
                ContextCompat.getColor(context, colorId)
            )
        )
    }
}