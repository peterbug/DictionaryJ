package mvpdemo.hd.net.ruler.UI;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.ArraySet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import mvpdemo.hd.greendao.DbInit;
import mvpdemo.hd.greendao.entity.Groups;
import mvpdemo.hd.greendao.entity.Word;
import mvpdemo.hd.net.ruler.R;
import mvpdemo.hd.net.ruler.Utils.JRInputMethodUtils;
import mvpdemo.hd.net.ruler.business.ILIST_LISTENER;

import static android.widget.Toast.LENGTH_SHORT;

public class GroupManageFragment extends Fragment {
    private ImageView image_selected;
    private TextView text_selected;
    private TextView play;
    private TextView delete;
    private CharSequence title;
    JRRecyclerAdapter jrRecyclerAdapter;
    //    private boolean selected_all = false;
    View edit_layout;
    EditText group_name_edittext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_manage_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        LinearLayoutManager mg = new LinearLayoutManager(getActivity());
        mg.setOrientation(LinearLayout.VERTICAL);
        mg.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(mg);
        jrRecyclerAdapter = new JRRecyclerAdapter(container.getContext());
        jrRecyclerAdapter.setData(DbInit.getAllGroups());
        recyclerView.setAdapter(jrRecyclerAdapter);
        image_selected = (ImageView) v.findViewById(R.id.image_selected);
        text_selected = (TextView) v.findViewById(R.id.text_selected);
        play = (TextView) v.findViewById(R.id.play);
        delete = (TextView) v.findViewById(R.id.delete);
        edit_layout = v.findViewById(R.id.edit_layout);
        group_name_edittext = (EditText) v.findViewById(R.id.group_name_edittext);
        image_selected.setOnClickListener(listener);
        text_selected.setOnClickListener(listener);
        play.setOnClickListener(listener);
        delete.setOnClickListener(listener);
        v.findViewById(R.id.preview_all_words).setOnClickListener(listener);
        v.findViewById(R.id.modify_name).setOnClickListener(listener);
        v.findViewById(R.id.add).setOnClickListener(listener);
        v.findViewById(R.id.back).setOnClickListener(listener);
        v.findViewById(R.id.ok).setOnClickListener(listener);
        TextView textView = (TextView) v.findViewById(R.id.title);
        if (!TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        jrRecyclerAdapter.setListener(ilist_listener);
        return v;
    }

    public void setTitle(CharSequence titleString) {
        title = titleString;
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_selected:
                    jrRecyclerAdapter.triggleSelectAll();
                    break;
                case R.id.text_selected:
                    break;
                case R.id.play: {
                    ArrayList<Word> list = new ArrayList<>();
                    ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
                    for (Integer g : set) {
                        List<Word> words = DbInit.getWordsByGroupId(jrRecyclerAdapter.getData().get(g).id);
//                            for (Word w : words) {
//                                list.add(DicApplication.JRConfig.getSoundPath(true, w.spell));
//                            }
                        list.addAll(words);
                    }
                    if (list.size() > 0) {
//                        JRMediaPlayerHelper.getInstance().startPlay(list);
                        GroupPlayFragment groupPlayFragment = new GroupPlayFragment();
                        groupPlayFragment.setWords(list);
                        ((MainActivity) getActivity()).addFragment(groupPlayFragment);
                    }
                }
                break;
                case R.id.delete: {
                    List<Groups> list = new ArrayList<>();
                    ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
                    for (Integer g : set) {
                        list.add(jrRecyclerAdapter.getData().get(g));
                    }
                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "必须选择一个词库", LENGTH_SHORT).show();
                    } else {
                        DbInit.deleteGroups(list);
                        jrRecyclerAdapter.setData(DbInit.getAllGroups());
                    }
                }
                break;
                case R.id.preview_all_words: {
                    List<Groups> list = new ArrayList<>();
                    ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
                    List<Long> ids = new ArrayList<>();
                    StringBuilder title = new StringBuilder();
                    for (Integer g : set) {
                        list.add(jrRecyclerAdapter.getData().get(g));
                        ids.add(jrRecyclerAdapter.getData().get(g).getId());
                        title.insert(0, ",");
                        title.insert(0, jrRecyclerAdapter.getData().get(g).getName());
                    }

                    if (list.size() == 0) {
                        Toast.makeText(getActivity(), "必须选择一个词库", LENGTH_SHORT).show();
                    } else {
                        AllWordsListFragment fragment = new AllWordsListFragment();
                        if (title.toString().endsWith(",")) {
                            title.delete(title.length() - 1, title.length());
                        }
                        fragment.setTitle(title.toString());
                        fragment.setGroupId(ids);
                        fragment.setDataType(AllWordsListFragment.JRRecyclerAdapter.TYPE_GROUP_MANAGE);
                        ((MainActivity) getActivity()).addFragment(fragment);
                    }
                }
                break;
                case R.id.modify_name: {
                    int count = jrRecyclerAdapter.getSelectedPositionSet().size();
                    if (count != 1) {
                        Toast.makeText(getActivity(), "只能选择一个词库", LENGTH_SHORT).show();
                    } else {
                        edit_layout.setVisibility(View.VISIBLE);
                    }
                }
                break;
                case R.id.add:
                    edit_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.dic_export:
                    break;
                case R.id.dic_import:
                    break;
                case R.id.back:
                    JRInputMethodUtils.hideSoftKeyboard(getActivity(), group_name_edittext);
                    edit_layout.setVisibility(View.GONE);
                    break;

                case R.id.ok: {
                    String name = group_name_edittext.getText().toString();
                    if (!TextUtils.isEmpty(name)) {
                        Groups g = DbInit.getGroup(name);
                        if (g == null) {
                            ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
                            for (Integer gs : set) {
                                g = jrRecyclerAdapter.getData().get(gs);
                            }

                            //找不到就是新增
                            if (g == null) {
                                g = new Groups();
                            }
                        } else {
                            Toast.makeText(getActivity(), "该词库已经存在", LENGTH_SHORT).show();
                            return;
                        }
                        g.setName(name);
                        DbInit.saveGroups(g);
                    }

                    jrRecyclerAdapter.setData(DbInit.getAllGroups());
                    JRInputMethodUtils.hideSoftKeyboard(getActivity(), group_name_edittext);
                    edit_layout.setVisibility(View.GONE);
                }
                break;
                default:
                    break;
            }
        }
    };

    private ILIST_LISTENER ilist_listener = new ILIST_LISTENER() {
        @Override
        public void select(int position) {
            jrRecyclerAdapter.triggleSelected(position);
        }

        @Override
        public void playBoy(int position) {
        }

        @Override
        public void playGirl(int position) {
        }
    };

    public class JRRecyclerAdapter extends JRRecyclerViewAdapter {
        private final ArrayList<Groups> allTypeData = new ArrayList<>();
        private LayoutInflater inflater;

        public ILIST_LISTENER getListener() {
            return listener;
        }

        public void setListener(ILIST_LISTENER listener) {
            this.listener = listener;
        }

        private ILIST_LISTENER listener;

        JRRecyclerAdapter(Context context) {
            inflater = LayoutInflater.from(context);
        }

        private void setListener(View v, ViewHolder holder) {
            v.setOnClickListener(onClickListener);
            v.setTag(holder);
        }

        @Override
        protected void triggleSelectedAllUI(boolean isSelectAll, long selectedCount, long allCount) {
            text_selected.setText(isSelectAll ? "全部取消" : "全部选中" + "\n" + selectedCount + "/" + allCount);
            image_selected.setImageResource(isSelectAll ? R.drawable.selected : R.drawable.unselected);
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = null;
            View v;
            switch (viewType) {
                //            case ITEM_TYPE_TOP:
                //                break;
                default:
                    v = inflater.inflate(R.layout.list_item_layout_type1, null);
                    holder = new ViewHolder(v);
                    setListener(v.findViewById(R.id.all_views), holder);
                    v.findViewById(R.id.meaning).setVisibility(View.GONE);
                    v.findViewById(R.id.sound_girl).setVisibility(View.GONE);
                    v.findViewById(R.id.sound_boy).setVisibility(View.GONE);
                    break;
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).position = position;
            ((ViewHolder) holder).spell.setText(allTypeData.get(position).name);
            ((ViewHolder) holder).selected.setImageResource(isSelected(position) ? R.drawable.selected : R.drawable.unselected);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    switch (v.getId()) {
                        case R.id.all_views:
                            listener.select(((ViewHolder) v.getTag()).position);
                            break;
                    }
                }
            }
        };

        @Override
        public int getItemCount() {
            return allTypeData.size();
        }

        @Override
        public void setData(List data) {
            super.setData(data);
            allTypeData.clear();
            allTypeData.addAll(data);
            notifyDataSetChanged();
//            Log.e("XXX", this.getClass().getSimpleName() + " setData: " + data);
        }

        public List<Groups> getData() {
            return allTypeData;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView spell;
            TextView meaning;
            ImageView selected;
            int position;

            @Override
            public void onClick(View v) {
            }

            public ViewHolder(View itemView) {
                super(itemView);
                spell = (TextView) itemView.findViewById(R.id.spell);
                meaning = (TextView) itemView.findViewById(R.id.meaning);
                selected = (ImageView) itemView.findViewById(R.id.selected);
                //            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
                //            if (lp == null) {
                //                lp = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                //            }
                //            lp.width = ViewGroup.LayoutParams.MATCH_PARENT;
                //            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                //            itemView.setLayoutParams(lp);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (iGroupSelectedListener != null) {
            ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
            for (Integer gs : set) {
                Groups g = jrRecyclerAdapter.getData().get(gs);
                iGroupSelectedListener.select(g.name, g.id);
                return;
            }
            iGroupSelectedListener.select(null, 0);
        }
    }

    public IGroupSelectedListener getiGroupSelectedListener() {
        return iGroupSelectedListener;
    }

    public void setiGroupSelectedListener(IGroupSelectedListener iGroupSelectedListener) {
        this.iGroupSelectedListener = iGroupSelectedListener;
    }

    private IGroupSelectedListener iGroupSelectedListener;

    public interface IGroupSelectedListener {
        public void select(String name, long groupId);
    }
}
