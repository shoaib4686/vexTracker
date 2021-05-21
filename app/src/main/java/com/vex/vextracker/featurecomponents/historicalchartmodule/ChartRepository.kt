package com.blockchain.vex.featurecomponents.historicalchartmodule

import com.blockchain.vex.network.*
import com.blockchain.vex.network.api.api
import com.blockchain.vex.network.models.CryptoCompareHistoricalResponse
import timber.log.Timber

/**
Created by Pranay Airan
 * Repository that interact with crypto api to get charts.
 */

class ChartRepository {

    /**
     * Get the historical data for specific crypto currencies. [period] specifies what time period you
     * want data from. [fromCurrencySymbol] specifies what currencies data you want for example bitcoin.[toCurrencySymbol]
     * is which currency you want data in for like USD
     */
    suspend fun getCryptoHistoricalData(period: String, fromCurrencySymbol: String?, toCurrencySymbol: String?): Pair<List<CryptoCompareHistoricalResponse.Data>, CryptoCompareHistoricalResponse.Data?> {

        val histoPeriod: String
        var limit = 30
        var aggregate = 1
        when (period) {
            HOUR -> {
                histoPeriod = HISTO_MINUTE
                limit = 60
                aggregate = 12 // this pulls for 12 hour
            }
            HOURS24 -> {
                histoPeriod = HISTO_HOUR
                limit = 24 // 1 day
            }
            WEEK -> {
                histoPeriod = HISTO_HOUR
                aggregate = 6 // 1 week limit is 128 hours default that is
            }
            MONTH -> {
                histoPeriod = HISTO_DAY
                limit = 30 // 30 days
            }
            MONTH3 -> {
                histoPeriod = HISTO_DAY
                limit = 90 // 90 days
            }
            YEAR -> {
                histoPeriod = HISTO_DAY
                aggregate = 13 // default limit is 30 so 30*12 365 days
            }
            ALL -> {
                histoPeriod = HISTO_DAY
                aggregate = 30
                limit = 2000
            }
            else -> {
                histoPeriod = HISTO_HOUR
                limit = 24 // 1 day
            }
        }

        val historicalData = api.getCryptoHistoricalData(histoPeriod, fromCurrencySymbol, toCurrencySymbol, limit, aggregate)
        Timber.d("Size of response %s", historicalData.data.size)
        val maxClosingValueFromHistoricalData = historicalData.data.maxBy { it.close.toFloat() }
        return Pair(historicalData.data, maxClosingValueFromHistoricalData)
    }
}
