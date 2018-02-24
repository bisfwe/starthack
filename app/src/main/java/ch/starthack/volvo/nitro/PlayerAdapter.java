package ch.starthack.volvo.nitro;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ch.starthack.volvo.nitro.R;

public class PlayerAdapter extends BaseAdapter {

    private final Context mContext;
    private List<Pair<String, Integer>> players;

    public PlayerAdapter(Context context, List<Pair<String, Integer>> players) {
        mContext = context;
        this.players = players;
    }

    static class ViewHolder {
        public TextView playerName;
        public TextView playerScore;
        public ImageView playerAvatar;
    }

    @Override
    public int getCount() {
        return players.size();
    }

    @Override
    public Object getItem(int position) {
        return players.get(position);
    }

    @Override
    public long getItemId(int position) {
        return players.get(position).hashCode();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {

            convertView = LayoutInflater.from(mContext).inflate(R.layout.rowitem, parent, false);
            holder = new ViewHolder();
            holder.playerName = (TextView) convertView.findViewById(R.id.list_player_name);
            holder.playerScore = (TextView) convertView.findViewById(R.id.list_player_score);
            holder.playerAvatar = (ImageView) convertView.findViewById(R.id.list_player_avatar);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String[] colors = new String[]{"pink", "blue", "green", "purple", "red"};

        Pair<String, Integer> item = players.get(position);

        holder.playerName.setText(item.first);
        holder.playerScore.setText(item.second.toString());
        Integer avatarId = this.mContext.getResources().getIdentifier("avatar_" + item.first.toLowerCase(), "drawable", "ch.starthack.volvo.nitro");
        holder.playerAvatar.setImageResource(avatarId);
        return convertView;
    }
}