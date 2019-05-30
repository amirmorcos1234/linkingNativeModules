package ro.vodafone.mcare.android.presenter.adapterelements.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.List;

import io.realm.RealmList;
import ro.vodafone.mcare.android.client.adapters.basic.RecyclerElementsAdapter;

/**
 * Created by Victor Radulescu on 8/29/2017.
 */

public abstract class DynamicAdapterElement<T> extends AdapterElement {

    protected List<T> list;
    WeakReference<Context> contextWeakReference;

    public DynamicAdapterElement(int type, int order, @NonNull Context context, List<T> list) {
        super(type, order);
        this.contextWeakReference = new WeakReference<>(context);
        this.list = list;
    }

    @Override
    public Object getData() {
        return null;
    }
    public T getData(int position){
        position = position - order;
        if(getChildrenCount()>position){
            return list.get(position);
        }
        return null;
    }
    protected View getView(int viewType){
        return getView();
    }


    @Override
    protected final boolean isShouldBeCreatedFromData() {
        return true;
    }
    public final int getChildrenCount(){
        return list != null && (!(list instanceof RealmList) || ((RealmList) list).isValid()) ? list.size():0;
    }
    public Context getContext(){
        if(contextWeakReference!=null){
            return contextWeakReference.get();
        }
        return null;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getItemViewType(int position) {
        return RecyclerElementsAdapter.LIST_TYPE;
    }
}
