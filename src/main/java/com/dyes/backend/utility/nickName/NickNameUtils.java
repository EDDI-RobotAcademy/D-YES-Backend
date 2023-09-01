package com.dyes.backend.utility.nickName;

import java.util.Random;

public class NickNameUtils {
    public static String getRandomNickName() {
        Random random = new Random();

        String firstRandomWord[] = {"멋쟁이 ", "건방진 ", "귀여운 ", "졸린 ", "배고픈 ", "배부른 ", "화난 ", "못난이 "};
        String secondRandomWord[] = {"배추", "당근", "오이", "양배추", "애호박", "양파", "감자", "대파"};

        int firstIndex = random.nextInt(firstRandomWord.length);
        int secondIndex = random.nextInt(secondRandomWord.length);

        String randomNickName = firstRandomWord[firstIndex] + secondRandomWord[secondIndex];

        return randomNickName;
    }
}
