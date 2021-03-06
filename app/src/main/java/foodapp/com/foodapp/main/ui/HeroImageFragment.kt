package foodapp.com.foodapp.main.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import com.squareup.picasso.Picasso
import foodapp.com.foodapp.R

class HeroImageFragment : Fragment() {

    companion object {
        const val ARG_IMAGE_RES = "image_res"
        const val ARG_IMAGE_URL = "image_url"

        fun newInstance(@DrawableRes imageRes: Int = -1, imageUrl: String = ""): HeroImageFragment {
            val fragment = HeroImageFragment()
            val bundle = Bundle()
            if (imageRes != -1) {
                bundle.putInt(ARG_IMAGE_RES, imageRes)
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                bundle.putString(ARG_IMAGE_URL, imageUrl)
            }
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_hero_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageView = view.findViewById<ImageView>(R.id.heroImageView)

        if (arguments!!.containsKey(ARG_IMAGE_RES)) {
            val imageRes = arguments!!.getInt(ARG_IMAGE_RES)
            Picasso.get().load(imageRes).fit().noFade().centerCrop().into(imageView)
            return
        }

        if (arguments!!.containsKey(ARG_IMAGE_URL)) {
            val imageUrl = arguments!!.getString(ARG_IMAGE_URL)
            ViewCompat.setTransitionName(imageView, imageUrl)
            Picasso.get().load(imageUrl).fit().noFade().centerCrop().into(imageView)

            view.setOnClickListener {
                activity?.let {
                    val p1 = androidx.core.util.Pair.create<View, String>(imageView, imageUrl)
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(it, p1)
                    //startActivity(DishDetailsActivity.newInstance(it, imageUrl), options.toBundle())
                }
            }
            return
        }
    }
}