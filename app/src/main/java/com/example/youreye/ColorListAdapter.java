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

    //preparation de la liste
    private final ArrayList<Color_util.ColorName> mWordList;


    //class wordviewHolder qui est le model qui decrit chaque ligne de la recycle view
    class WordViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        //decalration de variable
        public final TextView wordItemView;
        final ColorListAdapter mAdapter;

        //constructeur de wordviewholder
        public WordViewHolder(View itemView, ColorListAdapter adapter) {
            super(itemView);
            //relié item au resource xml
            wordItemView = itemView.findViewById(R.id.word);
            //passer l'adapter
            this.mAdapter = adapter;
            //set listener de action onclick sur ligne de la recycleview
            itemView.setOnClickListener(this);

        }


        @Override
        public void onClick(View view) {
            //récupere position de la ligne selectionné
            int mPosition = getLayoutPosition();
            //recupérer l'objet Color name a travers de sa position
            Color_util.ColorName element = mWordList.get(mPosition);
            //modifier le couleur de background par le couleur correspandant au nom de couleur recupérer a partir de l'objet
            view.setBackgroundColor( Color.rgb(element.r,element.b,element.g));
        }
    }

    public ColorListAdapter(Context context, ArrayList<Color_util.ColorName> wordList) {
        this.mWordList = wordList;


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


        // Ajouter l'élément au ViewHolder et reinitalisé le couleur de background vers couleur blanc
        holder.itemView.setBackgroundColor(Color.rgb(255,255,255));
        holder.wordItemView.setText(mCurrent);


    }

    //Retourne le nombre d'éléments de notre liste
    @Override
    public int getItemCount() {
        return mWordList.size();
    }

}