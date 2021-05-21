import com.blockchain.vex.data.database.entities.CoinTransaction
import com.vex.vextracker.data.database.entities.WatchedCoin
import com.blockchain.vex.features.BaseView
import com.blockchain.vex.network.models.CoinPrice
import com.blockchain.vex.network.models.CryptoCompareNews

/**
Created by Pranay Airan
 */

interface CoinDashboardContract {

    interface View : BaseView {
        fun onWatchedCoinsAndTransactionsLoaded(watchedCoinList: List<WatchedCoin>, coinTransactionList: List<CoinTransaction>)
        fun onCoinPricesLoaded(coinPriceListMap: HashMap<String, CoinPrice>)
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun loadWatchedCoinsAndTransactions()
        fun loadCoinsPrices(fromCurrencySymbol: String, toCurrencySymbol: String)
        fun getTopCoinsByTotalVolume24hours(toCurrencySymbol: String)
        fun getLatestNewsFromCryptoCompare()
    }
}
