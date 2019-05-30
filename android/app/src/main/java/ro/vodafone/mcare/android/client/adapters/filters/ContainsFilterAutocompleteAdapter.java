package ro.vodafone.mcare.android.client.adapters.filters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import ro.vodafone.mcare.android.ui.utils.TextUtils;

/**
 * Created by Ionut Neagu on 10/06/2017.
 */

public class ContainsFilterAutocompleteAdapter<T> extends ArrayAdapter<T> implements Filterable {
    private List<T> listObjects;
    List<T> suggestions = new ArrayList<>();
    private int resource;
    private int maxSuggestions;
    private boolean startsWith;

    private Filter mFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();

            if (constraint != null) {
                suggestions.clear();
                for (T object : listObjects) {

                    if(!startsWith) {
                        if (!(!constraint.toString().isEmpty() && (object.toString().toLowerCase().contains(constraint.toString().toLowerCase()) || TextUtils.removeDiacriticalMarks(object.toString()).toLowerCase().contains(constraint.toString().toLowerCase())))) {
                            continue;
                        }
                    }
                    else
                    {
                        if (!(!constraint.toString().isEmpty() && (object.toString().toLowerCase().startsWith(constraint.toString().toLowerCase()) || TextUtils.removeDiacriticalMarks(object.toString()).toLowerCase().startsWith(constraint.toString().toLowerCase())))) {
                            continue;
                        }
                    }


                    if (suggestions.size() == maxSuggestions) {
                        break;
                    } else {
                        suggestions.add(object);
                    }
                }

                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
            }
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence contraint, FilterResults results) {
            if (results == null) {
                return;
            }

            List<T> filteredList = (List<T>) results.values;
            if (results.count > 0) {
                clear();
                for (T filteredObject : filteredList) {
                    add(filteredObject);
                }
                notifyDataSetChanged();
            }
        }
    };



    public ContainsFilterAutocompleteAdapter(Context context, List<T> listObjects) {
        super(context, android.R.layout.simple_list_item_1, listObjects);
        _containsFilterAutocompleteAdapter(listObjects, 3, false);
    }

    public ContainsFilterAutocompleteAdapter(Context context, List<T> listObjects, int maxSuggestions, boolean startsWith) {
        super(context, android.R.layout.simple_list_item_1, listObjects);
        _containsFilterAutocompleteAdapter(listObjects, maxSuggestions, startsWith);
    }

    private void _containsFilterAutocompleteAdapter(List<T> listObjects, int maxSuggestions, boolean startsWith) {
        if(listObjects == null)
            listObjects = new ArrayList<>();
        this.listObjects = new ArrayList<>(listObjects);
        this.resource = android.R.layout.simple_list_item_1;
        this.maxSuggestions = maxSuggestions;
        this.startsWith = startsWith;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Object listObject = getItem(position);
        Holder holder;
        if (convertView != null) {
            holder = (Holder) convertView.getTag();
        } else {
            convertView = LayoutInflater.from(getContext()).inflate(resource, parent, false);
            holder = new Holder(convertView);
            convertView.setTag(holder);
        }
        holder.name.setText(listObject.toString());
        return convertView;
    }

    public T getFirstItem() {
        if (suggestions.size() > 0) {
            return suggestions.get(0);
        }
        return null;
    }


    static class Holder {
        TextView name;

        public Holder(View view) {
            name = (TextView) view.findViewById(android.R.id.text1);
        }
    }
}