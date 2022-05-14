package com.example.youreye;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;

public class ColorListAdapter extends
        RecyclerView.Adapter<ColorListAdapter.WordViewHolder> {

    //Préparer la structure qui contiendra les éléments de notre liste
    private final ArrayList<Color_util.ColorName> mWordList;
    //private final LayoutInflater mInflater;


    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        public final TextView wordItemView;
        final ColorListAdapter mAdapter;


        public WordViewHolder(View itemView, ColorListAdapter adapter) {
            super(itemView);
            wordItemView = itemView.findViewById(R.id.word);
            this.mAdapter = adapter;
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            int mPosition = getLayoutPosition();

            Color_util.ColorName element = mWordList.get(mPosition);

            view.setBackgroundColor( Color.rgb(element.r,element.b,element.g));


        }
    }

    public ColorListAdapter(Context context, ArrayList<Color_util.ColorName> wordList) {
        this.mWordList = wordList;
        //mInflater  = LayoutInflater.from(context);

    }

    //Appelée au moment de la création du ViewHolder qui affichera
    //les éléments chargés à partir de l'Adapter
    @Override
    public ColorListAdapter.WordViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // Inflater un view avec le layout déjà créé
        View mItemView = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.word_listitem, parent, false);
        return new WordViewHolder(mItemView, this);
    }

    //Elle est appelé à chaque fois qu'une vue doit être chargé.
    //On récupére alors la position du nouvel élément à afficher
    //et on le charge dans le TextView
    @Override
    public void onBindViewHolder(ColorListAdapter.WordViewHolder holder,
                                 int position) {



        // Récupérer l'élément qui doit etre affiché et chargé dans le ViewHolder
        String mCurrent = mWordList.get(position).getName();
        Log.i("color",mCurrent);

        // Ajouter l'élément au ViewHolder
        holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
        holder.wordItemView.setText(mCurrent);


    }

    //Retourne le nombre d'éléments de notre liste
    @Override
    public int getItemCount() {
        return mWordList.size();
    }

}