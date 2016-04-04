package com.adolph.jrm.calculator_voice;

import android.content.Context;
import android.media.SoundPool;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import android.os.Handler;

/**
 * Created by Adolph on 16/4/4.
 */
public class SoundManager {

    public static final String TAG = SoundManager.class.getSimpleName();
    private int currentStreamId;

    private SoundManager(){}

    private static SoundManager sMe;

    public synchronized static SoundManager getInstance(){
        if(sMe == null){
            sMe = new SoundManager();
        }
        return sMe;
    }

    private static final int[] RES ={
            R.raw.one,
            R.raw.two,
            R.raw.three,
            R.raw.four,
            R.raw.five,
            R.raw.six,
            R.raw.seven,
            R.raw.eight,
            R.raw.nine,
            R.raw.zero,
            R.raw.ac,
            R.raw.del,
            R.raw.plus,
            R.raw.mul,
            R.raw.minus,
            R.raw.div,
            R.raw.equal,
            R.raw.dot
    };

    private Context context;
    private boolean mPlaying;
    private SoundPool soundPool;
    private HashMap<String,Sound> soundMap;
    private Vector<String> SoundQuene = new Vector<>();

    public void initSounds(Context context){
        this.context = context;
        this.soundPool = new SoundPool(RES.length,3,0);
        this.mPlaying = false;
        this.soundMap = new HashMap<>();

        addSound(context.getString(R.string.digit1),R.raw.one,320);
        addSound(context.getString(R.string.digit2),R.raw.two,320);
        addSound(context.getString(R.string.digit3),R.raw.three,320);
        addSound(context.getString(R.string.digit4),R.raw.four,320);
        addSound(context.getString(R.string.digit5),R.raw.five,320);
        addSound(context.getString(R.string.digit6),R.raw.six,320);
        addSound(context.getString(R.string.digit7),R.raw.seven,320);
        addSound(context.getString(R.string.digit8),R.raw.eight,320);
        addSound(context.getString(R.string.digit9),R.raw.nine,320);
        addSound(context.getString(R.string.digit0),R.raw.zero,320);
        addSound(context.getString(R.string.clear),R.raw.ac,320);
        addSound(context.getString(R.string.del),R.raw.del,320);
        addSound(context.getString(R.string.plus),R.raw.plus,320);
        addSound(context.getString(R.string.mul),R.raw.mul,320);
        addSound(context.getString(R.string.minus),R.raw.minus,320);
        addSound(context.getString(R.string.div),R.raw.div,320);
        addSound(context.getString(R.string.dot),R.raw.dot,320);
        addSound(context.getString(R.string.equal),R.raw.equal,320);
    }

    private Handler handler = new Handler();

    private Runnable mPlayNext = new Runnable() {
        @Override
        public void run() {
            SoundManager.this.soundPool.stop(SoundManager.this.currentStreamId);
            SoundManager.this.playNextSound();
        }
    };

    private void playNextSound(){
        if(SoundQuene.isEmpty()){
            return;
        }
        if (!this.SoundQuene.isEmpty()) {
            String str = (String) this.SoundQuene.remove(0);
            Sound sound = (Sound) this.soundMap.get(str);
            if (sound != null) {
                this.currentStreamId = this.soundPool.play(sound.id, 0.2F,
                        0.2F, 1, 0, 1.0F);
                this.mPlaying = true;
                this.handler.postDelayed(this.mPlayNext, sound.time);
            }
        }
    }

    public void playSound(String text){
        stopSound();
        this.SoundQuene.add(text);
        playNextSound();
    }

    public void unloadAll(){
        stopSound();
        if(this.soundMap.size() > 0){
            Iterator<String> iterator = soundMap.keySet().iterator();
            while (iterator.hasNext()){
                String str = iterator.next();
                Sound sound = soundMap.get(str);
                this.soundPool.unload(this.soundMap.get(str).id);
            }
        }
    }

    public void cleanUp() {
        unloadAll();
        this.soundPool.release();
        this.soundPool = null;
        sMe = null;
    }
    public void playSeqSounds(String[] soundsPlay){
        int len = soundsPlay.length;
        for (int j = 0;;j++){
            if(j >= len){
                if(!this.mPlaying){
                    playNextSound();
                }
                return;
            }

            String str = soundsPlay[j];
            this.SoundQuene.add(str);
        }
    }

    public void stopSound(){
        this.handler.removeCallbacks(this.mPlayNext);
        this.SoundQuene.clear();
        this.soundPool.stop(this.currentStreamId);
        this.mPlaying = false;
    }

    private void addSound(String text,int resId,int time){
        Sound localSound = new Sound(this.soundPool.load(this.context,resId,1),time);
        this.soundMap.put(text,localSound);
    }

    private final class Sound{
        public int id;
        public int time;
        public Sound(int id,int time){
            this.id = id;
            this.time = time;
        }

    }
}
