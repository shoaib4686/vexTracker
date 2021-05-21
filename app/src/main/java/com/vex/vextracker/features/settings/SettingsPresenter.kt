package com.blockchain.vex.features.settings

import SettingsContract
import com.vex.vextracker.data.database.entities.WatchedCoin
import com.vex.vextracker.features.BasePresenter
import com.vex.vextracker.features.CryptoCompareRepository
import com.vex.vextracker.network.models.getCoinFromCCCoin
import com.vex.vextracker.utils.defaultExchange
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class SettingsPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<SettingsContract.View>(), SettingsContract.Presenter {

    override fun refreshCoinList(defaultCurrency: String) {
        launch {
            try {
                val allCoinsFromAPI = coinRepo.getAllCoinsFromAPI()
                val coinList: MutableList<WatchedCoin> = mutableListOf()
                val ccCoinList = allCoinsFromAPI.first
                ccCoinList.forEach { ccCoin ->
                    val coinInfo = allCoinsFromAPI.second[ccCoin.symbol.toLowerCase()]
                    coinList.add(getCoinFromCCCoin(ccCoin, defaultExchange, defaultCurrency, coinInfo))
                }
                Timber.d("Inserted all coins in db with size ${coinList.size}")
                currentView?.onCoinListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }

    override fun refreshExchangeList() {
        launch {
            try {
                coinRepo.insertExchangeIntoList(coinRepo.getExchangeInfo())
                Timber.d("all exchanges loaded and inserted into db")
                currentView?.onExchangeListRefreshed()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(ex.localizedMessage ?: "")
            }
        }
    }
}
