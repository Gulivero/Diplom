package slavin.fit.bstu.quest.Adapter;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import slavin.fit.bstu.quest.Model.Image;
import slavin.fit.bstu.quest.Model.Quest;
import slavin.fit.bstu.quest.R;

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private static ClickListener clickListener;
    private List<Quest> questsList;
    private List<Image> imagesList;
    private Context context;
    private int position;

    public DataAdapter(Context context, List<Quest> questsList, List<Image> imagesList) {
        this.context = context;
        this.questsList = questsList;
        this.imagesList = imagesList;

    }

    public void updateList(List<Quest> list){
        questsList = list;
        notifyDataSetChanged();
    }


    public void setOnItemClickListener(ClickListener clickListener) {
        DataAdapter.clickListener = clickListener;
    }

    public int getPosition() {
        return position;
    }

    public interface ClickListener {
        void onItemClick(int position, View v);
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        viewHolder.tv_androidName.setText(questsList.get(i).getName());
        viewHolder.tv_androidDescription.setText(questsList.get(i).getDescription());
        viewHolder.tv_androidReward.setText(questsList.get(i).getReward());
        for (Image image : imagesList) {
            if (image.getQuestId() != null && image.getQuestId() == questsList.get(i).getId())
                Glide.with(context).load(image.getImageRef()).into(viewHolder.img_android);
        }
    }

    @Override
    public int getItemCount() {
        return questsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnCreateContextMenuListener{
        TextView tv_androidName, tv_androidDescription, tv_androidReward;
        ImageView img_android;
        public ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            view.setOnCreateContextMenuListener(this);

            position = getAdapterPosition();
            tv_androidName = (TextView)view.findViewById(R.id.tv_androidName);
            tv_androidDescription = (TextView)view.findViewById(R.id.tv_androidDescription);
            tv_androidReward = (TextView)view.findViewById(R.id.tv_androidReward);
            img_android = (ImageView)view.findViewById(R.id.img_android);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            //menuInfo is null
            menu.add(this.getAdapterPosition(), R.id.complete, 0, "Завершить");
            menu.add(this.getAdapterPosition(), R.id.edit, 0, "Изменить");//groupId, itemId, order, title
            menu.add(this.getAdapterPosition(), R.id.delete, 0, "Удалить");
        }

        @Override
        public void onClick(View v) {
            clickListener.onItemClick(getAdapterPosition(), v);
        }
    }
}