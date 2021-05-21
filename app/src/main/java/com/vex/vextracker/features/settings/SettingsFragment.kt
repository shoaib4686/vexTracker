package com.blockchain.vex.features.settings

import SettingsContract
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.vex.vextracker.data.PreferenceManager
import com.vex.vextracker.features.CryptoCompareRepository
import com.blockchain.vex.utils.*
import com.google.android.material.snackbar.Snackbar
import com.mynameismidori.currencypicker.CurrencyPicker
import com.vex.vextracker.BuildConfig
import com.vex.vextracker.CoinBitApplication
import com.vex.vextracker.R
import com.vex.vextracker.utils.defaultCurrency
import kotlinx.android.synthetic.main.fragment_settings.*
import kotlinx.android.synthetic.main.fragment_settings.view.*
import timber.log.Timber

class SettingsFragment : Fragment(), SettingsContract.View {

    companion object {
        const val TAG = "SettingsFragment"
    }

    private val coinRepo by lazy {
        CryptoCompareRepository(CoinBitApplication.database)
    }

    private val settingsPresenter: SettingsPresenter by lazy {
        SettingsPresenter(coinRepo)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val inflate = inflater.inflate(R.layout.fragment_settings, container, false)

        val toolbar = inflate.toolbar
        toolbar?.title = getString(R.string.settings)

        (activity as AppCompatActivity).setSupportActionBar(toolbar)

        settingsPresenter.attachView(this)

        lifecycle.addObserver(settingsPresenter)

        initializeUI(inflate)

        //FirebaseCrashlytics.getInstance().log("SettingsFragment")

        return inflate
    }

    private fun initializeUI(inflatedView: View) {

        val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)

        inflatedView.tvCurrencyValue.text = currency

        inflatedView.clCurrency.setOnClickListener {
            openCurrencyPicker()
        }

        inflatedView.clCurrencyList.setOnClickListener {
            settingsPresenter.refreshCoinList(currency)
            inflatedView.ivCurrencyList.visibility = View.INVISIBLE
            inflatedView.pbLoadingCurrencyList.visibility = View.VISIBLE
        }

        inflatedView.clExchangeList.setOnClickListener {
            settingsPresenter.refreshExchangeList()
            inflatedView.ivExchangeList.visibility = View.INVISIBLE
            inflatedView.pbLoadingExchangeList.visibility = View.VISIBLE
        }

        inflatedView.clRate.setOnClickListener {
            rateCoinBit(requireContext())
        }

        inflatedView.clShare.setOnClickListener {
            shareCoinBit(requireContext())
        }

        inflatedView.clFeedback.setOnClickListener {
            sendEmail(requireContext(), getString(R.string.email_address), getString(R.string.feedback_coinbit), "Hello, \n")
        }

        inflatedView.clPrivacy.setOnClickListener {
            openCustomTab(getString(R.string.privacyPolicyUrl), requireContext())
        }

        inflatedView.tvAppVersionValue.text = BuildConfig.VERSION_NAME

        inflatedView.clAttribution.setOnClickListener {
            openCustomTab(getString(R.string.attributionUrl), requireContext())
        }

        inflatedView.clCryptoCompare.setOnClickListener {
            openCustomTab(getString(R.string.crypto_compare_url), requireContext())
        }

        inflatedView.clCoinGecko.setOnClickListener {
            openCustomTab(getString(R.string.coin_gecko_url), requireContext())
        }

        inflatedView.clCryptoPanic.setOnClickListener {
            openCustomTab(getString(R.string.crypto_panic_url), requireContext())
        }
    }

    private fun openCurrencyPicker() {
        val picker = CurrencyPicker.newInstance(getString(R.string.select_currency)) // dialog title

        picker.setListener { name, code, _, _ ->
            Timber.d("Currency code selected $name,$code")
            PreferenceManager.setPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, code)

            picker.dismiss() // Show currency that is picked.

            val currency = PreferenceManager.getPreference(requireContext(), PreferenceManager.DEFAULT_CURRENCY, defaultCurrency)

            tvCurrencyValue.text = currency

            // get list of all coins
            settingsPresenter.refreshCoinList(currency)
        }

        fragmentManager?.let {
            picker.show(it, "CURRENCY_PICKER")
        }
    }

    override fun onCoinListRefreshed() {
        ivCurrencyList.visibility = View.VISIBLE
        pbLoadingCurrencyList.visibility = View.GONE
    }

    override fun onExchangeListRefreshed() {
        ivExchangeList.visibility = View.VISIBLE
        pbLoadingExchangeList.visibility = View.GONE
    }

    override fun onNetworkError(errorMessage: String) {
        Snackbar.make(llSettings, errorMessage, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        (activity as AppCompatActivity).setSupportActionBar(null)
        super.onDestroyView()
    }
}
