import com.blockchain.vex.data.database.entities.CoinTransaction
import com.vex.vextracker.data.database.entities.WatchedCoin
import com.blockchain.vex.features.BaseView
import com.blockchain.vex.network.models.CoinPrice
import com.blockchain.vex.network.models.CryptoCompareHistoricalResponse

/**
Created by Pranay Airan
 */

interface CoinContract {

    interface View : BaseView {
        fun onCoinPriceLoaded(coinPrice: CoinPrice?, watchedCoin: WatchedCoin)
        fun onRecentTransactionLoaded(coinTransactionList: List<CoinTransaction>)
        fun onCoinWatchedStatusUpdated(watched: Boolean, coinSymbol: String)
        fun onHistoricalDataLoaded(period: String, historicalDataPair: Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?>)
    }

    interface Presenter {
        fun loadCurrentCoinPrice(watchedCoin: WatchedCoin, toCurrency: String)
        fun loadRecentTransaction(symbol: String)
        fun updateCoinWatchedStatus(watched: Boolean, coinID: String, coinSymbol: String)
        fun loadHistoricalData(period: String, fromCurrency: String, toCurrency: String)
    }
}
