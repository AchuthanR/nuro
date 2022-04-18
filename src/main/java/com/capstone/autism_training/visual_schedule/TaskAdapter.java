package com.capstone.autism_training.visual_schedule;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.capstone.autism_training.utilities.ImageHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textview.MaterialTextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final ArrayList<TaskModel> tasks;
    private VisualScheduleTableManager visualScheduleTableManager;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final MaterialTextView nameTextView;
        private final MaterialTextView instructionTextView;
        private final MaterialTextView startTimeTextView;
        private final MaterialButton timerButton;
        private final MaterialTextView timerTextView;
        private final MaterialButton resetButton;
        private final MaterialCheckBox markAsDoneCheckBox;
        private final TaskItemDetails taskItemDetails;
        public CountDownTimer countDownTimer;
        public long remainingTime;

        public ViewHolder(View view) {
            super(view);
            context = view.getContext();
            cardView = view.findViewById(R.id.cardView);
            imageView = view.findViewById(R.id.imageView);
            nameTextView = view.findViewById(R.id.nameTextView);
            instructionTextView = view.findViewById(R.id.instructionTextView);
            startTimeTextView = view.findViewById(R.id.startTimeTextView);
            timerButton = view.findViewById(R.id.timerButton);
            timerTextView = view.findViewById(R.id.timerTextView);
            resetButton = view.findViewById(R.id.resetButton);
            markAsDoneCheckBox = view.findViewById(R.id.markAsDoneCheckBox);
            taskItemDetails = new TaskItemDetails();
        }

        public Context getContext() {
            return context;
        }

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ImageView getImageView() {
            return imageView;
        }

        public MaterialTextView getNameTextView() {
            return nameTextView;
        }

        public MaterialTextView getInstructionTextView() {
            return instructionTextView;
        }

        public MaterialTextView getStartTimeTextView() {
            return startTimeTextView;
        }

        public MaterialButton getTimerButton() {
            return timerButton;
        }

        public MaterialTextView getTimerTextView() {
            return timerTextView;
        }

        public MaterialButton getResetButton() {
            return resetButton;
        }

        public MaterialCheckBox getMarkAsDoneCheckBox() {
            return markAsDoneCheckBox;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            taskItemDetails.setPosition(getAdapterPosition());
            taskItemDetails.setSelectionKey(tasks.get(getAdapterPosition()).id);
            return taskItemDetails;
        }
    }

    public TaskAdapter() {
        tasks = new ArrayList<>();
        setHasStableIds(true);
    }

    @Override
    public long getItemId(int position) {
        return tasks.get(position).id;
    }

    public void setVisualScheduleTableManager(VisualScheduleTableManager visualScheduleTableManager) {
        this.visualScheduleTableManager = visualScheduleTableManager;
    }

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_task_item, viewGroup, false);

        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.getTimerButton().setOnClickListener(view1 -> {
            if (viewHolder.countDownTimer == null) {
                viewHolder.getTimerButton().setText(R.string.pause_task_button_text_activity_visual_schedule);
                viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_pause_24);
                viewHolder.countDownTimer = new CountDownTimer(viewHolder.remainingTime, 1000) {
                    public void onTick(long millisUntilFinished) {
                        viewHolder.remainingTime = millisUntilFinished;
                        long hour = TimeUnit.MILLISECONDS.toHours(millisUntilFinished);
                        long minute = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - hour * 60;
                        long second = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - hour * 60 * 60 - minute * 60;
                        if (hour != 0) {
                            viewHolder.getTimerTextView().setText(String.format(Locale.getDefault(), "%d:%02d:%02d left", hour, minute, second));
                        }
                        else if (minute != 0) {
                            viewHolder.getTimerTextView().setText(String.format(Locale.getDefault(), "%d:%02d left", minute, second));
                        }
                        else {
                            viewHolder.getTimerTextView().setText(String.format(Locale.getDefault(), "%02d left", second));
                        }
                    }

                    public void onFinish() {
                        viewHolder.getTimerTextView().setText(R.string.completed_timer_text_view_text_activity_visual_schedule);
                        viewHolder.getTimerButton().setText(R.string.start_task_button_text_activity_visual_schedule);
                        viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
                        if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                            viewHolder.remainingTime = tasks.get(viewHolder.getAdapterPosition()).duration;
                            visualScheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, -1);
                        }
                        viewHolder.countDownTimer = null;
                    }
                };
                viewHolder.countDownTimer.start();
                viewHolder.getTimerTextView().setVisibility(View.VISIBLE);
                viewHolder.getResetButton().setEnabled(false);
                visualScheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, System.currentTimeMillis() + viewHolder.remainingTime);
            }
            else {
                viewHolder.getTimerButton().setText(R.string.continue_task_button_text_activity_visual_schedule);
                viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
                viewHolder.countDownTimer.cancel();
                viewHolder.countDownTimer = null;
                viewHolder.getResetButton().setEnabled(true);
                visualScheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, -1);
            }
        });

        viewHolder.getResetButton().setOnClickListener(view1 -> {
            viewHolder.getTimerButton().setText(R.string.start_task_button_text_activity_visual_schedule);
            viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
            if (TimeUnit.MILLISECONDS.toHours(tasks.get(viewHolder.getAdapterPosition()).duration) != 0) {
                long hour = TimeUnit.MILLISECONDS.toHours(tasks.get(viewHolder.getAdapterPosition()).duration);
                long minute = TimeUnit.MILLISECONDS.toMinutes(tasks.get(viewHolder.getAdapterPosition()).duration) - hour * 60;
                if (minute != 0) {
                    viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr %2$smin", hour, minute));
                }
                else {
                    viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr", hour));
                }
            }
            else {
                viewHolder.getTimerTextView().setText(String.format("Duration: %1$smin", TimeUnit.MILLISECONDS.toMinutes(tasks.get(viewHolder.getAdapterPosition()).duration)));
            }
            view1.setEnabled(false);
            viewHolder.remainingTime = tasks.get(viewHolder.getAdapterPosition()).duration;
        });

        viewHolder.getMarkAsDoneCheckBox().setOnCheckedChangeListener((compoundButton, b) -> {
            boolean success = visualScheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, b) > 0;
            if (success) {
                tasks.get(viewHolder.getAdapterPosition()).completed = b;
            }
            else {
                viewHolder.getMarkAsDoneCheckBox().setChecked(!b);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        viewHolder.getImageView().setImageBitmap(ImageHelper.toCompressedBitmap(tasks.get(position).image));
        viewHolder.getNameTextView().setText(tasks.get(position).name);
        viewHolder.getInstructionTextView().setText(tasks.get(position).instruction);
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        viewHolder.getStartTimeTextView().setText(dateFormat.format(tasks.get(position).start_time));
        viewHolder.getTimerButton().setText(R.string.start_task_button_text_activity_visual_schedule);
        viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
        if (viewHolder.countDownTimer != null) {
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;
        }
        if (TimeUnit.MILLISECONDS.toHours(tasks.get(position).duration) != 0) {
            long hour = TimeUnit.MILLISECONDS.toHours(tasks.get(position).duration);
            long minute = TimeUnit.MILLISECONDS.toMinutes(tasks.get(position).duration) - hour * 60;
            if (minute != 0) {
                viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr %2$smin", hour, minute));
            }
            else {
                viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr", hour));
            }
        }
        else {
            viewHolder.getTimerTextView().setText(String.format("Duration: %1$smin", TimeUnit.MILLISECONDS.toMinutes(tasks.get(position).duration)));
        }

        if (tasks.get(position).current_end_time > System.currentTimeMillis()) {
            viewHolder.remainingTime = tasks.get(position).current_end_time - System.currentTimeMillis();
            viewHolder.getTimerButton().callOnClick();
        }
        else if (tasks.get(position).current_end_time == -1) {
            viewHolder.remainingTime = tasks.get(position).duration;
        }
        else {
            viewHolder.remainingTime = tasks.get(position).duration;
            viewHolder.getTimerTextView().setText(R.string.completed_timer_text_view_text_activity_visual_schedule);
            visualScheduleTableManager.update(tasks.get(position).id, -1);
        }

        viewHolder.getMarkAsDoneCheckBox().setChecked(tasks.get(position).completed);

        viewHolder.getCardView().setChecked(selectionTracker.isSelected(tasks.get(position).id));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addItem(TaskModel taskModel) {
        tasks.add(taskModel);
        notifyItemInserted(tasks.size() - 1);
    }

    public void addItemAtRightPosition(TaskModel taskModel) {
        for (int i=0; i<tasks.size(); i++) {
            if (tasks.get(i).start_time > taskModel.start_time) {
                tasks.add(i, taskModel);
                notifyItemInserted(i);
                return;
            }
        }
        tasks.add(taskModel);
        notifyItemInserted(tasks.size() - 1);
    }

    public void removeItem(int position) {
        tasks.remove(position);
        notifyItemRemoved(position);
    }

    public void clearAll() {
        int size = tasks.size();
        tasks.clear();
        notifyItemRangeRemoved(0, size);
    }
}