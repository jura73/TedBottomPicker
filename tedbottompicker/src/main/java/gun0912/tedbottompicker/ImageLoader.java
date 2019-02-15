package gun0912.tedbottompicker;

import android.content.Context;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

public class ImageLoader {

    public static void loadImageInto(Context context, Uri imageUri, ImageView imageView) {
        Glide.with(context)
                .load(imageUri)
                .thumbnail(0.01f)
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.img_error))
                .into(imageView);
    }

    public static void loadImageInto(Fragment fragment, Uri imageUri, ImageView imageView) {
        Glide.with(fragment.getActivity())
                .load(imageUri)
                .thumbnail(0.01f)
                .apply(new RequestOptions().centerCrop()
                        .placeholder(R.drawable.ic_gallery)
                        .error(R.drawable.img_error))
                .into(imageView);
    }
}