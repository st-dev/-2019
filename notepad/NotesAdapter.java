package com.example.user.notepad;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder>
implements ItemTouchHelperAdapter{

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title, content;

        public ViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
        }
    }

    //用于实现点击编辑的内部接口
    public interface OnItemClickListener{
        void onItemClick(View view,int position);
    }

    private List<NotesBuilder> notesList;
    File directory;
    private OnItemClickListener mOnItemClickListener;
    private View view;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public NotesAdapter(List<NotesBuilder> notesList, File f, View v) {
        this.notesList = notesList;
        this.directory = f;
        this.view = v;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        NotesBuilder note = notesList.get(position);
        holder.title.setText(note.getTitle());
        holder.content.setText(note.getContent());
        if(mOnItemClickListener != null){//点击时调用处理函数
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public void onItemDelete(final int position) {
        final NotesBuilder note = notesList.get(position);//准备被删除的笔记副本
        final String fname = note.getTitle();
        notesList.remove(position);
        notifyItemRemoved(position);//在界面上删除笔记
        final File[] files = directory.listFiles();
        final boolean[] delete = {true};
        int i = 0;
        for(; i < files.length; ++i)
            if(files[i].getName().equals(fname))
                break;
        final int finalI = i;//得到笔记文件的索引
        final Snackbar snackbar = Snackbar
            .make(view, "REMOVED", Snackbar.LENGTH_LONG)
            .setAction("UNDO", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    notesList.add(position, note);
                    notifyItemInserted(position);
                    delete[0] = false;
                }
            })
            .addCallback(new Snackbar.Callback(){
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    if(delete[0])//如果没有撤销，则删除笔记文件
                        files[finalI].delete();
                }
            })
            ;
        snackbar.show();
    }
}
