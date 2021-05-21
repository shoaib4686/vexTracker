import com.vex.vextracker.data.database.entities.WatchedCoin
import com.blockchain.vex.features.BaseView

/**
Created by Pranay Airan
 */

interface CoinDetailsPagerContract {

    interface View : BaseView {
        fun onWatchedCoinsLoaded(watchedCoinList: List<WatchedCoin>?)
    }

    interface Presenter {
        fun loadWatchedCoins()
    }
}
