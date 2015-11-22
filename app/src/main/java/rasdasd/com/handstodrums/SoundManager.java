package rasdasd.com.handstodrums;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;
import android.util.SparseArray;

public class SoundManager {

    public static int SOUNDPOOLSND_BASS = 0;
    public static int SOUNDPOOLSND_MOUNT = 1;
    public static int SOUNDPOOLSND_FLOOR = 2;
    public static int SOUNDPOOLSND_SNARE = 3;

    public static boolean isSoundTurnedOff = false;

    private static SoundManager mSoundManager;

    private SoundPool mSoundPool;
    private SparseArray<Integer> mSoundPoolMap;
    private AudioManager mAudioManager;

    public static final int maxSounds = 100;

    public static SoundManager getInstance(Context context) {
        if (mSoundManager == null) {
            mSoundManager = new SoundManager(context);
        }

        return mSoundManager;
    }
    public SoundManager(Context mContext) {
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setMaxStreams(maxSounds)
                .setAudioAttributes(audioAttributes)
                .build();
        /*
        mSoundPool.setOnLoadCompleteListener(new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                loaded = true;
            }
        });
        */
        mSoundPoolMap = new SparseArray<Integer>();
        mSoundPoolMap.put(SOUNDPOOLSND_BASS, mSoundPool.load(mContext, R.raw.bass, 1));
        mSoundPoolMap.put(SOUNDPOOLSND_MOUNT, mSoundPool.load(mContext, R.raw.mount, 1));
        mSoundPoolMap.put(SOUNDPOOLSND_FLOOR, mSoundPool.load(mContext, R.raw.floor, 1));
        mSoundPoolMap.put(SOUNDPOOLSND_SNARE, mSoundPool.load(mContext, R.raw.snare, 1));

        // testing simultaneous playing
        //int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(0), 1, 1, 1, 20, 1f);
        mSoundPool.play(mSoundPoolMap.get(0), 1, 1, 1, 2, 1f);
        mSoundPool.play(mSoundPoolMap.get(0), 1, 1, 1, 0, 1f);


    }
    long lastplayed = 0;
    public void playSound(int index) {
        if (isSoundTurnedOff)
            return;
        if(System.currentTimeMillis()-lastplayed<200) {
            return;
        }
        lastplayed = System.currentTimeMillis();
        Log.e("SOUND","" + index);
        //int streamVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        mSoundPool.play(mSoundPoolMap.get(index), 1, 1, 100, 0, 1f);
    }

    public static void clear() {
        if (mSoundManager != null) {
            mSoundManager.mSoundPool = null;
            mSoundManager.mAudioManager = null;
            mSoundManager.mSoundPoolMap = null;
        }
        mSoundManager = null;
    }
}