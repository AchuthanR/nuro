package com.capstone.autism_training.visual_schedule;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.selection.ItemDetailsLookup;
import androidx.recyclerview.selection.SelectionTracker;
import androidx.recyclerview.widget.RecyclerView;

import com.capstone.autism_training.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder> {

    private final ArrayList<TaskModel> tasks;
    private SelectionTracker<Long> selectionTracker;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final Context context;
        private final MaterialCardView cardView;
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView instructionTextView;
        private final TextView startTimeTextView;
        private final MaterialButton timerButton;
        private final TextView timerTextView;
        private final MaterialButton resetButton;
        private final TaskItemDetails taskItemDetails;

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

        public TextView getNameTextView() {
            return nameTextView;
        }

        public TextView getInstructionTextView() {
            return instructionTextView;
        }

        public TextView getStartTimeTextView() {
            return startTimeTextView;
        }

        public MaterialButton getTimerButton() {
            return timerButton;
        }

        public TextView getTimerTextView() {
            return timerTextView;
        }

        public MaterialButton getResetButton() {
            return resetButton;
        }

        public ItemDetailsLookup.ItemDetails<Long> getItemDetails() {
            taskItemDetails.setPosition(getAdapterPosition());
            taskItemDetails.setSelectionKey(tasks.get(getAdapterPosition()).id);
            return taskItemDetails;
        }

        public final void bind(boolean isActive) {
            cardView.setChecked(isActive);
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

    public void setSelectionTracker(SelectionTracker<Long> selectionTracker) {
        this.selectionTracker = selectionTracker;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.layout_task_item, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        BitmapFactory.Options options1 = new BitmapFactory.Options();
        options1.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(tasks.get(position).image, 0, tasks.get(position).image.length, options1);

        final int REQUIRED_SIZE = 300;

        int width_tmp = options1.outWidth, height_tmp = options1.outHeight;
        int scale = 1;
        while (width_tmp / 2 >= REQUIRED_SIZE && height_tmp / 2 >= REQUIRED_SIZE) {
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options options2 = new BitmapFactory.Options();
        options2.inSampleSize = scale;
        options2.inJustDecodeBounds = false;
        viewHolder.getImageView().setImageBitmap(BitmapFactory.decodeByteArray(tasks.get(position).image, 0, tasks.get(position).image.length, options2));
        viewHolder.getNameTextView().setText(tasks.get(position).name);
        viewHolder.getInstructionTextView().setText(tasks.get(position).instruction);
        DateFormat dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        viewHolder.getStartTimeTextView().setText(dateFormat.format(tasks.get(position).start_time));
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

        final boolean[] isOngoing = {false};
        final CountDownTimer[] countDownTimer = new CountDownTimer[1];
        final long[] remainingTime = {tasks.get(position).duration};
        viewHolder.getTimerButton().setOnClickListener(view -> {
            if (!isOngoing[0]) {
                viewHolder.getTimerButton().setText(R.string.pause_task_button_text_activity_visual_schedule);
                viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_pause_24);
                countDownTimer[0] = new CountDownTimer(remainingTime[0], 1000) {
                    public void onTick(long millisUntilFinished) {
                        remainingTime[0] = millisUntilFinished;
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
                        remainingTime[0] = tasks.get(viewHolder.getAdapterPosition()).duration;
                        isOngoing[0] = false;
                    }
                };
                countDownTimer[0].start();
                viewHolder.getTimerTextView().setVisibility(View.VISIBLE);
                viewHolder.getResetButton().setEnabled(false);
                isOngoing[0] = true;
            }
            else {
                viewHolder.getTimerButton().setText(R.string.continue_task_button_text_activity_visual_schedule);
                viewHolder.getTimerButton().setIconResource(R.drawable.ic_round_play_arrow_24);
                countDownTimer[0].cancel();
                viewHolder.getResetButton().setEnabled(true);
                isOngoing[0] = false;
            }
        });

        viewHolder.getResetButton().setOnClickListener(view -> {
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
            view.setEnabled(false);
            remainingTime[0] = tasks.get(viewHolder.getAdapterPosition()).duration;
            isOngoing[0] = false;
        });

        viewHolder.bind(selectionTracker.isSelected(tasks.get(position).id));
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void addItem(TaskModel taskModel) {
        tasks.add(0, taskModel);
        notifyItemInserted(0);
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