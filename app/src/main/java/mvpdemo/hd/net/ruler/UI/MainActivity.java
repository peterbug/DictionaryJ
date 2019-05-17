package mvpdemo.hd.net.ruler.UI;


import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.ArrayMap;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.HelloWorld;
import com.example.annotation.cls.JPHelloWorld;
import com.fy.ruler.retrofit.LoadService;
import com.fy.ruler.retrofit.RequestUtils;
import com.fy.ruler.retrofit.up.LoadCallBack;
import com.fy.ruler.retrofit.up.UploadOnSubscribe;
import com.fy.ruler.utils.FileUpload;
import com.fy.ruler.utils.L;
import com.fy.ruler.utils.TransfmtUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import mvpdemo.hd.greendao.DbInit;
import mvpdemo.hd.greendao.entity.Word;
import mvpdemo.hd.net.ruler.R;
import mvpdemo.hd.net.ruler.Utils.JRInputMethodUtils;
import mvpdemo.hd.net.ruler.Utils.JRMediaPlayerHelper;
import mvpdemo.hd.net.ruler.Utils.JRTextUtils;
import mvpdemo.hd.net.ruler.app.DicApplication;
import mvpdemo.hd.net.ruler.business.ILIST_LISTENER;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static android.widget.Toast.LENGTH_SHORT;

@JPHelloWorld
public class MainActivity extends Activity {
    SharedPreferences sp;
    final String SP_KEY = "ratio";
    int index = 1;
    OkHttpClient client = new OkHttpClient();
    EditText input_word;
    Handler mHandler = new Handler();
    RecyclerView recyclerView;
    TextView meaning;
    TextView phoneti;
    AllWordsListFragment.JRRecyclerAdapter jrRecyclerAdapter;
    CheckBox default_to_diction;
    TextView group_name;
    TextView tips;
    long defaultGroupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideNavigationBar();
        setContentView(R.layout.activity_main);
//        View actionBar = findViewById(R.id.action_bar);
//        if (actionBar != null) {
//            actionBar.setVisibility(View.GONE);
//        }
        getWindow().setTitle(null);
        sp = this.getPreferences(MODE_PRIVATE);
        String ratio = sp.getString(SP_KEY, String.format("%.2f", RulerView.getPixel_and_mm_ratio2()));
        ((EditText) findViewById(R.id.ratio)).setText(ratio);
        input_word = ((EditText) findViewById(R.id.input_word));
        input_word.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                List list;
                if (s.length() > 0) {
                    list = DbInit.ambiguousSearch(s.toString());
//                    Log.e("XXX", this.getClass().getSimpleName() + " afterTextChanged: " + list + " " + Thread.currentThread().getName());
                } else {
                    list = new ArrayList();
                }
                ((AllWordsListFragment.JRRecyclerAdapter) recyclerView.getAdapter()).setItemHighlight(s.toString());
                ((AllWordsListFragment.JRRecyclerAdapter) recyclerView.getAdapter()).setData(list);
            }
        });
//        getFile(null);
        jrRecyclerAdapter = new AllWordsListFragment().new JRRecyclerAdapter(getApplicationContext());
        jrRecyclerAdapter.setDataType(AllWordsListFragment.JRRecyclerAdapter.TYPE_SPELL_ONLY);
        LinearLayoutManager mg = new LinearLayoutManager(getApplicationContext());
        mg.setOrientation(LinearLayout.VERTICAL);
        mg.setSmoothScrollbarEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(mg);
        jrRecyclerAdapter.setListener(new ILIST_LISTENER() {
            @Override
            public void playBoy(int position) {

            }

            @Override
            public void playGirl(int position) {

            }

            @Override
            public void select(int position) {
                input_word.setText(jrRecyclerAdapter.getData().get(position).spell);
            }
        });
        recyclerView.setAdapter(jrRecyclerAdapter);
        findViewById(R.id.play_girl).setOnClickListener(playMp3Listener);
        findViewById(R.id.play_boy).setOnClickListener(playMp3Listener);
        meaning = (TextView) findViewById(R.id.meaning);
        phoneti = (TextView) findViewById(R.id.phoneti);
//        input_word.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
////                    getSystemService(INPUT_METHOD_SERVICE).get
//                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(v
//                            .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS, null);
//                }
//            }
//        });
        default_to_diction = (CheckBox) findViewById(R.id.default_to_diction);
        default_to_diction = (CheckBox) findViewById(R.id.default_to_diction);
        default_to_diction.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                defaultGroupId = -1;
                if (isChecked) {
                    GroupManageFragment groupManageFragment = new GroupManageFragment();
                    groupManageFragment.setTitle("请选择一个词库");
                    groupManageFragment.setiGroupSelectedListener(new GroupManageFragment.IGroupSelectedListener() {
                        @Override
                        public void select(String name, long groupId) {
                            if (!TextUtils.isEmpty(name)) {
                                group_name.setText(name);
                                defaultGroupId = groupId;
                            } else {
                                group_name.setText("");
                                default_to_diction.setChecked(false);
                            }
                        }
                    });
                    addFragment(groupManageFragment);
                } else {
                    group_name.setText("");
                }
            }
        });
        group_name = (TextView) findViewById(R.id.group_name);
        tips = (TextView) findViewById(R.id.tips);
//        save();
    }

    private void save() {
        saveFileToDisk2(null, "0107---3", "M");
    }

    private void saveFileToDisk2(final InputStream is, final String word, final String gender) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
        //拿到字节流

        int len = 0;
        //设置下载图片存储路径和名称
        String filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
//        filePath = Environment.getExternalStorageDirectory() + "/a/" +   "words.txt";
//        String filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
        Log.e("XXX", this.getClass().getSimpleName() + " onResponse: " + filePath + " Thread:" + Thread.currentThread().getName());
        File file = new File(filePath);
        try {
            if (!file.exists()) {
//                file.createNewFile();

//            filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
//                String p = "android.permission.READ_EXTERNAL_STORAGE";
                FileOutputStream fos = new FileOutputStream(filePath);
                byte[] buf = new byte[128];
                buf = "abcde".getBytes();
                fos.write(buf, 0, len);
                fos.flush();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        });
    }

    public void searchHistory(View v) {
//        SharedPreferences.Editor editor = sp.edit();
//        String ratio = ((EditText) findViewById(R.id.ratio)).getText().toString();
//        editor.putString(SP_KEY, ratio);
//        editor.commit();
//        RulerView.setPixel_and_mm_ratio2(Double.parseDouble(ratio));
        JRInputMethodUtils.hideSoftKeyboard(this, input_word);
        AllWordsListFragment fragment = new AllWordsListFragment();
        fragment.setTitle("查找记录");
        HelloWorld.main(null);
        addFragment(fragment);
    }

    public void clearText(View v) {
        input_word.setText("");
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    private void upload() {
        List<String> files = new ArrayList<>();
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/679f6337gy1fr69ynfq3nj20hs0qodh0.jpg");
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/IMG_20181108_144507.jpg");
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/abc.mp3");
        files.add(TransfmtUtils.getSDCardPath() + "DCIM/abc.jpg");
        File f = new File(TransfmtUtils.getSDCardPath() + "DCIM/abc.mp3");
        L.e("IMG_0008:" + f.length() + " exist:" + f.exists() + " name:" + f.getName());
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/RED,胡歌 - 逍遥叹（Cover 胡歌）.mp3");
//        files.add(TransfmtUtils.getSDCardPath() + "DCIM/Camera/马郁 - 下辈子如果我还记得你.mp3");
        FileUpload.uploadFiles(files, tips);
    }

    public void getFile(View v) {
        if (true) {
            upload();
            return;
        }
        JRInputMethodUtils.hideSoftKeyboard(this, input_word);
        if (!isNetworkConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), "网络链接错误", LENGTH_SHORT).show();
            return;
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                String old = input_word.getText().toString();
                old = old.trim();
                old = JRTextUtils.replace(old, "  ", " ");
                old = JRTextUtils.replace(old, " ,", ",");
                old = JRTextUtils.replace(old, ", ", ",");

                String[] words = old.split(",");
                for (String w : words) {
                    getSoundName(w);
                }
            }
        }).start();
    }

    private View.OnClickListener playMp3Listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            for (Object obj : DbInit.getAllWords()) {
                Log.e("XXX", this.getClass().getSimpleName() + " playMp3: " + obj);
            }

            if (input_word.getText().length() > 0) {
                String filePath = DicApplication.JRConfig.getSoundPath(v.getId() == R.id.play_boy, input_word.getText().toString());
                JRMediaPlayerHelper.getInstance().startPlay(filePath);
            }
        }
    };

    private void downloadMp3File(final String mp3Name, final String word, final String gender) {

//        String url = "http://audio.dict.cn/muc0cdd51a8f4db31832568351615c08bedadb.mp3";
        String url = "http://audio.dict.cn/" + mp3Name;
        Request request = new Request.Builder()
                .url(url)
                .build();

        String body = null;
        Call call = client.newCall(request);
        //4.请求加入调度,重写回调方法
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("XXX", this.getClass().getSimpleName() + " onFailure: " + e);
            }

            @Override
            public void onResponse(Call call, final Response response) throws IOException {
                InputStream is = response.body().byteStream();
                saveFileToDisk(is, word, gender);
            }
        });
    }

    private void saveFileToDisk(final InputStream is, final String word, final String gender) {
//        mHandler.post(new Runnable() {
//            @Override
//            public void run() {
        //拿到字节流

        int len = 0;
        //设置下载图片存储路径和名称
        String filePath = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + word + "-" + gender + ".mp3";
//        String filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
        Log.e("XXX", this.getClass().getSimpleName() + " onResponse: " + filePath + " Thread:" + Thread.currentThread().getName());
        File file = new File(filePath);
        try {
            if (!file.exists()) {
//                file.createNewFile();

//            filePath = Environment.getExternalStorageDirectory() + "/a/" + word + "-" + gender + ".mp3";
//                String p = "android.permission.READ_EXTERNAL_STORAGE";
                FileOutputStream fos = new FileOutputStream(filePath);
                byte[] buf = new byte[128];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                fos.flush();
                fos.close();
                is.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//            }
//        });
    }

    private void getSoundName(String word) {
        if (TextUtils.isEmpty(word)) {
//            Toast.makeText(getApplicationContext(), "空字符", LENGTH_SHORT).show();
            return;
        }
        List<Word> list = DbInit.isExistWord(word);
        if (list.size() > 0) {
            boolean exist = true;
            try {
                File file = new File(DicApplication.JRConfig.getSoundPath(true, word));
                if (!file.exists()) {
                    exist = false;
                } else {
                    file = new File(DicApplication.JRConfig.getSoundPath(false, word));
                    if (!file.exists()) {
                        exist = false;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            meaning.setText(list.get(0).meaning);
            phoneti.setText(list.get(0).phoneti);
            if (exist) {
                if (defaultGroupId >= 0) {
                    list.get(0).getGroupIds().add(defaultGroupId);
                    DbInit.saveWord(list.get(0));
                }
                return;
            }
        }
        try {
            Word w = new Word();
            w.setSpell(word);
            //从一个URL加载一个Document对象。
            Document doc = Jsoup.connect("http://dict.cn/" + word).get();
            Elements elements = doc.select("ul.dict-basic-ul");

            for (Element e : elements) {
                StringBuilder meaningString = new StringBuilder();
                for (Element li : e.select("li")) {
                    for (Element i : li.select("span")) {
                        meaningString.append(i.text());
                    }
                    for (Element s : li.select("strong")) {
                        meaningString.append(s.text());
                    }
                    meaningString.append("\n");
                }
                int i = -1;
                while ((i = meaningString.lastIndexOf("\n")) == meaningString.length() - 1) {
                    meaningString.deleteCharAt(i);
                }
                w.setMeaning(meaningString.toString());
                meaning.setText(meaningString.toString());
            }

            elements = doc.select("div.phonetic");
            for (Element e : elements) {
                for (Element span : e.select("span")) {
                    String text = span.text();
                    if (!TextUtils.isEmpty(text) && text.contains("美")) {
                        for (Element i : span.select("bdo")) {
                            w.setPhoneti(i.text());
                            phoneti.setText(w.getPhoneti());
                        }
                        for (Element i : span.select("i")) {
                            String mp3Name = i.attr("naudio").split("\\?")[0];
                            Log.e("XXX", this.getClass().getSimpleName() + " getSoundName: mp3Name:" + mp3Name);
                            String gender = "f";
                            if (i.attr("title").contains("男")) {
                                gender = "m";
                            }

                            downloadMp3File(mp3Name, word, gender);
                            if (defaultGroupId >= 0) {
                                w.getGroupIds().add(defaultGroupId);
                            }
                            DbInit.saveWord(w);
                        }
                    }
                }
            }

        } catch (Exception e) {
            Log.e("XXX", this.getClass().getSimpleName() + " getSoundName: ");
        }
    }

    private void hideNavigationBar() {
//        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_FULLSCREEN;
//        decorView.setSystemUiVisibility(uiOptions);
//
//        Window window = getWindow();
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        window.setStatusBarColor(0x00000000);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View decorView = getWindow().getDecorView();
//        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                /*| View.SYSTEM_UI_FLAG_FULLSCREEN*/;
//        decorView.setSystemUiVisibility(uiOptions);
        boolean result = false;
        result = super.dispatchTouchEvent(ev);
//        Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: " + MotionEvent.actionToString(ev.getAction()) + " " + Integer.toBinaryString(decorView.getSystemUiVisibility()) + " " + Integer.toBinaryString(decorView.getWindowSystemUiVisibility()) + " " + result);
//
//        if ((decorView.getSystemUiVisibility() & View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR) == 0) {
//        hideNavigationBar();
//            Log.e("XXX", this.getClass().getSimpleName() + " dispatchTouchEvent: ");
//        }
        return result;
    }

    ArrayList<Fragment> fragmentList = new ArrayList();

    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        // 开启一个事务
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentList.add(fragment);
        fragmentTransaction.add(R.id.fragment, fragment);
        fragmentTransaction.show(fragment);

        // 提交事务
        fragmentTransaction.commit();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
            if (fragmentList.size() > 0) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Fragment fragment = fragmentList.get(fragmentList.size() - 1);
                fragmentTransaction.remove(fragment);
                fragmentList.remove(fragment);
                if (fragmentList.size() > 0) {
                    fragmentTransaction.replace(R.id.fragment, fragmentList.get(fragmentList.size() - 1));
                }
                fragmentTransaction.commit();
                return false;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
//            input_word.setText();
        }
    };

    public void setSearchWordInList(EditText editText, String word) {

    }
}
