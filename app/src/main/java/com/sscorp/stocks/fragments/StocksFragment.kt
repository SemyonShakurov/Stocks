package com.sscorp.stocks.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.sscorp.stocks.R
import com.sscorp.stocks.adapters.CompanyAdapter
import com.sscorp.stocks.models.Company
import com.sscorp.stocks.viewmodels.CompaniesViewModel
import kotlinx.serialization.ExperimentalSerializationApi
import java.util.*

class StocksFragment : Fragment() {

    companion object {
        private const val BOOLEAN_ARG = "Boolean"

        // Создание объекта фрагмента
        fun newInstance(isFavourites: Boolean): StocksFragment {
            val args = Bundle()
            args.putBoolean(BOOLEAN_ARG, isFavourites)
            val fragment = StocksFragment()
            fragment.arguments = args
            return fragment
        }
    }

    // Адаптер для списка акций
    private var adapter: CompanyAdapter? = null

    // Текущее состояние для вывода акций
    private var state = STATE.LOADING

    @ExperimentalSerializationApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        adapter = CompanyAdapter(activity!!)
        val listContainer = inflater.inflate(R.layout.fragment_stocks, container, false)
        val list = listContainer.findViewById<RecyclerView>(R.id.list_companies)
        updateData(CompaniesViewModel.companies)
        list.adapter = adapter
        return listContainer
    }

    /**
     * Обновление данных в списке
     * @param t - новый список
     */
    fun updateData(t: List<Company>?) {
        if (adapter == null || state == STATE.SEARCHING)
            return
        val list = if (!arguments?.getBoolean(BOOLEAN_ARG)!!) t?.toList() else t?.toList()
            ?.filter { it.isFavourite }
        adapter!!.submitList(list)
    }

    /**
     * Поиск акций по введенной строке
     * @param text - введенная строка
     */
    @ExperimentalSerializationApi
    fun filterData(text: String) {
        if (adapter == null)
            return
        state = STATE.SEARCHING
        adapter!!.submitList(listOf())
        val resultList: MutableList<Company>
        if (text.isEmpty()) {
            resultList = CompaniesViewModel.companies.toMutableList()
            state = STATE.LOADING
        } else {
            resultList = mutableListOf()
            for (company in CompaniesViewModel.companies) {
                if (company.ticker.toLowerCase(Locale.ROOT)
                        .contains(text.toLowerCase(Locale.ROOT)) ||
                    company.name.toLowerCase(Locale.ROOT)
                        .contains(text.toLowerCase(Locale.ROOT))
                )
                    resultList.add(company)
            }
        }

        if (!arguments?.getBoolean(BOOLEAN_ARG)!!)
            adapter!!.submitList(resultList)
        else adapter!!.submitList(resultList.filter { it.isFavourite })
    }

    enum class STATE {
        LOADING,
        SEARCHING
    }
}