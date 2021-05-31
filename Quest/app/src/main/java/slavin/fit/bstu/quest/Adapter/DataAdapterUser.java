package slavin.fit.bstu.quest.Adapter;

import android.content.Context;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.Model.User;
import slavin.fit.bstu.quest.R;

public class DataAdapterUser extends RecyclerView.Adapter<DataAdapterUser.ViewHolder> {
    private static ClickListener clickListener;
    private List<User> usersList;
    private List<Image> imagesList;
    private Context context;
    private int position;

    public DataAdapterUser(Context context, List<User> usersList, List<Image> imagesList) {
        this.context = context;
        this.usersList = usersList;
        this.imagesList = imagesList;

    }

    public void updateList(List<User> list){
        usersList = list;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        DataAdapterUser.clickListener = clickListener;
    }

    public int getPosition() {
        return position;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public DataAdapterUser.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layoutuser, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        Integer a = usersList.get(i).getCount_Complete();
        String b = a.toString();
        viewHolder.tv_androidName.setText(usersList.get(i).getName());
        viewHolder.tv_androidDescription.setText(usersList.get(i).getCommunication());
        viewHolder.tv_androidCount.setText(b);
        for (Image image : imagesList) {
            if (image.getUserId() != null && image.getUserId() == usersList.get(i).getId())
                Glide.with(context).load(image.getImageRef()).into(viewHolder.imageProfile);
        }
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tv_androidName, tv_androidDescription, tv_androidCount;
        ImageView imageProfile;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);

            position = getAdapterPosition();
            tv_androidName = (TextView)view.findViewById(R.id.tv_androidName);
            tv_androidDescription = (TextView)view.findViewById(R.id.tv_androidDescription);
            tv_androidCount = (TextView)view.findViewById(R.id.tv_androidCount);
            imageProfile = (ImageView)view.findViewById(R.id.imageProfile);
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}