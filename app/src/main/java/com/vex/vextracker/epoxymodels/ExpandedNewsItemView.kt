package com.blockchain.vex.epoxymodels

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import coil.load
import coil.transform.RoundedCornersTransformation
import com.airbnb.epoxy.ModelProp
import com.airbnb.epoxy.ModelView
import com.vex.vextracker.R
import com.blockchain.vex.featurecomponents.ModuleItem
import com.blockchain.vex.network.models.CryptoCompareNews
import com.blockchain.vex.utils.Formaters
import com.blockchain.vex.utils.openCustomTab
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManager
import com.blockchain.vex.utils.resourcemanager.AndroidResourceManagerImpl

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class ExpandedNewsItemView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : ConstraintLayout(context, attributeSet, defStyle) {

    private val tvSource: TextView
    private val tvHeadlines: TextView
    private val tvTimePeriod: TextView
    private val ivNewsCover: ImageView
    private val clNewsArticleContainer: View

    private val androidResourceManager: AndroidResourceManager by lazy {
        AndroidResourceManagerImpl(context)
    }

    init {
        View.inflate(context, R.layout.discovery_news_module, this)
        tvSource = findViewById(R.id.tvSource)
        tvHeadlines = findViewById(R.id.tvHeadlines)
        tvTimePeriod = findViewById(R.id.tvTimePeriod)
        ivNewsCover = findViewById(R.id.ivNewsCover)
        clNewsArticleContainer = findViewById(R.id.clNewsArticleContainer)
    }

    @ModelProp
    fun setNews(discoveryNewsModuleData: DiscoveryNewsModuleData) {
        tvSource.text = discoveryNewsModuleData.coinNews.source
        tvHeadlines.text = discoveryNewsModuleData.coinNews.title
        if (discoveryNewsModuleData.coinNews.published_on != null) {
            tvTimePeriod.text = Formaters(androidResourceManager).formatTransactionDate(discoveryNewsModuleData.coinNews.published_on)
        }

        ivNewsCover.load(discoveryNewsModuleData.coinNews.imageurl) {
            crossfade(true)
            transformations(RoundedCornersTransformation(15f))
        }

        clNewsArticleContainer.setOnClickListener {
            if (discoveryNewsModuleData.coinNews.url != null) {
                openCustomTab(discoveryNewsModuleData.coinNews.url, context)
            }
        }
    }

    data class DiscoveryNewsModuleData(val coinNews: CryptoCompareNews) : ModuleItem
}
