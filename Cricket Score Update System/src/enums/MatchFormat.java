package enums;

import lombok.Getter;

@Getter
public enum MatchFormat {
    TEST(90),      // 90 overs per innings (approximate)
    ODI(50),       // 50 overs per innings
    T20(20);       // 20 overs per innings

    private final int oversPerInnings;

    MatchFormat(int oversPerInnings) {
        this.oversPerInnings = oversPerInnings;
    }

}