package com.dxbcom.matchmanager.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.dxbcom.matchmanager.MatchManagerApplication;
import com.dxbcom.matchmanager.R;
import com.dxbcom.matchmanager.core.Communicator;
import com.dxbcom.matchmanager.models.Event;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mohammed on 6/30/16.
 */
public class EventsAdapter extends RecyclerView.Adapter {

    private static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout mWrapper;
        private ImageView mIcon;
        private ImageView mLogo;
        private TextView mPlayerName;
        private TextView mMinute;
        private TextView mNotes;


        public ViewHolder(View itemView) {
            super(itemView);

            mWrapper = ((LinearLayout) itemView.findViewById(R.id.wrapper));
            mIcon = ((ImageView) itemView.findViewById(R.id.icon));
            mLogo = ((ImageView) itemView.findViewById(R.id.logo));
            mPlayerName = ((TextView) itemView.findViewById(R.id.player_name));
            mMinute = ((TextView) itemView.findViewById(R.id.minute));
            mNotes = ((TextView) itemView.findViewById(R.id.note));

        }
    }

    public interface OnActionListener {
        void onItemClicked(int position, Event event);
    }

    private ArrayList<Event> mItems = new ArrayList<>();
    private OnActionListener mOnActionListener;

    public EventsAdapter() {

    }

    public EventsAdapter(OnActionListener listener) {
        mOnActionListener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_item, parent, false);
        final ViewHolder vh = new ViewHolder(v);
        vh.mWrapper.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int position = vh.getAdapterPosition();
                Event event = mItems.get(position);
                if (mOnActionListener != null) {
                    mOnActionListener.onItemClicked(position, event);
                }
                return true;
            }
        });
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Event event = mItems.get(position);

        if (event.getType() == Event.Type.GOAL) {
            ((ViewHolder) holder).mLogo.setVisibility(View.VISIBLE);
            Picasso.with(MatchManagerApplication.getContext())
                    .load(Communicator.API_URL + "Services/logo/" + event.getClubId())
                    .placeholder(R.drawable.placeholder)
                    .into(((ViewHolder) holder).mLogo);
        } else {
            ((ViewHolder) holder).mLogo.setVisibility(View.GONE);
        }

        ((ViewHolder) holder).mIcon.setImageResource(
                event.getType() == Event.Type.GOAL ? R.drawable.goal :
                        event.getType() == Event.Type.CHANGE ? R.drawable.change :
                                event.getType() == Event.Type.WARNING ? R.drawable.warning :
                                        R.drawable.kick
        );

        ((ViewHolder) holder).mPlayerName.setText(event.getPlayerName() + " • " + event.getPlayerNumber());
        ((ViewHolder) holder).mMinute.setText(MatchManagerApplication.getContext().getString(R.string.at_minute, event.getMinute()));
        ((ViewHolder) holder).mNotes.setText(event.getNotes());
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
