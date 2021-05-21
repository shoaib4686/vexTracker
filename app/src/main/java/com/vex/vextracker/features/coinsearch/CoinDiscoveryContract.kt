import com.blockchain.vex.features.BaseView
import com.blockchain.vex.network.models.CoinPair
import com.blockchain.vex.network.models.CoinPrice
import com.blockchain.vex.network.models.CryptoCompareNews

/**
Created by Pranay Airan
 */

interface CoinDiscoveryContract {

    interface View : BaseView {
        fun onTopCoinsByTotalVolumeLoaded(topCoins: List<CoinPrice>)
        fun onTopCoinListByPairVolumeLoaded(topPair: List<CoinPair>)
        fun onCoinNewsLoaded(coinNews: List<CryptoCompareNews>)
    }

    interface Presenter {
        fun getTopCoinListByMarketCap(toCurrencySymbol: String)
        fun getTopCoinListByPairVolume()
        fun getCryptoCurrencyNews()
    }
}
