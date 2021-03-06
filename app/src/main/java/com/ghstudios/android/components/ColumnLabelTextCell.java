package com.ghstudios.android.components;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ghstudios.android.mhgendatabase.R;
import com.ghstudios.android.mhgendatabase.databinding.CellColumnLabelTextBinding;


public class ColumnLabelTextCell extends ConstraintLayout implements LabelValueComponent {

    private CellColumnLabelTextBinding binding;

    public ColumnLabelTextCell(Context context, String labelText, String valueText) {
        super(context);
        init(labelText, valueText);
    }

    public ColumnLabelTextCell(Context context) {
        super(context);
        init(null, null);
    }

    public ColumnLabelTextCell(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.ColumnLabelTextCell);

        try {
            String labelText = attributes.getString(R.styleable.ColumnLabelTextCell_labelText);
            String valueText = attributes.getString(R.styleable.ColumnLabelTextCell_valueText);

            init(labelText, valueText);
        } finally {
            attributes.recycle();
        }
    }

    public void init(String labelText, String valueText) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = CellColumnLabelTextBinding.inflate(inflater, this);

        setLabelText(labelText);
        setValueText(valueText);
    }

    @Override
    public void setLabelText(CharSequence labelText) {
        binding.labelText.setText(labelText);
        if (labelText == null || labelText.length() == 0) {
            binding.labelText.setVisibility(View.GONE);
        } else {
            binding.labelText.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setValueText(CharSequence valueText) {
        binding.valueText.setText(valueText);
    }

    @Override
    public CharSequence getLabelText() {
        return binding.labelText.getText();
    }

    @Override
    public CharSequence getValueText() {
        return binding.valueText.getText();
    }
}
