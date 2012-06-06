package info.vanderkooy.ucheck;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class ListAdapter extends SimpleAdapter {

	private Context context;
	private List<HashMap<String, String>> data;
	
	public ListAdapter(Context context, List<HashMap<String, String>> data,
			int resource, String[] from, int[] to) {
		super(context, data, resource, from, to);
		this.context = context;
		this.data = data;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup viewGroup) {
		HashMap<String, String> entry = data.get(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.row, null);
		}
		TextView subject = (TextView) convertView.findViewById(R.id.subject);
		subject.setText(entry.get("subject"));

		TextView grade = (TextView) convertView.findViewById(R.id.grade);
		grade.setText(entry.get("grade"));
		
		TextView EC = (TextView) convertView.findViewById(R.id.EC);
		EC.setText(entry.get("EC"));
		
		if(subject.getText().equals("Vak")) {
			subject.setTypeface(null, Typeface.BOLD);
			grade.setTypeface(null, Typeface.BOLD);
			EC.setTypeface(null, Typeface.BOLD);
		}
		
		if(entry.get("gehaald") != null) {
			grade.setTextColor(Color.RED);
		}


		return convertView;
	}

}