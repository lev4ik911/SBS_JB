package by.iba.sbs.ui.dashboard

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.iba.mvvmbase.BaseFragment
import by.iba.mvvmbase.adapter.BaseBindingAdapter
import by.iba.sbs.BR
import by.iba.sbs.R
import by.iba.sbs.databinding.FavoritesFragmentBinding
import by.iba.sbs.databinding.InstructionListItemBinding
import by.iba.sbs.library.model.Guideline
import by.iba.sbs.ui.MainActivity
import by.iba.sbs.ui.guideline.GuidelineActivity
import kotlinx.android.synthetic.main.toolbar.*
import kotlinx.serialization.ImplicitReflectionSerializer
import kotlinx.serialization.UnstableDefault
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

/**
 * A simple [Fragment] subclass.
 */
class FavoritesFragment : BaseFragment<FavoritesFragmentBinding, DashboardViewModel>() {
    override val layoutId: Int = by.iba.sbs.R.layout.favorites_fragment
    override val viewModelVariableId: Int = BR.viewmodel
    override val viewModel: DashboardViewModel by sharedViewModel()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as MainActivity).apply {
            toolbar_main.navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.chevron_left)
            toolbar_main.setNavigationOnClickListener {
                onBackPressed()
            }
        }
    }

    @UnstableDefault
    @ImplicitReflectionSerializer
    override fun onStart() {
        super.onStart()
        val favoritesAdapter =
            BaseBindingAdapter<Guideline, InstructionListItemBinding, DashboardViewModel>(
                R.layout.favorites_instruction_list_item,
                BR.instruction,
                BR.viewmodel,
                viewModel,
                isItemsEquals = { oldItem, newItem ->
                    oldItem.name == newItem.name
                })
        favoritesAdapter.onItemClick = { pos, itemView, item ->
            val transitionSharedNameImgView = this.getString(R.string.transition_name_img_view)
            val transitionSharedNameTxtView = this.getString(R.string.transition_name_txt_view)
            var imageViewPair: Pair<View, String>
            val textViewPair: Pair<View, String>
            itemView?.findViewById<ImageView>(R.id.iv_preview).apply {
                this?.transitionName = transitionSharedNameImgView
                imageViewPair = Pair.create(this, transitionSharedNameImgView)
            }
            itemView?.findViewById<TextView>(R.id.tv_title).apply {
                this?.transitionName = transitionSharedNameTxtView
                textViewPair = Pair.create(this, transitionSharedNameTxtView)
            }
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                activity as Activity,
                imageViewPair,
                textViewPair
            )
            val intent = Intent(activity, GuidelineActivity::class.java)
            intent.putExtra("instructionId", item.id)
            startActivity(intent, options.toBundle())
        }
        (activity as MainActivity).apply {
            when (arguments?.getInt("Category")) {
                GuidelineCategory.RECOMMENDED.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_recommended)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    }
                    viewModel.recommended.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                    })
                    viewModel.loadRecommended(false)
                }
                GuidelineCategory.FAVORITE.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_favorites)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    }
                    viewModel.favorite.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                    })
                    viewModel.loadFavorites(false)
                }
                GuidelineCategory.POPULAR.ordinal -> {
                    toolbar_main.title = resources.getString(R.string.title_popular)
                    binding.rvFavorites.apply {
                        adapter = favoritesAdapter
                        layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                    }
                    viewModel.popular.observe(viewLifecycleOwner, Observer {
                        favoritesAdapter.addItems(it)
                    })
                    viewModel.loadPopular(false)
                }
                else -> {

                }
            }
        }
    }
}
