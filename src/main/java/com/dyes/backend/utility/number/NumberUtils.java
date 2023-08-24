package com.dyes.backend.utility.number;

import java.util.List;

public class NumberUtils {

    // 주어진 숫자 리스트 중 가장 낮은 숫자 값을 반환
    public static Long findMinValue(List<Long> numberList) {
        if(!numberList.isEmpty()) {
            Long minNumber = numberList.get(0);

            for (int i = 1; i < numberList.size(); i++) {
                Long current = numberList.get(i);
                if (current < minNumber) {
                    minNumber = current;
                }
            }
            return minNumber;
        }
        return 0L;
    }
}
