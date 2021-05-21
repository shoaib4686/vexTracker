package com.blockchain.vex.features.coinsearch

import CoinDiscoveryContract
import com.vex.vextracker.features.BasePresenter
import com.vex.vextracker.features.CryptoCompareRepository
import kotlinx.coroutines.launch
import timber.log.Timber

/**
Created by Pranay Airan
 */

class CoinDiscoveryPresenter(
    private val coinRepo: CryptoCompareRepository
) : BasePresenter<CoinDiscoveryContract.View>(),
    CoinDiscoveryContract.Presenter {

    override fun getTopCoinListByMarketCap(toCurrencySymbol: String) {
        launch {
            try {
                currentView?.onTopCoinsByTotalVolumeLoaded(coinRepo.getTopCoinsByTotalVolume(toCurrencySymbol))
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getTopCoinListByPairVolume() {

        launch {
            try {
                currentView?.onTopCoinListByPairVolumeLoaded(coinRepo.getTopPairsByTotalVolume("BTC"))
                Timber.d("Top coins by pair Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }

    override fun getCryptoCurrencyNews() {
        launch {
            try {
                val topNewsFromCryptoCompare = coinRepo.getTopNewsFromCryptoCompare()
                currentView?.onCoinNewsLoaded(topNewsFromCryptoCompare)
                Timber.d("All news Loaded")
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            }
        }
    }
}
