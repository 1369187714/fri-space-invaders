package sound;

import engine.PermanentState;

public class SoundPlay{
    private static SoundPlay instance;
    private int effectVolume = 70;
    private int bgmVolume = 50;
    private SoundBgm sb;

    private SoundPlay(){}
    public static SoundPlay getInstance(){
        if(instance == null) instance = new SoundPlay();
        return instance;
    }

    public void play(SoundType type){
        switch (type) {
            case hit:
                playEffect("Hit_001.wav");
                break;
            case shoot:
                playEffect("Shoot_" + String.format("%03d",PermanentState.getInstance().getBulletSFX()) + ".wav");
                break;
            case lasing:
                playEffect("lasing.wav");
                break;
            case enemyKill:
                playEffect("EnemyKill_001.wav");
                break;
            case menuClick:
                playEffect("MenuClick_001.wav");
                break;
            case menuSelect:
                playEffect("MenuSelect_001.wav");
                break;
            case inGameBGM:
                playBgm("BGM_MainGame_" + String.format("%03d",PermanentState.getInstance().getBGM()) + ".wav");
                break;
            case mainGameBgm:
                playBgm("MainScreen_BGM_001.wav");
                break;
            case gameOverBGM:
                playBgmLoop("GameOver_001.wav", false);
                break;
            case roundClear:
                playEffect("RoundClear_001.wav");
                break;
            case roundStart:
                playEffect("RoundStart_001.wav");
                break;
            case roundCounting:
                playEffect("RoundCounting_001.wav");
                break;
            case bonusEnemyKill:
                playEffect("Bonus_EnemyKill_001.wav");
                break;
        }
    }

    //SoundPlay.getInstance().Play("파일명.wav"); 로 음악 재생
    //음악은 res폴더안에
    private void playBgm(String filename){
        if(sb == null || !sb.bgmClip.isActive()){
            sb = new SoundBgm(filename);
            sb.bgmVolume(bgmVolume);
            sb.play();
        }
    }

    public void playBgmLoop(String filename, boolean isLoop){
        if(sb == null || !sb.bgmClip.isActive()){
            sb = new SoundBgm(filename);
            sb.bgmVolume(bgmVolume);
            sb.setLoop(isLoop);
            sb.play();
        }
    }

    public void stopBgm(){
        if(sb.bgmClip.isActive()){
            sb.stop();
        }
    }


    private void playEffect(String filename) {
        SoundEffect se = new SoundEffect(filename);
        se.setVolume(effectVolume);
        se.play();
    }

    public void setEffectVolume(int value){
        this.effectVolume += value;
    }

    public void setBgmVolume(int value){
        this.bgmVolume += value;
        sb.bgmVolume(bgmVolume);
    }

    public int getEffectVolume(){
        return effectVolume;
    }

    public int getBgmVolume(){ return bgmVolume; }

}
