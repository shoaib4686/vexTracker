package com.blockchain.vex.features.ticker

import CoinTickerContract
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.vex.vextracker.data.PreferenceManager
import com.blockchain.vex.epoxymodels.coinTickerView
import com.blockchain.vex.featurecomponents.cointickermodule.CoinTickerPresenter
import com.blockchain.vex.featurecomponents.cointickermodule.CoinTickerRepository
import com.blockchain.vex.network.models.CryptoTicker
import com.blockchain.vex.utils.Formaters
import com.blockchain.vex.utils.getUrlWithoutParameters
import com.blockchain.vex.utils.openCustomTab
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManagerImpl
import com.google.android.material.snackbar.Snackbar
import com.vex.vextracker.CoinBitApplication
import com.vex.vextracker.R
import kotlinx.android.synthetic.main.activity_coin_ticker_list.*
import java.util.*

/**
 * Created by Pranay Airan
 * Activity showing all ticker data
 */
class CoinTickerActivity : AppCompatActivity(), CoinTickerContract.View {

    companion object {
        private const val COIN_NAME = "COIN_FULL_NAME"

        @JvmStatic
        fun buildLaunchIntent(context: Context, coinName: String): Intent {
            val intent = Intent(context, CoinTickerActivity::class.java)
            intent.putExtra(COIN_NAME, coinName)
            return intent
        }
    }

    private val coinTickerRepository by lazy {
        CoinTickerRepository(CoinBitApplication.database)
    }

    private val androidResourceManager by lazy {
        AndroidResourceManagerImpl(this)
    }

    private val formatter: Formaters by lazy {
        Formaters(androidResourceManager)
    }

    private val coinTickerPresenter: CoinTickerPresenter by lazy {
        CoinTickerPresenter(coinTickerRepository, androidResourceManager)
    }

    private val currency: Currency by lazy {
        Currency.getInstance(PreferenceManager.getDefaultCurrency(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_ticker_list)

        val toolbar = findViewById<View>(R.id.toolbar)
        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val coinName = intent.getStringExtra(COIN_NAME)?.trim()

        supportActionBar?.title = getString(R.string.tickerActivityTitle, coinName)

        coinTickerPresenter.attachView(this)

        lifecycle.addObserver(coinTickerPresenter)

        if (coinName != null) {
            coinTickerPresenter.getCryptoTickers(coinName.toLowerCase())
        }

       // FirebaseCrashlytics.getInstance().log("CoinTickerActivity")
    }

    override fun showOrHideLoadingIndicatorForTicker(showLoading: Boolean) {
        if (!showLoading) {
            pbLoading.hide()
        } else {
            pbLoading.show()
        }
    }

    override fun onPriceTickersLoaded(tickerData: List<CryptoTicker>) {
        rvCoinTickerList.withModels {
            tickerData.forEachIndexed { index, cryptoTicker ->
                coinTickerView {
                    id(index)
                    ticker(cryptoTicker)
                    tickerPrice(formatter.formatAmount(cryptoTicker.convertedVolumeUSD, currency, true))
                    tickerVolume(formatter.formatAmount(cryptoTicker.last, currency, true))
                    itemClickListener { _ ->
                        if (cryptoTicker.exchangeUrl.isNotBlank()) {
                            openCustomTab(getUrlWithoutParameters(cryptoTicker.exchangeUrl), this@CoinTickerActivity)
                        }
                    }
                }
            }
        }

        tvFooter.setOnClickListener {
            openCustomTab(getString(R.string.coin_gecko_url), this)
        }
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvCoinTickerList, errorMessage, Snackbar.LENGTH_LONG)
    }
}
