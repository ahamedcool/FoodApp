package foodapp.com.foodapp.details.ui

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import dagger.android.AndroidInjection
import foodapp.com.data.model.FoodItem
import foodapp.com.foodapp.R
import foodapp.com.foodapp.main.adapter.HeroImageViewsAdapter
import foodapp.com.foodapp.views.AnimationEndListener
import foodapp.com.foodapp.views.CircleTransform
import foodapp.com.foodapp.views.DepthPageTransformer
import foodapp.com.foodapp.views.Utils
import kotlinx.android.synthetic.main.activity_food_details.*
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named


@SuppressLint("RestrictedApi")
class FoodDetailsActivity : AppCompatActivity() {

    var flag = true

    private var pixelDensity: Float = 0.toFloat()

    @Inject
    @field:Named("appearAlphaAnim")
    lateinit var appearAlphaAnimation: Animation

    @Inject
    @field:Named("disappearAlphaAnim")
    lateinit var disappearAlphaAnimation: Animation

    companion object {
        private const val ARG_FOOD_ITEM = "food_item"

        fun newInstance(context: Context, foodItem: FoodItem): Intent {
            val intent = Intent(context, FoodDetailsActivity::class.java)
            val bundle = Bundle()
            bundle.putParcelable(ARG_FOOD_ITEM, foodItem)
            intent.putExtras(bundle)
            return intent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_food_details)

        pixelDensity = resources.displayMetrics.density

        val foodItem = intent.getParcelableExtra<FoodItem>(ARG_FOOD_ITEM)

        Picasso.get().load(foodItem.profileImage).noFade().transform(CircleTransform()).into(profileImageView)
        ViewCompat.setTransitionName(profileImageView, foodItem.profileImage)

        supportPostponeEnterTransition()

        Picasso.get().load(foodItem.heroImage).fit().noFade().placeholder(R.drawable.ic_svg_food).centerCrop().into(foodImageView, object : Callback {
            override fun onError(e: Exception?) {
                supportStartPostponedEnterTransition()
            }

            override fun onSuccess() {
                supportStartPostponedEnterTransition()
                Handler().postDelayed({
                    if (foodImageView.drawable != null) {
                        if (foodImageView.drawable is BitmapDrawable) {
                            Utils.createPaletteAsync(this@FoodDetailsActivity, (foodImageView.drawable as BitmapDrawable).bitmap)
                        }
                    }
                }, 200)
            }
        })

        ViewCompat.setTransitionName(foodImageView, foodItem.heroImage)

        foodDescTextView.text = foodItem.foodDescription
        ViewCompat.setTransitionName(foodDescTextView, foodItem.foodDescription)

        ViewCompat.setTransitionName(votesButton, foodItem.votes.toString())

        nameTextView.text = foodItem.name

        dateTextView.text = foodItem.date

        votesTextView.text = "${foodItem.votes}"

        dishesViewPager.setPageTransformer(true, DepthPageTransformer())

        val fragmentManager = supportFragmentManager
        dishesViewPager.adapter = HeroImageViewsAdapter(imagesUrls = foodItem.foodImages, manager = fragmentManager)

        tabLayout.setupWithViewPager(dishesViewPager, true)

        votesButton.setOnClickListener {

            var x = foodImageFrameLayout.right
            val y = foodImageFrameLayout.bottom
            x -= (28 * pixelDensity + 16 * pixelDensity).toInt()

            val hypotenuse = Math.hypot(foodImageFrameLayout.width.toDouble(), foodImageFrameLayout.height.toDouble()).toInt()

            if (flag) {

                val parameters = linearView.layoutParams as FrameLayout.LayoutParams
                parameters.height = foodImageFrameLayout.height
                linearView.layoutParams = parameters

                val anim = ViewAnimationUtils.createCircularReveal(linearView, x, y, 0f, hypotenuse.toFloat())
                anim.duration = 700

                anim.addListener(object : AnimationEndListener() {
                    override fun onAnimationEnd(animator: Animator?) {
                        layoutButtons.visibility = View.VISIBLE
                        layoutButtons.startAnimation(appearAlphaAnimation)
                    }
                })

                linearView.visibility = View.VISIBLE
                anim.start()

                flag = false
            } else {

                val anim = ViewAnimationUtils.createCircularReveal(linearView, x, y, hypotenuse.toFloat(), 0f)
                anim.duration = 700

                anim.addListener(object : AnimationEndListener() {
                    override fun onAnimationEnd(animator: Animator?) {
                        linearView.visibility = View.GONE
                    }
                })

                layoutButtons.visibility = View.GONE
                layoutButtons.startAnimation(disappearAlphaAnimation)
                anim.start()
                flag = true
            }
        }
    }
}