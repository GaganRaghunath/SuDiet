package com.androidproject.sudiet.adapter;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.androidproject.sudiet.R;

import com.androidproject.sudiet.object.ActionTip;
import com.androidproject.sudiet.presenter.AssistantPresenter;

import java.util.List;

public class AssistantAdapter extends RecyclerView.Adapter<AssistantAdapter.ViewHolder> {
    private List<ActionTip> actionTips;
    private AssistantPresenter presenter;
    private Resources res;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AssistantAdapter(AssistantPresenter assistantPresenter, final Resources resources, List<ActionTip> tips) {
        this.res = resources;
        this.presenter = assistantPresenter;
        this.actionTips = tips;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AssistantAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_assistant_item, parent, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TextView actionTipTitle = (TextView) holder.mView.findViewById(R.id.fragment_assistant_item_title);
        TextView actionTipDescription = (TextView) holder.mView.findViewById(R.id.fragment_assistant_item_description);
        actionTipTitle.setText(actionTips.get(position).getTipTitle());
        actionTipDescription.setText(actionTips.get(position).getTipDescription());
        String actionTipTitleString = actionTips.get(position).getTipTitle();

        View.OnClickListener actionListener = getActionListener(actionTipTitleString);
    }

    @NonNull
    private View.OnClickListener getActionListener(String actionTipTitleString) {
        View.OnClickListener actionListener;
        //TODO: OOP or at least switch
        if (actionTipTitleString.equals(res.getString(R.string.assistant_feedback_title))) {
            actionListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.userSupportAsked();

                }
            };
        } else {
            actionListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    presenter.userAskedAddReading();
                }
            };
        }
        return actionListener;
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return actionTips.size();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        View mView;

        ViewHolder(View v) {
            super(v);
            mView = v;
        }
    }
}
