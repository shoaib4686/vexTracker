package com.blockchain.vex.featurecomponents.cryptonewsmodule

import com.blockchain.vex.data.CoinBitCache
import com.blockchain.vex.network.api.API
import com.blockchain.vex.network.api.cryptoCompareRetrofit
import com.blockchain.vex.network.models.CryptoPanicNews

/**
 * Created by Pragya Agrawal
 * Repository that interact with crypto api to get news.
 */

class CryptoNewsRepository {

    /**
     * Get the top news for specific coin from cryptopanic
     */
    suspend fun getCryptoPanicNews(coinSymbol: String): CryptoPanicNews {

        return if (CoinBitCache.newsMap.containsKey(coinSymbol)) {
            CoinBitCache.newsMap[coinSymbol]!!
        } else {
            cryptoCompareRetrofit.create(API::class.java)
                .getCryptoNewsForCurrency(coinSymbol, "important", true)
        }
    }
}
