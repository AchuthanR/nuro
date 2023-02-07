package com.technophile.nuro.schedule;

import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textview.MaterialTextView;
import com.technophile.nuro.R;
import com.technophile.nuro.utils.ImageHelper;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final ArrayList<TaskModel> tasks;
    private ScheduleTableManager scheduleTableManager;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialCardView cardView;
        private final ShapeableImageView imageView;
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

        public MaterialCardView getCardView() {
            return cardView;
        }

        public ShapeableImageView getImageView() {
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

    public void setScheduleTableManager(ScheduleTableManager scheduleTableManager) {
        this.scheduleTableManager = scheduleTableManager;
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
                viewHolder.getTimerButton().setText(R.string.pause_task_button_text_fragment_schedule);
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
                        viewHolder.getTimerTextView().setText(R.string.completed_timer_text_view_text_fragment_schedule);
                        viewHolder.getTimerButton().setText(R.string.start_task_button_text_fragment_schedule);
                        viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
                        viewHolder.getResetButton().setEnabled(true);
                        if (viewHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                            viewHolder.remainingTime = tasks.get(viewHolder.getAdapterPosition()).duration;
                            if (scheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, -1) > 0) {
                                tasks.get(viewHolder.getAdapterPosition()).current_end_time = -1;
                            }
                        }
                        viewHolder.countDownTimer = null;
                    }
                };
                viewHolder.countDownTimer.start();
                viewHolder.getTimerTextView().setVisibility(View.VISIBLE);
                viewHolder.getResetButton().setEnabled(false);
                if (scheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, System.currentTimeMillis() + viewHolder.remainingTime) > 0) {
                    tasks.get(viewHolder.getAdapterPosition()).current_end_time = System.currentTimeMillis() + viewHolder.remainingTime;
                }
            }
            else {
                viewHolder.getTimerButton().setText(R.string.continue_task_button_text_fragment_schedule);
                viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
                viewHolder.countDownTimer.cancel();
                viewHolder.countDownTimer = null;
                viewHolder.getResetButton().setEnabled(true);
                if (scheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, -1) > 0) {
                    tasks.get(viewHolder.getAdapterPosition()).current_end_time = -1;
                }
            }
        });

        viewHolder.getResetButton().setOnClickListener(view1 -> {
            viewHolder.getTimerButton().setText(R.string.start_task_button_text_fragment_schedule);
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
            if (tasks.get(viewHolder.getAdapterPosition()).current_end_time != -1) {
                if (scheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, -1) > 0) {
                    tasks.get(viewHolder.getAdapterPosition()).current_end_time = -1;
                }
            }
        });

        viewHolder.getMarkAsDoneCheckBox().setOnCheckedChangeListener((compoundButton, b) -> {
            if (scheduleTableManager.update(tasks.get(viewHolder.getAdapterPosition()).id, b) > 0) {
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
        viewHolder.getImageView().setImageBitmap(ImageHelper.toBitmap(tasks.get(position).image));
        viewHolder.getNameTextView().setText(tasks.get(position).name);
        if (tasks.get(position).instruction == null || tasks.get(position).instruction.isEmpty()) {
            viewHolder.getInstructionTextView().setVisibility(View.GONE);
        }
        else {
            viewHolder.getInstructionTextView().setText(tasks.get(position).instruction);
            viewHolder.getInstructionTextView().setVisibility(View.VISIBLE);
        }
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        viewHolder.getStartTimeTextView().setText(dateFormat.format(tasks.get(position).start_time));
        viewHolder.getTimerButton().setText(R.string.start_task_button_text_fragment_schedule);
        viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
        if (viewHolder.countDownTimer != null) {
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;
        }
        if (tasks.get(position).current_end_time <= System.currentTimeMillis()) {
            if (TimeUnit.MILLISECONDS.toHours(tasks.get(position).duration) != 0) {
                long hour = TimeUnit.MILLISECONDS.toHours(tasks.get(position).duration);
                long minute = TimeUnit.MILLISECONDS.toMinutes(tasks.get(position).duration) - hour * 60;
                if (minute != 0) {
                    viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr %2$smin", hour, minute));
                } else {
                    viewHolder.getTimerTextView().setText(String.format("Duration: %1$shr", hour));
                }
            } else {
                viewHolder.getTimerTextView().setText(String.format("Duration: %1$smin", TimeUnit.MILLISECONDS.toMinutes(tasks.get(position).duration)));
            }
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
            viewHolder.getTimerTextView().setText(R.string.completed_timer_text_view_text_fragment_schedule);
            viewHolder.getResetButton().setEnabled(true);
        }

        viewHolder.getMarkAsDoneCheckBox().setChecked(tasks.get(position).completed);

        viewHolder.getCardView().setChecked(selectionTracker.isSelected(tasks.get(position).id));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public long getMaxId() {
        Optional<TaskModel> result = tasks.stream().max(Comparator.comparingLong(taskModel -> taskModel.id));
        if (result.isPresent()) {
            return result.get().id;
        }
        else {
            return getItemCount();
        }
    }

    public TaskModel getItem(int position) {
        return tasks.get(position);
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

    public void changeItem(int position, TaskModel newTaskModel) {
        tasks.set(position, newTaskModel);
        notifyItemChanged(position);
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