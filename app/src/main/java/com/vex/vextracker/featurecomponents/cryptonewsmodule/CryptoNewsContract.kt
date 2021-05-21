import com.blockchain.vex.features.BaseView
import com.blockchain.vex.network.models.CryptoPanicNews

/**
 * Created by Pragya Agrawal
 */

interface CryptoNewsContract {

    interface View : BaseView {
        fun showOrHideLoadingIndicator(showLoading: Boolean = true)
        fun onNewsLoaded(cryptoPanicNews: CryptoPanicNews)
    }

    interface Presenter {
        fun getCryptoNews(coinSymbol: String)
    }
}
