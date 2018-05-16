package com.ruidi.gamedata.var;

import com.ruidi.gamedata.GameData;
import com.smartfoxserver.v2.entities.Room;
import com.smartfoxserver.v2.entities.User;
import com.smartfoxserver.v2.extensions.SFSExtension;

import java.util.List;

/**
 * 牛牛房间的各种参数
 */
public abstract class TabVar {
    protected GameType type;//牌型
    protected BaseScore baseScore;//底分
    protected Round round;//局数
    protected DouRule rule;//翻倍规则
    protected boolean AA;//房费AA值
    protected boolean shunziniu;//顺子牛
    protected boolean wuhuaniu;//五花牛
    protected boolean tonghuaniu;//同花牛
    protected boolean huluniu;//葫芦牛
    protected boolean zhadanniu;//炸弹牛
    protected boolean xiaoshuainiu;//小帅牛
    protected AutoStart autoStart; // 0 手动开始
    protected Tui tui;//推注
    protected QZMul qzMul;//抢庄倍数
    protected boolean entryAfterStart;//游戏开始后加入房间
    protected boolean twist;//搓牌
    protected boolean betLimit;//下注限制
    protected boolean ma;//买码
    protected boolean joker;//王癞
    protected GameData[] seatData;//房间每个位置的玩家
    protected List<User> lookUser;//观众
    protected Room room;//房间
    protected boolean gameStart;//是否开始游戏
    protected List<User> joinedUser;//加入过房间的玩家
    protected List<GameData> curGames;//当前玩游戏的玩家
    protected List<Byte> pokers;//一副扑克牌
    protected GameData banker;//庄家

    protected static final long RANDOM_BANKER_ANIMATION = 3000;//客户端随机选择庄家的动画时间
    protected boolean betAction = false;//下注开关
    protected boolean showAction = false;//亮牌开关
    protected boolean qzAction = false;//抢庄开关
    protected static final long BET_ACTION_TIME = 10000;//下注等待时间
    protected long start_bet_time = 0;//开始下注的时间
    protected static final long SHOW_HAND_TIME = 20000;//亮牌等待时间
    protected long start_show_hand = 0;//开始亮牌的时间
    protected static final long QZ_ACTION_TIME = 10000;//抢庄等待时间
    protected long start_qz_time = 0;//开始抢庄的时间
    protected long ownerId;//房主id
    protected List<GameData> bankers;
    protected int curRound;

    /**
     * 牌型和倍数
     */
    public enum PaiType {
        xiaoshuainiu(16), zhadanniu(15), huluniu(14), tonghuaniu(13), wuhuaniu(12), shunziniu(11), niuniu(10), niu9(9),
        niu8(8), niu7(7), niu6(6), niu5(5), niu4(4), niu3(3), niu2(2), niu1(1), meiniu(0);
        private int code;

        PaiType(int i) {
            code = i;
        }

        public int getTime(DouRule rule) {
            switch (code) {
                case 0:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                    return 1;
                case 7:
                    if (rule == DouRule.DR1)
                        return 2;
                    return 1;
                case 8:
                    return 2;
                case 9:
                    if (rule == DouRule.DR1)
                        return 3;
                    else return 2;
                case 10:
                    if (rule == DouRule.DR1)
                        return 4;
                    return 3;
                default:
                    return 1;
            }
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 局数
     */
    public enum Round {
        R1(15),
        R2(20),
        R3(30);
        private int round;

        Round(int i) {
            round = i;
        }

        public int getRound() {
            return round;
        }

        public static Round valueOf(int r, SFSExtension extension) {
            Round[] rounds = values();
            for (Round round : rounds) {
                if (round.round == r)
                    return round;
            }
            extension.trace("局数{}设置错误,已重置为默认值", r);
            return R1;
        }
    }

    /**
     * 游戏玩法
     */
    public enum GameType {
        niuniushangzhuang(1),
        ziyouqiangzhuang(2),
        mingpaiqiangzhuang(3),
        barenmingpai(4),
        gongpainiuniu(5);
        private int type;
        GameType(int i) {
            type = i;
        }

        public int getType() {
            return type;
        }

        public static GameType valueOf(int t, SFSExtension extension) {
            GameType[] types = values();
            for (GameType type : types)
                if (type.type == t)
                    return type;
            extension.trace("牛牛玩法{}设置错误,已重置为初始值", t);
            return niuniushangzhuang;
        }
    }

    /**
     * 底分
     */
    public enum BaseScore {
        B1(1), B2(2), B3(3), B4(4), B5(5);
        private int base;

        BaseScore(int i) {
            base = i;
        }

        public int getBase() {
            return base;
        }

        public static BaseScore valueOf(int b, SFSExtension e) {
            BaseScore[] baseScores = values();
            for (BaseScore bs : baseScores)
                if (bs.base == b)
                    return bs;
            e.trace("底分{}设置错误,已重置为默认值", b);
            return B1;
        }
    }

    /**
     * 翻倍规则
     */
    public enum DouRule {
        DR1(1), DR2(2);
        private int code;

        DouRule(int i) {
            code = i;
        }

        public static DouRule valueOf(int c, SFSExtension e) {
            DouRule[] douRules = values();
            for (DouRule dr : douRules)
                if (dr.code == c)
                    return dr;
            e.trace("翻倍code:{}设置错误,已重置为默认值", c);
            return DR1;
        }

        public int getCode() {
            return code;
        }
    }

    /**
     * 自动开始
     */
    public enum AutoStart {
        START0(0), START4(4), START5(5), START6(6), START7(7), START8(8);
        private int num;
        AutoStart(int i) {
            num = i;
        }

        public static AutoStart valueOf(GameType type, int i, SFSExtension e) {
            AutoStart[] autoStarts = values();
            for (AutoStart a : autoStarts) {
                if (a.num == i) {
                    if (type == GameType.barenmingpai) {
                        if (a == START0 || a == START6 || a == START7 || a == START8)
                            return a;
                    } else if (a == START0 || a == START4 || a == START5 || a == START6)
                        return a;
                }
            }
            e.trace("自动开始code:{}设置错误,已重置为默认值", i);
            return START0;
        }

        public int getNum() {
            return num;
        }
    }

    /**
     * 抢庄倍数
     */
    public enum QZMul {
        QZ1(1), QZ2(2), QZ3(3), QZ4(4);
        private int mul;

        QZMul(int i) {
            mul = i;
        }

        public static QZMul valueOf(int m, SFSExtension e) {
            QZMul[] qzMuls = values();
            for (QZMul q : qzMuls)
                if (q.mul == m)
                    return q;
            e.trace("抢庄code:{}设置错误,已重置为默认值", m);
            return QZ1;
        }
        public int getMul() {
            return mul;
        }
    }

    /**
     * 推注
     */
    public enum Tui {
        TUI0(0), TUI5(5), TUI10(10), TUI15(15);
        private int mul;

        Tui(int i) {
            mul = i;
        }

        public static Tui valueOf(int m, SFSExtension e) {
            Tui[] tuis = values();
            for (Tui t : tuis)
                if (t.mul == m)
                    return t;
            e.trace("推注mul:{}设置错误,已重置为默认值", m);
            return TUI0;
        }

        public int getMul() {
            return mul;
        }
    }
}
