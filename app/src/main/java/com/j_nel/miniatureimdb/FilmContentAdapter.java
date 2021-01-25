package com.j_nel.miniatureimdb;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FilmContentAdapter extends RecyclerView.Adapter<FilmContentAdapter.ViewHolder> {
    //Global Variables
    protected List<FilmContentModel> filmContentList;
    protected Map<String, Bitmap> bitmaps = new HashMap<>();
    Context context;
    Intent intent;

    public FilmContentAdapter(List<FilmContentModel> filmContentList) {
        this.filmContentList = filmContentList;
    }

    @NonNull
    @Override
    public FilmContentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Inflate the layout
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.film_content_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //Set data on all the view items listed below.
        FilmContentModel filmContentModel = filmContentList.get(position);
        holder.tvFilmTitle.setText(filmContentModel.getTitle());
        holder.tvYear.setText(filmContentModel.getYear());
        holder.tvType.setText(filmContentModel.getType());
        if (bitmaps.containsKey(position)) {
            holder.imgPoster.setImageBitmap(bitmaps.get(position));
        } else {
            if (!filmContentModel.getPoster().trim().toUpperCase().equals("N/A")) {
                new LoadPosterTask(holder.imgPoster).execute(filmContentModel.getPoster());
            }
        }
        holder.cv.setOnClickListener(view -> {
            intent = new Intent(context, SelectedFilmView.class);
            intent.putExtra("FilmID", filmContentModel.getImdbID());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        //Number of items in RecyclerView
        return filmContentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imgPoster;
        TextView tvFilmTitle, tvYear, tvType;
        CardView cv;

        public ViewHolder(View itemView) {
            super(itemView);
            imgPoster = itemView.findViewById(R.id.imgPoster);
            tvFilmTitle = itemView.findViewById(R.id.tvTitle);
            tvYear = itemView.findViewById(R.id.tvYear);
            tvType = itemView.findViewById(R.id.tvType);
            cv = itemView.findViewById(R.id.cv);
        }
    }

    private class LoadPosterTask extends AsyncTask<String, Void, Bitmap> {
        //Retrieve image for each item on an AsyncTask.
        private final ImageView imageView;
        private final Map<String, Object> additionalProperties = new HashMap<String, Object>();

        public LoadPosterTask(ImageView imageView) {
            //Store image
            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            Bitmap bitmap = null;
            HttpURLConnection connection;
            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                try (InputStream inputStream = connection.getInputStream()) {
                    bitmap = BitmapFactory.decodeStream(inputStream);
                    additionalProperties.put(strings[0], bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}