package com.blockchain.vex.featurecomponents.cryptonewsmodule

import CryptoNewsContract
import com.vex.vextracker.features.BasePresenter
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Pragya Agrawal
 */

class CryptoNewsPresenter(private val cryptoNewsRepository: CryptoNewsRepository) :
    BasePresenter<CryptoNewsContract.View>(), CryptoNewsContract.Presenter {

    /**
     * Load the crypto news from the crypto panic api
     */
    override fun getCryptoNews(coinSymbol: String) {

        currentView?.showOrHideLoadingIndicator(true)

        launch {
            try {
                val cryptoPanicNews = cryptoNewsRepository.getCryptoPanicNews(coinSymbol)
                currentView?.onNewsLoaded(cryptoPanicNews)
            } catch (ex: Exception) {
                Timber.e(ex.localizedMessage)
            } finally {
                currentView?.showOrHideLoadingIndicator(false)
            }
        }
    }
}
