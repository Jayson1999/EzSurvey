package com.example.ezsurvey;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;


public class FormAdapter extends BaseAdapter {
    private final Context mContext;
    private final ArrayList<Form> forms;

    public FormAdapter(Context context, ArrayList<Form> forms) {
        this.mContext = context;
        this.forms = forms;
    }

    @Override
    public int getCount() {
        return forms.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Form form = forms.get(position);

        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.form_layout, null);
        }

        final TextView formLayoutName = (TextView)convertView.findViewById(R.id.formLTV);
        final ImageView formImg = (ImageView)convertView.findViewById(R.id.formIV);

        formLayoutName.setText(form.getName());     //set Form Name on XML

        //Setting initial as thumbnail icons for Forms with stored array XML
        Resources res = convertView.getResources();
        TypedArray icons = res.obtainTypedArray(R.array.initial);
        //Conduct comparison search with Index and ASCII codes of initial
            for(int i = 0; i<36 ;i++){
                //if initial is alphabet
                if(i==((int)form.getName().charAt(0))-65){
                    Drawable drawable = icons.getDrawable(i);
                    formImg.setImageDrawable(drawable);
                }
                //if initial is number
                else if(i==((int)form.getName().charAt(0))-48+26){
                    Drawable drawable = icons.getDrawable(i);
                    formImg.setImageDrawable(drawable);
                }
            }
        return convertView;
    }
}
