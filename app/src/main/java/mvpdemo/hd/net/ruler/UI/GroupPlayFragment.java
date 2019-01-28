package mvpdemo.hd.net.ruler.UI;

import android.app.Fragment;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import mvpdemo.hd.greendao.DbInit;
import mvpdemo.hd.greendao.entity.Setting;
import mvpdemo.hd.greendao.entity.Word;
import mvpdemo.hd.net.ruler.R;
import mvpdemo.hd.net.ruler.Utils.JRMediaPlayerHelper;
import mvpdemo.hd.net.ruler.app.DicApplication;
import mvpdemo.hd.net.ruler.business.ILIST_LISTENER;

public class GroupPlayFragment extends Fragment {
    private Button setting;
    private TextView spell;
    private TextView meaning;
    private Button pause;
    SettingAdapter settingAdapter;
    RecyclerView setting_list;
    Setting playSetting;
    View setting_layout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.group_play_fragment, container, false);
        setting_list = (RecyclerView) v.findViewById(R.id.setting_list);
        LinearLayoutManager mg = new LinearLayoutManager(getActivity());
        playSetting = DbInit.getSetting();
        generateRandomIndex();
        mg.setOrientation(LinearLayout.VERTICAL);
        mg.setSmoothScrollbarEnabled(true);
        setting_list.setLayoutManager(mg);
        settingAdapter = new SettingAdapter(getActivity());
        ArrayList<SettingData> data = new ArrayList<>();
        data.add(new SettingData("显示单词", playSetting.displaySpell == Setting.ENABLE));
        data.add(new SettingData("显示中文注释", playSetting.displayMeaning == Setting.ENABLE));
        data.add(new SettingData("循环播放", playSetting.repeated == Setting.ENABLE));
        data.add(new SettingData("随机播放", playSetting.random == Setting.ENABLE));
        int[] durations = {
                1,
                2,
                3,
                4,
                5,
                8,
                10,
                15};
        for (int d : durations) {
            data.add(new SettingData(d + "秒播放间隔", playSetting.intervals == d ? true : false));
        }
        settingAdapter.setData(data);
        setting_layout = v.findViewById(R.id.setting_layout);
        setting_list.setAdapter(settingAdapter);
        setting = (Button) v.findViewById(R.id.setting);
        spell = (TextView) v.findViewById(R.id.spell);
        meaning = (TextView) v.findViewById(R.id.meaning);
        spell.setVisibility(playSetting.displaySpell == Setting.ENABLE ? View.VISIBLE : View.INVISIBLE);
        meaning.setVisibility(playSetting.displayMeaning == Setting.ENABLE ? View.VISIBLE : View.INVISIBLE);
        pause = (Button) v.findViewById(R.id.pause);
        setting.setOnClickListener(listener);
        v.findViewById(R.id.back).setOnClickListener(listener);
        pause.setOnClickListener(listener);
        JRMediaPlayerHelper.getInstance().addOnCompletionListener(mediaCompleteListener);
        mHandler.post(playMp3Runnalbe);

        index = 0;
        isPause = false;
        return v;
    }

    private Runnable playMp3Runnalbe = new Runnable() {
        @Override
        public void run() {
            if (index + 1 == words.size()) {
                generateRandomIndex();
            }
            index %= words.size();
            int rand = getRandomIndex(index);
            spell.setText(words.get(rand).spell);
            meaning.setText(words.get(rand).meaning);
            JRMediaPlayerHelper.getInstance().startPlay(DicApplication.JRConfig.getSoundPath(true, words.get(rand).spell));
        }
    };


    MediaPlayer.OnCompletionListener mediaCompleteListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            index++;
            if (playSetting.repeated == Setting.ENABLE) {
                mHandler.postDelayed(playMp3Runnalbe, playSetting.getIntervals() * 1000 + 400);
            } else {
                isPause = true;
                pause.setText("开始播放");
            }
        }
    };

    private int index = 0;
    private ArrayMap<Integer, Integer> randomIndexList = new ArrayMap<>();

    private void generateRandomIndex() {
        if (playSetting.random != Setting.ENABLE) {
            return;
        }
        Random random = new Random();
        random.setSeed(words.size());
        int x = 0;
        int value;
        do {
            value = random.nextInt();
            if (value < 0) {
                value = -value;
            }
            value %= words.size();
            if (!randomIndexList.containsValue(value)) {
                randomIndexList.put(x, value);
                x++;
            } else {
                Log.e("XXX", this.getClass().getSimpleName() + " generateRandomIndex: " + Arrays.toString(randomIndexList.values().toArray()));
            }
        }
        while (randomIndexList.size() < words.size());

    }

    private int getRandomIndex(int index) {
        if (playSetting.random == Setting.ENABLE) {
            return randomIndexList.get(index);
        } else {
            return index;
        }
    }

    public void setWords(List<Word> words) {
        this.words.clear();
        this.words.addAll(words);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mHandler.removeCallbacks(playMp3Runnalbe);
        JRMediaPlayerHelper.getInstance().stopPlay();
        JRMediaPlayerHelper.getInstance().removeOnCompletionListener(mediaCompleteListener);
        super.onDestroy();
    }

    private List<Word> words = new ArrayList<>();
    private boolean isPause = false;

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:
                    setting_layout.setVisibility(View.GONE);
                    break;
                case R.id.setting:
                    setting_layout.setVisibility(View.VISIBLE);
                    break;
                case R.id.pause:
                    if (isPause) {
                        isPause = false;
                        pause.setText("暂停");
                        JRMediaPlayerHelper.getInstance().startPlay(DicApplication.JRConfig.getSoundPath(true, words.get(getRandomIndex(index % words.size())).spell));
                    } else {
                        isPause = true;
                        pause.setText("继续");
                        index += words.size();
                        index--;
                        mHandler.removeCallbacks(playMp3Runnalbe);
                        JRMediaPlayerHelper.getInstance().stopPlay();
                    }
                    break;

                default:
                    break;
            }
        }
    };

    private static class SettingData {
        String text;
        boolean selected;

        public SettingData(String text, boolean selected) {
            this.selected = selected;
            this.text = text;
        }
    }

    public class SettingAdapter extends JRRecyclerViewAdapter {
        private final ArrayList<SettingData> allTypeData = new ArrayList<>();
        private LayoutInflater inflater;

        public ILIST_LISTENER getListener() {
            return listener;
        }

        public void setListener(ILIST_LISTENER listener) {
            this.listener = listener;
        }

        private ILIST_LISTENER listener;

        SettingAdapter(Context context) {
            inflater = LayoutInflater.from(context);
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
                    v = inflater.inflate(R.layout.setting_item_layout, null);
                    holder = new ViewHolder(v);
                    setListener(v.findViewById(R.id.selected), holder);
                    break;
            }

            return holder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).position = position;
            ((ViewHolder) holder).item_text.setText(allTypeData.get(position).text);
            ((ViewHolder) holder).selected.setImageResource(allTypeData.get(position).selected ? R.drawable.selected : R.drawable.unselected);

        }

        @Override
        protected void triggleSelectedAllUI(boolean isSelectAll, long selectedCount, long allCount) {

        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.selected:
                        boolean changed = true;
                        int positon = ((ViewHolder) v.getTag()).position;
                        switch (positon) {
                            case 0:
                                allTypeData.get(positon).selected = !allTypeData.get(positon).selected;
                                playSetting.displaySpell = allTypeData.get(positon).selected ? Setting.ENABLE : Setting.DISABLE;
                                spell.setVisibility(playSetting.displaySpell == Setting.ENABLE ? View.VISIBLE : View.INVISIBLE);
                                break;
                            case 1:
                                allTypeData.get(positon).selected = !allTypeData.get(positon).selected;
                                playSetting.displayMeaning = allTypeData.get(positon).selected ? Setting.ENABLE : Setting.DISABLE;
                                meaning.setVisibility(playSetting.displayMeaning == Setting.ENABLE ? View.VISIBLE : View.INVISIBLE);
                                break;
                            case 2:
                                allTypeData.get(positon).selected = !allTypeData.get(positon).selected;
                                playSetting.repeated = allTypeData.get(positon).selected ? Setting.ENABLE : Setting.DISABLE;
                                break;
                            case 3:
                                allTypeData.get(positon).selected = !allTypeData.get(positon).selected;
                                playSetting.random = allTypeData.get(positon).selected ? Setting.ENABLE : Setting.DISABLE;
                                generateRandomIndex();
                                break;
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                            case 9:
                            case 10:
                            case 11:
                                if (!allTypeData.get(positon).selected) {
                                    int[] durations = {
                                            1,
                                            2,
                                            3,
                                            4,
                                            5,
                                            8,
                                            10,
                                            15};
                                    int start = 4;
                                    int index = 0;
                                    while (index < start + durations.length) {
                                        allTypeData.get(index).selected = false;
                                        index++;
                                    }
                                    allTypeData.get(positon).selected = true;
                                    playSetting.intervals = durations[positon - start];
                                } else {
                                    changed = false;
                                }
                                break;
                            default:
                                changed = false;
                                break;
                        }
                        if (changed) {
                            notifyDataSetChanged();
                            DbInit.saveSetting(playSetting);
                        }
                        break;
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

        public List<SettingData> getData() {
            return allTypeData;
        }

        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            TextView item_text;
            TextView meaning;
            ImageView selected;
            int position;

            @Override
            public void onClick(View v) {
            }

            public ViewHolder(View itemView) {
                super(itemView);
                item_text = (TextView) itemView.findViewById(R.id.item_text);
                selected = (ImageView) itemView.findViewById(R.id.selected);
            }
        }
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
}
