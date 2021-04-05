package com.sscorp.stocks.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sscorp.stocks.api.FinnhubApiService
import com.sscorp.stocks.api.NetworkModule
import com.sscorp.stocks.database.AppDataBase
import com.sscorp.stocks.database.CompanyItem
import com.sscorp.stocks.models.Company
import com.sscorp.stocks.responses.CompaniesResponse
import com.sscorp.stocks.responses.ProfileResponse
import com.sscorp.stocks.responses.StockResponse
import kotlinx.coroutines.*
import kotlinx.serialization.ExperimentalSerializationApi
import retrofit2.HttpException

@ExperimentalSerializationApi
class CompaniesViewModel : ViewModel() {

    // Компании
    private val companiesLiveData: MutableLiveData<List<Company>> = MutableLiveData()

    companion object {
        // Время задержки при неудачном запросе
        private const val DELAY_TIME = 1000L

        private val uiScope = CoroutineScope(Dispatchers.Main)

        // Список компаний в базе данных
        private var companyItems = listOf<CompanyItem>()

        // Список компаний
        val companies: MutableList<Company> = mutableListOf()

        fun stop() {
            if (uiScope.isActive)
                uiScope.cancel()
        }
    }

    fun getCompaniesLiveData(): MutableLiveData<List<Company>> = companiesLiveData

    // Загрузка информации о компаниях
    @ExperimentalSerializationApi
    fun startFetchCompanies() {
        loadCompanies()
        val api = NetworkModule.finnhubApiService
        uiScope.launch {
            fetchCompanies(api)
        }
    }

    /**
     * Обновление элемента в базе данных
     * @param company - компания
     */
    fun updateItemInDb(company: Company) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                AppDataBase.instance.companyDao().updateItem(company.name, company.isFavourite)
            }
        }
    }

    /**
     * Загрузка компаний из базы данных
     */
    private fun loadCompanies() {
        viewModelScope.launch {
            val localCompanies = withContext(Dispatchers.IO) {
                AppDataBase.instance.companyDao().getAll()
            }

            if (localCompanies.isNotEmpty())
                companyItems = localCompanies
        }
    }

    /**
     * Загрузка информации о компаниях с api
     * @param api - api
     */
    private suspend fun fetchCompanies(api: FinnhubApiService) {
        val companiesResponse: CompaniesResponse = getCompanies(api)
        for (ticker in companiesResponse.constituents.sorted()) {
            val company = getCompany(api, ticker) ?: continue
            if (company.icon == "" || companies.contains(company)) continue
            companies.add(company)
            companiesLiveData.value = companies
        }
    }

    /**
     * Получение тикеров компаний с api
     * @param finnhubApiService - api
     */
    private suspend fun getCompanies(finnhubApiService: FinnhubApiService): CompaniesResponse =
        withContext(Dispatchers.IO) {
            try {
                finnhubApiService.getCompanies("^GSPC", NetworkModule.API_KEY)
            } catch (e: Exception) {
                delay(DELAY_TIME)
                getCompanies(finnhubApiService)
            }
        }

    /**
     * Добавление компании в базу данных
     * @param companyItem - компания, которую нужно добавить в бд
     */
    private suspend fun insertToDb(companyItem: CompanyItem) = withContext(Dispatchers.IO) {
        val db = AppDataBase.instance
        db.companyDao().insert(companyItem)
    }

    /**
     * Получение объекта компании
     * @param finnhubApiService - api
     * @param ticker - тикер
     */
    private suspend fun getCompany(
        finnhubApiService: FinnhubApiService,
        ticker: String
    ): Company? {
        if (!companyItems.any { it.ticker == ticker }) { // Если компания загружается в первый раз
            val profileResponse = getProfile(finnhubApiService, ticker)
            val stockResponse = getStock(finnhubApiService, ticker)
            if (profileResponse == null || stockResponse == null)
                return null
            insertToDb(CompanyItem(null, profileResponse.name, profileResponse.logo, ticker))
            val change = stockResponse.currentPrice - stockResponse.previousPrice
            return Company(
                profileResponse.name,
                profileResponse.logo,
                ticker,
                stockResponse.currentPrice,
                change
            )
        } else { // Если компания уже есть в базе данных
            val stockResponse = getStock(finnhubApiService, ticker) ?: return null
            val change = stockResponse.currentPrice - stockResponse.previousPrice
            val companyItem = companyItems.find { it.ticker == ticker }!!
            return Company(
                companyItem.name,
                companyItem.icon,
                ticker,
                stockResponse.currentPrice,
                change,
                companyItem.isFavourite
            )
        }
    }

    /**
     * Получение лого и названия компании с api
     * @param finnhubApiService - api
     * @param ticker - тикер
     */
    private suspend fun getProfile(
        finnhubApiService: FinnhubApiService,
        ticker: String
    ): ProfileResponse? =
        withContext(Dispatchers.IO) {
            try {
                finnhubApiService.getProfile(ticker, NetworkModule.API_KEY)
            } catch (e: HttpException) {
                delay(DELAY_TIME)
                getProfile(finnhubApiService, ticker)
            } catch (e: Exception) {
                null
            }
        }

    /**
     * Получение информации об акциях компании с api
     * @param finnhubApiService - api
     * @param ticker - тикер
     */
    private suspend fun getStock(
        finnhubApiService: FinnhubApiService,
        ticker: String
    ): StockResponse? =
        withContext(Dispatchers.IO) {
            try {
                finnhubApiService.getStock(ticker, NetworkModule.API_KEY)
            } catch (e: HttpException) {
                delay(DELAY_TIME)
                getStock(finnhubApiService, ticker)
            } catch (e: Exception) {
                null
            }
        }
}