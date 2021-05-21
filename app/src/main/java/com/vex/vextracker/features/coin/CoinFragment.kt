package com.blockchain.vex.features.coin

import CoinContract
import CoinTickerContract
import CryptoNewsContract
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import com.vex.vextracker.data.PreferenceManager
import com.blockchain.vex.data.database.entities.CoinTransaction
import com.vex.vextracker.data.database.entities.WatchedCoin
import com.blockchain.vex.epoxymodels.*
import com.blockchain.vex.featurecomponents.ModuleItem
import com.blockchain.vex.featurecomponents.cointickermodule.CoinTickerPresenter
import com.blockchain.vex.featurecomponents.cointickermodule.CoinTickerRepository
import com.blockchain.vex.featurecomponents.cryptonewsmodule.CryptoNewsPresenter
import com.blockchain.vex.featurecomponents.cryptonewsmodule.CryptoNewsRepository
import com.blockchain.vex.featurecomponents.historicalchartmodule.ChartRepository
import com.vex.vextracker.features.CryptoCompareRepository
import com.blockchain.vex.features.coindetails.CoinDetailsActivity
import com.blockchain.vex.features.coindetails.CoinDetailsPagerActivity
import com.blockchain.vex.features.newslist.NewsListActivity
import com.blockchain.vex.features.ticker.CoinTickerActivity
import com.blockchain.vex.network.HOUR
import com.blockchain.vex.network.models.CoinPrice
import com.blockchain.vex.network.models.CryptoCompareHistoricalResponse
import com.blockchain.vex.network.models.CryptoPanicNews
import com.blockchain.vex.network.models.CryptoTicker
import com.vex.vextracker.utils.defaultExchange
import com.blockchain.vex.utils.dpToPx
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManager
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManagerImpl
import com.blockchain.vex.utils.ui.OnVerticalScrollListener
import com.google.android.material.snackbar.Snackbar
import com.vex.vextracker.CoinBitApplication
import com.vex.vextracker.R
import kotlinx.android.synthetic.main.fragment_coin_details.*
import java.math.BigDecimal

class CoinFragment : Fragment(), CoinContract.View, CryptoNewsContract.View, CoinTickerContract.View {

    private val coinDetailList: MutableList<ModuleItem> = mutableListOf()
    private var coinPrice: CoinPrice? = null
    private var watchedMenuItem: MenuItem? = null
    private var isCoinWatched = false
    private var isCoinedPurchased = false
    private var watchedCoin: WatchedCoin? = null

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val chartRepo by lazy {
        ChartRepository()
    }

    private val coinPresenter: CoinPresenter by lazy {
        CoinPresenter(coinRepo, chartRepo)
    }

    private val cryptoNewsRepository by lazy {
        CryptoNewsRepository()
    }
    private val cryptoNewsPresenter: CryptoNewsPresenter by lazy {
        CryptoNewsPresenter(cryptoNewsRepository)
    }

    private val coinTickerRepository by lazy {
        CoinTickerRepository(CoinBitApplication.database)
    }
    private val coinTickerPresenter: CoinTickerPresenter by lazy {
        CoinTickerPresenter(coinTickerRepository, androidResourceManager)
    }

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(requireContext())
    }

    private val toCurrency: String by lazy {
        PreferenceManager.getDefaultCurrency(context?.applicationContext)
    }

    companion object {
        private const val WATCHED_COIN = "WATCHED_COIN"

        @JvmStatic
        fun getArgumentBundle(watchedCoin: WatchedCoin): Bundle {
            val bundle = Bundle()
            bundle.putParcelable(WATCHED_COIN, watchedCoin)
            return bundle
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val inflate = inflater.inflate(R.layout.fragment_coin_details, container, false)

        watchedCoin = arguments?.getParcelable(WATCHED_COIN)

        setHasOptionsMenu(true)

        //FirebaseCrashlytics.getInstance().log("CoinFragment")

        return inflate
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        watchedCoin?.let {

            coinPresenter.attachView(this)
            cryptoNewsPresenter.attachView(this)
            coinTickerPresenter.attachView(this)

            lifecycle.addObserver(coinPresenter)
            lifecycle.addObserver(cryptoNewsPresenter)
            lifecycle.addObserver(coinTickerPresenter)

            val toolBarDefaultElevation = dpToPx(context, 12) // default elevation of toolbar

            rvCoinDetails.addOnScrollListener(object : OnVerticalScrollListener() {
                override fun onScrolled(offset: Int) {
                    super.onScrolled(offset)
                    (activity as? CoinDetailsPagerActivity)?.supportActionBar?.elevation = Math.min(toolBarDefaultElevation.toFloat(), offset.toFloat())
                    (activity as? CoinDetailsActivity)?.supportActionBar?.elevation = Math.min(toolBarDefaultElevation.toFloat(), offset.toFloat())
                }
            })

            rvCoinDetails.setItemSpacingDp(8)

            // load data
            coinPresenter.loadCurrentCoinPrice(it, toCurrency)

            if (it.purchaseQuantity > BigDecimal.ZERO) {
                isCoinedPurchased = true
                isCoinWatched = true
            } else if (it.watched) {
                isCoinWatched = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.coin_details_menu, menu)

        watchedMenuItem = menu.findItem(R.id.action_watch)

        changeCoinMenu(isCoinWatched, isCoinedPurchased)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_watch -> {
                isCoinWatched = !isCoinWatched
                changeCoinMenu(isCoinWatched, isCoinedPurchased)
                changeCoinWatchedStatus(isCoinWatched)
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeCoinWatchedStatus(isCoinWatched: Boolean) {
        watchedCoin?.let {
            coinPresenter.updateCoinWatchedStatus(isCoinWatched, it.coin.id, it.coin.symbol)

            (activity as? CoinDetailsPagerActivity)?.isCoinInfoChanged = true
        }
    }

    private fun changeCoinMenu(isCoinWatched: Boolean, isCoinPurchased: Boolean) {
        if (!isCoinPurchased) {
            if (isCoinWatched) {
               // watchedMenuItem?.icon = context?.getDrawable(R.drawable.ic_watching)
                watchedMenuItem?.title = context?.getString(R.string.remove_to_watchlist)
            } else {
               // watchedMenuItem?.icon = context?.getDrawable(R.drawable.ic_watch)
                watchedMenuItem?.title = context?.getString(R.string.add_to_watchlist)
            }
        } else {
            watchedMenuItem?.isVisible = false
        }
    }

    override fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String) {

        val statusText = if (watched) {
            getString(R.string.coin_added_to_watchlist, coinSymbol)
        } else {
            getString(R.string.coin_removed_to_watchlist, coinSymbol)
        }

        Snackbar.make(rvCoinDetails, statusText, Snackbar.LENGTH_LONG).show()
    }

    override fun showOrHideLoadingIndicator(showLoading: Boolean) {
        // do nothing
    }

    override fun showOrHideLoadingIndicatorForTicker(showLoading: Boolean) {
        // do nothing
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(rvCoinDetails, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onCoinPriceLoaded(coinPrice: CoinPrice?, watchedCoin: WatchedCoin) {

        this.coinPrice = coinPrice

        coinDetailList.add(CoinHistoricalChartItemView.HistoricalChartModuleData(coinPrice, HOUR, watchedCoin.coin.symbol, null))

        // we do not support Adding new coin yet
        // coinDetailList.add(AddCoinModule.AddCoinModuleData(watchedCoin.coin))

        if (coinPrice != null) {
            coinDetailList.add(CoinStatsticsItemView.CoinStatisticsModuleData(coinPrice))

            coinDetailList.add(
                CoinInfoItemView.CoinInfoModuleData(
                    coinPrice.market
                        ?: defaultExchange,
                    watchedCoin.coin.algorithm, watchedCoin.coin.proofType
                )
            )
        }

        coinDetailList.add(CoinAboutItemView.AboutCoinModuleData(watchedCoin.coin))

        coinPresenter.loadHistoricalData(HOUR, watchedCoin.coin.symbol, toCurrency)
        coinTickerPresenter.getCryptoTickers(watchedCoin.coin.coinName.toLowerCase())
        cryptoNewsPresenter.getCryptoNews(watchedCoin.coin.symbol)
        coinPresenter.loadRecentTransaction(watchedCoin.coin.symbol)

        coinDetailList.add(GenericFooterItemView.FooterModuleData())

        showCoinDataInView(coinDetailList)
    }

    override fun onHistoricalDataLoaded(period: String, historicalDataPair: Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>) {
        watchedCoin?.coin?.let {
            coinDetailList[0] = CoinHistoricalChartItemView.HistoricalChartModuleData(coinPrice, period, it.symbol, historicalDataPair)
            showCoinDataInView(coinDetailList)
        }
    }

    private fun showCoinDataInView(detailList: List<ModuleItem>) {
        rvCoinDetails.withModels {
            detailList.forEachIndexed { index, moduleItem ->
                when (moduleItem) {
                    is CoinHistoricalChartItemView.HistoricalChartModuleData -> coinHistoricalChartItemView {
                        id("coinHistoricalChartItem")
                        chartData(moduleItem)
                        onChartRangeSelected(object : CoinHistoricalChartItemView.OnHistoricalChardRangeSelectionListener {
                            override fun onRangeSelected(period: String, fromCurrency: String, toCurrency: String) {
                                coinPresenter.loadHistoricalData(period, fromCurrency, toCurrency)
                            }
                        })
                    }
                    is AddCoinTransactionItemView.AddCoinTransactionModuleItem -> addCoinTransactionItemView {
                        id("addCoin")
                        itemClickListener { _ ->
                            // add coin button clicked.
                        }
                    }
                    is CoinPositionItemView.CoinPositionCardModuleData -> coinPositionItemView {
                        id("coinPosition")
                        coinPrice(moduleItem)
                    }
                    is CoinInfoItemView.CoinInfoModuleData -> coinInfoItemView {
                        id("coinInfo")
                        exchange(moduleItem)
                    }
                    is CoinStatsticsItemView.CoinStatisticsModuleData -> coinStatsticsItemView {
                        id("coinStats")
                        coinPrice(moduleItem)
                    }
                    is CoinTickerItemView.CoinTickerModuleData -> coinTickerItemView {
                        id("coinTickerItem")
                        coinTickerData(moduleItem)
                        moreClickListener { _ ->
                            watchedCoin?.coin?.let {
                                startActivity(CoinTickerActivity.buildLaunchIntent(requireContext(), it.coinName))
                            }
                        }
                    }
                    is CoinNewsItemView.CoinNewsItemModuleData -> coinNewsItemView {
                        id("coinNewsItem")
                        coinNews(moduleItem)
                        moreClickListener { _ ->
                            watchedCoin?.coin?.let {
                                startActivity(NewsListActivity.buildLaunchIntent(requireContext(), it.coinName, it.symbol))
                            }
                        }
                    }
                    is CoinAboutItemView.AboutCoinModuleData -> coinAboutItemView {
                        id("aboutCoin")
                        coin(moduleItem)
                    }
                    is CoinTransactionHistoryItemView.CoinTransactionHistoryModuleData -> coinTransactionHistoryItemView {
                        id("coinTransactionHistory")
                        coinTransactionHistoryModuleData(moduleItem)
                    }
                    is GenericFooterItemView.FooterModuleData -> genericFooterItemView {
                        id("footer")
                        footerContent(moduleItem)
                    }
                }
            }
        }
    }

    override fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews) {
        val matchingIndex = coinDetailList.indexOfFirst { moduleItem ->
            moduleItem is CoinAboutItemView.AboutCoinModuleData
        }

        if (matchingIndex > 0) {
            coinDetailList.add(matchingIndex, CoinNewsItemView.CoinNewsItemModuleData(cryptoPanicNews))
        } else {
            coinDetailList.add(CoinNewsItemView.CoinNewsItemModuleData(cryptoPanicNews))
        }

        showCoinDataInView(coinDetailList)
    }

    override fun onPriceTickersLoaded(tickerData: List<CryptoTicker>) {

        val matchingIndex = coinDetailList.indexOfFirst { moduleItem ->
            moduleItem is CoinInfoItemView.CoinInfoModuleData
        }

        if (matchingIndex > 0) {
            coinDetailList.add(matchingIndex + 1, CoinTickerItemView.CoinTickerModuleData(tickerData))
        } else {
            coinDetailList.add(CoinTickerItemView.CoinTickerModuleData(tickerData))
        }

        showCoinDataInView(coinDetailList)
    }

    override fun onRecentTransactionLoaded(coinTransactionList: List<CoinTransaction>) {
        if (coinTransactionList.isNotEmpty()) {
            coinPrice?.let {
                // add position module
                coinDetailList.add(2, CoinPositionItemView.CoinPositionCardModuleData(it, coinTransactionList))
            }

            // add transaction module
            coinDetailList.add(4, CoinTransactionHistoryItemView.CoinTransactionHistoryModuleData(coinTransactionList))
            showCoinDataInView(coinDetailList)
        }
    }
}
