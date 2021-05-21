package com.blockchain.vex.featurecomponents.cointickermodule

import CoinTickerContract

import com.vex.vextracker.features.BasePresenter
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManager
import com.vex.vextracker.R
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Pranay Airan
 */

class CoinTickerPresenter(
    private val coinTickerRepository: CoinTickerRepository,
    private val androidResourceManager: AndroidResourceManager
) : BasePresenter<CoinTickerContract.View>(), CoinTickerContract.Presenter {

    /**
     * Load the crypto ticker from the crypto panic api
     */
    override fun getCryptoTickers(coinName: String) {

        var updatedCoinName = coinName

        if (coinName.equals("XRP", true)) {
            updatedCoinName = "ripple"
        }

        currentView?.showOrHideLoadingIndicatorForTicker(true)

        launch {
            try {
                val cryptoTickers = coinTickerRepository.getCryptoTickers(updatedCoinName)
                if (cryptoTickers != null) {
                    currentView?.onPriceTickersLoaded(cryptoTickers)
                }
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
                currentView?.onNetworkError(androidResourceManager.getString(R.string.error_ticker))
            } finally {
                currentView?.showOrHideLoadingIndicatorForTicker(false)
            }
        }
    }
}
