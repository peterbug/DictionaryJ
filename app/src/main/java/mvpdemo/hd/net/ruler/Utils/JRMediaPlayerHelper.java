package mvpdemo.hd.net.ruler.Utils;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class JRMediaPlayerHelper {
    public static JRMediaPlayerHelper getInstance() {
        return Holder.INSTANCE;
    }

    public void startPlay(String mediaPath) {
        ArrayList<String> list = new ArrayList<>();
        list.add(mediaPath);
        startPlay(list);
    }

    private static int index = 0;
    private static ArrayList<String> playList = new ArrayList<>();

    public static void startPlay(ArrayList<String> mediaPaths) {
        index = 0;
        playList.clear();
        playList.addAll(mediaPaths);
        start();
    }

    public static void stopPlay() {
        if (mPlayer.isPlaying()) {
            mPlayer.stop();
        }
    }

    public static void pausePlay() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        }
    }

    public static void resumePlay() {
        if (mPlayer.isPlaying()) {
            mPlayer.start();
        }
    }

    private static void start() {
        try {
            mPlayer.reset();
            if (!new File(playList.get(index % playList.size())).exists()) {
                onCompletionListener.onCompletion(null);
                return;
            }
            mPlayer.setDataSource(playList.get(index % playList.size()));
        } catch (IOException e) {
            Log.e("XXX",  " start: set setDataSource");
            e.printStackTrace();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    mPlayer.prepareAsync();
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                    Log.e("XXX", this.getClass().getSimpleName() + " run: prepareAsync");
                }
            }
        }).start();
    }

    private static MediaPlayer mPlayer;
    private static MediaPlayer.OnCompletionListener onCompletionListener;

    static {
        mPlayer = new MediaPlayer();
        mPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Log.e("XXX", this.getClass().getSimpleName() + " onPrepared: ");
                mp.start();
            }
        });
        mPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                Log.e("XXX", this.getClass().getSimpleName() + " onError: what:" + what + " extra:" + extra);
                File file = new File(playList.get(index % playList.size()));
                if(file.exists()){
                    file.delete();
                }
                return true;
            }
        });
        onCompletionListener = new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(final MediaPlayer mp) {
                Log.e("XXX", this.getClass().getSimpleName() + " mPlayer onCompletion: " + mp);
                if (mp != null) {
                    mp.stop();
                } else {
                    mPlayer.stop();
                }
                if (playList.size() > 1) {
                    index++;
                    start();
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        for (MediaPlayer.OnCompletionListener listener : completionListenerList) {
                            Log.e("XXX", this.getClass().getSimpleName() + " onCompletion: " + listener);
                            listener.onCompletion(mp);
                        }
                    }
                }).start();
            }
        };

        mPlayer.setOnCompletionListener(onCompletionListener);
    }

    static ArrayList<MediaPlayer.OnCompletionListener> completionListenerList = new ArrayList<>();

    public static void addOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        completionListenerList.add(listener);
    }

    public static void removeOnCompletionListener(MediaPlayer.OnCompletionListener listener) {
        completionListenerList.remove(listener);
    }

    private JRMediaPlayerHelper() {
    }

    private static class Holder {
        private static final JRMediaPlayerHelper INSTANCE = new JRMediaPlayerHelper();
    }
}
