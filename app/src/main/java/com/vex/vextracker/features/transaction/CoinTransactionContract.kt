import com.blockchain.vex.data.database.entities.CoinTransaction
import com.blockchain.vex.features.BaseView
import com.blockchain.vex.network.models.ExchangePair
import java.math.BigDecimal
import java.util.HashMap

/**
Created by Pranay Airan 2/3/18.
 */

interface CoinTransactionContract {

    interface View : BaseView {
        fun onAllSupportedExchangesLoaded(exchangeCoinMap: HashMap<String, MutableList<ExchangePair>>)
        fun onCoinPriceLoaded(prices: MutableMap<String, BigDecimal>)
        fun onTransactionAdded()
    }

    interface Presenter {
        fun getAllSupportedExchanges()
        fun getPriceForPair(fromCoin: String, toCoin: String, exchange: String, timeStamp: String)
        fun addTransaction(transaction: CoinTransaction)
    }
}
