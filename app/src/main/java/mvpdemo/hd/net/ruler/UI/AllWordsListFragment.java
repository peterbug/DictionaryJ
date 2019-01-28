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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import mvpdemo.hd.greendao.DbInit;
import mvpdemo.hd.greendao.entity.Word;
import mvpdemo.hd.net.ruler.R;
import mvpdemo.hd.net.ruler.Utils.JRMediaPlayerHelper;
import mvpdemo.hd.net.ruler.Utils.JRTextUtils;
import mvpdemo.hd.net.ruler.app.DicApplication;
import mvpdemo.hd.net.ruler.business.ILIST_LISTENER;

public class AllWordsListFragment extends Fragment {
    private ImageView image_selected;
    private TextView text_selected;
    private TextView play;
    private TextView delete;
    private CharSequence title;
    private TextView manage_group;
    JRRecyclerAdapter jrRecyclerAdapter;
    private boolean selected_all = false;
    private int dataType = 0;

    public List<Long> getGroupId() {
        return groupId;
    }

    public void setDataType(int dataType) {
        this.dataType = dataType;
    }

    public void setGroupId(List<Long> groups) {
        this.groupId = groups;
    }

    public void setTitle(CharSequence titleString) {
        title = titleString;
    }

    private List<Long> groupId = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.list_layout_fragment, container, false);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        LinearLayoutManager mg = new LinearLayoutManager(getActivity());
        mg.setOrientation(LinearLayout.VERTICAL);
        mg.setSmoothScrollbarEnabled(true);
        recyclerView.setLayoutManager(mg);
        jrRecyclerAdapter = new JRRecyclerAdapter(container.getContext());
        jrRecyclerAdapter.setDataType(dataType);
        List list = DbInit.getWordsByGroupId(groupId);
        jrRecyclerAdapter.setData(list);
        recyclerView.setAdapter(jrRecyclerAdapter);
        image_selected = (ImageView) v.findViewById(R.id.image_selected);
        text_selected = (TextView) v.findViewById(R.id.text_selected);
        play = (TextView) v.findViewById(R.id.play);
        delete = (TextView) v.findViewById(R.id.delete);
        TextView textView = (TextView) v.findViewById(R.id.title);
        if (!TextUtils.isEmpty(title)) {
            textView.setText(title);
            textView.setVisibility(View.VISIBLE);
        } else {
            textView.setVisibility(View.GONE);
        }
        manage_group = (TextView) v.findViewById(R.id.manage_group);
        image_selected.setOnClickListener(listener);
        text_selected.setOnClickListener(listener);
        play.setOnClickListener(listener);
        delete.setOnClickListener(listener);
        manage_group.setOnClickListener(listener);
        jrRecyclerAdapter.setListener(ilist_listener);
        return v;
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
                        list.add(jrRecyclerAdapter.getData().get(g));
                    }
//                    JRMediaPlayerHelper.startPlay(list);
                    if (list.size() > 0) {
//                        FragmentManager fragmentManager = getActivity().getFragmentManager();
//                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment,new GroupPlayFragment(), GroupPlayFragment.class.getName());
//                        fragmentTransaction.commitAllowingStateLoss();
                        GroupPlayFragment groupPlayFragment = new GroupPlayFragment();
                        groupPlayFragment.setWords(list);
                        ((MainActivity) getActivity()).addFragment(groupPlayFragment);
                    }
                }
                break;
                case R.id.delete: {
                    ArrayList<Word> list = new ArrayList<>();
                    ArraySet<Integer> set = jrRecyclerAdapter.getSelectedPositionSet();
                    for (Integer g : set) {
                        list.add(jrRecyclerAdapter.getData().get(g));
                    }
                    if (list.size() > 0) {
                        if (dataType == JRRecyclerAdapter.TYPE_GROUP_MANAGE) {
                            for (Word w : list) {
                                w.getGroupIds().remove(groupId);
                            }
                            DbInit.saveWord(list);
                        } else {
                            DbInit.deleteWord(list);
                        }
                        jrRecyclerAdapter.setData(DbInit.getWordsByGroupId(groupId));
                    }
                }
                break;

                case R.id.manage_group:
                    GroupManageFragment fragment = new GroupManageFragment();
                    fragment.setTitle("词库管理");
                    ((MainActivity) getActivity()).addFragment(fragment);
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
//            boolean sel = jrRecyclerAdapter.getData().get(position).isSelected();
//            sel = !sel;
//            jrRecyclerAdapter.getData().get(position).setSelected(sel);
//            if (sel) {
//                boolean unselct = false;
//                for (Word w : jrRecyclerAdapter.getData()) {
//                    if (!w.isSelected()) {
//                        unselct = true;
//                        break;
//                    }
//                }
//
//                if (unselct && selected_all) {
//                    selected_all = false;
//                    text_selected.setText(selected_all ? "全部取消" : "全部选中");
//                    image_selected.setImageResource(selected_all ? R.drawable.selected : R.drawable.unselected);
//                } else if (!unselct && !selected_all) {
//                    selected_all = true;
//                    text_selected.setText(selected_all ? "全部取消" : "全部选中");
//                    image_selected.setImageResource(selected_all ? R.drawable.selected : R.drawable.unselected);
//                }
//            } else if (selected_all) {
//                selected_all = false;
//                text_selected.setText(selected_all ? "全部取消" : "全部选中");
//                image_selected.setImageResource(selected_all ? R.drawable.selected : R.drawable.unselected);
//            }
//
//            jrRecyclerAdapter.notifyDataSetChanged();
        }

        @Override
        public void playBoy(int position) {
            JRMediaPlayerHelper.getInstance().startPlay(DicApplication.JRConfig.getSoundPath(true, jrRecyclerAdapter.getData().get(position).spell));
        }

        @Override
        public void playGirl(int position) {
            JRMediaPlayerHelper.getInstance().startPlay(DicApplication.JRConfig.getSoundPath(false, jrRecyclerAdapter.getData().get(position).spell));
        }
    };

    public class JRRecyclerAdapter extends JRRecyclerViewAdapter {
        public static final int TYPE_SPELL_ONLY = 1;
        public static final int TYPE_GROUP_MANAGE = 2;
        private final ArrayList<Word> allTypeData = new ArrayList<>();
        private LayoutInflater inflater;
        private String stringItemHighlight = "";
        private int type = 0;

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

        public void setDataType(int type) {
            this.type = type;
        }

        private void setListener(View v, ViewHolder holder) {
            v.setOnClickListener(onClickListener);
            v.setTag(holder);
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
                    setListener(v.findViewById(R.id.sound_boy), holder);
                    setListener(v.findViewById(R.id.sound_girl), holder);
                    break;
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).position = position;
            if (type != TYPE_SPELL_ONLY) {
                ((ViewHolder) holder).spell.setText(allTypeData.get(position).spell);
                ((ViewHolder) holder).meaning.setText(allTypeData.get(position).meaning);
                ((ViewHolder) holder).meaning.setVisibility(View.VISIBLE);
            } else {
                if (TextUtils.isEmpty(stringItemHighlight)) {
                    ((ViewHolder) holder).spell.setText(allTypeData.get(position).spell);
                } else {
                    ((ViewHolder) holder).spell.setText(JRTextUtils.getSpanningString(allTypeData.get(position).spell, stringItemHighlight, "#ff0000"));
                }
                ((ViewHolder) holder).meaning.setVisibility(View.GONE);
            }
            ((ViewHolder) holder).selected.setImageResource(isSelected(position) ? R.drawable.selected : R.drawable.unselected);
        }

        @Override
        protected void triggleSelectedAllUI(boolean isSelectAll) {
            text_selected.setText(isSelectAll ? "全部取消" : "全部选中");
            image_selected.setImageResource(isSelectAll ? R.drawable.selected : R.drawable.unselected);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    switch (v.getId()) {
                        case R.id.all_views:
                            listener.select(((ViewHolder) v.getTag()).position);
                            break;

                        case R.id.sound_boy:
                            listener.playBoy(((ViewHolder) v.getTag()).position);
                            break;

                        case R.id.sound_girl:
                            listener.playGirl(((ViewHolder) v.getTag()).position);
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

        public List<Word> getData() {
            return allTypeData;
        }

        public void setItemHighlight(String string) {
            this.stringItemHighlight = string;
        }
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
