package xoho.com.cam;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

public class ImagesAdapter extends RecyclerView.Adapter<ImagesAdapter.ImageViewHolder> {
    private Context context;
    private List<File> localImages;
    private List<Upload> uploadedImages;
    private OnRecyclerItemClickListener mListener;
    private boolean isUploadsActivity;

    ImagesAdapter(Context context) {
        this.context = context;
    }

    public void setUploadedImagesList(List<Upload> uploadsList) {
        uploadedImages = uploadsList;
        isUploadsActivity = true;
    }

    public void setLocalImagesList(List<File> localFilesList) {
        localImages = localFilesList;
        isUploadsActivity = false;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.image_item, parent, false);
        return new ImagesAdapter.ImageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        if (isUploadsActivity) {
            Upload uploadCurrent = uploadedImages.get(position);
            holder.textViewName.setText(uploadCurrent.getName());
            Picasso.get().load(uploadCurrent.getImageUrl()).into(holder.imageView);
        } else {
            File imageFile = localImages.get(position);
            holder.textViewName.setText(imageFile.getName());
            //Picasso.get().load(imageFile).fit().centerInside().into(holder.imageView);
            //Picasso does not parse EXIF tags instead relies on Media Store columns therefore orientation is not checked
            // and rotated according to for local images i.e private to app as they are not scanned by Media Scanner
            //Glide takes care of the above
            Glide.with(context).load(Uri.fromFile(imageFile)).into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        if (isUploadsActivity) {
            return uploadedImages.size();
        } else {
            return localImages.size();
        }
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView textViewName;
        public ImageView imageView;

        ImageViewHolder(View itemView) {
            super(itemView);

            textViewName = itemView.findViewById(R.id.item_name);
            imageView = itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItemClick(position);
                }
            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select Action");
            MenuItem share = contextMenu.add(Menu.NONE, 1, 1, "Share");
            MenuItem delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");

            share.setOnMenuItemClickListener(this);
            delete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    switch (menuItem.getItemId()) {
                        case 1:
                            mListener.onShareClick(position);
                            break;
                        case 2:
                            mListener.onDeleteClick(position);
                            break;
                    }
                }
            }
            return false;
        }
    }

    public interface OnRecyclerItemClickListener {
        void onItemClick(int position);

        void onShareClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListner(OnRecyclerItemClickListener listener) {
        mListener = listener;
    }
}
