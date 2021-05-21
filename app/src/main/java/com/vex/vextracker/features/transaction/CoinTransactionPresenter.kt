package com.blockchain.vex.features.transaction

import CoinTransactionContract
import com.blockchain.vex.data.database.entities.CoinTransaction
import com.vex.vextracker.features.BasePresenter
import com.vex.vextracker.features.CryptoCompareRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinTransactionPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinTransactionContract.View>(), CoinTransactionContract.Presenter {

    override fun getAllSupportedExchanges() {
        launch {
            try {
                currentView?.onAllSupportedExchangesLoaded(coinRepo.getAllSupportedExchanges())
                Timber.d("All Exchange Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    // to coins is , separated multiple coin list.
    override fun getPriceForPair(fromCoin: String, toCoin: String, exchange: String, timeStamp: String) {
        if (exchange.isNotEmpty()) {
            launch {
                try {
                    currentView?.onCoinPriceLoaded(coinRepo.getCoinPriceForTimeStamp(fromCoin, toCoin, exchange, timeStamp))
                } catch (ex: Exception) {
                    Timber.e(ex.localizedMessage)
                }
            }
        }
    }

    override fun addTransaction(transaction: CoinTransaction) {
        launch {
            try {
                coinRepo.insertTransaction(transaction)
                Timber.d("Coin Transaction Added")
                currentView?.onTransactionAdded()
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
