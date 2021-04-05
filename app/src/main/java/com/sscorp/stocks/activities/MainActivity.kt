package com.sscorp.stocks.activities

import android.annotation.SuppressLint
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.*
import android.util.TypedValue
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.sscorp.stocks.App
import com.sscorp.stocks.R
import com.sscorp.stocks.fragments.StocksFragment
import com.sscorp.stocks.models.Company
import com.sscorp.stocks.viewmodels.CompaniesViewModel
import kotlinx.serialization.ExperimentalSerializationApi

class MainActivity : AppCompatActivity(), Observer<List<Company>> {

    // Выбранная вкладка stocks / favourites
    private var chosenFragment: FragmentEnum = FragmentEnum.Stocks

    // Фрагмент, содержащий список акций
    private var stocksFragment: StocksFragment? = null

    @ExperimentalSerializationApi
    lateinit var viewModel: CompaniesViewModel

    @ExperimentalSerializationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        App.setContext(this)

        initialize(savedInstanceState)
    }

    @ExperimentalSerializationApi
    override fun onStart() {
        super.onStart()

        // Запуск потока для загрузки информации о компаниях
        viewModel.startFetchCompanies()
        // Назначение слушателя
        viewModel.getCompaniesLiveData().observe(this, this)
    }

    /**
     * Обновление данных
     */
    override fun onChanged(t: List<Company>?) {
        stocksFragment?.updateData(t)
    }

    @ExperimentalSerializationApi
    private fun initialize(savedInstanceState: Bundle?) {
        // Инициализация viewModel
        viewModel = ViewModelProvider(this).get(CompaniesViewModel::class.java)
        initFragment(savedInstanceState)
        initSearch()
        initTextViews()
    }

    /**
     * Инициализация фрагмента и добавление на экран
     */
    private fun initFragment(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            stocksFragment = StocksFragment.newInstance(false)

            stocksFragment?.apply {
                supportFragmentManager.beginTransaction()
                    .add(R.id.main_container, this, STOCKS_FRAGMENT_TAG)
                    .commit()
            }
        } else {
            stocksFragment =
                supportFragmentManager.findFragmentByTag(STOCKS_FRAGMENT_TAG) as? StocksFragment
        }
    }

    /**
     * Настройка переключения между вкладками stocks / favourites
     */
    private fun initTextViews() {
        val stocks = findViewById<TextView>(R.id.text_stocks)
        val favourite = findViewById<TextView>(R.id.text_favourites)

        stocks.setOnClickListener {
            if (chosenFragment == FragmentEnum.Stocks)
                return@setOnClickListener
            findViewById<EditText>(R.id.editText_search).text =
                SpannableStringBuilder("")
            transitToStocksFragment(stocks, favourite)
        }

        favourite.setOnClickListener {
            if (chosenFragment == FragmentEnum.Favourites)
                return@setOnClickListener
            findViewById<EditText>(R.id.editText_search).text =
                SpannableStringBuilder("")
            transitToFavouritesFragment(stocks, favourite)
        }
    }

    /**
     * Переключение на вкладку со всеми акциями
     */
    private fun transitToStocksFragment(
        stocks: TextView, favourites: TextView
    ) {
        chosenFragment = FragmentEnum.Stocks
        changeStyles(stocks, favourites)
        stocksFragment = StocksFragment.newInstance(false)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, stocksFragment!!, STOCKS_FRAGMENT_TAG)
            .commit()
    }

    /**
     * Переключение на вкладку с избранными акциями
     */
    private fun transitToFavouritesFragment(stocks: TextView, favourite: TextView) {
        chosenFragment = FragmentEnum.Favourites
        changeStyles(favourite, stocks)
        stocksFragment = StocksFragment.newInstance(true)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_container, stocksFragment!!, STOCKS_FRAGMENT_TAG)
            .commit()
    }


    /**
     * Смена стилей для textView текущей вкладки и textView другой вкладки
     * @param chosenTextView - текущая textView
     * @param otherTextView - другая textView
     */
    private fun changeStyles(chosenTextView: TextView, otherTextView: TextView) {
        chosenTextView.setTextColor(resources.getColor(R.color.black, null))
        otherTextView.setTextColor(resources.getColor(R.color.grey, null))
        chosenTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.focused_size)
        )
        otherTextView.setTextSize(
            TypedValue.COMPLEX_UNIT_PX,
            resources.getDimension(R.dimen.unfocused_size)
        )
    }

    /**
     * Настройка стилей и событий для поисковой строки
     */
    @ExperimentalSerializationApi
    @SuppressLint("ClickableViewAccessibility")
    private fun initSearch() {
        val editTextSearch = findViewById<EditText>(R.id.editText_search)
        editTextSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editTextSearch.hint = ""
            } else {
                editTextSearch.hint = getString(R.string.hint_search)
            }
        }
        editTextSearch.setOnTouchListener { _, event ->
            val sizeBound = 120
            if (editTextSearch.isFocused && event.rawX <= sizeBound) {
                editTextSearch.apply {
                    hideKeyboard()
                    clearFocus()
                    setText("")
                }
                true
            } else false
        }
        editTextSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                stocksFragment?.filterData(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    /**
     * Скрытие клавиатуры
     */
    private fun hideKeyboard() {
        this.currentFocus?.let { view ->
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            imm?.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    @ExperimentalSerializationApi
    override fun onDestroy() {
        super.onDestroy()
        CompaniesViewModel.stop()
    }

    /**
     * Обновление элемента в локальной базе данных
     * @param company - элемент, в котором произошли изменения
     */
    @ExperimentalSerializationApi
    fun updateItem(company: Company) {
        viewModel.updateItemInDb(company)
    }

    enum class FragmentEnum {
        Stocks,
        Favourites
    }

    companion object {
        const val STOCKS_FRAGMENT_TAG = "StocksFragment"
    }
}