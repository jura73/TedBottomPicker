package gun0912.tedbottompicker;

import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

//import com.bumptech.glide.Glide;
//import com.bumptech.glide.request.RequestOptions;

public class ImageLoader {

    public static void loadImageInto(Context context, Uri imageUri, ImageView imageView) {
        Picasso.get().load(imageUri)
                .placeholder(R.drawable.ic_gallery)
                .error(R.drawable.img_error)
                .into(imageView);
    }

//    public static void loadImageInto(Context context, Uri imageUri, ImageView imageView) {
//        Glide.with(context)
//                .load(imageUri)
//                .apply(new RequestOptions().centerCrop()
//                        .placeholder(R.drawable.ic_gallery)
//                        .error(R.drawable.img_error))
//                .into(imageView);
//    }
}