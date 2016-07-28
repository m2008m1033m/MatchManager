package com.dxbcom.matchmanager.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dxbcom.matchmanager.MatchManagerApplication;
import com.dxbcom.matchmanager.R;
import com.dxbcom.matchmanager.core.Communicator;
import com.dxbcom.matchmanager.models.Match;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/29/16.
 */
public class MatchesAdapter extends RecyclerView.Adapter {

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private CardView mCardView;
        private ImageView mTeam1Logo;
        private ImageView mTeam2Logo;
        private TextView mClubs;
        private TextView mGoals;
        private TextView mTournament;


        public ViewHolder(View itemView) {
            super(itemView);

            mCardView = ((CardView) itemView.findViewById(R.id.card_view));
            mTeam1Logo = ((ImageView) itemView.findViewById(R.id.team_1_logo));
            mTeam2Logo = ((ImageView) itemView.findViewById(R.id.team_2_logo));
            mClubs = ((TextView) itemView.findViewById(R.id.clubs));
            mGoals = ((TextView) itemView.findViewById(R.id.goals));
            mTournament = ((TextView) itemView.findViewById(R.id.tournament));
        }
    }

    public interface OnActionListener {
        void onItemClicked(int position, Match match);

        void onItemLongPressed(int position, Match match);
    }

    private ArrayList<Match> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;

    public MatchesAdapter() {

    }

    public MatchesAdapter(OnActionListener listener) {
        mOnActionListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.match_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        vh.mCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnActionListener != null) {
                    int position = vh.getAdapterPosition();
                    mOnActionListener.onItemClicked(position, mItems.get(position));
                }
            }
        });

        vh.mCardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnActionListener != null) {
                    int position = vh.getAdapterPosition();
                    mOnActionListener.onItemLongPressed(position, mItems.get(position));
                }
                return true;
            }
        });

        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Match match = mItems.get(position);

        ((ViewHolder) holder).mTournament.setText(match.getTournament());
        ((ViewHolder) holder).mGoals.setText(match.getTeam1Goals() + ":" + match.getTeam2Goals());
        ((ViewHolder) holder).mClubs.setText(match.getTeam1Name() + " x " + match.getTeam2Name());
        ((ViewHolder) holder).mTournament.setText(match.getTournament() + " " + match.getDateAsString("dd MMM, yyyy"));

        Picasso.with(MatchManagerApplication.getContext())
                .load(Communicator.API_URL + "/Services/logo/" + match.getClub1Id()) // TODO: change this to the actual url
                .placeholder(R.drawable.placeholder)
                .into(((ViewHolder) holder).mTeam1Logo);

        Picasso.with(MatchManagerApplication.getContext())
                .load(Communicator.API_URL + "/Services/logo/" + match.getClub2Id()) // TODO: change this to the actual url
                .placeholder(R.drawable.placeholder)
                .into(((ViewHolder) holder).mTeam2Logo);

    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    public void clearAndAddAll(ArrayList items) {
        mItems.clear();
        mItems.addAll(items);
        notifyDataSetChanged();
    }

    public void clear() {
        mItems.clear();
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
    }


}
