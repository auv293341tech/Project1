package com.example.project1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class QuestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SINGLE_CHOICE = 1;
    private static final int TYPE_MULTIPLE_CHOICE = 2;
    private static final int TYPE_QUANTITY = 3;
    private static final int TYPE_PHOTO = 4;

    private List<Question> questionList;
    private Context context;
    private List<RecyclerView.ViewHolder> holders = new ArrayList<>();

    public QuestionAdapter(Context context, List<Question> questionList) {
        this.context = context;
        this.questionList = questionList;
    }

    @Override
    public int getItemViewType(int position) {
        switch (questionList.get(position).getType()) {
            case "SINGLE_CHOICE":
                return TYPE_SINGLE_CHOICE;
            case "MULTIPLE_CHOICE":
                return TYPE_MULTIPLE_CHOICE;
            case "QUANTITY":
                return TYPE_QUANTITY;
            case "PHOTO":
                return TYPE_PHOTO;
            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder holder;
        switch (viewType) {
            case TYPE_SINGLE_CHOICE:
                holder = new SingleChoiceViewHolder(inflater.inflate(R.layout.item_question_single_choice, parent, false));
                break;
            case TYPE_MULTIPLE_CHOICE:
                holder = new MultipleChoiceViewHolder(inflater.inflate(R.layout.item_question_multiple_choice, parent, false));
                break;
            case TYPE_QUANTITY:
                holder = new QuantityViewHolder(inflater.inflate(R.layout.item_question_quantity, parent, false));
                break;
            case TYPE_PHOTO:
                holder = new PhotoViewHolder(inflater.inflate(R.layout.item_question_photo, parent, false));
                break;
            default:
                throw new IllegalArgumentException("Invalid view type");
        }
        holders.add(holder);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Question question = questionList.get(position);
        switch (holder.getItemViewType()) {
            case TYPE_SINGLE_CHOICE:
                ((SingleChoiceViewHolder) holder).bind(question);
                break;
            case TYPE_MULTIPLE_CHOICE:
                ((MultipleChoiceViewHolder) holder).bind(question);
                break;
            case TYPE_QUANTITY:
                ((QuantityViewHolder) holder).bind(question);
                break;
            case TYPE_PHOTO:
                ((PhotoViewHolder) holder).bind(question);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    public HashMap<String, Object> getAnswers() {
        HashMap<String, Object> answers = new HashMap<>();
        for (int i = 0; i < questionList.size(); i++) {
            Question question = questionList.get(i);
            RecyclerView.ViewHolder holder = holders.get(i);
            switch (holder.getItemViewType()) {
                case TYPE_SINGLE_CHOICE:
                    answers.put(question.getQuestionId(), ((SingleChoiceViewHolder) holder).getAnswer());
                    break;
                case TYPE_MULTIPLE_CHOICE:
                    answers.put(question.getQuestionId(), ((MultipleChoiceViewHolder) holder).getAnswer());
                    break;
                case TYPE_QUANTITY:
                    answers.put(question.getQuestionId(), ((QuantityViewHolder) holder).getAnswer());
                    break;
                case TYPE_PHOTO:
                    // TODO: Get photo answer
                    break;
            }
        }
        return answers;
    }

    // ViewHolder for Single Choice Questions
    static class SingleChoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        RadioGroup rgOptions;

        SingleChoiceViewHolder(View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            rgOptions = itemView.findViewById(R.id.rgOptions);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getText());
            rgOptions.removeAllViews();
            for (String option : question.getOptions()) {
                RadioButton radioButton = new RadioButton(itemView.getContext());
                radioButton.setText(option);
                rgOptions.addView(radioButton);
            }
        }

        public String getAnswer() {
            int selectedId = rgOptions.getCheckedRadioButtonId();
            if (selectedId != -1) {
                RadioButton selectedRadioButton = itemView.findViewById(selectedId);
                return selectedRadioButton.getText().toString();
            }
            return "";
        }
    }

    // ViewHolder for Multiple Choice Questions
    static class MultipleChoiceViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        ViewGroup llCheckboxes;

        MultipleChoiceViewHolder(View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            llCheckboxes = itemView.findViewById(R.id.llCheckboxes);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getText());
            llCheckboxes.removeAllViews();
            for (String option : question.getOptions()) {
                CheckBox checkBox = new CheckBox(itemView.getContext());
                checkBox.setText(option);
                llCheckboxes.addView(checkBox);
            }
        }

        public List<String> getAnswer() {
            List<String> selectedAnswers = new ArrayList<>();
            for (int i = 0; i < llCheckboxes.getChildCount(); i++) {
                CheckBox checkBox = (CheckBox) llCheckboxes.getChildAt(i);
                if (checkBox.isChecked()) {
                    selectedAnswers.add(checkBox.getText().toString());
                }
            }
            return selectedAnswers;
        }
    }

    // ViewHolder for Quantity Questions
    static class QuantityViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        TextView tvQuantityValue;
        TextView btnMinus;
        TextView btnPlus;

        QuantityViewHolder(View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
            tvQuantityValue = itemView.findViewById(R.id.tvQuantityValue);
            btnMinus = itemView.findViewById(R.id.btnMinus);
            btnPlus = itemView.findViewById(R.id.btnPlus);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getText());
            tvQuantityValue.setText("1");

            btnMinus.setOnClickListener(v -> {
                int quantity = Integer.parseInt(tvQuantityValue.getText().toString());
                if (quantity > 1) {
                    quantity--;
                    tvQuantityValue.setText(String.valueOf(quantity));
                }
            });

            btnPlus.setOnClickListener(v -> {
                int quantity = Integer.parseInt(tvQuantityValue.getText().toString());
                quantity++;
                tvQuantityValue.setText(String.valueOf(quantity));
            });
        }

        public String getAnswer() {
            return tvQuantityValue.getText().toString();
        }
    }

    // ViewHolder for Photo Upload Questions
    static class PhotoViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestionText;
        // TODO: Add Button and ImageView for photo upload logic

        PhotoViewHolder(View itemView) {
            super(itemView);
            tvQuestionText = itemView.findViewById(R.id.tvQuestionText);
        }

        void bind(Question question) {
            tvQuestionText.setText(question.getText());
        }
    }
}
