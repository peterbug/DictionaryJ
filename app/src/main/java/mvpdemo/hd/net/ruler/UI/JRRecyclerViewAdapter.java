package mvpdemo.hd.net.ruler.UI;

import android.support.v4.util.ArraySet;
import android.support.v7.widget.RecyclerView.Adapter;

import java.util.List;

public abstract class JRRecyclerViewAdapter extends Adapter {
    private ArraySet<Integer> selectedList = new ArraySet<>();
    private boolean[] returnValue = new boolean[2];

    public void setData(List data) {
        selectedList.clear();
    }

    /**
     * @return returnValue [0]-current stated after user operation, returnValue[1]- true when all items are selected
     ***/
    protected boolean[] triggleSelected(int position) {
        boolean sel = selectedList.contains(position);
        if (sel) {
            selectedList.remove((Integer) position);
        } else {
            selectedList.add(position);
        }
        returnValue[1] = false;
        if (!sel) {
            returnValue[1] = selectedList.size() == getItemCount();
        }
        triggleSelectedAllUI(returnValue[1], selectedList.size(), getItemCount());
        notifyDataSetChanged();
        return returnValue;
    }

    protected void triggleSelectAll() {
        returnValue[1] = !returnValue[1];
        if (returnValue[1]) {
            for (int index = 0; index < getItemCount(); index++) {
                selectedList.add(index);
            }
        } else {
            selectedList.clear();
        }
        triggleSelectedAllUI(returnValue[1], selectedList.size(), getItemCount());
        notifyDataSetChanged();
    }

    protected ArraySet<Integer> getSelectedPositionSet() {
        return selectedList;
    }

    protected boolean isSelected(int position) {
        return selectedList.contains(position);
    }

    /**
     * @param isSelectAll   [0]-current stated after user operation, returnValue[1]- true when all items are selected
     * @param selectedCount
     * @param allCount
     ***/
    protected abstract void triggleSelectedAllUI(boolean isSelectAll, long selectedCount, long allCount);
}
