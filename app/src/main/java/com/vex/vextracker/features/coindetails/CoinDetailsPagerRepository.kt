package com.blockchain.vex.features.coindetails

import com.blockchain.vex.data.database.CoinBitDatabase
import com.vex.vextracker.data.database.entities.WatchedCoin

/**
Created by Pranay Airan
 * Repository that interact with crypto api and database for getting data.
 */

class CoinDetailsPagerRepository(
    private val coinBitDatabase: CoinBitDatabase?
) {

    /**
     * Get list of all coins that is added in watch list
     */
    suspend fun loadWatchedCoins(): List<WatchedCoin>? {

        coinBitDatabase?.let {
            return it.watchedCoinDao().getAllWatchedCoinsOnetime()
        }
        return null
    }
}
