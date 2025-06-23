package com.idle.rushcutter.enums;

import java.util.*;

public class DirectionResolver {

    private static final Map<String, Set<String>> UPBOUND_DESTINATIONS = Map.ofEntries(
            Map.entry("1호선", Set.of("연천", "소요산", "동두천", "양주", "의정부", "광운대", "청량리", "동묘앞",
                    "서울역", "영등포", "구로", "병점", "천안", "동두천급행", "청량리급행", "서울역급행", "용산급행", "용산특급", "구로급행")),
            Map.entry("2호선", Set.of("내선순환", "신도림", "성수")),
            Map.entry("3호선", Set.of("대화", "구파발", "독립문", "압구정", "백석")),
            Map.entry("4호선", Set.of("진접", "불암산", "노원", "한성대입구", "사당", "금정")),
            Map.entry("5호선", Set.of("방화", "화곡", "여의도", "애오개", "왕십리", "군자", "강동")),
            Map.entry("6호선", Set.of("응암순환", "응암", "새절", "대흥", "공덕", "한강진", "안암")),
            Map.entry("7호선", Set.of("장암", "도봉산", "수락산", "태릉입구", "건대입구", "청담", "내방", "온수")),
            Map.entry("8호선", Set.of("별내", "암사", "잠실")),
            Map.entry("9호선", Set.of("중앙보훈병원", "중앙보훈병원급행", "삼전", "신논현", "동작", "당산", "가양")),
            Map.entry("인천1호선", Set.of("계양", "박촌", "신연수")),
            Map.entry("인천2호선", Set.of("검단오류(검단산업단지)")),
            Map.entry("경강선", Set.of("판교")),
            Map.entry("경의중앙선", Set.of("문산", "문산급행", "일산", "능곡", "대곡", "수색", "수색급행", "용산", "임진강", "도라산")),
            Map.entry("경춘선", Set.of("청량리", "상봉", "광운대")),
            Map.entry("공항철도", Set.of("서울역")),
            Map.entry("서해선", Set.of("대곡", "일산")),
            Map.entry("서해선(대곡-원시)", Set.of("대곡", "일산")),
            Map.entry("수인.분당선", Set.of("청량리", "청량리급행", "왕십리", "왕십리급행", "죽전", "고색", "오이도", "오이도급행")),
            Map.entry("신분당선", Set.of("신사")),
            Map.entry("신림선", Set.of("샛강")),
            Map.entry("우이신설선", Set.of("북한산우이")),
            Map.entry("김포골드라인", Set.of("양촌", "구래")),
            Map.entry("에버라인", Set.of("기흥")),
            Map.entry("의정부경전철", Set.of("발곡")),
            Map.entry("GTX-A", Set.of("운정중앙", "수서"))
    );

    private static final Map<String, Set<String>> DOWNBOUND_DESTINATIONS = Map.ofEntries(
            Map.entry("1호선", Set.of("광운대", "서울역", "구로", "부평", "부평급행", "인천", "광명", "병점", "서동탄",
                    "천안", "신창", "광운대급행", "구로급행", "동인천급행", "동인천특급", "인천급행", "천안급행", "신창급행", "인천.신창")),
            Map.entry("2호선", Set.of("외선순환", "까치산", "신설동")),
            Map.entry("3호선", Set.of("오금", "수서", "도곡", "약수", "구파발", "삼송")),
            Map.entry("4호선", Set.of("오이도", "안산", "사당", "금정", "산본", "서울역", "불암산")),
            Map.entry("5호선", Set.of("하남검단산", "마천", "상일동", "군자", "왕십리", "애오개", "화곡")),
            Map.entry("6호선", Set.of("신내", "봉화산", "안암", "한강진", "공덕", "대흥", "새절", "응암", "독바위")),
            Map.entry("7호선", Set.of("석남", "석남(거북시장)","온수", "신풍", "내방", "청담", "건대입구")),
            Map.entry("8호선", Set.of("모란", "가락시장", "암사")),
            Map.entry("9호선", Set.of("개화", "김포공항급행", "마곡나루", "염창", "여의도", "동작", "신논현", "삼전")),
            Map.entry("인천1호선", Set.of("작전", "송도달빛축제공원", "국제업무지구", "동막", "예술회관")),
            Map.entry("인천2호선", Set.of("운연(서창)")),
            Map.entry("경강선", Set.of("여주")),
            Map.entry("경의중앙선", Set.of("지평", "용문", "팔당", "팔당급행", "용문급행", "덕소", "청량리", "용산", "서울역", "서울역급행", "문산", "임진강")),
            Map.entry("경춘선", Set.of("마석", "춘천")),
            Map.entry("공항철도", Set.of("인천공항2터미널")),
            Map.entry("서해선", Set.of("원시")),
            Map.entry("서해선(대곡-원시)", Set.of("원시")),
            Map.entry("수인.분당선", Set.of("죽전", "고색", "고색급행", "오이도", "인천", "인천급행")),
            Map.entry("신분당선", Set.of("광교(경기대)")),
            Map.entry("신림선", Set.of("관악산(서울대)")),
            Map.entry("우이신설선", Set.of("신설동")),
            Map.entry("김포골드라인", Set.of("김포공항")),
            Map.entry("에버라인", Set.of("전대.에버랜드")),
            Map.entry("의정부경전철", Set.of("탑석")),
            Map.entry("GTX-A", Set.of("서울역", "동탄"))
    );

    public static String resolve(String lineCode, String destination) {
        if (UPBOUND_DESTINATIONS.getOrDefault(lineCode, Set.of()).contains(destination)) {
            return "상행";
        }
        if (DOWNBOUND_DESTINATIONS.getOrDefault(lineCode, Set.of()).contains(destination)) {
            return "하행";
        }
        return "알 수 없음";
    }
}
